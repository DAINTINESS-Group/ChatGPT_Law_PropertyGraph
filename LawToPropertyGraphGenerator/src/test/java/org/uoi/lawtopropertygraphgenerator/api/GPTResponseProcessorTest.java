package org.uoi.lawtopropertygraphgenerator.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.TestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class GPTResponseProcessorTest {

    private static final String TEST_OUTPUT_DIR = "src/test/resources/tmp_output/";
    private static final String MOCK_GRAPHML_PATH = "mock-data/graphml.graphml";
    private GPTResponseProcessor processor;

    @BeforeEach
    void setUp() throws Exception {
        processor = new GPTResponseProcessor(TEST_OUTPUT_DIR);

        Files.createDirectories(Path.of(TEST_OUTPUT_DIR));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(Path.of(TEST_OUTPUT_DIR))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void testWriteGraphMLToFile_createsFile() throws Exception {
        String mockGraphML = TestUtils.readResourceFile(MOCK_GRAPHML_PATH);

        processor.writeGraphMLToFile(mockGraphML);

        File folder = new File(TEST_OUTPUT_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".graphML"));

        assertNotNull(files);
        assertTrue(files.length > 0, "Expected at least one .graphML file to be created");

        String content = Files.readString(files[0].toPath());
        assertTrue(content.contains("<graphML>"));
        assertTrue(content.contains("EntityA"));
        assertTrue(content.contains("EntityB"));
    }

    @Test
    void testWriteGraphMLToFile_handlesEmptyInput() throws IOException {
        processor.writeGraphMLToFile("[]");

        File folder = new File(TEST_OUTPUT_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".graphML"));

        assertNotNull(files);
        assertEquals(0, files.length, "No files should be created for empty input");
    }
}
