package org.uoi.legislativetextparser.gui;

import org.uoi.legislativetextparser.model.Node;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class TreeVisualizer {

    private JFrame frame;

    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Displays a hierarchical tree structure along with associated text in a split pane.
     *
     * @param rootNode   The root node of the tree to display.
     * @param relativeTo A component relative to which the window is displayed.
     */
    public void displayTree(DefaultMutableTreeNode rootNode, Component relativeTo) {
        initializeFrame(relativeTo);
        JSplitPane splitPane = setupSplitPane(rootNode);
        JPanel buttonPanel = setupButtonPanel();

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * Initializes the main frame.
     *
     * @param relativeTo The component relative to which the window will be displayed.
     */
    private void initializeFrame(Component relativeTo) {
        frame = new JFrame("Law Structure Navigator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(relativeTo);
    }

    /**
     * Sets up the split pane with the tree and text area.
     *
     * @param rootNode The root node of the tree.
     * @return Configured JSplitPane.
     */
    private JSplitPane setupSplitPane(DefaultMutableTreeNode rootNode) {
        JTree tree = createTree(rootNode);
        JScrollPane treeScrollPane = new JScrollPane(tree);

        JTextArea textArea = createTextArea(rootNode);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, textScrollPane);
        splitPane.setDividerLocation(400);

        setupTreeSelectionListener(tree, textArea);
        return splitPane;
    }

    /**
     * Creates the tree component.
     *
     * @param rootNode The root node of the tree.
     * @return Configured JTree.
     */
    private JTree createTree(DefaultMutableTreeNode rootNode) {
        return new JTree(rootNode);
    }

    /**
     * Creates the text area for displaying node details.
     *
     * @param rootNode The root node of the tree.
     * @return Configured JTextArea.
     */
    private JTextArea createTextArea(DefaultMutableTreeNode rootNode) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (rootNode.getUserObject() instanceof Node root) {
            textArea.setText(root.getText());
            textArea.setCaretPosition(0);
        }

        return textArea;
    }

    /**
     * Sets up the tree selection listener to update the text area when a node is selected.
     *
     * @param tree     The tree component.
     * @param textArea The text area to update.
     */
    private void setupTreeSelectionListener(JTree tree, JTextArea textArea) {
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getUserObject() instanceof Node node) {
                textArea.setText(node.getText());
                textArea.setCaretPosition(0);
            }
        });
    }

    /**
     * Sets up the button panel with a close button.
     *
     * @return Configured JPanel.
     */
    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);
        return buttonPanel;
    }
}