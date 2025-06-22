package org.uoi.legislativetextparser.engine;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.config.Config;
import org.uoi.legislativetextparser.model.Law;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class LawProcessorTest {


    private Config config;
    private LawProcessor lawProcessor;

    @BeforeEach
    public void setUp() {
        config = new Config(
                "src/test/resources/input/AI_Law.pdf",
                "src/test/resources/output/law.json"
        );
        lawProcessor = new LawProcessor(config);
    }

    @Test
    public void testProcessLegislativeDocumentSuccess() throws IOException {
        Files.deleteIfExists(Paths.get(config.getLawJsonPath()));

        Law law = lawProcessor.processLegislativeDocument();

        assertNotNull(law, "Law object should not be null");

        assertTrue(Files.exists(Paths.get(config.getLawJsonPath())), "JSON output file should exist");

        String jsonContent = Files.readString(Paths.get(config.getLawJsonPath()));
        JSONObject root = new JSONObject(jsonContent);

        assertTrue(root.has("chapters"), "JSON should have 'chapters'");
        JSONArray chapters = root.getJSONArray("chapters");
        assertFalse(chapters.isEmpty(), "Chapters should not be empty");

        JSONObject firstChapter = chapters.getJSONObject(0);
        assertTrue(firstChapter.has("articles"), "First chapter should have articles");
    }

    @Test
    public void testProcessLegislativeDocumentMissingPdf() {
        Config invalidConfig = new Config(
                "src/test/resources/input/missing_law.pdf",
                "src/test/resources/output/law.json"
        );
        LawProcessor invalidLawProcessor = new LawProcessor(invalidConfig);

        assertThrows(IOException.class, invalidLawProcessor::processLegislativeDocument);
    }

    @Test
    public void testClearChaptersDirectory() throws IOException {
        String chaptersDir = Config.getChaptersDir();
        Files.createDirectories(Paths.get(chaptersDir));
        Files.writeString(Paths.get(chaptersDir, "old_chapter1.txt"), "Old chapter content");

        assertTrue(Files.list(Paths.get(chaptersDir)).findAny().isPresent(), "Chapters directory should initially contain old files");

        lawProcessor.processLegislativeDocument();

        assertFalse(Files.list(Paths.get(chaptersDir)).anyMatch(path -> path.getFileName().toString().startsWith("old")),
                "Old files should be removed during processing");

        assertTrue(Files.list(Paths.get(chaptersDir)).findAny().isPresent(), "New files should be added to the chapters directory after processing");
    }


    @Test
    public void testWriteLawToJsonFailure() {
        Config invalidConfig = new Config(
                "src/test/resources/input/sample_law.pdf",
                "src/test/resources/output/invalid_path/law.json"
        );
        LawProcessor invalidLawProcessor = new LawProcessor(invalidConfig);

        IOException exception = assertThrows(IOException.class, invalidLawProcessor::processLegislativeDocument);
        assertEquals("Failed to extract text from the PDF document.", exception.getMessage(), "Exception message should match the expected error");
    }

    @Test
    public void testBuildLawObject() throws IOException {
        Files.createDirectories(Paths.get(Config.getChaptersDir()));
        Files.writeString(Paths.get(Config.getChaptersDir(), "chapter1.txt"), "Chapter 1 content");

        Law law = lawProcessor.processLegislativeDocument();

        assertNotNull(law, "Law object should not be null");
        assertFalse(law.getChapters().isEmpty(), "Law should have chapters");
    }
}