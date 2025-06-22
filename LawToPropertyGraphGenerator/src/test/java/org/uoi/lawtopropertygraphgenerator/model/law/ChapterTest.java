package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ChapterTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testChapterDeserialization() throws Exception {
        String json = TestUtils.readResourceFile("mock-data/chapter.json");
        Chapter chapter = objectMapper.readValue(json, Chapter.class);

        assertEquals("Chapter Title Example", chapter.getChapterTitle());
        assertNotNull(chapter.getArticles());
        assertFalse(chapter.getArticles().isEmpty());
        assertTrue(chapter.getText().contains("Chapter Title Example"));
    }
}
