package org.uoi.lawchecklistgenerator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void testAddAndGetNode() {
        Graph graph = new Graph();
        Node node = graph.addNode("n1", "First Node");

        assertNotNull(node);
        assertEquals("n1", node.getId());
        assertEquals("First Node", node.getLabel());

        Node fetchedNode = graph.getNode("n1");
        assertSame(node, fetchedNode);
    }

    @Test
    void testAddEdge() {
        Graph graph = new Graph();

        Edge edge = graph.addEdge("n1", "n2", "related_to", "P5");

        assertNotNull(edge);
        assertEquals("n1", edge.getSource().getId());
        assertEquals("n2", edge.getTarget().getId());
        assertEquals("related_to", edge.getLabel());

        assertEquals(1, graph.getOutgoing().size());
        assertEquals(1, graph.getIncoming().size());
    }

    @Test
    void testGetNodes() {
        Graph graph = new Graph();
        graph.addNode("n1", "Node 1");
        graph.addNode("n2", "Node 2");

        assertEquals(2, graph.getNodes().size());
    }
}