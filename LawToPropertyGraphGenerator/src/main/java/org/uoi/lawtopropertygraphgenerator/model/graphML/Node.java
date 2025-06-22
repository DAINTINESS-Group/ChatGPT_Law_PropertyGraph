package org.uoi.lawtopropertygraphgenerator.model.graphML;

public class Node {

    private String id;
    private String label;

    public Node(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Node{id='" + id + "', label='" + label + "'}";
    }
}
