package org.uoi.lawchecklistgenerator.graphML;

import org.uoi.lawchecklistgenerator.model.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GraphMLParser {

    private static final String GRAPHML_NS = "http://graphml.graphdrawing.org/xmlns";
    private static final String YFILES_NS  = "http://www.yworks.com/xml/graphml";

    public Graph parse(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();

        String nodeKey = findKey(doc, "node");
        String edgeKey = findKey(doc, "edge");

        Graph graph = new Graph();

        NodeList nodes = doc.getElementsByTagNameNS(GRAPHML_NS, "node");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element nodeEl = (Element) nodes.item(i);
            String id    = nodeEl.getAttribute("id");
            String label = extractLabel(nodeEl, nodeKey, true);
            graph.addNode(id, label);
        }

        NodeList edges = doc.getElementsByTagNameNS(GRAPHML_NS, "edge");
        for (int i = 0; i < edges.getLength(); i++) {
            Element edgeEl = (Element) edges.item(i);
            String src = edgeEl.getAttribute("source");
            String tgt = edgeEl.getAttribute("target");

            String pid = edgeEl.getAttribute("data");
            if (pid == null || pid.isEmpty()) {
                pid = extractParagraphId(edgeEl);
            }

            String label = extractLabel(edgeEl, edgeKey, false);
            graph.addEdge(src, tgt, label, pid);
        }

        return graph;
    }

    private String findKey(Document doc, String forWhat) {
        NodeList keyList = doc.getElementsByTagNameNS(GRAPHML_NS, "key");
        for (int i = 0; i < keyList.getLength(); i++) {
            Element key = (Element) keyList.item(i);
            if (forWhat.equals(key.getAttribute("for")) &&
                    key.getAttribute("attr.name").toLowerCase().contains("label")) {
                return key.getAttribute("id");
            }
        }
        return forWhat.equals("node") ? "n0" : "e0";
    }

    private String extractLabel(Element el, String key, boolean isNode) {
        NodeList dataList = el.getElementsByTagNameNS(GRAPHML_NS, "data");
        for (int i = 0; i < dataList.getLength(); i++) {
            Element data = (Element) dataList.item(i);
            if (key.equals(data.getAttribute("key"))) {
                String tag = isNode ? "NodeLabel" : "EdgeLabel";
                NodeList labels = data.getElementsByTagNameNS(YFILES_NS, tag);
                if (labels.getLength() > 0) {
                    return labels.item(0).getTextContent().trim();
                } else {
                    return data.getTextContent().trim();
                }
            }
        }
        return el.getAttribute("id");
    }

    private String extractParagraphId(Element edgeEl) {
        NodeList dataList = edgeEl.getElementsByTagNameNS(GRAPHML_NS, "data");
        for (int i = 0; i < dataList.getLength(); i++) {
            Element data = (Element) dataList.item(i);
            if (data.getAttribute("key").toLowerCase().contains("paragraph")) {
                return data.getTextContent().trim();
            }
        }
        return null;
    }
}
