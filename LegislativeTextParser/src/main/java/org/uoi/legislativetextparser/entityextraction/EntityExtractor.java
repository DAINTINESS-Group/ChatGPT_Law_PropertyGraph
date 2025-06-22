package org.uoi.legislativetextparser.entityextraction;

import org.uoi.legislativetextparser.model.Entity;

import java.util.List;

public interface EntityExtractor {


    /**
    * @param jsonFilePath Path to the JSON file.
    * @return List of extracted entities.
    * @throws Exception if JSON parsing fails or a problem occurs at entity extraction.
    */
    List<Entity> extractEntities(String jsonFilePath) throws Exception;

}
