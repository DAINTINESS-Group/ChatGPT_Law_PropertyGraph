package org.uoi.legislativetextparser.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LawTest {

    @Test
    void testBuilderCreatesLawSuccessfully() {
        Law law = new Law.Builder(new ArrayList<>()).build();

        assertNotNull(law.getChapters());
        assertTrue(law.getChapters().isEmpty());
    }

    @Test
    void testBuilderWithChapters() {
        ArrayList<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter.Builder(1, new ArrayList<>(), "Chapter 1").build());
        chapters.add(new Chapter.Builder(2, new ArrayList<>(), "Chapter 2").build());

        Law law = new Law.Builder(chapters).build();

        assertEquals(2, law.getChapters().size());
        assertEquals("Chapter 1", law.getChapters().get(0).getChapterTitle());
        assertEquals("Chapter 2", law.getChapters().get(1).getChapterTitle());
    }

    @Test
    void testGetTextWithNoChapters() {
        Law law = new Law.Builder(new ArrayList<>()).build();
        assertEquals("", law.getText());
    }

    @Test
    void testGetTextWithChapters() {
        ArrayList<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter.Builder(1, new ArrayList<>(), "Chapter 1").build());
        chapters.add(new Chapter.Builder(2, new ArrayList<>(), "Chapter 2").build());

        Law law = new Law.Builder(chapters).build();

        String text = law.getText();
        assertTrue(text.contains("Chapter 1"));
        assertTrue(text.contains("Chapter 2"));
    }

    @Test
    void testGetChildren() {
        ArrayList<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter.Builder(1, new ArrayList<>(), "Chapter 1").build());
        chapters.add(new Chapter.Builder(2, new ArrayList<>(), "Chapter 2").build());

        Law law = new Law.Builder(chapters).build();

        assertEquals(chapters, law.getChildren());
    }

    @Test
    void testToString() {
        Law law = new Law.Builder(new ArrayList<>()).build();
        assertEquals("Law", law.toString());
    }
}

// TODO Need to add Json test case