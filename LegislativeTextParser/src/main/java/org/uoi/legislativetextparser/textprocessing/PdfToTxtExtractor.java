package org.uoi.legislativetextparser.textprocessing;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Extracts text from a PDF file and saves it to a text file.
 */
public class PdfToTxtExtractor {

    private final String saveFilePath;

    public PdfToTxtExtractor(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }
    /**
     * Extracts text from a PDF file.
     *
     * @param pdfFile the PDF file to process
     * @calls saveToFile method to save the extracted text to a file
     * @throws IOException if there is an error reading or processing the PDF file
     */
    public void extractTextFromPDF(File pdfFile) throws IOException {
        PDDocument document = Loader.loadPDF(pdfFile);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        saveToFile(pdfStripper.getText(document));
        document.close();
    }

    /**
     * Saves the extracted text to a file at ../resources/output/selectedLaw.txt.
     *
     * @param text the extracted text from the PDF
     */
    public void saveToFile(String text) throws IOException {
        File txtOutputFile = new File(saveFilePath);
        Files.writeString(txtOutputFile.toPath(), text);
    }
}
