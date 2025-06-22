package org.uoi.legislativetextparser.tree;

import org.uoi.legislativetextparser.model.Article;
import org.uoi.legislativetextparser.model.Chapter;
import org.uoi.legislativetextparser.model.Law;
import org.uoi.legislativetextparser.model.Paragraph;
import org.uoi.legislativetextparser.model.Point;

import javax.swing.tree.DefaultMutableTreeNode;

public class LawTreeBuilder {

    public static DefaultMutableTreeNode buildTree(Law law) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(law);

        for (Chapter chapter : law.getChapters()) {
            DefaultMutableTreeNode chapterNode = new DefaultMutableTreeNode(chapter);
            root.add(chapterNode);

            for (Article article : chapter.getChildren()) {
                DefaultMutableTreeNode articleNode = new DefaultMutableTreeNode(article);
                chapterNode.add(articleNode);

                for (Paragraph paragraph : article.getChildren()) {
                    DefaultMutableTreeNode paragraphNode = new DefaultMutableTreeNode(paragraph);
                    articleNode.add(paragraphNode);

                    for (Point point : paragraph.getChildren()) {
                        DefaultMutableTreeNode pointNode = new DefaultMutableTreeNode(point);
                        paragraphNode.add(pointNode);
                    }
                }
            }
        }
        return root;
    }
}