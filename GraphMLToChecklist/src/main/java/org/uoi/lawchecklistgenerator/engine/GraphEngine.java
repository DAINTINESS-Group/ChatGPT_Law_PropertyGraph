package org.uoi.lawchecklistgenerator.engine;

import org.uoi.lawchecklistgenerator.model.ChecklistEntry;
import org.uoi.lawchecklistgenerator.model.Edge;
import org.uoi.lawchecklistgenerator.model.Node;
import org.uoi.lawchecklistgenerator.model.law.Paragraph;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface GraphEngine {

    /**
     * Loads the graph from a GraphML file and the law from a JSON file.
     * @param graphmlFile
     * @param lawJsonFile
     * @throws Exception if there is an error during loading or parsing the files.
     */
    void loadGraph(File graphmlFile, File lawJsonFile) throws Exception;

    /**
     * Returns all nodes in the graph.
     * @return a collection of all nodes.
     */
    Collection<Node> getAllNodes();

    /**
     * Retrieves a node by its ID.
     * @param nodeId the ID of the node to retrieve.
     * @return the node with the specified ID, or null if not found.
     */
    Node getNodeById(String nodeId);

    /**
     * Retrieves all outgoing edges from a node.
     * @param nodeId the ID of the node.
     * @return a list of outgoing edges from the specified node.
     */
    List<Edge> getOutgoingEdges(String nodeId);

    /**
     * Retrieves all incoming edges to a node.
     * @param nodeId the ID of the node.
     * @return a list of incoming edges to the specified node.
     */
    List<Edge> getIncomingEdges(String nodeId);

    /**
     * Retrieves the checklist entries for a specific node.
     * @param nodeId the ID of the node.
     * @return a list of checklist entries associated with the specified node.
     */
    List<ChecklistEntry> getChecklistForNode(String nodeId);

    /**
     * Finds a paragraph by its ID.
     * @param paragraphId the ID of the paragraph to find.
     * @return the paragraph with the specified ID, or null if not found.
     */
    Paragraph findParagraphById(String paragraphId);

    /**
     * Generates a Markdown report of the checklist.
     * @param out the path where the Markdown report will be saved.
     * @throws Exception if there is an error during report generation.
     */
    void generateMarkdownReport(Path out) throws Exception;
}
