package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a law, which is a collection of chapters.
 */
public class Law implements Node {

    @JsonProperty("text")
    private String text;

    @JsonProperty("chapters")
    private ArrayList<Chapter> chapters;

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonIgnore
    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (Chapter chapter : chapters) {
            text.append(chapter.getText()).append("\n");
        }
        return text.toString().trim();
    }

    @JsonIgnore
    @Override
    public String getTitle() {
        return "Law";
    }

    @JsonIgnore
    @Override
    public List<Chapter> getChildren() {
        return chapters;
    }
}