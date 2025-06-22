package org.uoi.lawtopropertygraphgenerator.data;

import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.TestUtils;
import org.uoi.lawtopropertygraphgenerator.model.law.Law;
import static org.junit.jupiter.api.Assertions.*;

class LawReaderTest {

    private final LawReader lawReader = new LawReader();

    @Test
    void testReadLawObjectFromValidJson() throws Exception {
        String validJsonPath = TestUtils.readResourceFile("mock-data/law.json");

        Law law = lawReader.readLawObjectFromJson(validJsonPath);

        assertNotNull(law);
        assertNotNull(law.getChapters());
        assertFalse(law.getChapters().isEmpty());
        assertEquals("Law", law.getTitle());
    }

    @Test
    void testReadLawObjectFromInvalidPath() {
        String invalidPath = "non_existent_folder/non_existent_file.json";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lawReader.readLawObjectFromJson(invalidPath);
        });

        assertTrue(exception.getMessage().contains("Failed to read law JSON file"));
    }
}
