package org.uoi.legislativetextparser.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleTest {

    @Test
    void testBuilderCreatesArticleSuccessfully() {
        Article article = new Article.Builder(1, new ArrayList<>(), "1.1", "Title 1").build();

        assertEquals(1, article.getArticleNumber());
        assertEquals("1.1", article.getArticleID());
        assertEquals("Title 1", article.getArticleTitle());
        assertNotNull(article.getParagraphs());
        assertTrue(article.getParagraphs().isEmpty());
    }

    @Test
    void testBuilderWithParagraphs() {
        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        paragraphs.add(new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build());
        paragraphs.add(new Paragraph.Builder(2, new ArrayList<>(), "1.1.2").build());

        Article article = new Article.Builder(1, paragraphs, "1.1", "Title 1").build();

        assertEquals(2, article.getParagraphs().size());
        assertEquals("1.1.1", article.getParagraphs().get(0).getParagraphID());
        assertEquals("1.1.2", article.getParagraphs().get(1).getParagraphID());
    }

    @Test
    void testGetTextWithNoParagraphs() {
        Article article = new Article.Builder(1, new ArrayList<>(), "1.1", "Title 1").build();
        String expectedText = "Title 1\n\n";
        assertEquals(expectedText.trim(), article.getText());
    }

    @Test
    void testGetTextWithParagraphs() {
        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        paragraphs.add(new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build());
        paragraphs.add(new Paragraph.Builder(2, new ArrayList<>(), "1.1.2").build());

        Article article = new Article.Builder(1, paragraphs, "1.1", "Title 1").build();


        assertEquals("Title 1", article.getText());
    }

    @Test
    void testGetChildren() {
        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        paragraphs.add(new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build());
        paragraphs.add(new Paragraph.Builder(2, new ArrayList<>(), "1.1.2").build());

        Article article = new Article.Builder(1, paragraphs, "1.1", "Title 1").build();

        assertEquals(paragraphs, article.getChildren());
    }

    @Test
    void testToString() {
        Article article = new Article.Builder(1, new ArrayList<>(), "1.1", "Title 1").build();
        assertEquals("Article 1.1: Title 1", article.toString());
    }
}

// TODO Need to add Json test case