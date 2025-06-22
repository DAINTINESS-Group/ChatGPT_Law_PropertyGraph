package org.uoi.legislativetextparser.textprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Splits the text into chapters based on the "CHAPTER" marker and saves each chapter to a separate file.
 */
public class ChapterSplitter {

    private final String saveToDir ;

    public ChapterSplitter(String saveToDir) {
        this.saveToDir = saveToDir;
    }

    /**
     * Splits the text into chapters based on the "CHAPTER" marker.
     * Calls the saveToFile method to save the chapters to a file.
     *
     * @param text the cleaned input text, ready to be split into chapters
     */
    public void splitIntoChapters(String text) throws IOException {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }

        String[] parts = text.split("(?=\\bCHAPTER\\s+[IVXLCDM]+)");
        ArrayList<String> chapters = new ArrayList<>();

        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                chapters.add(trimmedPart);
            }
        }
        saveToFile(chapters);
    }

    /**
     * Saves each chapter to a separate file in the ../resources/output/chapters/ directory.
     *
     * @param chapters the list of chapters to save
     */
    public void saveToFile(List<String> chapters) throws IOException {

        for (int i = 0; i < chapters.size(); i++) {
            File chapterFile = new File(saveToDir, "chapter_" + (i + 1) + ".txt");
            Files.writeString(chapterFile.toPath(), chapters.get(i));
        }
    }
}
