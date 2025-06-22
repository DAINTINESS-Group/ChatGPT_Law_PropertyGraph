package org.uoi.lawchecklistgenerator.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.lawchecklistgenerator.engine.GraphEngine;
import org.uoi.lawchecklistgenerator.engine.GraphEngineImpl;

import java.io.File;
import java.util.Objects;

public class ChecklistViewerLauncher extends Application {
    private static final Logger log = LoggerFactory.getLogger(ChecklistViewerLauncher.class);
    private final GraphEngine engine = new GraphEngineImpl();
    private static final String STYLE_CSS = "/styling/styles.css";

    @Override
    public void start(Stage stage) throws Exception {
        Label instruction = new Label(
                "Choose JSON file from LegislativeTextParser and GraphML file from LawToKnowledgeGraphGenerator"
        );
        instruction.setWrapText(true);
        instruction.getStyleClass().add("checklist-header");

        TextField jsonField = new TextField();
        jsonField.setPromptText("No JSON selected");
        jsonField.setEditable(false);
        HBox jsonBox = createJsonBox(stage, jsonField);

        TextField graphField = new TextField();
        graphField.setPromptText("No GraphML selected");
        graphField.setEditable(false);
        HBox graphBox = createGraphMLBox(stage, graphField);

        Button continueBtn = new Button("Continue");
        continueBtn.setDisable(true);
        continueBtn.getStyleClass().add("button");
        ChangeListener<String> enabler = (obs, oldVal, newVal) ->
                continueBtn.setDisable(jsonField.getText().isEmpty() || graphField.getText().isEmpty());
        jsonField.textProperty().addListener(enabler);
        graphField.textProperty().addListener(enabler);

        continueBtn.setOnAction(e -> {
            File graphML = new File(graphField.getText());
            File lawJson = new File(jsonField.getText());
            try {
                engine.loadGraph(graphML, lawJson);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            NodeList canvas = new NodeList(engine);
            ScrollPane canvasScroll = new ScrollPane(canvas);
            canvasScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            canvasScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            canvasScroll.setFitToHeight(true);

            Label nodesHeader = new Label("Nodes extracted");
            nodesHeader.getStyleClass().add("canvas-header");
            VBox nodesPane = new VBox(nodesHeader, canvasScroll);
            nodesPane.setSpacing(4);

            ChecklistPane checklist = new ChecklistPane(engine);
            ScrollPane checklistScroll = new ScrollPane(checklist);
            checklistScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            checklistScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            checklistScroll.setFitToWidth(true);

            canvas.setOnNodeSelected(checklist::showFor);
            SplitPane split = new SplitPane(nodesPane, checklistScroll);
            split.setDividerPositions(0.25);

            Button makeReport = new Button("Create Report");
            makeReport.getStyleClass().add("button");

            makeReport.setOnAction(ev -> setPathAndGenerateReport(stage));
            Button exit = new Button("Exit");
            exit.getStyleClass().add("button");
            exit.setOnAction(ev -> Platform.exit());
            BorderPane bottom = new BorderPane();
            bottom.setLeft(makeReport);
            bottom.setRight(exit);
            BorderPane.setMargin(makeReport, new Insets(6));
            BorderPane.setMargin(exit, new Insets(6));

            BorderPane root = new BorderPane(split);
            root.setBottom(bottom);

            Scene scene = new Scene(root, 1550, 700);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource(STYLE_CSS)).toExternalForm()
            );
            stage.setScene(scene);
            stage.setTitle("Checklist Viewer");
            stage.show();
            Platform.runLater(stage::sizeToScene);
        });

        VBox chooserRoot = new VBox(15, instruction, jsonBox, graphBox, continueBtn);
        chooserRoot.setAlignment(Pos.CENTER);
        chooserRoot.setPadding(new Insets(20));
        chooserRoot.getStyleClass().add("checklist-sep");
        Scene chooserScene = new Scene(chooserRoot, 900, 500);
        chooserScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLE_CSS)).toExternalForm());
        stage.setScene(chooserScene);
        stage.setTitle("Select Input Files");
        stage.show();
    }

    private HBox createGraphMLBox(Stage stage, TextField graphField) {
        Button graphBrowse = new Button("Browse GraphML");
        graphBrowse.getStyleClass().add("button");
        graphBrowse.setOnAction(e -> {

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select LawToKnowledgeGraphGenerator GraphML");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("GraphML files (*.graphml)", "*.graphml")
            );
            File f = chooser.showOpenDialog(stage);
            if (f != null && f.exists() && f.getName().endsWith(".graphml")) {
                graphField.setText(f.getAbsolutePath());
            } else if (f != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a valid .graphml file", ButtonType.OK);
                alert.initOwner(stage);
                alert.showAndWait();
            }
        });
        HBox graphBox = new HBox(10, graphField, graphBrowse);
        graphBox.setAlignment(Pos.CENTER);
        graphBox.getStyleClass().add("section-header");
        return graphBox;
    }

    private HBox createJsonBox(Stage stage, TextField jsonField) {
        Button jsonBrowse = new Button("Browse JSON");
        jsonBrowse.getStyleClass().add("button");
        jsonBrowse.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select LegislativeTextParser JSON");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
            );
            File f = chooser.showOpenDialog(stage);
            if (f != null && f.exists() && f.getName().endsWith(".json")) {
                jsonField.setText(f.getAbsolutePath());
            } else if (f != null) {
                Alert alert = new  Alert(Alert.AlertType.ERROR, "Please select a valid .json file", ButtonType.OK);
                alert.initOwner(stage);
                alert.showAndWait();
            }
        });
        HBox jsonBox = new HBox(10, jsonField, jsonBrowse);
        jsonBox.setAlignment(Pos.CENTER);
        jsonBox.getStyleClass().add("section-header");
        return jsonBox;
    }

    private void setPathAndGenerateReport(Stage owner) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save Markdown Report");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));
        fc.setInitialFileName("ChecklistReport.md");

        File target = fc.showSaveDialog(owner);
        if (target == null) return;

        try {
            engine.generateMarkdownReport(target.toPath());

            Alert ok = new Alert(Alert.AlertType.INFORMATION,
                    "Report created successfully:\n" + target.getAbsolutePath(),
                    ButtonType.OK);
            ok.setHeaderText(null);
            ok.initOwner(owner);
            ok.showAndWait();

        } catch (Exception ex) {
            log.error("Report creation failed : {}", ex.getMessage());

            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Failed to create report:\n" + ex.getMessage(),
                    ButtonType.OK);
            err.setHeaderText("Error");
            err.initOwner(owner);
            err.showAndWait();
        }
    }
}
