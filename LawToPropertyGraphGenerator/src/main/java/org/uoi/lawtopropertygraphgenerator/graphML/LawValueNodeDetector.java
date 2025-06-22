package org.uoi.lawtopropertygraphgenerator.graphML;

import org.uoi.lawtopropertygraphgenerator.model.graphML.GraphModel;
import org.uoi.lawtopropertygraphgenerator.model.graphML.LowDegreeNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LawValueNodeDetector {

    private final GraphMLParser parser = new GraphMLParser();

    public List<LowDegreeNode> analyzeLowDegreeNodes(String graphMLPath, String tier4Path, int degreeThreshold) {
        GraphModel graph = parser.parseGraphML(graphMLPath);
        Set<String> tier4Labels = readTier4Labels(tier4Path);

        List<LowDegreeNode> lowDegreeNodes = new ArrayList<>();

        Map<String, Integer> inDegrees = new HashMap<>();
        Map<String, Integer> outDegrees = new HashMap<>();

        graph.getEdges().forEach(edge -> {
            outDegrees.put(edge.getSource(), outDegrees.getOrDefault(edge.getSource(), 0) + 1);
            inDegrees.put(edge.getTarget(), inDegrees.getOrDefault(edge.getTarget(), 0) + 1);
        });

        graph.getNodes().forEach(node -> {
            String label = node.getLabel();
            if (tier4Labels.contains(label)) {
                int in = inDegrees.getOrDefault(node.getId(), 0);
                int out = outDegrees.getOrDefault(node.getId(), 0);
                if (in + out < degreeThreshold) {
                    lowDegreeNodes.add(new LowDegreeNode(node.getId(), label, in, out, 4));
                }
            }
        });
        return lowDegreeNodes;
    }

    private Set<String> readTier4Labels(String filePath) {
        Set<String> labels = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    labels.add(line.trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read tier4.txt file: " + filePath, e);
        }
        return labels;
    }
}
