package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an article, which is a collection of paragraphs.
 */
public class Article implements Node {

    @JsonProperty("articleNumber")
    private int articleNumber;

    @JsonProperty("articleID")
    private String articleID;

    @JsonProperty("articleTitle")
    private String articleTitle;

    @JsonProperty("paragraphs")
    private ArrayList<Paragraph> paragraphs;

    public int getArticleNumber() {
        return articleNumber;
    }

    public String getArticleID() {
        return articleID;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public ArrayList<Paragraph> getParagraphs() {
        return paragraphs;
    }

    @JsonIgnore
    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (Paragraph paragraph : paragraphs) {
            text.append(paragraph.getText()).append("\n");
        }
        return text.toString().trim();
    }

    @JsonIgnore
    @Override
    public String getTitle() {
        return articleTitle;
    }

    @JsonIgnore
    @Override
    public List<? extends Node> getChildren() {
        return paragraphs;
    }

}