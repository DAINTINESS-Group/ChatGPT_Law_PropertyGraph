package org.uoi.lawchecklistgenerator.engine;

import org.uoi.lawchecklistgenerator.graphML.GraphMLParser;
import org.uoi.lawchecklistgenerator.data.LawReader;
import org.uoi.lawchecklistgenerator.model.ChecklistEntry;
import org.uoi.lawchecklistgenerator.model.Edge;
import org.uoi.lawchecklistgenerator.model.Graph;
import org.uoi.lawchecklistgenerator.model.Node;
import org.uoi.lawchecklistgenerator.model.law.Law;
import org.uoi.lawchecklistgenerator.model.law.Paragraph;
import org.uoi.lawchecklistgenerator.report.MarkdownReportGenerator;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GraphEngineImpl implements GraphEngine{

    private final GraphMLParser parser = new GraphMLParser();
    private final LawReader lawReader = new LawReader();
    private final MarkdownReportGenerator markdownReportGenerator = new MarkdownReportGenerator();
    private Graph graph;
    private Law law;


    @Override
    public void loadGraph(File graphmlFile, File lawJsonFile) throws Exception {
        graph = parser.parse(graphmlFile);
        law = lawReader.readLawObjectFromJson(lawJsonFile.getPath());
        law.initIndex();
    }

    @Override
    public Collection<Node> getAllNodes() {
        return graph.getNodes();
    }

    @Override
    public Node getNodeById(String nodeId) {
        return graph.getNode(nodeId);
    }

    @Override
    public List<Edge> getOutgoingEdges(String nodeId) {
        Node node = graph.getNode(nodeId);
        return node != null ? node.getOutgoing() : Collections.emptyList();
    }

    @Override
    public List<Edge> getIncomingEdges(String nodeId) {
        Node node = graph.getNode(nodeId);
        return node != null ? node.getIncoming() : Collections.emptyList();
    }

    @Override
    public List<ChecklistEntry> getChecklistForNode(String nodeId) {
        List<ChecklistEntry> checklist = new ArrayList<>();
        getOutgoingEdges(nodeId).forEach(e -> {
            String pid = e.getParagraphId();
            Paragraph p = law.findParagraphById(pid);
            Node target = graph.getNode(e.getTarget().getId());
            checklist.add(new ChecklistEntry(e, target, p.getText()));
        });
        return checklist;
    }

    @Override
    public Paragraph findParagraphById(String id) {
        return law.findParagraphById(id);
    }

    @Override
    public void generateMarkdownReport(Path out) throws Exception {
        markdownReportGenerator.generateReport(this, out);
    }
}
