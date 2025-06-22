package org.uoi.legislativetextparser.gui;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MainMenuTest {

    @Test
    public void testMainMenuInitialization() {
        MainMenu mainMenu = new MainMenu();

        SwingUtilities.invokeLater(() -> {
            mainMenu.createMainMenu();

            JFrame frame = getMainFrame(mainMenu);
            assertNotNull(frame, "Main frame should be initialized");
            assertEquals("Legislative Text Parser - Entry Screen", frame.getTitle(), "Frame title mismatch");
            assertEquals(500, frame.getWidth(), "Frame width should be 500");
            assertEquals(400, frame.getHeight(), "Frame height should be 400");
            assertEquals(JFrame.EXIT_ON_CLOSE, frame.getDefaultCloseOperation(), "Default close operation mismatch");
        });
    }

    @Test
    public void testInputAndOutputPanelSetup() {
        MainMenu mainMenu = new MainMenu();

        SwingUtilities.invokeLater(() -> {
            mainMenu.createMainMenu();
            JFrame frame = getMainFrame(mainMenu);

            JPanel mainPanel = (JPanel) frame.getContentPane().getComponent(0);
            assertNotNull(mainPanel, "Main panel should be initialized");

            Component[] components = mainPanel.getComponents();
            assertNotNull(components, "Main panel components should not be null");

            JPanel inputPanel = (JPanel) components[0];
            JPanel outputPanel = (JPanel) components[2];

            assertInputOutputPanel(inputPanel, "Input PDF Path:");
            assertInputOutputPanel(outputPanel, "Output JSON Path:");
        });
    }

    @Test
    public void testDefinitionCheckboxBehavior() {
        MainMenu mainMenu = new MainMenu();

        SwingUtilities.invokeLater(() -> {
            mainMenu.createMainMenu();
            JFrame frame = getMainFrame(mainMenu);

            JPanel mainPanel = (JPanel) frame.getContentPane().getComponent(0);
            JCheckBox definitionCheckbox = (JCheckBox) mainPanel.getComponent(4);
            JPanel definitionForm = (JPanel) mainPanel.getComponent(5);

            assertNotNull(definitionCheckbox, "Definition checkbox should be initialized");
            assertNotNull(definitionForm, "Definition form should be initialized");

            assertFalse(definitionForm.isVisible(), "Definition form should initially be hidden");

            definitionCheckbox.setSelected(true);
            assertTrue(definitionForm.isVisible(), "Definition form should be visible after checkbox selection");

            definitionCheckbox.setSelected(false);
            assertFalse(definitionForm.isVisible(), "Definition form should be hidden after checkbox deselection");
        });
    }

    @Test
    public void testStartButtonAction() {
        MainMenu mainMenu = new MainMenu();

        SwingUtilities.invokeLater(() -> {
            mainMenu.createMainMenu();
            JFrame frame = getMainFrame(mainMenu);

            JPanel mainPanel = (JPanel) frame.getContentPane().getComponent(0);
            JButton startButton = (JButton) ((JPanel) mainPanel.getComponent(7)).getComponent(0);
            assertNotNull(startButton, "Start button should be initialized");

            startButton.doClick();

            JOptionPane.showMessageDialog(null, "Please specify both input and output paths.", "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private JFrame getMainFrame(MainMenu mainMenu) {
        return (JFrame) Arrays.stream(Frame.getFrames())
                .filter(frame -> frame instanceof JFrame && frame.getTitle().equals("Legislative Text Parser - Entry Screen"))
                .findFirst()
                .orElse(null);
    }

    private void assertInputOutputPanel(JPanel panel, String labelText) {
        JLabel label = (JLabel) panel.getComponent(0);
        JTextField textField = (JTextField) panel.getComponent(1);
        JButton browseButton = (JButton) panel.getComponent(2);

        assertNotNull(label, "Label should be initialized");
        assertEquals(labelText, label.getText(), "Label text mismatch");

        assertNotNull(textField, "Text field should be initialized");
        assertEquals(250, textField.getPreferredSize().width, "Text field width should be 250");

        assertNotNull(browseButton, "Browse button should be initialized");
        assertEquals("Browse", browseButton.getText(), "Browse button text mismatch");
    }
}
