package org.uoi.lawtopropertygraphgenerator.graphML;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.lawtopropertygraphgenerator.model.LabelPair;
import org.uoi.lawtopropertygraphgenerator.model.UnionFind;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class GraphmlSimilarityAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(GraphmlSimilarityAnalyzer.class);

    public void analyzeGraphML(String graphMLFilePath, String keyAttribute, int standardThreshold, int significantThreshold) {
        List<String> labels = extractNodeLabels(graphMLFilePath, keyAttribute);
        log.info("Extracted {} labels from GraphML.", labels.size());

        List<LabelPair> similarPairs = analyzeNodeSimilarity(labels, standardThreshold, significantThreshold);
        log.info("Found {} similar label pairs with standard threshold {}% and significant threshold {}%.", similarPairs.size(), standardThreshold, significantThreshold);
        similarPairs.forEach(pair -> log.info("Similar Pair: {}", pair));

        Map<String, Set<String>> mergedClusters = mergeSimilarLabels(labels, standardThreshold, significantThreshold);
        log.info("Merged into {} canonical label clusters:", mergedClusters.size());
        mergedClusters.forEach((canonical, group) ->
                log.info("Canonical: [{}] => Merged Labels: {}", canonical, group)
        );
    }

    private List<String> extractNodeLabels(String graphMLFilePath, String keyAttribute) {
        List<String> labels = new ArrayList<>();
        try {
            File inputFile = new File(graphMLFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("node");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList dataList = element.getElementsByTagName("data");
                    for (int j = 0; j < dataList.getLength(); j++) {
                        Element dataElement = (Element) dataList.item(j);
                        if (keyAttribute.equals(dataElement.getAttribute("key"))) {
                            labels.add(dataElement.getTextContent().trim().toLowerCase());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse GraphML file: {}", e.getMessage(), e);
        }
        return labels;
    }

    private boolean isSignificantEntity(String label) {
        return label.contains("article") || label.contains("chapter") || label.contains("section");
    }

    private boolean shouldMergeLabels(String label1, String label2, int standardThreshold, int significantThreshold, LevenshteinDistance ld) {
        int distance = ld.apply(label1, label2);
        int maxLength = Math.max(label1.length(), label2.length());
        double similarity = (1 - ((double) distance / maxLength)) * 100;

        return isSignificantEntity(label1) && isSignificantEntity(label2) ? similarity >= significantThreshold : similarity >= standardThreshold;
    }

    public List<LabelPair> analyzeNodeSimilarity(List<String> labels, int standardThreshold, int significantThreshold) {
        List<LabelPair> similarPairs = new ArrayList<>();
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

        for (int i = 0; i < labels.size(); i++) {
            String label1 = labels.get(i);
            for (int j = i + 1; j < labels.size(); j++) {
                String label2 = labels.get(j);
                int distance = levenshteinDistance.apply(label1, label2);
                int maxLength = Math.max(label1.length(), label2.length());
                double similarityPercentage = (1 - ((double) distance / maxLength)) * 100;
                if (shouldMergeLabels(label1, label2, standardThreshold, significantThreshold, levenshteinDistance)) {
                    similarPairs.add(new LabelPair(label1, label2, similarityPercentage));
                }
            }
        }
        return similarPairs;
    }

    public Map<String, Set<String>> mergeSimilarLabels(List<String> labels, int standardThreshold, int significantThreshold) {
        int n = labels.size();
        UnionFind uf = new UnionFind(n);
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                String label1 = labels.get(i);
                String label2 = labels.get(j);
                if (shouldMergeLabels(label1, label2, standardThreshold, significantThreshold, levenshteinDistance)) {
                    uf.union(i, j);
                }
            }
        }

        Map<Integer, Set<String>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int parent = uf.find(i);
            clusters.computeIfAbsent(parent, k -> new HashSet<>()).add(labels.get(i));
        }

        Map<String, Set<String>> mergedClusters = new HashMap<>();
        clusters.values().stream().map(ArrayList::new).forEach(sorted -> {
            Collections.sort(sorted);
            String canonical = sorted.get(0);
            mergedClusters.put(canonical, new HashSet<>(sorted));
        });
        return mergedClusters;
    }
}