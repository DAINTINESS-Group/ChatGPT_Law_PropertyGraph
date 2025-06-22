package org.uoi.lawtopropertygraphgenerator.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnvConfigTest {

    @Test
    void testGetExistingProperty() {
        String value = EnvConfig.get("GPT_API_KEY");
        assertNotNull(value, "Expected property to be found");
    }

    @Test
    void testGetNonExistingProperty() {
        String value = EnvConfig.get("non.existing.key");
        assertNull(value, "Expected null for non-existing key");
    }
}
