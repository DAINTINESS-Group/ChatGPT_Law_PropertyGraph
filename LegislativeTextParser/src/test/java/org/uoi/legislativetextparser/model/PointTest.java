package org.uoi.legislativetextparser.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PointTest {

    @Test
    void testBuilderCreatesPointSuccessfully() {
        Point point = new Point.Builder(1, "Main point text").build();
        assertEquals(1, point.getPointNumber());
        assertEquals("Main point text", point.getPointText());
        assertNull(point.getInnerPoints());
    }

    @Test
    void testBuilderWithInnerPoints() {
        ArrayList<Point> innerPoints = new ArrayList<>();
        innerPoints.add(new Point.Builder(2, "Inner point text").build());
        Point point = new Point.Builder(1, "Main point text", innerPoints).build();

        assertEquals(1, point.getPointNumber());
        assertEquals("Main point text", point.getPointText());
        assertEquals(1, point.getInnerPoints().size());
        assertEquals("Inner point text", point.getInnerPoints().get(0).getPointText());
    }

    @Test
    void testGetTextWithNoInnerPoints() {
        Point point = new Point.Builder(1, "Main point text").build();
        assertEquals("Main point text", point.getText());
    }

    @Test
    void testGetTextWithInnerPoints() {
        ArrayList<Point> innerPoints = new ArrayList<>();
        innerPoints.add(new Point.Builder(2, "Inner point 1 text").build());
        innerPoints.add(new Point.Builder(3, "Inner point 2 text").build());
        Point point = new Point.Builder(1, "Main point text", innerPoints).build();

        String expectedText = "Inner point 1 text\nInner point 2 text\n\nMain point text";
        assertEquals(expectedText, point.getText());
    }

    @Test
    void testGetChildren() {
        ArrayList<Point> innerPoints = new ArrayList<>();
        innerPoints.add(new Point.Builder(2, "Inner point 1").build());
        Point point = new Point.Builder(1, "Main point", innerPoints).build();

        assertEquals(innerPoints, point.getChildren());
    }

    @Test
    void testToString() {
        Point point = new Point.Builder(1, "Main point text").build();
        assertEquals("Point: 1", point.toString());
    }

    @Test
    void testEmptyInnerPoints() {
        Point point = new Point.Builder(1, "Main point text", new ArrayList<>()).build();
        assertNotNull(point.getInnerPoints());
        assertTrue(point.getInnerPoints().isEmpty());
    }

    @Test
    void testNullInnerPoints() {
        Point point = new Point.Builder(1, "Main point text", null).build();
        assertNull(point.getInnerPoints());
    }

    @Test
    void testNullPointText() {
        Point point = new Point.Builder(1, null).build();
        assertNull(point.getPointText());
    }

}

// TODO Need to add Json test case