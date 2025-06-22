package org.uoi.lawchecklistgenerator.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Graph {

    private final Map<String,Node> nodes = new LinkedHashMap<>();

    public Node addNode(String id, String label) {
        return nodes.computeIfAbsent(id, k -> new Node(k, label));
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public Collection<Edge> getOutgoing() {
        return nodes.values().stream()
                .flatMap(n -> n.getOutgoing().stream())
                .toList();
    }

    public Collection<Edge> getIncoming() {
        return nodes.values().stream()
                .flatMap(n -> n.getIncoming().stream())
                .toList();
    }


    public Edge addEdge(String sourceId, String targetId, String label, String paragraphId) {
        Node src = addNode(sourceId, null);
        Node tgt = addNode(targetId,   null);
        Edge e  = new Edge(src, tgt, label, paragraphId);
        src.addOutgoing(e);
        tgt.addIncoming(e);
        return e;
    }
}