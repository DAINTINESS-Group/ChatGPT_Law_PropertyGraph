package org.uoi.lawchecklistgenerator.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.uoi.lawchecklistgenerator.model.law.Law;

import java.io.File;

public class LawReader {
    public Law readLawObjectFromJson(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(filePath), Law.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read law JSON file", e);
        }
    }
}
