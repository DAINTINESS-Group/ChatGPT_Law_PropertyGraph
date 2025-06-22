package org.uoi.lawchecklistgenerator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChecklistEntryTest {

    @Test
    void testChecklistEntryCreation() {
        Node node = new Node("node1", "Node 1");
        Edge edge = new Edge(node, node, "relates_to", "P1");
        ChecklistEntry entry = new ChecklistEntry(edge, node, "Sample paragraph text");

        assertNotNull(entry.edge());
        assertNotNull(entry.relatedNode());
        assertNotNull(entry.paragraphText());

        assertEquals("relates_to", entry.edge().getLabel());
        assertEquals("node1", entry.relatedNode().getId());
        assertEquals("Sample paragraph text", entry.paragraphText());
    }
}