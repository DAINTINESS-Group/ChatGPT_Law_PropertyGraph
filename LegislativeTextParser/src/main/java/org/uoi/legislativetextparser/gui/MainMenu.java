package org.uoi.legislativetextparser.gui;

import org.uoi.legislativetextparser.config.Config;
import org.uoi.legislativetextparser.engine.LawProcessor;
import org.uoi.legislativetextparser.entityextraction.EntityExtractor;
import org.uoi.legislativetextparser.entityextraction.ManualEntityExtractor;
import org.uoi.legislativetextparser.entityextraction.SpecificLocationEntityExtractor;
import org.uoi.legislativetextparser.model.Entity;
import org.uoi.legislativetextparser.model.Law;
import org.uoi.legislativetextparser.tree.LawTreeBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.File;

public class MainMenu {

    private JFrame frame;
    private JTextField inputField;
    private JTextField outputField;
    private JCheckBox definitionCheckbox;
    private JTextField chapterField;
    private JTextField articleField;

    public void createMainMenu() {
        initializeFrame();
        JPanel mainPanel = setupMainPanel();
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame("Legislative Text Parser - Entry Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout(10, 10));
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel definitionForm = setupDefinitionForm();
        mainPanel.add(setupInputPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(setupOutputPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(setupDefinitionCheckbox(definitionForm));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(definitionForm);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(setupButtonPanel());

        return mainPanel;
    }

    private JPanel setupInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel inputLabel = new JLabel("Input PDF Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(inputLabel, gbc);

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(250, 25));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(inputField, gbc);

        JButton inputBrowseButton = new JButton("Browse");
        inputBrowseButton.addActionListener(e -> handleBrowse(inputField));
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(inputBrowseButton, gbc);

        return inputPanel;
    }

    private JPanel setupOutputPanel() {
        JPanel outputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel outputLabel = new JLabel("Output JSON Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        outputPanel.add(outputLabel, gbc);

        outputField = new JTextField();
        outputField.setPreferredSize(new Dimension(250, 25));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        outputPanel.add(outputField, gbc);

        JButton outputBrowseButton = new JButton("Browse");
        outputBrowseButton.addActionListener(e -> handleBrowse(outputField));
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        outputPanel.add(outputBrowseButton, gbc);

        return outputPanel;
    }

    private JCheckBox setupDefinitionCheckbox(JPanel definitionForm) {
        definitionCheckbox = new JCheckBox("Specify Definitions Location");
        definitionCheckbox.addActionListener(e -> handleDefinitionCheckbox(definitionForm));
        return definitionCheckbox;
    }

    private JPanel setupDefinitionForm() {
        JPanel definitionForm = new JPanel(new GridLayout(2, 2, 5, 5));
        definitionForm.setBorder(BorderFactory.createTitledBorder("Definitions Location"));
        definitionForm.setVisible(false);

        chapterField = new JTextField(5);
        articleField = new JTextField(5);

        definitionForm.add(new JLabel("Chapter Number:"));
        definitionForm.add(chapterField);
        definitionForm.add(new JLabel("Article Number:"));
        definitionForm.add(articleField);

        return definitionForm;
    }

    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> handleStartButton());
        buttonPanel.add(startButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    private void handleBrowse(JTextField targetField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            targetField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void handleDefinitionCheckbox(JPanel definitionForm) {
        definitionForm.setVisible(definitionCheckbox.isSelected());
        frame.revalidate();
        frame.repaint();
    }

    private void handleStartButton() {
        String inputPath = inputField.getText().trim();
        String outputPath = outputField.getText().trim();

        if (inputPath.isEmpty() || outputPath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please specify both input and output paths.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!inputPath.endsWith(".pdf") || !outputPath.endsWith(".json")) {
            JOptionPane.showMessageDialog(frame, "Input file must be a PDF and output file must be JSON.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Config config = new Config(inputPath, outputPath);
            LawProcessor lawProcessor = new LawProcessor(config);
            Law extractedLaw = lawProcessor.processLegislativeDocument();

            EntityExtractor extractor = definitionCheckbox.isSelected()
                    ? new SpecificLocationEntityExtractor(Integer.parseInt(chapterField.getText()), Integer.parseInt(articleField.getText()))
                    : new ManualEntityExtractor();

            List<Entity> entities = extractor.extractEntities(outputPath);

            if (entities.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No entities found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int response = JOptionPane.showConfirmDialog(
                    frame,
                    "Processing completed successfully!",
                    "Confirmation",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (response == JOptionPane.OK_OPTION) {
                EntityVisualizer entityVisualizer = new EntityVisualizer();
                entityVisualizer.displayEntities(entities);

                TreeVisualizer treeVisualizer = new TreeVisualizer();
                treeVisualizer.displayTree(LawTreeBuilder.buildTree(extractedLaw), frame);

                positionWindows(entityVisualizer.getFrame(), treeVisualizer.getFrame());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void positionWindows(JFrame entityFrame, JFrame treeFrame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int frameHeight = Math.min(entityFrame.getHeight(), treeFrame.getHeight());
        int frameWidth = (int) (screenSize.getWidth() / 4);

        entityFrame.setSize(frameWidth, frameHeight);
        treeFrame.setSize(frameWidth + 350, frameHeight);

        int totalWidth = frameWidth * 2;
        int startX = (screenSize.width - totalWidth) / 2;
        entityFrame.setLocation(startX, (screenSize.height - frameHeight) / 2);
        treeFrame.setLocation(startX + frameWidth, (screenSize.height - frameHeight) / 2);
    }
}