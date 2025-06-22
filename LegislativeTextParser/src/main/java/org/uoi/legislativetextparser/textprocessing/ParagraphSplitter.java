package org.uoi.legislativetextparser.textprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for splitting articles into paragraphs.
 */
public class ParagraphSplitter {

    /**
     * Splits the articles into paragraphs based on numbered sections (e.g., 1., 2., etc.).
     *
     * @param article the article to be split into paragraphs
     * @return a list of paragraphs extracted from the article
     */
    public static List<String> splitIntoParagraphs(String article) {

        List<String> paragraphs = new ArrayList<>();

        Pattern pattern = Pattern.compile("(?<=\\n)\\(?\\d+[a-z]?+\\)?\\.?\\s");
        Matcher matcher = pattern.matcher(article);

        int start = 0;
        while (matcher.find()) {
            int end = matcher.start();

            String paragraph = article.substring(start, end).trim();
            if (!paragraph.isEmpty()) {
                paragraphs.add(paragraph);
            }
            start = end;
        }

        String remainingParagraph = article.substring(start).trim();
        if (!remainingParagraph.isEmpty()) {
            paragraphs.add(remainingParagraph);
        }

        return paragraphs;
    }

}
