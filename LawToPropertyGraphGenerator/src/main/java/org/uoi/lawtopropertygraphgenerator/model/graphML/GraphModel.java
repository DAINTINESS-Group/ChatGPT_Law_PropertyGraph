package org.uoi.lawtopropertygraphgenerator.model.graphML;

import java.util.ArrayList;
import java.util.List;

public class GraphModel {
        public List<Node> nodes = new ArrayList<>();
        public List<Edge> edges = new ArrayList<>();

        public List<Node> getNodes() {
            return nodes;
        }

        public List<Edge> getEdges() {
            return edges;
        }
}
