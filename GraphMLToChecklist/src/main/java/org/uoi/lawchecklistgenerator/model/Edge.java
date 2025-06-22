package org.uoi.lawchecklistgenerator.model;

import java.util.Objects;

public class Edge {
    
    private final Node source;
    private final Node target;
    private final String label;
    private final String paragraphId;

    public Edge(Node source, Node target, String label, String paragraphId) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.label  = label != null ? label : "";
        this.paragraphId = paragraphId;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public String getLabel()     { return label; }
    public String getParagraphId() { return paragraphId; }

    @Override
    public String toString() {
        return "Edge{" +
                source.getId() + (label.isEmpty() ? "" : " '" + label + "' ") + target.getId() +
                (paragraphId != null ? " @"+paragraphId : "") +
                "}";
    }
}
