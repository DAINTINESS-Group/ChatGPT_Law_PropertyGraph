package org.uoi.legislativetextparser.textprocessing;

import org.uoi.legislativetextparser.model.Point;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PointSplitter {

    /**
     * Splits the paragraph into points and handles nested subpoints.
     *
     * @param paragraph The paragraph to be split into points.
     * @return A list of Point objects representing the structure.
     */
    public static ArrayList<Point> splitIntoPoints(String paragraph) {
        ArrayList<Point> points = new ArrayList<>();
        int pointCounter = 1;

        Pattern topLevelPattern = Pattern.compile("(?<=\\n)\\([a-u]\\)\\s");
        Matcher topLevelMatcher = topLevelPattern.matcher(paragraph);

        int start = 0;
        while (topLevelMatcher.find()) {
            int end = topLevelMatcher.start();
            String pointText = paragraph.substring(start, end).trim();

            if (!pointText.isEmpty()) {
                ArrayList<Point> subPoints = extractSubPoints(paragraph.substring(start, end));

                StringBuilder fullText = new StringBuilder(pointText);
                for (Point subPoint : subPoints) {
                    fullText.append("\n").append(subPoint.getPointText());
                }
                points.add(new Point.Builder(pointCounter++, fullText.toString())
                        .innerPoints(subPoints)
                        .build());
            }
            start = end;
        }

        if (start < paragraph.length()) {
            String remainingText = paragraph.substring(start).trim();
            if (!remainingText.isEmpty()) {
                points.add(new Point.Builder(pointCounter++, remainingText).build());
            }
        }
        return points;
    }


    /**
     * Extracts subpoints (e.g., (i), (ii), etc.) from the given text.
     */
    public static ArrayList<Point> extractSubPoints(String text) {
        ArrayList<Point> subPoints = new ArrayList<>();
        int subPointCounter = 1;

        Pattern subPointPattern = Pattern.compile("(?<=\\n)\\(([ivxlc]+)\\)\\s");
        Matcher subPointMatcher = subPointPattern.matcher(text);

        int start = 0;
        while (subPointMatcher.find()) {
            int end = subPointMatcher.start();
            String subPointText = text.substring(start, end).trim();

            if (!subPointText.equals(text.trim()) && !subPointText.isEmpty()) {
                subPoints.add(new Point.Builder(subPointCounter++, subPointText).build());
            }

            start = end;
        }

        if (start < text.length()) {
            String remainingText = text.substring(start).trim();
            if (!remainingText.isEmpty() && !remainingText.equals(text.trim())) {
                subPoints.add(new Point.Builder(subPointCounter++, remainingText).build());
            }
        }
        return subPoints;
    }
}
