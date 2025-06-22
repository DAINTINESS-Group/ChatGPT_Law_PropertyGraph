package org.uoi.legislativetextparser.textprocessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans the text by applying filtering methods and saving the cleaned text to a file.
 */
public class TxtCleaner {

    private static final String START_MARKER = "CHAPTER I";
    private static final String END_MARKER = "ANNEX I";

    /**
     * Cleans the text by applying all filtering methods sequentially.
     * Calls the saveTextToFile method to save the cleaned text to a file.
     *
     * @param text the input text to be cleaned
     * @throws IllegalArgumentException if the text is null, empty, or missing required markers
     */
    public void cleanText(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }
        text = filterFromSection(text);
        text = filterUntilSection(text);
        text = filterClosingSection(text);
        text = preprocessText(text);
        saveTextToFile(text);
    }

    /**
     * Filters the text from the start marker to the end of the text.
     *
     * @param text the input text to be filtered
     * @return the filtered text
     * @throws IllegalArgumentException if the start marker is not found in the text
     */
    public String filterFromSection(String text) {
        int startIndex = text.indexOf(START_MARKER);
        if (startIndex == -1) {
            throw new IllegalArgumentException("It seems like this document is not a legislative file. " + "\n" + "Please check your input!");
        }
        return text.substring(startIndex).trim();
    }

    /**
     * Filters the text from the beginning to the end marker.
     *
     * @param text the input text to be filtered
     * @return the filtered text
     * @throws IllegalArgumentException if the end marker is not found in the text
     */
    public String filterUntilSection(String text) {
        int endIndex = text.indexOf(END_MARKER);
        if (endIndex == -1) {
            return text;
        }
        return text.substring(0, endIndex).trim();
    }

    /**
     * Filters the text from the beginning to the closing section.
     *
     * @param text the input text to be filtered
     * @return the filtered text
     */
    public String filterClosingSection(String text) {
        Pattern closingPattern = Pattern.compile("(?s)Done at.*?(For the European Parliament|For the Council).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = closingPattern.matcher(text);

        if (matcher.find()) {
            return text.substring(0, matcher.start()).trim();
        }
        return text;
    }

    /**
     * Preprocesses the text by removing unwanted characters and extra spaces.
     *
     * @param rawText the input text to be preprocessed
     * @return the preprocessed text
     */
    public String preprocessText(String rawText) {
        return rawText.replace("`", "").replace("▌", "");
    }

    /**
     * Saves the cleaned text to a file at ../resources/output/cleanedSelectedLaw.txt.
     *
     * @param text the cleaned text to be saved
     */
    public void saveTextToFile(String text) {
        try {
            File cleanedTextFile = new File("src/main/resources/output/cleanedSelectedLaw.txt");
            try (FileWriter writer = new FileWriter(cleanedTextFile)) {
                writer.write(text);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while saving text to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
