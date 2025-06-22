package org.uoi.legislativetextparser.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphTest {

    @Test
    void testBuilderCreatesParagraphSuccessfully() {
        Paragraph paragraph = new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build();

        assertEquals(1, paragraph.getParagraphNumber());
        assertEquals("1.1.1", paragraph.getParagraphID());
        assertNotNull(paragraph.getParagraphPoints());
        assertTrue(paragraph.getParagraphPoints().isEmpty());
    }

    @Test
    void testBuilderWithParagraphPoints() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point.Builder(1, "Point 1 text").build());
        points.add(new Point.Builder(2, "Point 2 text").build());

        Paragraph paragraph = new Paragraph.Builder(1, points, "1.1.1").build();

        assertEquals(2, paragraph.getParagraphPoints().size());
        assertEquals("Point 1 text", paragraph.getParagraphPoints().get(0).getPointText());
        assertEquals("Point 2 text", paragraph.getParagraphPoints().get(1).getPointText());
    }

    @Test
    void testGetTextWithNoPoints() {
        Paragraph paragraph = new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build();
        assertEquals("", paragraph.getText());
    }

    @Test
    void testGetTextWithPoints() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point.Builder(1, "Point 1 text").build());
        points.add(new Point.Builder(2, "Point 2 text").build());

        Paragraph paragraph = new Paragraph.Builder(1, points, "1.1.1").build();

        String expectedText = "Point 1 text\nPoint 2 text";
        assertEquals(expectedText, paragraph.getText());
    }

    @Test
    void testGetChildren() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point.Builder(1, "Point 1 text").build());
        points.add(new Point.Builder(2, "Point 2 text").build());

        Paragraph paragraph = new Paragraph.Builder(1, points, "1.1.1").build();

        assertEquals(points, paragraph.getChildren());
    }

    @Test
    void testToString() {
        Paragraph paragraph = new Paragraph.Builder(1, new ArrayList<>(), "1.1.1").build();
        assertEquals("Paragraph: 1.1.1", paragraph.toString());
    }
}

// TODO Need to add Json test case