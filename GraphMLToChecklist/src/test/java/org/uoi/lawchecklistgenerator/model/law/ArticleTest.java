package org.uoi.lawchecklistgenerator.model.law;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.uoi.lawchecklistgenerator.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testArticleDeserialization() throws Exception {
        String json = TestUtils.readResourceFile("mock-data/article.json");
        Article article = objectMapper.readValue(json, Article.class);

        assertEquals("Sample Article", article.getArticleTitle());
        assertNotNull(article.getParagraphs());
        assertFalse(article.getParagraphs().isEmpty());
        assertTrue(article.getText().contains("Point text inside paragraph."));
    }
}