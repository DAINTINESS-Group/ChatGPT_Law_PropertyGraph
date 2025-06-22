package org.uoi.lawtopropertygraphgenerator.graphML;

import org.uoi.lawtopropertygraphgenerator.model.graphML.Edge;
import org.uoi.lawtopropertygraphgenerator.model.graphML.GraphModel;
import org.uoi.lawtopropertygraphgenerator.model.graphML.Node;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GraphMLParser {

    public GraphModel parseGraphML(String graphmlPath) {
        GraphModel model = new GraphModel();

        try {
            File file = new File(graphmlPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("node");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element nodeEl = (Element) nodeList.item(i);
                String nodeId = nodeEl.getAttribute("id");
                String label = extractLabel(nodeEl);
                model.nodes.add(new Node(nodeId, label));
            }

            NodeList edgeList = doc.getElementsByTagName("edge");
            for (int i = 0; i < edgeList.getLength(); i++) {
                Element edgeEl = (Element) edgeList.item(i);
                String source = edgeEl.getAttribute("source");
                String target = edgeEl.getAttribute("target");
                String label = edgeEl.getAttribute("label");
                String paragraphId = edgeEl.getAttribute("data");
                if (paragraphId == null || paragraphId.isEmpty()) {
                    paragraphId = extractParagraphId(edgeEl);
                }

                model.edges.add(new Edge(source, target, label, paragraphId));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error parsing GraphML: " + e.getMessage(), e);
        }

        return model;
    }

    private String extractLabel(Element nodeEl) {
        NodeList dataList = nodeEl.getElementsByTagName("data");
        for (int j = 0; j < dataList.getLength(); j++) {
            Element data = (Element) dataList.item(j);
            if (data.getAttribute("key").equalsIgnoreCase("label") || data.getAttribute("key").equalsIgnoreCase("d0")) {
                return data.getTextContent().trim();
            }
        }
        return nodeEl.getAttribute("id");
    }

    private String extractParagraphId(Element edgeEl) {
        NodeList dataList = edgeEl.getElementsByTagName("data");
        for (int j = 0; j < dataList.getLength(); j++) {
            Element data = (Element) dataList.item(j);
            if (data.getAttribute("key").toLowerCase().contains("paragraph")) {
                return data.getTextContent().trim();
            }
        }
        return null;
    }
}
