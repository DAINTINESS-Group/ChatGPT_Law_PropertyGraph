package org.uoi.lawchecklistgenerator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testNodeCreationAndGetters() {
        Node node = new Node("n1", "Node Label");

        assertEquals("n1", node.getId());
        assertEquals("Node Label", node.getLabel());
        assertTrue(node.getOutgoing().isEmpty());
        assertTrue(node.getIncoming().isEmpty());
    }

    @Test
    void testAddIncomingAndOutgoing() {
        Node node1 = new Node("n1", "Node 1");
        Node node2 = new Node("n2", "Node 2");

        Edge edge = new Edge(node1, node2, "connects", "P1");

        node1.addOutgoing(edge);
        node2.addIncoming(edge);

        assertEquals(1, node1.getOutgoing().size());
        assertEquals(1, node2.getIncoming().size());
    }

    @Test
    void testToString() {
        Node node = new Node("n1", "Test Node");
        assertEquals("Node{Test Node}", node.toString());
    }
}