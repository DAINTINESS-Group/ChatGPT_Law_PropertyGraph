package org.uoi.lawchecklistgenerator.model.law;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Paragraph implements Node{
    @JsonProperty("paragraphNumber")
    private int paragraphNumber;

    @JsonProperty("paragraphID")
    private String paragraphID;

    @JsonProperty("text")
    private ArrayList<Point> paragraphPoints;

    @JsonIgnore
    private transient Article article;

    public Article getArticle() { return article; }

    public void setArticle(Article article) { this.article = article; }

    public int getParagraphNumber() {
        return paragraphNumber;
    }

    public void setParagraphNumber(int paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }


    public ArrayList<Point> getParagraphPoints() {
        return paragraphPoints;
    }

    public void setParagraphPoints(ArrayList<Point> paragraphPoints) {
        this.paragraphPoints = paragraphPoints;
    }

    public String getParagraphID() {
        return paragraphID;
    }

    public void setParagraphID(String paragraphID) {
        this.paragraphID = paragraphID;
    }

    @Override
    public String toString() {
        return "Paragraph: " + paragraphID;
    }

    @JsonIgnore
    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (Point point : paragraphPoints) {
            text.append(point.getText()).append("\n");
        }
        return text.toString().trim();
    }

    @JsonIgnore
    @Override
    public String getTitle() {
        return paragraphID;
    }

    @JsonIgnore
    @Override
    public ArrayList<Point> getChildren() {
        return paragraphPoints;
    }
}
