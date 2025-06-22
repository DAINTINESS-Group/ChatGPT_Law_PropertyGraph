package org.uoi.lawtopropertygraphgenerator.model.graphML;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testNodeGetters() {
        Node node = new Node("id1", "Label A");

        assertEquals("id1", node.getId());
        assertEquals("Label A", node.getLabel());
    }

    @Test
    void testNodeSetters() {
        Node node = new Node("id1", "Label A");

        node.setId("id2");
        node.setLabel("Label B");

        assertEquals("id2", node.getId());
        assertEquals("Label B", node.getLabel());
    }

    @Test
    void testNodeToString() {
        Node node = new Node("id1", "Label A");
        String expected = "Node{id='id1', label='Label A'}";

        assertEquals(expected, node.toString());
    }
}