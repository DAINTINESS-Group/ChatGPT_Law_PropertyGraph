package org.uoi.lawchecklistgenerator.graphML;

import org.junit.jupiter.api.Test;
import org.uoi.lawchecklistgenerator.model.Edge;
import org.uoi.lawchecklistgenerator.model.Graph;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GraphMLParserTest {
    private final GraphMLParser parser = new GraphMLParser();

    @Test
    void testParseValidGraphML() throws Exception {
        String validGraphMLPath = Paths.get(
                GraphMLParserTest.class.getClassLoader().getResource("mock-data/graphml.graphml").toURI()
        ).toString();

        File graphMLFile = new File(validGraphMLPath);

        Graph graph = parser.parse(graphMLFile);

        assertNotNull(graph);
        assertEquals(2, graph.getNodes().size(), "Expected exactly 2 nodes in the graph");
        assertEquals(1, graph.getOutgoing().size(), "Expected exactly 1 outgoing edge");
        assertEquals(1, graph.getIncoming().size(), "Expected exactly 1 incoming edge");

        assertNotNull(graph.getNode("EntityA"), "Expected node EntityA to exist");
        assertNotNull(graph.getNode("EntityB"), "Expected node EntityB to exist");
    }

    @Test
    void testParseGraphMLWithMissingParagraphId() throws Exception {
        String graphMLPath = Paths.get(
                GraphMLParserTest.class.getClassLoader().getResource("mock-data/missing-paragraph.graphml").toURI()
        ).toString();

        File graphMLFile = new File(graphMLPath);

        Graph graph = parser.parse(graphMLFile);

        assertNotNull(graph);
        assertEquals(2, graph.getNodes().size());
        assertEquals(1, graph.getOutgoing().size());

        Edge edge = graph.getOutgoing().iterator().next();
        assertNull(edge.getParagraphId(), "Expected paragraphId to be null when missing in GraphML");
    }
}
