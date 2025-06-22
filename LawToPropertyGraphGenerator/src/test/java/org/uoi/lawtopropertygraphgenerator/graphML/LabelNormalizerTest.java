package org.uoi.lawtopropertygraphgenerator.graphML;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LabelNormalizerTest {

    private static LabelNormalizer normalizer;

    @BeforeAll
    static void setup() {
        normalizer = new LabelNormalizer();
    }

    @Test
    void testNormalizeCommonFormats() {
        assertEquals("public authority", normalizer.normalize("PublicAuthority"));
        assertEquals("agency", normalizer.normalize("AGENCY"));
        assertEquals("deployer", normalizer.normalize("the_deployer"));
    }

    @Test
    void testNormalizeLegalReferences() {
        assertEquals("article 5", normalizer.normalize("Article 5"));
        assertEquals("article5 ( a )", normalizer.normalize("Article5(a)"));
    }

    @Test
    void testGenerateNormalizationMapGroupsSimilarTerms() {
        Set<String> inputs = Set.of(
                "Public Authority", "public_authority", "PUBLICAUTHORITY", "Pub Authority",
                "Deployer", "the deployer", "deploy"
        );

        Map<String, String> map = normalizer.generateNormalizationMap(inputs);

        assertEquals(map.get("Public Authority"), map.get("public_authority"));
        assertEquals(map.get("Deployer"), map.get("the deployer"));
        assertEquals(map.get("PUBLICAUTHORITY"), map.get("Public Authority"));
    }

    @Test
    void testDistinctLegalReferencesNotGrouped() {
        Set<String> labels = Set.of("Article 5", "Article 5(a)");
        Map<String, String> map = normalizer.generateNormalizationMap(labels);

        assertNotEquals(map.get("Article 5"), map.get("Article 5(a)"));
    }
}

