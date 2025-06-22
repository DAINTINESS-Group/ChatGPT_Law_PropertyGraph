package org.uoi.legislativetextparser.entityextraction;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uoi.legislativetextparser.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts entities from the "Definitions" articles in the JSON file.
 */
public class ManualEntityExtractor extends AbstractEntityExtractor  {

    @Override
    public List<Entity> extractEntities(String jsonFilePath) throws Exception {
        JSONObject root = parseJsonFile(jsonFilePath);
        JSONArray chapters = getChapters(root);
        List<Entity> entities = new ArrayList<>();

        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            JSONArray articles = getArticles(chapter);

            for (int j = 0; j < articles.length(); j++) {
                JSONObject article = articles.getJSONObject(j);
                JSONArray paragraphs = getParagraphs(article);

                if (containsDefinitions(paragraphs)) {
                    for (int k = 0; k < paragraphs.length(); k++) {
                        String paragraphText = paragraphs.getJSONObject(k).getJSONArray("text").getJSONObject(0).getString("text");
                        Entity entity = extractEntityFromParagraph(paragraphText);
                        if (entity != null) {
                            entities.add(entity);
                        }
                    }
                }
            }
        }
        return entities;
    }


    /**
     * Checks if the paragraphs contain definitions.
     *
     * @param paragraphs JSONArray of paragraphs.
     * @return True if definitions are found, false otherwise.
     */
    private boolean containsDefinitions(JSONArray paragraphs) {
        if (paragraphs.isEmpty()) {
            return false;
        }
        JSONObject firstParagraph = paragraphs.getJSONObject(0);
        JSONArray texts = firstParagraph.getJSONArray("text");

        if (!texts.isEmpty()) {
            String firstParagraphText = texts.getJSONObject(0).getString("text");
            return firstParagraphText.toLowerCase().contains("definitions");
        }
        return false;
    }
}
