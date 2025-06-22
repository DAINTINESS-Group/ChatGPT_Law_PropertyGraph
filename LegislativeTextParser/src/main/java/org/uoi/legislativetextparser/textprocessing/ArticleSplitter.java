package org.uoi.legislativetextparser.textprocessing;

import java.util.ArrayList;

/**
 * A utility class that provides a method to split a chapter into articles.
 */
public class ArticleSplitter {

    /**
     * Takes a chapter and splits it into articles.
     *
     * @param chapter the chapter to split into articles
     * @return a list of article chunks extracted from the chapter
     */
    public static ArrayList<String> splitIntoArticles(String chapter) {

        ArrayList<String> articles = new ArrayList<>();


        chapter = chapter.replaceAll("\\u00A0", " ").replaceAll("\\r", "").trim();
        String[] parts = chapter.split("(?m)(?=^Article \\d+[a-z]?$)");

        for (String part : parts) {
            String trimmedPart = part.trim();

            if (trimmedPart.startsWith("Article")) {
                articles.add(trimmedPart);
            } else if (!articles.isEmpty()) {
                int lastIndex = articles.size() - 1;
                articles.set(lastIndex, articles.get(lastIndex) + "\n" + trimmedPart);
            }
        }
        return articles;
    }
}
