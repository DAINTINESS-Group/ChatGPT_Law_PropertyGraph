package org.uoi.legislativetextparser.entityextraction;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.Entity;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpecificLocationEntityExtractorTest {

    private SpecificLocationEntityExtractor extractor;
    private static final String DEFINITIONS_JSON_FILE_PATH = "src/test/resources/input/definitions.json";
    private static final String EMPTY_JSON_FILE_PATH = "src/test/resources/input/empty.json";

    @BeforeEach
    public void setUp() {
        extractor = new SpecificLocationEntityExtractor(1, 3);
    }

    @Test
    public void testExtractEntities() throws Exception {
        assertTrue(Files.exists(Paths.get(DEFINITIONS_JSON_FILE_PATH)), "Definitions JSON file should exist");

        List<Entity> entities = extractor.extractEntities(DEFINITIONS_JSON_FILE_PATH);

        assertNotNull(entities, "Extracted entities should not be null");
        assertEquals(4, entities.size(), "There should be 14 extracted entities");

        Entity firstEntity = entities.get(0);
        assertEquals("AI system", firstEntity.getName(), "First entity name mismatch");
        assertTrue(firstEntity.getDefinition().contains("machine-based system"), "First entity definition mismatch");
    }

    @Test
    public void testNoEntitiesInEmptyJson() {
        assertTrue(Files.exists(Paths.get(EMPTY_JSON_FILE_PATH)), "Empty JSON file should exist");

        Exception exception = assertThrows(JSONException.class, () -> extractor.extractEntities(EMPTY_JSON_FILE_PATH));

        assertEquals("JSONObject[\"chapters\"] not found.", exception.getMessage(), "Exception message mismatch");
    }

    @Test
    public void testInvalidChapterOrArticle() {
        extractor = new SpecificLocationEntityExtractor(99, 99);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> extractor.extractEntities(DEFINITIONS_JSON_FILE_PATH));

        assertEquals("Chapter 99 not found.", exception.getMessage(), "Exception message mismatch");
    }
}
