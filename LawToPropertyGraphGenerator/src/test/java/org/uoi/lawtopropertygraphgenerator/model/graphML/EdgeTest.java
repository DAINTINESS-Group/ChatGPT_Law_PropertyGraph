package org.uoi.lawtopropertygraphgenerator.model.graphML;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void testEdgeGetters() {
        Edge edge = new Edge("node1", "node2", "related_to", "para1");

        assertEquals("node1", edge.getSource());
        assertEquals("node2", edge.getTarget());
        assertEquals("related_to", edge.getRelationship());
        assertEquals("para1", edge.getParagraphRef());
    }

    @Test
    void testEdgeSetters() {
        Edge edge = new Edge("a", "b", "type", "ref");

        edge.setSource("newSource");
        edge.setTarget("newTarget");
        edge.setRelationship("newRelation");
        edge.setParagraphRef("newRef");

        assertEquals("newSource", edge.getSource());
        assertEquals("newTarget", edge.getTarget());
        assertEquals("newRelation", edge.getRelationship());
        assertEquals("newRef", edge.getParagraphRef());
    }

    @Test
    void testEdgeToString() {
        Edge edge = new Edge("node1", "node2", "related_to", "para1");
        String expected = "Edge{source='node1', target='node2', label='related_to', paragraphReference='para1'}";

        assertEquals(expected, edge.toString());
    }
}