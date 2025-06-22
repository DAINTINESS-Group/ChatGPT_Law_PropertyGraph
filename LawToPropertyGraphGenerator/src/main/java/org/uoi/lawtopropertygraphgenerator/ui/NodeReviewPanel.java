package org.uoi.lawtopropertygraphgenerator.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.lawtopropertygraphgenerator.engine.LawKnowledgeGraphEngineFacadeImpl;
import org.uoi.lawtopropertygraphgenerator.model.graphML.LowDegreeNode;

import java.io.IOException;
import java.util.List;

public class NodeReviewPanel extends Application {
    private static final Logger log = LoggerFactory.getLogger(NodeReviewPanel.class);
    private static final String NODES_TO_REMOVE_PATH = "src/main/resources/output/nodesToRemove.txt";
    private static final String ENTITY_LIST_PATH = "src/main/resources/output/entities.txt";
    private static final String NORMALIZATION_MAP_PATH = "src/main/resources/output/normalizationMap.json";
    private static final String MERGED_GRAPHML_FILE = "src/main/resources/output/merged.graphML";
    private static final String TIER4_OUTPUT_PATH = "src/main/resources/output/tier4.txt";
    private static final String NORMALIZED_GRAPH_PATH = "src/main/resources/output/final.graphML";
    private final long start;

    private final List<LowDegreeNode> lowValueNodes;
    private final LawKnowledgeGraphEngineFacadeImpl engine;
    public NodeReviewPanel(List<LowDegreeNode> lowValueNodes, LawKnowledgeGraphEngineFacadeImpl engine, long start) {
        this.lowValueNodes = lowValueNodes;
        this.engine = engine;
        this.start = start;

    }

    @Override
    public void start(Stage stage) {
        TableView<LowDegreeNode> tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<LowDegreeNode, String> labelCol = new TableColumn<>("Label");
        labelCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLabel()));

        TableColumn<LowDegreeNode, Integer> inCol = new TableColumn<>("In Degree");
        inCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getInDegree()).asObject());
        inCol.setPrefWidth(100);

        TableColumn<LowDegreeNode, Integer> outCol = new TableColumn<>("Out Degree");
        outCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getOutDegree()).asObject());
        outCol.setPrefWidth(100);

        TableColumn<LowDegreeNode, Boolean> selectedCol = new TableColumn<>("Remove Node");
        selectedCol.setCellValueFactory(data -> data.getValue().selectedProperty());
        selectedCol.setCellFactory(col -> {
            CheckBoxTableCell<LowDegreeNode, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(index -> {
                if (index >= 0 && index < tableView.getItems().size()) {
                    return tableView.getItems().get(index).selectedProperty();
                }
                return null;
            });
            return cell;
        });
        selectedCol.setEditable(true);

        tableView.getColumns().addAll(labelCol, inCol, outCol, selectedCol);
        tableView.getItems().addAll(lowValueNodes);

        Button proceedButton = new Button("Delete Node/s");
        proceedButton.setOnAction(e -> {
            List<String> selectedLabels = tableView.getItems().stream()
                    .filter(LowDegreeNode::isSelected)
                    .map(LowDegreeNode::getLabel)
                    .toList();

            try {
                if (!selectedLabels.isEmpty()) {
                    engine.removeSelectedNodes(selectedLabels, NODES_TO_REMOVE_PATH);
                }
                engine.normalizeGraphMLAndExportToJSON(MERGED_GRAPHML_FILE, ENTITY_LIST_PATH, NORMALIZATION_MAP_PATH, TIER4_OUTPUT_PATH);
                engine.analyzeNodeLabelSimilarity(NORMALIZED_GRAPH_PATH, 85, 95);
                long end = System.currentTimeMillis();
                long seconds = (end - start) / 1000;
                long minutes = seconds / 60;
                log.info("Finished in {} minutes and {} seconds", minutes, seconds % 60);
                Alert completionAlert = new Alert(Alert.AlertType.INFORMATION);
                completionAlert.setTitle("Process Completed");
                completionAlert.setHeaderText("Node removal and normalization completed successfully.\nFile saved to: " + NORMALIZED_GRAPH_PATH);
                completionAlert.showAndWait();
                stage.close();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            stage.close();
        });

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(12);
        buttonBox.setPadding(new Insets(16, 24, 16, 24));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().addAll(proceedButton, spacer, exitButton);

        BorderPane root = new BorderPane();
        root.setCenter(tableView);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 700, 600);
        scene.getStylesheets().add(getClass().getResource("/styling/nodeReviewPanel.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Review Low-Tier Nodes");
        stage.show();
    }
}
