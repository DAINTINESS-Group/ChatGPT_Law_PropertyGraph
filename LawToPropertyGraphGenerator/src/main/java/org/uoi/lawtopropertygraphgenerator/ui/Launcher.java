package org.uoi.lawtopropertygraphgenerator.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.lawtopropertygraphgenerator.engine.LawKnowledgeGraphEngineFacadeImpl;
import org.uoi.lawtopropertygraphgenerator.model.graphML.LowDegreeNode;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


public class Launcher extends Application {

    private static final Logger log = LoggerFactory.getLogger(Launcher.class);
    private static final String GRAPHML_DIR = "src/main/resources/output/graphML";
    private static final String MERGED_GRAPHML_FILE = "src/main/resources/output/merged.graphML";
    private static final String TIER4_OUTPUT_PATH = "src/main/resources/output/tier4.txt";
    private static final String NORMALIZED_GRAPH_PATH = "src/main/resources/output/final.graphML";
    private final LawKnowledgeGraphEngineFacadeImpl engine = new LawKnowledgeGraphEngineFacadeImpl();
    private final long start = System.currentTimeMillis();


    @Override
    public void start(Stage primaryStage) {
        Label status = new Label("Select the JSON file created from LegislativeTextParser and generate graph");
        Button browseBtn = new Button("Browse");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(event -> {
            primaryStage.close();
        });


        browseBtn.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Input JSON");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files", "*.json")
            );
            File jsonFile = chooser.showOpenDialog(primaryStage);

            if (jsonFile == null || !jsonFile.exists() || !jsonFile.getName().endsWith(".json")) {
                Alert noFileAlert = new Alert(Alert.AlertType.ERROR);
                noFileAlert.setTitle("No File Selected");
                noFileAlert.setHeaderText("No JSON file selected.");
                noFileAlert.setContentText("Please select a valid JSON file.");
                noFileAlert.showAndWait();
                Platform.exit();
                return;
            }
            String jsonPath = jsonFile.getAbsolutePath();

            browseBtn.setDisable(true);
            progressIndicator.setVisible(true);
            status.setText("Generating...");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    engine.cleanOutputDir(GRAPHML_DIR);
                    engine.loadLawFromJson(jsonPath);
                    engine.setupGPTContext();
                    engine.extractRelationshipsToGraphML();
                    engine.mergeGraphmlFilesToOutputFile();
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                status.setText("Done! Opening graph...");
                List<LowDegreeNode> flaggedNodes = engine.getLowValueTier4Nodes(MERGED_GRAPHML_FILE, TIER4_OUTPUT_PATH, 2);
                log.info("Flagged nodes: {}",
                        flaggedNodes.stream()
                                .map(LowDegreeNode::getLabel)
                                .collect(Collectors.joining(","))
                );

                if (!flaggedNodes.isEmpty()) {
                    new NodeReviewPanel(flaggedNodes, engine, start)
                            .start(primaryStage);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Graph Generation Complete");
                    alert.setHeaderText("No low-value Tier 4 nodes detected.");
                    alert.setContentText("Final GraphML saved to:\n" + NORMALIZED_GRAPH_PATH);
                    alert.showAndWait();
                    Platform.exit();
                }
            });

            task.setOnFailed(e -> {
                status.setText("Error: " + task.getException().getMessage());
                browseBtn.setDisable(false);
                progressIndicator.setVisible(false);
            });

            new Thread(task).start();
        });

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(status, browseBtn, progressIndicator);

        HBox bottomRightBox = new HBox();
        bottomRightBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomRightBox.setPadding(new Insets(0, 20, 20, 0));
        bottomRightBox.getChildren().add(exitBtn);

        StackPane centerWrapper = new StackPane(centerBox);
        centerWrapper.setPrefHeight(500);

        VBox root = new VBox();
        root.getChildren().addAll(centerWrapper, bottomRightBox);
        VBox.setVgrow(centerWrapper, Priority.ALWAYS);
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/styling/launcher.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Law Knowledge Graph Generator");
        primaryStage.show();
    }
}
