package org.uoi.legislativetextparser.gui;

import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.Entity;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityVisualizerTest {

    @Test
    public void testDisplayEntities() {
        EntityVisualizer visualizer = new EntityVisualizer();

        List<Entity> sampleEntities = Arrays.asList(
                new Entity.Builder("Entity1", "Definition1").build(),
                new Entity.Builder("Entity2", "Definition2").build(),
                new Entity.Builder("Entity3", "Definition3").build()
        );

        visualizer.displayEntities(sampleEntities);

        JFrame frame = visualizer.getFrame();
        assertNotNull(frame, "Frame should not be null");
        assertEquals("Entity Visualizer", frame.getTitle(), "Frame title should be 'Entity Visualizer'");
        assertEquals(800, frame.getWidth(), "Frame width should be 800");
        assertEquals(600, frame.getHeight(), "Frame height should be 600");
        assertEquals(JFrame.EXIT_ON_CLOSE, frame.getDefaultCloseOperation(), "Default close operation should be EXIT_ON_CLOSE");

        JLabel titleLabel = (JLabel) frame.getContentPane().getComponent(0);
        assertNotNull(titleLabel, "Title label should not be null");
        assertEquals("Extracted Entities", titleLabel.getText(), "Title label text should be 'Extracted Entities'");

        JSplitPane splitPane = (JSplitPane) frame.getContentPane().getComponent(1);
        JScrollPane listScrollPane = (JScrollPane) splitPane.getLeftComponent();
        JList<?> entityList = (JList<?>) listScrollPane.getViewport().getView();
        assertNotNull(entityList, "Entity list should not be null");

        ListModel<?> listModel = entityList.getModel();
        Object[] actualEntities = new Object[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) {
            actualEntities[i] = listModel.getElementAt(i);
        }

        assertArrayEquals(sampleEntities.toArray(), actualEntities, "Entity list content should match sample entities");

        JPanel buttonPanel = (JPanel) frame.getContentPane().getComponent(2);
        JButton saveButton = (JButton) buttonPanel.getComponent(0);
        JButton closeButton = (JButton) buttonPanel.getComponent(1);
        assertNotNull(saveButton, "Save button should not be null");
        assertNotNull(closeButton, "Close button should not be null");
        assertEquals("Save as JSON", saveButton.getText(), "Save button text should be 'Save as JSON'");
        assertEquals("Close", closeButton.getText(), "Close button text should be 'Close'");

        closeButton.doClick();
        assertFalse(frame.isVisible(), "Frame should not be visible after close button click");
    }

    @Test
    public void testSaveEntitiesToTxt() {
        EntityVisualizer visualizer = new EntityVisualizer();

        List<Entity> sampleEntities = Arrays.asList(
                new Entity.Builder("Entity1", "Definition1").build(),
                new Entity.Builder("Entity2", "Definition2").build(),
                new Entity.Builder("Entity3", "Definition3").build()
        );

        String expectedFilePath = "src/main/resources/output/entities.txt";
        File txtFile = new File(expectedFilePath);

        try {
            if (txtFile.exists()) {
                assertTrue(txtFile.delete(), "Existing entities.txt file should be deleted before the test");
            }

            // Display entities, which triggers automatic saving
            visualizer.displayEntities(sampleEntities);

            // Verify the file exists
            assertTrue(txtFile.exists(), "The entities.txt file should be created automatically");

            // Verify file content
            List<String> lines = Files.readAllLines(txtFile.toPath());
            assertEquals(3, lines.size(), "The file should contain exactly 3 lines");

            assertEquals("Entity1 means Definition1", lines.get(0), "First line content should match");
            assertEquals("Entity2 means Definition2", lines.get(1), "Second line content should match");
            assertEquals("Entity3 means Definition3", lines.get(2), "Third line content should match");

        } catch (Exception e) {
            fail("No exception should occur during testSaveEntitiesToTxt: " + e.getMessage());
        } finally {
            if (txtFile.exists()) {
                assertTrue(txtFile.delete(), "entities.txt file should be deleted after the test");
            }
        }
    }
}
