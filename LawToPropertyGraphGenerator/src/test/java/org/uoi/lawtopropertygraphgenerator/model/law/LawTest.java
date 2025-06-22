package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class LawTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLawDeserialization() throws Exception {
        String json = TestUtils.readResourceFile("mock-data/law.json");
        Law law = objectMapper.readValue(json, Law.class);

        assertEquals("Law", law.getTitle());
        assertNotNull(law.getChapters());
        assertFalse(law.getChapters().isEmpty());
        assertTrue(law.getText().contains("Point inside paragraph."));
    }
}