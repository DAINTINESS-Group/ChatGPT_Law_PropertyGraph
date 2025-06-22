package org.uoi.lawchecklistgenerator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node {
    private final String id;
    private String label;
    private final List<Edge> outgoing = new ArrayList<>();
    private final List<Edge> incoming = new ArrayList<>();

    public Node(String id, String label) {
        this.id    = Objects.requireNonNull(id);
        this.label = label != null ? label : id;
    }

    public String getId()       { return id; }
    public String getLabel()    { return label; }
    public void setLabel(String label) { this.label = label; }

    public List<Edge> getOutgoing() { return Collections.unmodifiableList(outgoing); }
    public List<Edge> getIncoming() { return Collections.unmodifiableList(incoming); }

    void addOutgoing(Edge e) { outgoing.add(e); }

    void addIncoming(Edge e) { incoming.add(e); }

    @Override
    public String toString() {
        return "Node{" + label + "}";
    }
}
