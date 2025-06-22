package org.uoi.lawtopropertygraphgenerator.graphML;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.uoi.lawtopropertygraphgenerator.model.graphML.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class GraphMLNormalizer {

    public void normalizeAndExport(String inputGraphML,
                                          String entityListPath,
                                          String outputJsonPath,
                                          String tier4OutputPath) throws IOException {

        GraphMLParser parser = new GraphMLParser();
        GraphModel model = parser.parseGraphML(inputGraphML);

        Set<String> knownEntities = Files.lines(Paths.get(entityListPath))
                .map(line -> line.split(" means ")[0].trim().toLowerCase())
                .collect(Collectors.toSet());

        LabelNormalizer normalizer = new LabelNormalizer();
        Map<String, Integer> scoreMap = NodeScorer.scoreNodes(model.nodes, model.edges, knownEntities, normalizer);

        List<String> tier4Labels = model.nodes.stream()
                .filter(n -> scoreMap.getOrDefault(n.getId(), 4) == 4)
                .map(Node::getLabel)
                .sorted()
                .collect(Collectors.toList());

        Files.write(Paths.get(tier4OutputPath), tier4Labels, StandardCharsets.UTF_8);

        Set<String> allLabels = model.nodes.stream().map(Node::getLabel).collect(Collectors.toSet());
        Map<String, String> normalizationMap = normalizer.generateNormalizationMap(allLabels);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputJsonPath), normalizationMap);
    }
}
