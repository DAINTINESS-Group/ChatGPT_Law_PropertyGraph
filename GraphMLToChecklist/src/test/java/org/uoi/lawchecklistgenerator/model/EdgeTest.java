package org.uoi.lawchecklistgenerator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void testEdgeCreationAndToString() {
        Node source = new Node("src", "Source Node");
        Node target = new Node("tgt", "Target Node");
        Edge edge = new Edge(source, target, "connects", "P2");

        assertEquals(source, edge.getSource());
        assertEquals(target, edge.getTarget());
        assertEquals("connects", edge.getLabel());
        assertEquals("P2", edge.getParagraphId());

        String expected = "Edge{src 'connects' tgt @P2}";
        assertEquals(expected, edge.toString());
    }

    @Test
    void testEdgeWithoutLabel() {
        Node source = new Node("src", "Source Node");
        Node target = new Node("tgt", "Target Node");
        Edge edge = new Edge(source, target, null, "P3");

        assertEquals("", edge.getLabel());
    }
}