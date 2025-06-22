package org.uoi.lawtopropertygraphgenerator.model.graphML;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class LowDegreeNode {

    private String id;
    private String label;
    private int inDegree;
    private int outDegree;
    private int tier;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public LowDegreeNode(String id, String label, int inDegree, int outDegree, int tier) {
        this.id = id;
        this.label = label;
        this.inDegree = inDegree;
        this.outDegree = outDegree;
        this.tier = tier;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public int getTier() {
        return tier;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
