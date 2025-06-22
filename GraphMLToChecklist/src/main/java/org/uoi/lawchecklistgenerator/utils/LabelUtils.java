package org.uoi.lawchecklistgenerator.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LabelUtils {
    private static Set<String> acronyms;

    public static Set<String> getAcronyms() {
        if (acronyms == null) {
            try (Stream<String> lines = Files.lines(Paths.get("src/main/resources/config/acronyms.txt"))) {
                acronyms = lines.map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to load acronyms list", e);
            }
        }
        return acronyms;
    }

    public static String formatNodeLabel(String input) {
        if (input == null) return "";

        Set<String> acronyms = getAcronyms();

        String label = input.replace("_", " ")
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .replaceAll("([A-Z])([A-Z][a-z])", "$1 $2");

        StringBuilder formatted = new StringBuilder();
        for (String word : label.split(" ")) {
            if (word.isBlank()) continue;

            String upper = word.toUpperCase();
            if (acronyms.contains(upper)) {
                formatted.append(upper).append(" ");
            } else {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return formatted.toString().trim();
    }
}
