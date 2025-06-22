package org.uoi.lawtopropertygraphgenerator.graphML;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uoi.lawtopropertygraphgenerator.model.graphML.GraphModel;
import org.uoi.lawtopropertygraphgenerator.model.graphML.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphMLParserTest {

    private static GraphModel model;

    @BeforeAll
    public static void setUp() {
        GraphMLParser parser = new GraphMLParser();
        model = parser.parseGraphML("src/test/resources/mock-data/cleanGraphml.graphml");

    }

    @Test
    public void testNodesParsedCorrectly() {
        assertNotNull(model.nodes);
        assertEquals(4, model.nodes.size());

        List<String> nodeIds = model.nodes.stream()
                .map(Node::getId)
                .toList();

        assertTrue(nodeIds.contains("Deployer"));
        assertTrue(nodeIds.contains("Public authority"));
        assertTrue(nodeIds.contains("Agency") || nodeIds.contains("Body"));
    }

    @Test
    public void testEdgesParsedCorrectly() {
        assertNotNull(model.edges);
        assertEquals(3, model.edges.size());

        boolean hasExpectedEdge = model.edges.stream()
                .anyMatch(edge -> edge.getSource().equals("Deployer") &&
                        edge.getTarget().equals("Agency") || edge.getTarget().equals("Body"));
        assertTrue(hasExpectedEdge);
    }

    @Test
    public void testMalformedGraphMLThrowsException() throws IOException {

        GraphMLParser parser = new GraphMLParser();
        assertThrows(RuntimeException.class, () -> parser.parseGraphML("src/test/resources/mock-data/wrongGraphML.graphml"),
                "Parsing malformed GraphML should throw an exception");
    }
}
