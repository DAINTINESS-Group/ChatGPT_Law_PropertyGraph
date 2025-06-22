package org.uoi.legislativetextparser.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.uoi.legislativetextparser.config.Config;
import org.uoi.legislativetextparser.model.Article;
import org.uoi.legislativetextparser.model.Chapter;
import org.uoi.legislativetextparser.model.Law;
import org.uoi.legislativetextparser.model.Paragraph;
import org.uoi.legislativetextparser.model.Point;
import org.uoi.legislativetextparser.textprocessing.ArticleSplitter;
import org.uoi.legislativetextparser.textprocessing.ParagraphSplitter;
import org.uoi.legislativetextparser.textprocessing.PointSplitter;

public class LawConstructor {

    private final String chaptersDir;

    public LawConstructor() {
        this.chaptersDir = Config.getChaptersDir();
    }

    /**
     * Constructs a Law object from the text files in the chapters directory.
     *
     * @return the Law object
     * @throws IOException if an I/O error occurs
     */
    public Law constructLawObject() throws IOException {
        File chaptersDirectory = new File(chaptersDir);
        if (!chaptersDirectory.exists() || !chaptersDirectory.isDirectory()) {
            throw new IllegalArgumentException("Chapters directory does not exist: " + chaptersDir);
        }

        File[] chapterFiles = chaptersDirectory.listFiles((dir, name) -> name.startsWith("chapter_") && name.endsWith(".txt"));
        Arrays.sort(chapterFiles, (f1, f2) -> {
            int chapterNumber1 = extractChapterNumber(f1.getName());
            int chapterNumber2 = extractChapterNumber(f2.getName());
            return Integer.compare(chapterNumber1, chapterNumber2);
        });

        if (chapterFiles == null || chapterFiles.length == 0) {
            throw new IllegalArgumentException("No chapter files found in directory: " + chaptersDir);
        }

        ArrayList<Chapter> chapters = new ArrayList<>();
        int chapterCounter = 1;

        for (File chapterFile : chapterFiles) {
            String chapterText = Files.readString(chapterFile.toPath());

            List<String> articleTexts = ArticleSplitter.splitIntoArticles(chapterText);
            ArrayList<Article> articles = new ArrayList<>();

            int articleCounter = 1;
            for (String articleText : articleTexts) {
                String articleID = chapterCounter + "." + articleCounter;
                List<String> paragraphTexts = ParagraphSplitter.splitIntoParagraphs(articleText);
                ArrayList<Paragraph> paragraphs = new ArrayList<>();

                int paragraphCounter = 1;
                ArrayList<Point> paragraphPoints;
                for (String paragraphText : paragraphTexts) {
                    paragraphPoints = PointSplitter.splitIntoPoints(paragraphText);
                    String paragraphID = articleID + "." + paragraphCounter;
                    Paragraph paragraph = new Paragraph.Builder(paragraphCounter++, paragraphPoints, paragraphID).build();
                    paragraphs.add(paragraph);
                }

                String articleTitle = extractArticleTitle(articleText);
                Article article = new Article.Builder(articleCounter++, paragraphs, articleID, articleTitle).build();
                articles.add(article);
            }

            String chapterTitle = extractChapterTitle(chapterText);
            Chapter chapter = new Chapter.Builder(chapterCounter++, articles, chapterTitle).build();
            chapters.add(chapter);
        }
        return new Law.Builder(chapters).build();
    }

    /**
     * Extracts the chapter title from the chapter text.
     *
     * @param chapterText the text of the chapter
     * @return the chapter title
     */
    private String extractChapterTitle(String chapterText) {
        Pattern pattern = Pattern.compile("CHAPTER\\s+[IVXLCDM]+(?:\\s+)?(?:\\r?\\n)([A-Z][A-Z\\s\\-,]*)\n");
        Matcher matcher = pattern.matcher(chapterText);
        if (matcher.find()) {
            return StringUtils.capitalize(matcher.group(1).toLowerCase().replace("\n", ""));
        }
        return "Could not extract chapter title";
    }

    /**
     * Extracts the title of an article from its text.
     *
     * @param articleText The String object representing the article's text.
     * @return The title of the article.
     */
    static String extractArticleTitle(String articleText) {
        String[] lines = articleText.split("\\r?\\n");
    
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().matches("Article\\s+\\d+[a-z]?")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String potentialTitle = lines[j].trim();
                    if (!potentialTitle.isEmpty()) {
                        return StringUtils.capitalize(potentialTitle.toLowerCase().replace("\n", ""));
                    }
                }
            }
        }
        return "Could not extract article title";
    }
    
    
    /**
     * Extracts the chapter number from the file name.
     *
     * @param fileName the name of the file
     * @return the chapter number
     */
    private int extractChapterNumber(String fileName) {
        String numberPart = fileName.replace("chapter_", "").replace(".txt", "");
        return Integer.parseInt(numberPart);
    }
}
