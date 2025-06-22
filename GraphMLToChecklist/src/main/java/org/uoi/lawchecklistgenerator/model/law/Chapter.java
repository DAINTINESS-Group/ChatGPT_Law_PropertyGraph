package org.uoi.lawchecklistgenerator.model.law;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements Node {

    @JsonProperty("chapterNumber")
    private int chapterNumber;

    @JsonProperty("chapterTitle")
    private String chapterTitle;

    @JsonProperty("articles")
    private ArrayList<Article> articles;

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }


    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Chapter " + chapterNumber + ": " + chapterTitle;
    }

    @JsonIgnore
    @Override
    public String getText() {
        StringBuilder text = new StringBuilder(chapterTitle + "\n\n");
        for (Article article : articles) {
            text.append(article.getText()).append("\n");
        }
        return text.toString().trim();
    }

    @JsonIgnore
    @Override
    public String getTitle() {
        return chapterTitle;
    }

    @JsonIgnore
    @Override
    public List<Article> getChildren() {
        return articles;
    }
}
