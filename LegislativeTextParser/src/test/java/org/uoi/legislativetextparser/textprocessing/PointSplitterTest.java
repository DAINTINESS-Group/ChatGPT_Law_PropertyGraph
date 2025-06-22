package org.uoi.legislativetextparser.textprocessing;

import org.junit.jupiter.api.Test;
import org.uoi.legislativetextparser.model.Point;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PointSplitterTest {

    @Test
    void testSplitIntoPointsWithTopLevelAndNestedPoints() {
        String paragraph = """
            (a) Main point A text.\n
            (i) Nested point A.1.\n
            (ii) Nested point A.2.\n
            (b) Main point B text.\n
            """;

        ArrayList<Point> points = PointSplitter.splitIntoPoints(paragraph);

        assertNotNull(points, "Points list should not be null");
        assertEquals(3, points.size(), "There should be two top-level points");

        Point pointA = points.get(1);
        assertEquals("(i) Nested point A.1.\n\n(ii) Nested point A.2.\n(i) Nested point A.1.\n(ii) Nested point A.2.", pointA.getPointText(), "Top-level point A text mismatch");

        ArrayList<Point> nestedPointsA = pointA.getInnerPoints();
        assertNotNull(nestedPointsA, "Nested points for point A should not be null");
        assertEquals(2, nestedPointsA.size(), "Point A should have two nested points");
        assertEquals("(i) Nested point A.1.", nestedPointsA.get(0).getPointText(), "First nested point of A mismatch");
        assertEquals("(ii) Nested point A.2.", nestedPointsA.get(1).getPointText(), "Second nested point of A mismatch");

        Point pointB = points.get(2);
        assertEquals("(b) Main point B text.", pointB.getPointText(), "Top-level point B text mismatch");
        assertNull(pointB.getInnerPoints(), "Point B should not have nested points");
    }

    @Test
    void testSplitIntoPointsWithEmptyParagraph() {
        String paragraph = "";

        ArrayList<Point> points = PointSplitter.splitIntoPoints(paragraph);

        assertNotNull(points, "Points list should not be null");
        assertTrue(points.isEmpty(), "Points list should be empty for an empty paragraph");
    }


    @Test
    void testExtractSubPointsWithNestedPoints() {
        String subpointText = """
            (i) Subpoint one text.\n
            (ii) Subpoint two text.\n
            (iii) Subpoint three text.\n
            """;

        ArrayList<Point> subPoints = PointSplitter.extractSubPoints(subpointText);

        assertNotNull(subPoints, "Subpoints list should not be null");
        assertEquals(3, subPoints.size(), "There should be three subpoints");
        assertEquals("(i) Subpoint one text.", subPoints.get(0).getPointText(), "First subpoint text mismatch");
        assertEquals("(ii) Subpoint two text.", subPoints.get(1).getPointText(), "Second subpoint text mismatch");
        assertEquals("(iii) Subpoint three text.", subPoints.get(2).getPointText(), "Third subpoint text mismatch");
    }
}
