package org.uoi.legislativetextparser.textprocessing;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TxtCleanerTest {

    private final TxtCleaner txtCleaner = new TxtCleaner();

    @Test
    void cleanTextShouldThrowExceptionForNullText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> txtCleaner.cleanText(null));
        assertEquals("Text cannot be null or empty.", exception.getMessage());
    }

    @Test
    void cleanTextShouldThrowExceptionForEmptyText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> txtCleaner.cleanText(""));
        assertEquals("Text cannot be null or empty.", exception.getMessage());
    }

    @Test
    void filterFromSectionShouldExtractFromStartMarker() {
        String input = "Some text before\nCHAPTER I\nContent starts here.";
        String result = txtCleaner.filterFromSection(input);
        assertEquals("CHAPTER I\nContent starts here.", result);
    }

    @Test
    void filterFromSectionShouldThrowExceptionIfStartMarkerNotFound() {
        String input = "Some unrelated text.";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> txtCleaner.filterFromSection(input));
        assertTrue(exception.getMessage().contains("It seems like this document is not a legislative file."));
    }

    @Test
    void filterUntilSectionShouldExtractUntilEndMarker() {
        String input = "CHAPTER I\nSome content here.\nANNEX I\nAdditional text.";
        String result = txtCleaner.filterUntilSection(input);
        assertEquals("CHAPTER I\nSome content here.", result);
    }

    @Test
    void filterUntilSectionShouldReturnFullTextIfEndMarkerNotFound() {
        String input = "CHAPTER I\nSome content here.";
        String result = txtCleaner.filterUntilSection(input);
        assertEquals(input, result);
    }

    @Test
    void filterClosingSectionShouldRemoveClosingSection() {
        String input = "Content starts here.\nDone at some place\nFor the European Parliament\n";
        String result = txtCleaner.filterClosingSection(input);
        assertEquals("Content starts here.", result);
    }

    @Test
    void filterClosingSectionShouldReturnUnchangedIfNoClosingSection() {
        String input = "Content starts here.";
        String result = txtCleaner.filterClosingSection(input);
        assertEquals(input, result);
    }

    @Test
    void preprocessTextShouldRemoveUnwantedCharacters() {
        String input = "Content with `special` ▌characters.";
        String result = txtCleaner.preprocessText(input);
        assertEquals("Content with special characters.", result);
    }

    @Test
    void saveTextToFileShouldSaveTextToFile() throws IOException {
        String input = "Some cleaned content.";
        txtCleaner.saveTextToFile(input);

        File outputFile = new File("src/main/resources/output/cleanedSelectedLaw.txt");
        assertTrue(outputFile.exists(), "Output file should exist.");

        try (FileReader reader = new FileReader(outputFile)) {
            char[] buffer = new char[(int) outputFile.length()];
            reader.read(buffer);
            String fileContent = new String(buffer);
            assertEquals(input, fileContent);
        }
    }
}
