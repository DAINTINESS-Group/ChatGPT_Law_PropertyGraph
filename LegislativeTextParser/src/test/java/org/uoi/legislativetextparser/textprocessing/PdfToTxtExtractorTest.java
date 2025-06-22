package org.uoi.legislativetextparser.textprocessing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class PdfToTxtExtractorTest {

    private static final String OUTPUT_PATH = "src/test/resources/output/selectedLaw.txt";
    private PdfToTxtExtractor pdfToTxtExtractor;

    @BeforeEach
    void setup() {
        pdfToTxtExtractor = new PdfToTxtExtractor(OUTPUT_PATH);
    }

    @Test
    public void testExtractTextFromPDFHappyDayScenarioValidPath() throws IOException {
        String pdfPath = "src/test/resources/input/AI_Law.pdf";
        File pdfFile = new File(pdfPath);
        PdfToTxtExtractor pdfToTxtExtractor = new PdfToTxtExtractor(OUTPUT_PATH);
        pdfToTxtExtractor.extractTextFromPDF(pdfFile);
        String text = Files.readString(new File(OUTPUT_PATH).toPath());

        assertNotNull(text);
        assertFalse(text.isEmpty());
        assertTrue(text.startsWith("European Parliament"));
        assertTrue(text.contains("Definitions"));
        assertTrue(text.contains("This Regulation does not apply to AI systems where and in so far they are placed on the"));
    }


    @Test
    void testExtractTextFromPDFInvalidFile() {
        File invalidFile = new File("non_existent_file.pdf");

        assertThrows(IOException.class, () -> pdfToTxtExtractor.extractTextFromPDF(invalidFile));
    }

    @Test
    void testSaveToFile() throws IOException {
        String content = "This is a test content for saving";
        File outputFile = new File(OUTPUT_PATH);

        pdfToTxtExtractor.saveToFile(content);

        assertTrue(outputFile.exists(), "Output file should be created");
        assertEquals(content, Files.readString(outputFile.toPath()), "Output file should contain the correct content");
    }
}