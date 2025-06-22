package org.uoi.lawtopropertygraphgenerator.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.uoi.lawtopropertygraphgenerator.model.law.Law;

import java.io.File;

public class LawReader {

    /**
     * Reads the JSON file and returns a populated Law object.
     *
     * @param filePath the path to the JSON file
     * @return the Law object
     */
    public Law readLawObjectFromJson(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(filePath), Law.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read law JSON file", e);
        }
    }
}