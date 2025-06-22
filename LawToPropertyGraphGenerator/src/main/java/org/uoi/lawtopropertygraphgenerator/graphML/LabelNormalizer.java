package org.uoi.lawtopropertygraphgenerator.graphML;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelNormalizer {

    private final StanfordCoreNLP pipeline;

    public LabelNormalizer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public String normalize(String label) {
        if (label == null || label.isBlank()) return null;

        String pre = label
                .replaceAll("(?<=[A-Z])(?=[A-Z][a-z])", " ")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .replaceAll("([A-Z]{1,4})(\\d+)", "$1 $2")
                .toLowerCase()
                .replaceAll("[_\\-]", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("^the\\s+", "")
                .trim();

        CoreDocument doc = new CoreDocument(pre);
        pipeline.annotate(doc);

        List<String> lemmas = new ArrayList<>();
        for (CoreLabel token : doc.tokens()) {
            lemmas.add(token.lemma());
        }

        return String.join(" ", lemmas);
    }

    public Map<String, String> generateNormalizationMap(Set<String> originalLabels) {
        Map<String, String> rawToNorm = new HashMap<>();
        Map<String, Set<String>> normToOriginals = new HashMap<>();

        for (String original : originalLabels) {
            String norm = normalize(original);
            rawToNorm.put(original, norm);
            normToOriginals.computeIfAbsent(norm, k -> new HashSet<>()).add(original);
        }

        Map<String, String> normToCanonical = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : normToOriginals.entrySet()) {
            String norm = entry.getKey();
            Set<String> candidates = entry.getValue();
            String best = candidates.stream()
                    .min(Comparator.comparingInt(s -> s.replace("_", " ").length()))
                    .orElse(norm);
            normToCanonical.put(norm, best);
        }

        Map<String, String> finalMap = new HashMap<>();
        Map<String, String> normToGroup = new HashMap<>();
        LevenshteinDistance ld = new LevenshteinDistance();

        for (Map.Entry<String, String> entry : rawToNorm.entrySet()) {
            String original = entry.getKey();
            String norm = entry.getValue();

            Optional<String> match = normToGroup.keySet().stream()
                    .filter(existing -> {
                        int distance = ld.apply(norm, existing);
                        int maxLength = Math.max(norm.length(), existing.length());
                        double ratio = (double) distance / maxLength;
                        return ratio <= 0.15 && !isDistinctLegalReference(norm, existing);
                    })
                    .findFirst();

            String groupKey = match.orElse(norm);
            normToGroup.putIfAbsent(groupKey, normToCanonical.getOrDefault(groupKey, groupKey));
            finalMap.put(original, normToGroup.get(groupKey));
        }

        return finalMap;
    }


    private boolean isDistinctLegalReference(String a, String b) {
        Pattern pattern = Pattern.compile("^(\\w+)\\s*(\\d+(\\([^)]+\\))?)$", Pattern.CASE_INSENSITIVE);
        Matcher m1 = pattern.matcher(a);
        Matcher m2 = pattern.matcher(b);

        if (m1.matches() && m2.matches()) {
            String prefix1 = m1.group(1);
            String number1 = m1.group(2);
            String prefix2 = m2.group(1);
            String number2 = m2.group(2);
            return prefix1.equalsIgnoreCase(prefix2) && !number1.equalsIgnoreCase(number2);
        }
        return false;
    }
}
