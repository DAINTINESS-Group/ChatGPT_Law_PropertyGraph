package org.uoi.legislativetextparser.entityextraction;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uoi.legislativetextparser.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts entities from the "Definitions" article of a specific location in the JSON file.
 */
public class SpecificLocationEntityExtractor extends AbstractEntityExtractor {

    private final int chapterNumber;
    private final int articleNumber;

    public SpecificLocationEntityExtractor(int chapterNumber, int articleNumber) {
        this.chapterNumber = chapterNumber;
        this.articleNumber = articleNumber;
    }

    @Override
    public List<Entity> extractEntities(String jsonFilePath) throws Exception {
        JSONObject root = parseJsonFile(jsonFilePath);

        JSONArray chapters = getChapters(root);

        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            if (chapter.getInt("chapterNumber") == chapterNumber) {
                JSONArray articles = getArticles(chapter);

                for (int j = 0; j < articles.length(); j++) {
                    JSONObject article = articles.getJSONObject(j);
                    if (article.getInt("articleNumber") == articleNumber) {
                        JSONArray paragraphs = getParagraphs(article);
                        List<Entity> entities = new ArrayList<>();
                        for (int k = 0; k < paragraphs.length(); k++) {
                            String paragraphText = paragraphs.getJSONObject(k).getJSONArray("text").getJSONObject(0).getString("text");
                            Entity entity = extractEntityFromParagraph(paragraphText);
                            if (entity != null) {
                                entities.add(entity);
                            }
                        }
                        return entities;
                    }
                }
                throw new IllegalArgumentException("Article " + articleNumber + " not found in chapter " + chapterNumber + ".");
            }
        }
        throw new IllegalArgumentException("Chapter " + chapterNumber + " not found.");
    }

}
