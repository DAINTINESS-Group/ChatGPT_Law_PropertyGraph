package org.uoi.lawtopropertygraphgenerator.engine;


import org.uoi.lawtopropertygraphgenerator.model.graphML.LowDegreeNode;

import java.io.IOException;
import java.util.List;

public interface LawKnowledgeGraphEngineFacade {

    /**
     * Cleans the output directory by removing all files from the specified path from previous executions.
     * @param filePath the path to the directory to be cleaned.
     * @throws IOException if an I/O error occurs during file operations.
     */
    void cleanOutputDir(String filePath) throws IOException;

    /**
     * Loads and deserializes a Law object from a JSON file.
     * @param jsonFilePath the path to the JSON file containing the Law data.
     */
    void loadLawFromJson(String jsonFilePath);

    /**
     * Initializes GPT-4o-mini context by setting up predefined entities and passing extraction instructions.
     */
    void setupGPTContext();

    /**
     * Iterates over each paragraph of the loaded Law object, extracting relationships via GPT-4o-mini.
     * Responses are received as GraphML scripts.
     */
    void extractRelationshipsToGraphML();

    /**
     * Invokes an external Python script to merge individual GraphML files into one final Knowledge Graph file.
     */
    void mergeGraphmlFilesToOutputFile();


    /**
     * Analyzes the merged GraphML file to identify low-value nodes based on their degree and saves them to a file.
     * @param inputGraphML the path to the merged GraphML file.
     * @param entityListPath the path to the entity list file.
     * @param outputJsonPath the path where the output JSON file will be saved.
     * @param tier4OutputPath the path where the tier 4 nodes will be saved.
     */
    void normalizeGraphMLAndExportToJSON(String inputGraphML, String entityListPath, String outputJsonPath, String tier4OutputPath) throws IOException ;


    /**
     * Detects low-value nodes in the GraphML file based on a degree threshold.
     * @param graphmlPath the path to the GraphML file.
     * @param tier4Path the path to the tier 4 nodes file.
     * @param degreeThreshold the degree threshold for identifying low-value nodes.
     * @return a list of LowDegreeNode objects representing the detected low-value nodes.
     */
    List<LowDegreeNode> getLowValueTier4Nodes(String graphmlPath, String tier4Path, int degreeThreshold);


    /**
     * Removes selected nodes from the GraphML file based on user selection and saves the modified graph to a new file.
     * @param selectedLabels the list of node labels to be removed.
     * @param outputPath the path where the modified GraphML file will be saved.
     * @throws IOException if an I/O error occurs during file operations.
     */
    void removeSelectedNodes(List<String> selectedLabels, String outputPath) throws IOException;


    /**
     * Analyzes node label similarity in the GraphML file using Levenshtein Distance.
     * @param graphMLFilePath the path to the GraphML file.
     * @param similarityThreshold the threshold for considering nodes as similar.
     * @param significantThreshold the threshold for determining significant similarity (chapter, article and paragraph numbers and other important values).
     */
    void analyzeNodeLabelSimilarity(String graphMLFilePath, int similarityThreshold, int significantThreshold);
}
