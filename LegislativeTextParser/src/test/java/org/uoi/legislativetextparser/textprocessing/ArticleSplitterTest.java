package org.uoi.legislativetextparser.textprocessing;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleSplitterTest {

    @Test
    public void testSplitIntoArticlesWithValidChapter() {
        String chapter = "Article 1\nThis is the first article.\n\nArticle 2\nThis is the second article.";

        ArrayList<String> articles = ArticleSplitter.splitIntoArticles(chapter);

        assertNotNull(articles, "Articles list should not be null");
        assertEquals(2, articles.size(), "Should split into 2 articles");
        assertTrue(articles.get(0).startsWith("Article 1"), "First article should start with 'Article 1'");
        assertTrue(articles.get(1).startsWith("Article 2"), "Second article should start with 'Article 2'");
    }

    @Test
    public void testSplitIntoArticlesWithEmptyChapter() {
        String chapter = "";

        ArrayList<String> articles = ArticleSplitter.splitIntoArticles(chapter);

        assertNotNull(articles, "Articles list should not be null");
        assertTrue(articles.isEmpty(), "Articles list should be empty for an empty chapter");
    }

    @Test
    public void testSplitIntoArticlesWithNoArticles() {
        String chapter = "This is a chapter without articles.";

        ArrayList<String> articles = ArticleSplitter.splitIntoArticles(chapter);

        assertNotNull(articles, "Articles list should not be null");
        assertTrue(articles.isEmpty(), "Articles list should be empty for a chapter with no articles");
    }

    @Test
    public void testSplitIntoArticlesWithArticleNumberSuffix() {
        String chapter = "Article 1a\nThis is article 1a.\n\nArticle 2b\nThis is article 2b.";

        ArrayList<String> articles = ArticleSplitter.splitIntoArticles(chapter);

        assertNotNull(articles, "Articles list should not be null");
        assertEquals(2, articles.size(), "Should split into 2 articles with suffixes");
        assertTrue(articles.get(0).startsWith("Article 1a"), "First article should start with 'Article 1a'");
        assertTrue(articles.get(1).startsWith("Article 2b"), "Second article should start with 'Article 2b'");
    }

    @Test
    public void testSplitIntoArticlesWithAdditionalText() {
        String chapter = "Article 1\nThis is the first article.\n\nExtra text that does not belong to any article.";

        ArrayList<String> articles = ArticleSplitter.splitIntoArticles(chapter);

        assertNotNull(articles, "Articles list should not be null");
        assertEquals(1, articles.size(), "Should only contain 1 article with extra text appended");
        assertTrue(articles.get(0).contains("Extra text"), "The extra text should be part of the last article");
    }
}
