package org.uoi.legislativetextparser.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ChapterTest {

    @Test
    void testBuilderCreatesChapterSuccessfully() {
        Chapter chapter = new Chapter.Builder(1, new ArrayList<>(), "Chapter 1 Title").build();

        assertEquals(1, chapter.getChapterNumber());
        assertEquals("Chapter 1 Title", chapter.getChapterTitle());
        assertNotNull(chapter.getArticles());
        assertTrue(chapter.getArticles().isEmpty());
    }

    @Test
    void testBuilderWithArticles() {
        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article.Builder(1, new ArrayList<>(), "1.1", "Article 1").build());
        articles.add(new Article.Builder(2, new ArrayList<>(), "1.2", "Article 2").build());

        Chapter chapter = new Chapter.Builder(1, articles, "Chapter 1 Title").build();

        assertEquals(2, chapter.getArticles().size());
        assertEquals("1.1", chapter.getArticles().get(0).getArticleID());
        assertEquals("1.2", chapter.getArticles().get(1).getArticleID());
    }

    @Test
    void testGetTextWithNoArticles() {
        Chapter chapter = new Chapter.Builder(1, new ArrayList<>(), "Chapter 1 Title").build();
        String expectedText = "Chapter 1 Title\n\n";
        assertEquals(expectedText.trim(), chapter.getText());
    }

    @Test
    void testGetTextWithArticles() {
        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article.Builder(1, new ArrayList<>(), "1.1", "Article 1").build());
        articles.add(new Article.Builder(2, new ArrayList<>(), "1.2", "Article 2").build());

        Chapter chapter = new Chapter.Builder(1, articles, "Chapter 1 Title").build();

        String text = chapter.getText();
        assertTrue(text.contains("Article 1"));
        assertTrue(text.contains("Article 2"));
    }

    @Test
    void testGetChildren() {
        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article.Builder(1, new ArrayList<>(), "1.1", "Article 1").build());
        articles.add(new Article.Builder(2, new ArrayList<>(), "1.2", "Article 2").build());

        Chapter chapter = new Chapter.Builder(1, articles, "Chapter 1 Title").build();

        assertEquals(articles, chapter.getChildren());
    }

    @Test
    void testToString() {
        Chapter chapter = new Chapter.Builder(1, new ArrayList<>(), "Chapter 1 Title").build();
        assertEquals("Chapter 1: Chapter 1 Title", chapter.toString());
    }
}

// TODO Need to add Json test case