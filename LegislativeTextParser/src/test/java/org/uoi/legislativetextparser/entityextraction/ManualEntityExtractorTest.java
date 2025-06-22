package org.uoi.legislativetextparser.entityextraction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.Entity;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManualEntityExtractorTest {

    private ManualEntityExtractor extractor;
    private String sampleJsonFilePath;
    private String emptyJsonFilePath;
    private String invalidJsonFilePath;
    private String definitionsJsonFilePath;
    private String noDefinitionsJsonFilePath;


    @BeforeEach
    public void setUp() {
        extractor = new ManualEntityExtractor();
        sampleJsonFilePath = "src/test/resources/input/sample.json";
        emptyJsonFilePath = "src/test/resources/input/empty.json";
        invalidJsonFilePath = "src/test/resources/input/invalid.json";
        definitionsJsonFilePath = "src/test/resources/input/definitions.json";
        noDefinitionsJsonFilePath = "src/test/resources/input/noDefinitions.json";
    }

    @Test
    public void testCorrectSampleJson() throws Exception {
        List<Entity> entities = extractor.extractEntities(definitionsJsonFilePath);

        assertNotNull(entities, "Extracted entities should not be null");
        assertFalse(entities.isEmpty(), "Extracted entities list should not be empty");
    }

    @Test
    public void testEmptyJsonFile() {
        assertTrue(Files.exists(Paths.get(emptyJsonFilePath)), "Empty JSON file should exist");

        assertThrows(Exception.class, () -> extractor.extractEntities(emptyJsonFilePath), "Empty JSON file should throw an exception");
    }

    @Test
    public void testInvalidJsonFile() {
        assertTrue(Files.exists(Paths.get(invalidJsonFilePath)), "Invalid JSON file should exist");

        assertThrows(Exception.class, () -> extractor.extractEntities(invalidJsonFilePath), "Invalid JSON file should throw an exception");
    }

    @Test
    public void testContainsDefinitionsFalse() throws Exception {

        List<Entity> entities = extractor.extractEntities(noDefinitionsJsonFilePath);

        assertNotNull(entities, "Extracted entities should not be null");
        assertTrue(entities.isEmpty(), "Extracted entities list should be empty");
    }
}
