package org.uoi.legislativetextparser.tree;

import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LawTreeBuilderTest {

    @Test
    public void testBuildTreeSuccess() {
        // Create mock data for the Law object
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point.Builder(1, "Point content").build());

        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        paragraphs.add(new Paragraph.Builder(1, points, "1.1.1").build());

        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article.Builder(1, paragraphs, "1.1", "Article Title").build());

        ArrayList<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter.Builder(1, articles, "Chapter Title").build());

        Law law = new Law.Builder(chapters).build();

        DefaultMutableTreeNode root = LawTreeBuilder.buildTree(law);

        assertNotNull(root, "Root node should not be null");
        assertEquals(law, root.getUserObject(), "Root node should represent the Law object");
        assertEquals(1, root.getChildCount(), "Root node should have one child (Chapter)");

        DefaultMutableTreeNode chapterNode = (DefaultMutableTreeNode) root.getChildAt(0);
        Chapter chapter = (Chapter) chapterNode.getUserObject();
        assertEquals("Chapter Title", chapter.getTitle(), "Chapter title mismatch");
        assertEquals(1, chapterNode.getChildCount(), "Chapter node should have one child (Article)");

        DefaultMutableTreeNode articleNode = (DefaultMutableTreeNode) chapterNode.getChildAt(0);
        Article article = (Article) articleNode.getUserObject();
        assertEquals("Article Title", article.getTitle(), "Article title mismatch");
        assertEquals(1, articleNode.getChildCount(), "Article node should have one child (Paragraph)");

        DefaultMutableTreeNode paragraphNode = (DefaultMutableTreeNode) articleNode.getChildAt(0);
        Paragraph paragraph = (Paragraph) paragraphNode.getUserObject();
        assertEquals("1.1.1", paragraph.getTitle(), "Paragraph title mismatch");
        assertEquals(1, paragraphNode.getChildCount(), "Paragraph node should have one child (Point)");

        DefaultMutableTreeNode pointNode = (DefaultMutableTreeNode) paragraphNode.getChildAt(0);
        Point point = (Point) pointNode.getUserObject();
        assertEquals("1", point.getTitle(), "Point title mismatch");
    }

    @Test
    public void testBuildTreeEmptyLaw() {
        ArrayList<Chapter> chapters = new ArrayList<>();
        Law law = new Law.Builder(chapters).build();

        DefaultMutableTreeNode root = LawTreeBuilder.buildTree(law);

        assertNotNull(root, "Root node should not be null");
        assertEquals(law, root.getUserObject(), "Root node should represent the Law object");
        assertEquals(0, root.getChildCount(), "Root node should have no children for an empty Law");
    }
}
