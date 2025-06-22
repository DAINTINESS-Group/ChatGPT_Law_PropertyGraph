package org.uoi.lawtopropertygraphgenerator.graphML;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.uoi.lawtopropertygraphgenerator.model.graphML.Edge;
import org.uoi.lawtopropertygraphgenerator.model.graphML.Node;
import java.util.*;

@Slf4j
public class NodeScorer {

    public static Map<String, Integer> scoreNodes(List<Node> nodes,
                                                  List<Edge> edges,
                                                  Set<String> knownEntities,
                                                  LabelNormalizer normalizer) {

        Map<String, Integer> scoreMap = new HashMap<>();
        LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();

        Set<String> tier1 = new HashSet<>();
        nodes.forEach(node -> {
            String norm = normalizer.normalize(node.getLabel());
            if (knownEntities.contains(norm)) {
                scoreMap.put(node.getId(), 1);
                tier1.add(node.getId());
            }
        });

        Set<String> tier2 = new HashSet<>();
        for (Node node : nodes) {
            if (scoreMap.containsKey(node.getId())) continue;
            String norm = normalizer.normalize(node.getLabel());
            for (String entity : knownEntities) {
                int distance = levenshtein.apply(norm, entity);
                int max = Math.max(norm.length(), entity.length());
                if ((1 - (double) distance / max) * 100 >= 80) {
                    scoreMap.put(node.getId(), 2);
                    tier2.add(node.getId());
                    break;
                }
            }
        }

        Set<String> tier3 = new HashSet<>();
        edges.forEach(edge -> {
            if ((tier1.contains(edge.getSource()) || tier2.contains(edge.getSource()))
                    && !scoreMap.containsKey(edge.getTarget())) {
                scoreMap.put(edge.getTarget(), 3);
                tier3.add(edge.getTarget());
            }
            if ((tier1.contains(edge.getTarget()) || tier2.contains(edge.getTarget()))
                    && !scoreMap.containsKey(edge.getSource())) {
                scoreMap.put(edge.getSource(), 3);
                tier3.add(edge.getSource());
            }
        });

        nodes.forEach(node -> scoreMap.putIfAbsent(node.getId(), 4));

        return scoreMap;
    }
}