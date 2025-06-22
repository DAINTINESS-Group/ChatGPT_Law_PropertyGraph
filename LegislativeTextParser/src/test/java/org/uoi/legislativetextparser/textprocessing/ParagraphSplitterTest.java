package org.uoi.legislativetextparser.textprocessing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphSplitterTest {

    @Test
    void testSplitIntoParagraphsSingleParagraph() {
        String article = "1. This is a single paragraph article.";

        List<String> paragraphs = ParagraphSplitter.splitIntoParagraphs(article);

        assertNotNull(paragraphs, "Paragraph list should not be null");
        assertEquals(1, paragraphs.size(), "Article should contain one paragraph");
        assertEquals("1. This is a single paragraph article.", paragraphs.get(0),
                "The paragraph content should match the original text");
    }

    @Test
    void testSplitIntoParagraphsMultipleParagraphs() {
        String article = "1. This is the first paragraph.\n2. This is the second paragraph.\n3. This is the third paragraph.";

        List<String> paragraphs = ParagraphSplitter.splitIntoParagraphs(article);

        assertNotNull(paragraphs, "Paragraph list should not be null");
        assertEquals(3, paragraphs.size(), "Article should contain three paragraphs");
        assertEquals("1. This is the first paragraph.", paragraphs.get(0),
                "The first paragraph content should match");
        assertEquals("2. This is the second paragraph.", paragraphs.get(1),
                "The second paragraph content should match");
        assertEquals("3. This is the third paragraph.", paragraphs.get(2),
                "The third paragraph content should match");
    }

    @Test
    void testSplitIntoParagraphsEmptyArticle() {
        String article = "";

        List<String> paragraphs = ParagraphSplitter.splitIntoParagraphs(article);

        assertNotNull(paragraphs, "Paragraph list should not be null");
        assertTrue(paragraphs.isEmpty(), "Article should result in an empty paragraph list");
    }

    @Test
    void testSplitIntoParagraphsUnmatchedPattern() {
        String article = "No paragraph markers here.";

        List<String> paragraphs = ParagraphSplitter.splitIntoParagraphs(article);

        assertNotNull(paragraphs, "Paragraph list should not be null");
        assertEquals(1, paragraphs.size(), "Article should be treated as one single paragraph");
        assertEquals("No paragraph markers here.", paragraphs.get(0),
                "The entire article content should be considered one paragraph");
    }
}
