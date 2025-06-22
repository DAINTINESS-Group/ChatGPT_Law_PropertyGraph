package org.uoi.lawchecklistgenerator.model.law;

import java.util.List;

public interface Node {

    String getText();
    String getTitle();
    List<? extends Node> getChildren();
}
