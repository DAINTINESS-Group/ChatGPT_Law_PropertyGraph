package org.uoi.legislativetextparser.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.uoi.legislativetextparser.model.Entity;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class EntityVisualizer {

    private JFrame frame;

    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Displays the list of entities in a Swing GUI.
     *
     * @param entities List of entities to display.
     */
    public void displayEntities(List<Entity> entities) {
        initializeFrame();
        addTitleLabel();
        JSplitPane splitPane = setupSplitPane(entities);
        JPanel buttonPanel = setupButtonPanel(entities);

        try {
            saveEntitiesToTxt(entities);
            System.out.println("Entities automatically saved to: src/main/resources/output/entities.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * Initializes the main frame.
     */
    private void initializeFrame() {
        frame = new JFrame("Entity Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
    }

    /**
     * Adds a title label to the frame.
     */
    private void addTitleLabel() {
        JLabel titleLabel = new JLabel("Extracted Entities", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);
    }

    /**
     * Sets up the split pane containing the entity list and the definition text area.
     *
     * @param entities List of entities to display.
     * @return Configured JSplitPane.
     */
    private JSplitPane setupSplitPane(List<Entity> entities) {
        DefaultListModel<Entity> entityModel = createEntityModel(entities);
        JList<Entity> entityList = setupEntityList(entityModel);
        JScrollPane listScrollPane = new JScrollPane(entityList);

        JTextArea definitionArea = setupDefinitionArea();
        JScrollPane definitionScrollPane = new JScrollPane(definitionArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, definitionScrollPane);
        splitPane.setDividerLocation(300);

        setupEntityListSelectionListener(entityList, definitionArea);
        return splitPane;
    }

    /**
     * Creates the list model for entities.
     *
     * @param entities List of entities to add to the model.
     * @return DefaultListModel containing the entities.
     */
    private DefaultListModel<Entity> createEntityModel(List<Entity> entities) {
        DefaultListModel<Entity> model = new DefaultListModel<>();
        entities.forEach(model::addElement);
        return model;
    }

    /**
     * Sets up the entity list component.
     *
     * @param entityModel The model containing the entities.
     * @return Configured JList.
     */
    private JList<Entity> setupEntityList(DefaultListModel<Entity> entityModel) {
        JList<Entity> entityList = new JList<>(entityModel);
        entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entityList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getName());
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        return entityList;
    }

    /**
     * Sets up the definition text area.
     *
     * @return Configured JTextArea.
     */
    private JTextArea setupDefinitionArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return textArea;
    }

    /**
     * Adds a selection listener to update the definition area when an entity is selected.
     *
     * @param entityList    The entity list component.
     * @param definitionArea The definition text area to update.
     */
    private void setupEntityListSelectionListener(JList<Entity> entityList, JTextArea definitionArea) {
        entityList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && entityList.getSelectedValue() != null) {
                Entity selectedEntity = entityList.getSelectedValue();
                definitionArea.setText(selectedEntity.getDefinition());
            }
        });
    }

    /**
     * Sets up the button panel containing "Save as JSON" and "Close" buttons.
     *
     * @param entities The entities to save.
     * @return Configured JPanel.
     */
    private JPanel setupButtonPanel(List<Entity> entities) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save as JSON");
        saveButton.addActionListener(e -> saveEntitiesAsJsonPrompt(entities));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    /**
     * Prompts the user to save entities as a JSON file.
     *
     * @param entities The entities to save.
     */
    private void saveEntitiesAsJsonPrompt(List<Entity> entities) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Entities as JSON");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showSaveDialog(this.frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(".json")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".json");
            }
            try {
                saveEntitiesAsJson(entities, fileToSave);
                JOptionPane.showMessageDialog(this.frame, "Entities saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this.frame, "Failed to save entities: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Saves the entities to a JSON file.
     *
     * @param entities List of entities to save.
     * @param file     File to save the entities in.
     * @throws Exception If an error occurs during file writing.
     */
    public void saveEntitiesAsJson(List<Entity> entities, File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, entities);
    }

    /**
     * Saves the entities to a text (.txt) file.
     *
     * @param entities List of entities to save.
     * @throws Exception If an error occurs during file writing.
     */
    private void saveEntitiesToTxt(List<Entity> entities) throws Exception {
        File file = new File("src/main/resources/output/entities.txt");
        try (FileWriter writer = new FileWriter(file)) {
            for (Entity entity : entities) {
                writer.write(entity.getDefinition() + System.lineSeparator());
            }
        }
    }
}