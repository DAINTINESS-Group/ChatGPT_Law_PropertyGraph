package org.uoi.lawtopropertygraphgenerator.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GPTResponseProcessor {

    private static final Logger log = LoggerFactory.getLogger(GPTResponseProcessor.class);
    private final String gptResponseFilePath;

    public GPTResponseProcessor() {
        this.gptResponseFilePath = "src/main/resources/output/graphML/";
    }

    public GPTResponseProcessor(String filePath) {
        this.gptResponseFilePath = filePath;
    }

    public void writeGraphMLToFile(String gptGraphMLResponse) throws IOException {
        if (gptGraphMLResponse == null || gptGraphMLResponse.trim().equalsIgnoreCase("[]")) {
            log.warn("GPT response is null or empty.");
            log.info("GPT response: {}", gptGraphMLResponse);
            return;
        }

        String trimmed = gptGraphMLResponse
                .replace("```xml", "")
                .replace("```", "")
                .replace("[", "")
                .replace("]", "")
                .trim();

        if (trimmed.isEmpty()) {
            log.warn("GraphML content is empty after initial trimming.");
            return;
        }

        // Extract only <graphML> ... </graphML>
        String graphmlClean = extractCustomGraphML(trimmed);

        if (graphmlClean.isBlank()) {
            log.warn("GraphML content is blank after cleanup.");
            return;
        }

        int index = 0;
        File file;
        do {
            String fileName = index == 0 ? "response.graphML" : "response_" + index + ".graphML";
            file = new File(this.gptResponseFilePath + fileName);
            index++;
        } while (file.exists());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(graphmlClean);
        }

        log.info("Cleaned GraphML response written to file: {}", file.getName());
    }

    private static String extractCustomGraphML(String trimmed) {
        if (trimmed == null || trimmed.isBlank()) return "";

        trimmed = trimmed
                .replace("```xml", "")
                .replace("```", "")
                .replace("[", "")
                .replace("]", "")
                .trim();

        String extracted;

        int start = trimmed.indexOf("<graphML");
        int end = trimmed.lastIndexOf("</graphML>");
        if (start != -1 && end != -1 && start < end) {
            extracted = trimmed.substring(start, end + "</graphML>".length());
        } else {
            start = trimmed.indexOf("<graph");
            end = trimmed.lastIndexOf("</graph>");
            if (start != -1 && end != -1 && start < end) {
                String graphOnly = trimmed.substring(start, end + "</graph>".length());
                extracted = "<graphML>\n" + graphOnly + "\n</graphML>";
            } else {
                return "";
            }
        }

        extracted = extracted.replaceAll("<\\?xml.*?\\?>", "");
        extracted = extracted.replaceAll("xmlns(:\\w+)?=\"[^\"]*\"", "");
        extracted = extracted.replaceAll("xsi(:\\w+)?=\"[^\"]*\"", "");
        extracted = extracted.replaceAll("xsi:schemaLocation=\"[^\"]*\"", "");
        extracted = extracted.replaceAll(" +>", ">");

        return extracted.trim();
    }
}