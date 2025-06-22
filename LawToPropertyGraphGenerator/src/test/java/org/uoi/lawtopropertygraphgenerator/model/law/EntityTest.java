package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testEntityDeserialization() throws Exception {
        String json = TestUtils.readResourceFile("mock-data/entity.json");
        Entity entity = objectMapper.readValue(json, Entity.class);

        assertEquals("Entity Name", entity.getName());
        assertEquals("Entity Definition", entity.getDefinition());
    }
}