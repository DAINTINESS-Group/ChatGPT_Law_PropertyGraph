package org.uoi.legislativetextparser.entityextraction;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.uoi.legislativetextparser.model.Entity;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractEntityExtractor implements EntityExtractor{

    /**
     * Abstract method to extract entities, implemented by subclasses.
     *
     * @param jsonFilePath Path to the JSON file.
     * @return List of extracted entities.
     * @throws Exception if extraction fails.
     */
    @Override
    public abstract List<Entity> extractEntities(String jsonFilePath) throws Exception;

    /**
     * Reads and parses the JSON file.
     *
     * @param jsonFilePath Path to the JSON file.
     * @return Parsed JSON object.
     * @throws Exception if file reading or parsing fails.
     */
    protected JSONObject parseJsonFile(String jsonFilePath) throws Exception {
        String jsonContent = Files.readString(Paths.get(jsonFilePath));
        return new JSONObject(jsonContent);
    }

    /**
     * Retrieves all chapters from the JSON object.
     *
     * @param root Root JSON object.
     * @return JSONArray of chapters.
     */
    protected JSONArray getChapters(JSONObject root) {
        return root.getJSONArray("chapters");
    }

    /**
     * Retrieves all articles from a chapter.
     *
     * @param chapter Chapter JSON object.
     * @return JSONArray of articles.
     */
    protected JSONArray getArticles(JSONObject chapter) {
        return chapter.getJSONArray("articles");
    }

    /**
     * Retrieves all paragraphs from an article.
     *
     * @param article Article JSON object.
     * @return JSONArray of paragraphs.
     */
    protected JSONArray getParagraphs(JSONObject article) {
        return article.getJSONArray("paragraphs");
    }


    /**
     * Extracts an entity from a paragraph based on a regex pattern.
     *
     * @param paragraph The paragraph text.
     * @return Extracted entity or null if no match is found.
     */
    protected Entity extractEntityFromParagraph(String paragraph) {
        String name = extractNameFromParagraph(paragraph);
        String definition = extractDefinitionFromParagraph(paragraph);
        if (name != null && definition != null) {
            String fullDefinition = name + " means " + definition;
            return new Entity.Builder(name, fullDefinition).build();
        }
        return null;
    }

    /**
     * Extracts the name from an entity in a Paragraph.
     *
     * @param paragraph The paragraph text.
     * @return Extracted name or null if no match is found.
     */
    private String extractNameFromParagraph(String paragraph) {
        String entityPattern = "\\(?\\d+[a-z]?\\)?\\.?\\s+‘([^’]+)’";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(entityPattern);
        java.util.regex.Matcher matcher = pattern.matcher(paragraph);

        if (matcher.find()) {
            return StringUtils.capitalize(matcher.group(1));
        }
        return null;
    }

    /**
     * Extracts the definition from an entity in a Paragraph.
     *
     * @param paragraph The paragraph text.
     * @return Extracted definition or null if no match is found.
     */
    private String extractDefinitionFromParagraph(String paragraph) {
        String definitionPattern = "means\\s+([^;]+);";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(definitionPattern);
        java.util.regex.Matcher matcher = pattern.matcher(paragraph);

        if (matcher.find()) {
            return matcher.group(1).replace("\n", " ");
        }
        return null;
    }


}
