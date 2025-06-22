package org.uoi.lawchecklistgenerator.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.uoi.lawchecklistgenerator.utils.LabelUtils;
import org.uoi.lawchecklistgenerator.engine.GraphEngine;
import org.uoi.lawchecklistgenerator.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class NodeList extends VBox {

    private Consumer<Node> onNodeSelected;

    public NodeList(GraphEngine engine) {
        setSpacing(6);
        setPadding(new Insets(12));

        List<Node> nodes = new ArrayList<>(engine.getAllNodes());
        nodes.forEach(n -> {
            Label item = new Label(LabelUtils.formatNodeLabel(n.getLabel()));
            item.getStyleClass().add("node-list-item");
            item.setOnMouseClicked(e -> fireNodeSelected(n));
            getChildren().add(item);
        });
    }

    private void fireNodeSelected(Node node) {
        if (onNodeSelected != null) {
            onNodeSelected.accept(node);
        }
    }

    public void setOnNodeSelected(Consumer<Node> handler) {
        this.onNodeSelected = handler;
    }

    @Override
    protected double computePrefHeight(double width) {
        int rows = getChildren().size();
        double rowH = 24;
        double spacing = getSpacing();
        double paddingV = getPadding().getTop() + getPadding().getBottom();

        return rows * rowH + (rows - 1) * spacing + paddingV;
    }
}