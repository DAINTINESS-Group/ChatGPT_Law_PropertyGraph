package org.uoi.legislativetextparser.gui;

import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.*;
import org.uoi.legislativetextparser.model.Point;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TreeVisualizerTest {

    @Test
    void testTreeStructure() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Law.Builder(new ArrayList<>()).build());
        DefaultMutableTreeNode chapter1 = new DefaultMutableTreeNode(new Chapter.Builder(1, new ArrayList<>(), "Chapter 1 Title").build());
        DefaultMutableTreeNode article1 = new DefaultMutableTreeNode(new Article.Builder(1, new ArrayList<>(), "1.1", "Article 1.1 Title").build());
        DefaultMutableTreeNode paragraph1 = new DefaultMutableTreeNode(new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build());
        DefaultMutableTreeNode point1 = new DefaultMutableTreeNode(new Point.Builder(1, "Point 1.1.1.a Content").build());

        paragraph1.add(point1);
        article1.add(paragraph1);
        chapter1.add(article1);
        root.add(chapter1);

        assertEquals("Law", ((Law) root.getUserObject()).getTitle(), "Root node title mismatch");
        assertEquals(1, root.getChildCount(), "Root node should have one child");
        assertEquals("Chapter 1 Title", ((Chapter) ((DefaultMutableTreeNode) root.getChildAt(0)).getUserObject()).getTitle(), "Chapter title mismatch");
        assertEquals(1, root.getChildAt(0).getChildCount(), "Chapter node should have one article");
        assertEquals("Article 1.1 Title", ((Article) ((DefaultMutableTreeNode) root.getChildAt(0).getChildAt(0)).getUserObject()).getTitle(), "Article title mismatch");
        assertEquals("1.1.1", ((Paragraph) ((DefaultMutableTreeNode) root.getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject()).getTitle(), "Paragraph title mismatch");
        assertEquals("1", ((Point) ((DefaultMutableTreeNode) root.getChildAt(0).getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject()).getTitle(), "Point title mismatch");
    }

    @Test
    void testTreeVisualizerGUI() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Law.Builder(new ArrayList<>()).build());
        DefaultMutableTreeNode chapter1 = new DefaultMutableTreeNode(new Chapter.Builder(1, new ArrayList<>(), "Chapter 1 Title").build());
        DefaultMutableTreeNode article1 = new DefaultMutableTreeNode(new Article.Builder(1, new ArrayList<>(), "1.1", "Article 1.1 Title").build());
        root.add(chapter1);
        chapter1.add(article1);

        TreeVisualizer visualizer = new TreeVisualizer();

        SwingUtilities.invokeLater(() -> {
            visualizer.displayTree(root, null);

            JFrame frame = visualizer.getFrame();
            assertNotNull(frame, "Frame should be created");
            assertEquals("Law Structure Navigator", frame.getTitle(), "Frame title mismatch");
            assertEquals(JFrame.EXIT_ON_CLOSE, frame.getDefaultCloseOperation(), "Close operation mismatch");

            Component[] components = frame.getContentPane().getComponents();
            assertInstanceOf(JSplitPane.class, components[0], "First component should be a JSplitPane");
            assertInstanceOf(JPanel.class, components[1], "Second component should be a JPanel");

            JSplitPane splitPane = (JSplitPane) components[0];
            JScrollPane treeScrollPane = (JScrollPane) splitPane.getLeftComponent();
            JScrollPane textScrollPane = (JScrollPane) splitPane.getRightComponent();

            assertInstanceOf(JTree.class, treeScrollPane.getViewport().getView(), "Left split pane component should be a JTree");
            assertInstanceOf(JTextArea.class, textScrollPane.getViewport().getView(), "Right split pane component should be a JTextArea");

            JTree tree = (JTree) treeScrollPane.getViewport().getView();
            assertNotNull(tree.getModel(), "Tree model should not be null");

            JTextArea textArea = (JTextArea) textScrollPane.getViewport().getView();
            assertNotNull(textArea, "Text area should not be null");
            assertEquals("", textArea.getText(), "Text area should initially be empty");

            // Simulate tree selection and verify text area updates
            tree.setSelectionPath(tree.getPathForRow(1)); // Select "Chapter 1"
            assertEquals("Chapter 1 Title", textArea.getText(), "Text area should update based on tree selection");

            tree.setSelectionPath(tree.getPathForRow(2)); // Select "Article 1.1"
            assertEquals("Article 1.1 Title", textArea.getText(), "Text area should update based on tree selection");

            frame.dispose();
        });
    }

    @Test
    void testCloseButtonFunctionality() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Law.Builder(new ArrayList<>()).build());
        TreeVisualizer visualizer = new TreeVisualizer();

        SwingUtilities.invokeLater(() -> {
            visualizer.displayTree(root, null);

            JFrame frame = visualizer.getFrame();
            assertNotNull(frame, "Frame should be created");

            JPanel buttonPanel = (JPanel) frame.getContentPane().getComponent(1);
            JButton closeButton = (JButton) buttonPanel.getComponent(0);
            assertNotNull(closeButton, "Close button should not be null");

            closeButton.doClick();
            assertFalse(frame.isVisible(), "Frame should not be visible after close button click");
        });
    }
}
