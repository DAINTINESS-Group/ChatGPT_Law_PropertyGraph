package org.uoi.legislativetextparser.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.legislativetextparser.config.Config;
import org.uoi.legislativetextparser.model.Law;
import org.uoi.legislativetextparser.textprocessing.ChapterSplitter;
import org.uoi.legislativetextparser.textprocessing.PdfToTxtExtractor;
import org.uoi.legislativetextparser.textprocessing.TxtCleaner;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LawProcessor {

    private static final Logger log = LoggerFactory.getLogger(LawProcessor.class);
    private final Config config;
    private final LawConstructor lawConstructor;

    public LawProcessor(Config config) {
        this.config = config;
        this.lawConstructor = new LawConstructor();
    }

    public Law processLegislativeDocument() throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting legislative text processing...");
        clearChaptersDirectory();
    
        if (!extractTextFromPDF()) throw new IOException("Failed to extract text from the PDF document.");
        if (!cleanText()) throw new IOException("Failed to clean the extracted text.");
        if (!splitIntoChapters()) throw new IOException("Failed to split the document into chapters.");
    
        Law law = buildLawObject();
        if (law == null) throw new IOException("Failed to build the law object.");
    
        if (!writeLawToJSON(law)) throw new IOException("Failed to write the law object to JSON.");

        long seconds = (System.currentTimeMillis() - startTime) / 1000;
        long milliseconds = (System.currentTimeMillis() - startTime) % 1000;
        log.info("Total time taken: {}s and {}ms.", seconds, milliseconds);
        return law;
    }

    private boolean extractTextFromPDF() {
        try {
            log.info("Extracting text from the legislative document...");
            PdfToTxtExtractor pdfToTxtExtractor = new PdfToTxtExtractor(Config.getSelectedLaw());
            pdfToTxtExtractor.extractTextFromPDF(new File(config.getLawPdfPath()));
            log.info("Text extraction completed successfully.");
            return true;
        } catch (Exception e) {
            log.error("Error during PDF to text extraction: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean cleanText() {
        File txtFile = new File(Config.getSelectedLaw());
        try {
            log.info("Cleaning text from the legislative document...");
            TxtCleaner txtCleaner = new TxtCleaner();
            txtCleaner.cleanText(Files.readString(txtFile.toPath()));
            log.info("Text cleaning completed successfully.");
            return true;
        } catch (IOException e) {
            log.error("I/O Error during text cleaning: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean splitIntoChapters() {
        File txtFile = new File(Config.getCleanedLaw());
        try {
            log.info("Splitting the document into chapters...");
            ChapterSplitter chapterSplitter = new ChapterSplitter(Config.getChaptersDir());
            chapterSplitter.splitIntoChapters(Files.readString(txtFile.toPath()));
            log.info("Chapters saved successfully.");
            return true;
        } catch (IOException e) {
            log.error("I/O Error during chapter splitting: {}", e.getMessage(), e);
            return false;
        }
    }

    private Law buildLawObject() {
        try {
            log.info("Building the law object...");
            Law law = lawConstructor.constructLawObject();
            log.info("Law object built successfully.");
            return law;
        } catch (NullPointerException | IOException e) {
            log.error("Error during law object construction: {}", e.getMessage(), e);
            return null;
        }
    }

    
    private boolean writeLawToJSON(Law law) {
        try {
            log.info("Writing the law object to JSON file...");
            Files.writeString(new File(config.getLawJsonPath()).toPath(), law.toJsonString());
            log.info("Law object written successfully.");
            return true;
        } catch (IOException e) {
            log.error("I/O Error writing law object to JSON file: {}", e.getMessage(), e);
            return false;
        }
    }

    private void clearChaptersDirectory() {
        Path chaptersDir = Paths.get(Config.getChaptersDir());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(chaptersDir)) {
            for (Path path : stream) {
                File file = path.toFile();
                if (file.isFile()) {
                    if (!file.delete()) {
                        log.error("Failed to delete file: {}", file.getName());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while clearing chapters directory: {}", e.getMessage(), e);
        }
    }

}
