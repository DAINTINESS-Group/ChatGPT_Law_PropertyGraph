package org.uoi.lawtopropertygraphgenerator.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.lawtopropertygraphgenerator.api.GPTApiHandler;
import org.uoi.lawtopropertygraphgenerator.data.LawReader;
import org.uoi.lawtopropertygraphgenerator.graphML.GraphMLNormalizer;
import org.uoi.lawtopropertygraphgenerator.graphML.GraphmlSimilarityAnalyzer;
import org.uoi.lawtopropertygraphgenerator.graphML.LawValueNodeDetector;
import org.uoi.lawtopropertygraphgenerator.model.graphML.LowDegreeNode;
import org.uoi.lawtopropertygraphgenerator.model.law.Chapter;
import org.uoi.lawtopropertygraphgenerator.model.law.Law;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LawKnowledgeGraphEngineFacadeImpl implements LawKnowledgeGraphEngineFacade {
    private final Logger log = LoggerFactory.getLogger(LawKnowledgeGraphEngineFacadeImpl.class);

    private Law law;
    private final GPTApiHandler apiHandler;
    private final LawReader lawReader;
    private final GraphmlSimilarityAnalyzer graphMLSimilarityAnalyzer;
    private final GraphMLNormalizer graphMLNormalizer;
    private final LawValueNodeDetector lawValueNodeDetector;

    public LawKnowledgeGraphEngineFacadeImpl() {
        graphMLSimilarityAnalyzer = new GraphmlSimilarityAnalyzer();
        apiHandler = new GPTApiHandler();
        lawReader = new LawReader();
        graphMLNormalizer = new GraphMLNormalizer();
        lawValueNodeDetector = new LawValueNodeDetector();
    }

    @Override
    public void cleanOutputDir(String filePath) throws IOException {
        // Clean output directory from previous runs
        Path graphMLDir = Paths.get("src/main/resources/output/graphML");
        if (Files.exists(graphMLDir) && Files.isDirectory(graphMLDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(graphMLDir)) {
                for (Path file : stream) {
                    Files.deleteIfExists(file);
                }
            }
        }
        log.info("Cleaned output directory.");
    }

    @Override
    public void loadLawFromJson(String jsonFilePath) {
        this.law = lawReader.readLawObjectFromJson(jsonFilePath);
        log.info("Law object loaded and deserialized successfully from JSON file. {}", jsonFilePath);
    }

    @Override
    public void setupGPTContext() {
        this.apiHandler.setupEntitiesAndGiveExample();
        this.apiHandler.passInstructionsToModel();
        log.info("GPT-4o-mini context initialized successfully.");
    }

    @Override
    public void extractRelationshipsToGraphML() {
        // NORMAL FLOW
//        this.law.getChapters().forEach(c -> {
//            c.getArticles().
//                    stream().
//                    flatMap(a -> a.getParagraphs()
//                            .stream())
//                    .forEach(p -> this.apiHandler.extractRelationships(p.getText(), p.getParagraphID()));
//        });

        // TEST CODE
        // Run specific chapters
//        List<Chapter> c = this.law.getChapters();
//        for (int i = 8; i < c.size(); i++) {
//            Chapter chapter = c.get(i);
//            log.info("Chapter NUM: {}", chapter.getChapterNumber());
//            chapter.getArticles().forEach(a -> {
//                a.getParagraphs().forEach(p ->
//                        this.apiHandler.extractRelationships(p.getText(), p.getParagraphID())
//                );
//            });
//        }
        // Run specific chapter
        Chapter chapter = this.law.getChapters().get(7);
        chapter.getArticles().forEach(
                article -> article.getParagraphs().forEach(p -> {
            this.apiHandler.extractRelationships(p.getText(), p.getParagraphID());
        }));
        log.info("Relationships extracted and written to GraphML files.");
    }



    @Override
    public void mergeGraphmlFilesToOutputFile() {
        callPythonScript("process_graphML_files.py");
    }

    @Override
    public List<LowDegreeNode> getLowValueTier4Nodes(String graphmlPath, String tier4Path, int degreeThreshold) {
        return lawValueNodeDetector.analyzeLowDegreeNodes(graphmlPath, tier4Path, degreeThreshold);
    }

    @Override
    public void removeSelectedNodes(List<String> selectedLabels, String outputPath) throws IOException {
        Files.write(Paths.get(outputPath), selectedLabels);
        callPythonScript("delete_selected_nodes.py");

        log.info("Selected nodes removed successfully.");
    }



    @Override
    public void normalizeGraphMLAndExportToJSON(String inputGraphML, String entityListPath, String outputJsonPath, String tier4OutputPath) throws IOException {
        graphMLNormalizer.normalizeAndExport(inputGraphML, entityListPath, outputJsonPath, tier4OutputPath);
        log.info("GraphML normalization map exported successfully to JSON file.");

        callPythonScript("merge_nodes.py");
    }

    @Override
    public void analyzeNodeLabelSimilarity(String graphMLFilePath, int similarityThreshold, int significantThreshold) {
        graphMLSimilarityAnalyzer.analyzeGraphML(graphMLFilePath, "n0", similarityThreshold, significantThreshold);
    }

    private void callPythonScript(String pythonFileName) {
        try {
            String pythonExecutable = "python";
            String scriptPath = "src/main/resources/python_scripts/" + pythonFileName;

            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);
            Process process = processBuilder.start();
            log.info("Process started to execute Python script: {}", scriptPath.split("/")[4]);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Script executed successfully.");
            } else {
                log.error("Script execution failed. Exit code: {}", exitCode);
            }

        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
