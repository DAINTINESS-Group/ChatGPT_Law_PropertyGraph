package org.uoi.lawchecklistgenerator.model.law;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.uoi.lawchecklistgenerator.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ParagraphTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testParagraphDeserialization() throws Exception {
        String json = TestUtils.readResourceFile("mock-data/paragraph.json");
        Paragraph paragraph = objectMapper.readValue(json, Paragraph.class);

        assertEquals("P1", paragraph.getParagraphID());
        assertNotNull(paragraph.getParagraphPoints());
        assertFalse(paragraph.getParagraphPoints().isEmpty());
        assertTrue(paragraph.getText().contains("Single point inside paragraph."));
    }
}