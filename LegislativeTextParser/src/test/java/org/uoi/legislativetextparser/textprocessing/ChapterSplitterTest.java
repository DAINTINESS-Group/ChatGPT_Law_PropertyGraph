package org.uoi.legislativetextparser.textprocessing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChapterSplitterTest {

    private final ChapterSplitter chapterSplitter = new ChapterSplitter("src/test/resources/output/chapters/");

    private static final String SAMPLE_TEXT = """
            CHAPTER I
            Introduction to AI
            This is the content of chapter I.
            
            CHAPTER II
            Regulations
            This is the content of chapter II.
            
            CHAPTER III
            Implementation
            This is the content of chapter III.
            """;

    private static final File OUTPUT_DIR = new File("src/test/resources/output/chapters/");

    @BeforeEach
    public void setUp() throws IOException {
        if (OUTPUT_DIR.exists()) {
            for (File file : OUTPUT_DIR.listFiles()) {
                file.delete();
            }
        } else {
            OUTPUT_DIR.mkdirs();
        }
    }

    @Test
    public void testSplitIntoChapters() throws IOException {
        chapterSplitter.splitIntoChapters(SAMPLE_TEXT);

        File[] chapterFiles = OUTPUT_DIR.listFiles();
        assertNotNull(chapterFiles, "Output directory should contain files");
        assertEquals(3, chapterFiles.length, "Three chapter files should be created");

        List<String> chapters = List.of(SAMPLE_TEXT.split("(?=\\bCHAPTER\\s+[IVXLCDM]+)"));
        for (int i = 0; i < chapters.size(); i++) {
            File chapterFile = new File(OUTPUT_DIR, "chapter_" + (i + 1) + ".txt");
            assertTrue(chapterFile.exists(), "File " + chapterFile.getName() + " should exist");

            String fileContent = Files.readString(chapterFile.toPath()).trim();
            assertEquals(chapters.get(i).trim(), fileContent, "File content should match chapter content");
        }
    }

    @Test
    public void testSplitIntoChaptersEmptyText() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                chapterSplitter.splitIntoChapters("")
        );
        assertEquals("Text cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testSplitIntoChaptersNullText() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                chapterSplitter.splitIntoChapters(null)
        );
        assertEquals("Text cannot be null or empty.", exception.getMessage());
    }
}
