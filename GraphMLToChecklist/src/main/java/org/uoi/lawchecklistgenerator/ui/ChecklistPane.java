package org.uoi.lawchecklistgenerator.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.uoi.lawchecklistgenerator.engine.GraphEngine;
import org.uoi.lawchecklistgenerator.model.Edge;
import org.uoi.lawchecklistgenerator.model.Node;
import org.uoi.lawchecklistgenerator.model.law.Article;
import org.uoi.lawchecklistgenerator.model.law.Chapter;
import org.uoi.lawchecklistgenerator.model.law.Paragraph;

import java.util.List;

public class ChecklistPane extends VBox {

    private final GraphEngine engine;

    public ChecklistPane(GraphEngine engine) {
        this.engine = engine;
        setSpacing(12);
        setPadding(new Insets(12));
    }

    public void showFor(Node node) {
        getChildren().clear();
        addHeader(node.getLabel());

        addSection("Outgoing relationships",  node.getOutgoing(),  true);
        addSection("Incoming relationships",  node.getIncoming(),  false);
    }


    private void addHeader(String title) {
        Label hdr = new Label(title);
        hdr.getStyleClass().add("checklist-header");
        getChildren().add(hdr);
    }

    private void addSection(String title, List<Edge> edges, boolean outgoing) {
        if (edges == null || edges.isEmpty()) return;

        Label sec = new Label(title);
        sec.getStyleClass().add("section-header");
        getChildren().add(sec);

        int sz = edges.size();
        int idx = 0;
        for (Edge e : edges) {
            idx++;
            getChildren().add(buildItem(e, outgoing));
            if (idx < sz) getChildren().add(new Separator());
        }
        getChildren().add(new Separator());
    }

    private VBox buildItem(Edge edge, boolean outgoing) {

        String rel = outgoing
                ? String.format("• %s → %s", edge.getLabel(), edge.getTarget().getLabel())
                : String.format("• %s ← %s", edge.getLabel(), edge.getSource().getLabel());

        Label relLbl = new Label(rel);
        relLbl.getStyleClass().add("rel-label");

        TextFlow pathFlow = new TextFlow();
        pathFlow.getStyleClass().add("path-label");

        Label paraLbl = new Label();
        paraLbl.getStyleClass().add("para-label");
        paraLbl.setWrapText(true);

        fillPathAndParagraph(edge, pathFlow, paraLbl);

        VBox box = new VBox(relLbl, pathFlow, paraLbl);
        box.setSpacing(4);
        return box;
    }

    private void fillPathAndParagraph(Edge edge, TextFlow path, Label para) {

        String pid = edge.getParagraphId();
        if (pid == null) return;

        Paragraph p = engine.findParagraphById(pid);
        if (p == null) return;

        para.setText("“" + oneLine(p.getText()) + "”");

        Article a = p.getArticle();
        Chapter c = (a != null) ? a.getChapter() : null;
        if (a == null || c == null) return;

        path.getChildren().addAll(
                bold("Chapter "), plain(c.getChapterNumber() + " : "),
                plain(oneLine(c.getTitle()) + ", "),
                bold("Article "), plain(a.getArticleNumber() + " : "),
                plain(oneLine(a.getTitle()) + ", "),
                bold("Paragraph "), plain(String.valueOf(p.getParagraphNumber()))
        );
    }

    private static String oneLine(String s) {
        return s == null ? "" : s.replaceAll("\\R", " ").trim();
    }

    private static Text bold(String s)  {
        Text t = new Text(s);
        t.getStyleClass().add("bold");
        return t;
    }

    private static Text plain(String s) {
        return new Text(s);
    }
}
