package org.uoi.lawchecklistgenerator.data;

import org.junit.jupiter.api.Test;
import org.uoi.lawchecklistgenerator.model.law.Law;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class LawReaderTest {
    private final LawReader lawReader = new LawReader();

    @Test
    void testReadLawObjectFromValidJson() throws Exception {
        String validJsonPath = Paths.get(
                LawReaderTest.class.getClassLoader().getResource("mock-data/law.json").toURI()
        ).toString();

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