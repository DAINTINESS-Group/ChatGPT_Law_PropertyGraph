package org.uoi.lawtopropertygraphgenerator.graphML;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphNormalizerTest {

    @Test
    void testNormalizeAndExport() throws Exception {
        GraphMLNormalizer normalizer = new GraphMLNormalizer();

        String graphmlPath = Paths.get(
                getClass().getClassLoader().getResource("mock-data/cleanGraphml.graphml").toURI()
        ).toString();

        Path entityFile = Files.createTempFile("entities", ".txt");
        Files.write(entityFile, List.of("AI system means an artificial system", "Person means a human being"));

        Path jsonOut = Files.createTempFile("normalization", ".json");
        Path tier4Out = Files.createTempFile("tier4", ".txt");

        normalizer.normalizeAndExport(
                graphmlPath,
                entityFile.toString(),
                jsonOut.toString(),
                tier4Out.toString()
        );

        assertTrue(Files.exists(jsonOut));
        ObjectMapper mapper = new ObjectMapper();
        Map normalizationMap = mapper.readValue(jsonOut.toFile(), Map.class);

        assertEquals(4, normalizationMap.size());
        assertEquals("Agency", normalizationMap.get("Agency"));
        assertEquals("Public authority", normalizationMap.get("Public authority"));
        assertEquals("Body", normalizationMap.get("Body"));
        assertEquals("Deployer", normalizationMap.get("Deployer"));

        Files.deleteIfExists(entityFile);
        Files.deleteIfExists(jsonOut);
        Files.deleteIfExists(tier4Out);
    }
}
