package org.uoi.legislativetextparser.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.Chapter;
import org.uoi.legislativetextparser.model.Law;
import org.uoi.legislativetextparser.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LawConstructorTest {

    private LawConstructor lawConstructor;
    private final Config config = new Config("src/test/resources/input/AI_Law.pdf", "src/test/resources/output/law.json");

    @BeforeEach
    public void setUp() {
        lawConstructor = new LawConstructor();
    }

    @Test
    public void testConstructLawObjectSuccess() throws IOException {
        Files.createDirectories(Paths.get(Config.getChaptersDir()));
        Files.writeString(Paths.get(Config.getChaptersDir(), "chapter_1.txt"), """
            CHAPTER I
            GENERAL PROVISIONS

            Article 1
            Purpose

            This Regulation establishes the rules for AI systems.

            Article 2
            Scope

            This Regulation applies to all AI systems used in the EU.
            """);

        Files.writeString(Paths.get(Config.getChaptersDir(), "chapter_2.txt"), """
            CHAPTER II
            OBLIGATIONS

            Article 3
            Definitions

            Definitions are provided here.

            Article 4
            Responsibilities

            Responsibilities for providers and deployers are outlined.
            """);

        Law law = lawConstructor.constructLawObject();

        assertNotNull(law, "Law object should not be null");
        List<Chapter> chapters = law.getChapters();
        assertEquals(2, chapters.size(), "Law should have 2 chapters");

        Chapter firstChapter = chapters.get(0);
        assertEquals("General provisions", firstChapter.getTitle().replace("\n",""), "First chapter title mismatch");
        assertEquals(2, firstChapter.getArticles().size(), "First chapter should have 2 articles");
    }

    @Test
    public void testConstructLawObjectNoChapters() {
        File chaptersDir = new File(Config.getChaptersDir());
        if (chaptersDir.exists()) {
            for (File file : chaptersDir.listFiles()) {
                file.delete();
            }
        }

        assertThrows(IllegalArgumentException.class, lawConstructor::constructLawObject, "Should throw exception if no chapters found");
    }

    @Test
    public void testConstructLawObjectMalformedChapter() throws IOException {
        Files.createDirectories(Paths.get(Config.getChaptersDir()));

        Files.writeString(Paths.get(Config.getChaptersDir(), "chapter_1.txt"), """
        CHAPTER
        MALFORMED DATA
        """);

        Files.writeString(Paths.get(Config.getChaptersDir(), "chapter_2.txt"), """
        CHAPTER II
        VALID CHAPTER

        Article 1
        Purpose

        This Regulation establishes rules for AI systems.
        """);

        Law law = lawConstructor.constructLawObject();

        // Verify the Law object
        assertNotNull(law, "Law object should not be null");
        assertEquals(13, law.getChapters().size(), "Law should contain both chapters");

        Chapter malformedChapter = law.getChapters().get(0);
        assertEquals("Could not extract chapter title", malformedChapter.getTitle(), "Malformed chapter title mismatch");
        assertTrue(malformedChapter.getArticles().isEmpty(), "Malformed chapter should have no articles");

        Chapter validChapter = law.getChapters().get(1);
        assertEquals("Valid chapter", validChapter.getTitle(), "Valid chapter title mismatch");
        assertEquals(1, validChapter.getArticles().size(), "Valid chapter should contain one article");

        assertEquals("Purpose", validChapter.getArticles().get(0).getTitle(), "Article title mismatch");
    }


    @Test
    public void testExtractArticleTitle() {
        String articleText = """
            Article 6
            Technical documentation requirements

            Providers shall maintain documentation for AI systems.
            """;

        String articleTitle = LawConstructor.extractArticleTitle(articleText);
        assertEquals("Technical documentation requirements", articleTitle, "Article title should match extracted title");
    }
}
