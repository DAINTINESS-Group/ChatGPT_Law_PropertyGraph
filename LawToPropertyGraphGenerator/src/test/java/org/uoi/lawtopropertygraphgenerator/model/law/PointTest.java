package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPointDeserialization() throws Exception {
        String json = TestUtils.readResourceFile("mock-data/point.json");
        Point point = objectMapper.readValue(json, Point.class);

        assertEquals(1, point.getPointNumber());
        assertEquals("Simple point text.", point.getText());
    }
}