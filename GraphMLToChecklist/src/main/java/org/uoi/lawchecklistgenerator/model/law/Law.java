package org.uoi.lawchecklistgenerator.model.law;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Law implements Node{

    @JsonProperty("text")
    private String text;

    @JsonProperty("chapters")
    private ArrayList<Chapter> chapters;

    private transient Map<String, Paragraph> paragraphIndex;
    private transient Map<Integer, Chapter> chapterMap;
    private transient Map<Integer, Map<Integer, Article>> articleMap;


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

    @JsonIgnore
    public void initIndex() {
        paragraphIndex = new HashMap<>();
        chapterMap = new HashMap<>();
        articleMap = new HashMap<>();

        for (Chapter ch : chapters) {
            chapterMap.put(ch.getChapterNumber(), ch);

            Map<Integer, Article> artsOfChapter = new HashMap<>();
            articleMap.put(ch.getChapterNumber(), artsOfChapter);

            for (Article art : ch.getArticles()) {
                art.setChapter(ch);
                artsOfChapter.put(art.getArticleNumber(), art);

                for (Paragraph p : art.getParagraphs()) {
                    p.setArticle(art);
                    paragraphIndex.put(p.getParagraphID(), p);
                }
            }
        }
    }

    @JsonIgnore
    public Paragraph findParagraphById(String id) {
        if (paragraphIndex == null) initIndex();
        return paragraphIndex.get(id);
    }

    @JsonIgnore
    public Chapter getChapter(int number) {
        if (chapterMap == null) initIndex();
        return chapterMap.get(number);
    }

    @JsonIgnore
    public Article getArticle(int chapterNum, int articleNum) {
        if (articleMap == null) initIndex();
        Map<Integer, Article> arts = articleMap.get(chapterNum);
        return arts == null ? null : arts.get(articleNum);
    }

    @JsonIgnore
    public Paragraph getParagraph(int chapterNum, int articleNum, int paragraphNum) {
        Article art = getArticle(chapterNum, articleNum);
        if (art == null) return null;
        return art.getParagraphByNumber(paragraphNum);
    }
}
