package org.uoi.lawchecklistgenerator.report;

import org.uoi.lawchecklistgenerator.engine.GraphEngine;
import org.uoi.lawchecklistgenerator.model.Edge;
import org.uoi.lawchecklistgenerator.model.law.Article;
import org.uoi.lawchecklistgenerator.model.law.Chapter;
import org.uoi.lawchecklistgenerator.model.law.Paragraph;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MarkdownReportGenerator implements ReportGenerator{

    public void generateReport(GraphEngine engine, Path out) throws Exception {

        StringBuilder md = new StringBuilder("# Law Checklist Report\n\n");

        engine.getAllNodes().forEach(n -> {
            List<Edge> outEdges = n.getOutgoing();
            List<Edge> inEdges = n.getIncoming();
            if ((outEdges == null || outEdges.isEmpty()) &&
                    (inEdges == null || inEdges.isEmpty())) {
                return;
            }
            md.append("## ").append(n.getLabel()).append("\n\n");
            if (outEdges != null && !outEdges.isEmpty()) {
                md.append("### Outgoing relationships\n\n");
                for (Edge e : outEdges) {
                    appendEdge(md, e, true, engine);
                }
            }
            if (inEdges != null && !inEdges.isEmpty()) {
                md.append("### Incoming relationships\n\n");
                for (Edge e : inEdges) {
                    appendEdge(md, e, false, engine);
                }
            }
        });
        Files.writeString(out, md.toString());
    }

    private static void appendEdge(StringBuilder md, Edge e,
                                   boolean outgoing, GraphEngine engine) {

        md.append("* **")
                .append(e.getLabel())
                .append(outgoing ? " → " : " ← ")
                .append(outgoing ? e.getTarget().getLabel() : e.getSource().getLabel())
                .append("**\n");

        String pid = e.getParagraphId();
        if (pid == null) { md.append('\n'); return; }

        Paragraph p = engine.findParagraphById(pid);
        if (p == null)   { md.append('\n'); return; }

        Article  a = p.getArticle();
        Chapter  c = (a != null) ? a.getChapter() : null;

        if (a != null && c != null) {
            md.append("  * **Chapter ").append(c.getChapterNumber()).append("** : ")
                    .append(clean(c.getTitle())).append(", ")
                    .append("**Article ").append(a.getArticleNumber()).append("** : ")
                    .append(clean(a.getTitle())).append(", ")
                    .append("**Paragraph ").append(p.getParagraphNumber()).append("**\n");
        }
        md.append("  * > ").append(clean(p.getText())).append("\n\n");
    }

    private static String clean(String s) { return s == null ? "" : s.replaceAll("\\R", " ").trim(); }
}
