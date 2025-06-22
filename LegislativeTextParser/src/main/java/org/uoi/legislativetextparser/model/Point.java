package org.uoi.legislativetextparser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Point implements Node {

    @JsonProperty("pointNumber")
    private int pointNumber;

    @JsonProperty("text")
    private String pointText;

    @JsonProperty("innerPoints")
    private ArrayList<Point> innerPoints;

    public Point(Builder builder) {
        this.pointNumber = builder.pointNumber;
        this.pointText = builder.pointText;
        this.innerPoints = builder.innerPoints;
    }

    public int getPointNumber() {
        return pointNumber;
    }

    public void setPointNumber(int pointNumber) {
        this.pointNumber = pointNumber;
    }


    public String getPointText() {
        return pointText;
    }

    public void setPointText(String pointText) {
        this.pointText = pointText;
    }

    public ArrayList<Point> getInnerPoints() {
        return innerPoints;
    }

    public void setInnerPoints(ArrayList<Point> innerPoints) {
        this.innerPoints = innerPoints;
    }

    @Override
    public String toString() {
        return "Point: " + pointNumber;
    }

    @JsonIgnore
    @Override
    public String getText() {
        return innerPoints != null ? getInnerPointsText() : pointText.trim();

    }

    private String getInnerPointsText() {
        StringBuilder text = new StringBuilder();
        for (Point point : innerPoints) {
            text.append(point.getText()).append("\n");
        }
        text.append("\n").append(pointText);
        return text.toString().trim();
    }

    @JsonIgnore
    @Override
    public String getTitle() {
        return String.valueOf(pointNumber);
    }

    @JsonIgnore
    @Override
    public ArrayList<Point> getChildren() {
        return innerPoints;
    }


    public static class Builder {

        private int pointNumber;
        private String pointText;

        private ArrayList<Point> innerPoints;

        public Builder(int pointNumber, String pointText, ArrayList<Point> innerPoints) {
            this.pointNumber = pointNumber;
            this.pointText = pointText;
            this.innerPoints = innerPoints;
        }

        public Builder(int pointNumber, String pointText) {
            this.pointNumber = pointNumber;
            this.pointText = pointText;
        }

        public Builder pointNumber(int pointNumber) {
            this.pointNumber = pointNumber;
            return this;
        }

        public Builder pointText(String pointText) {
            this.pointText = pointText;
            return this;
        }

        public Builder innerPoints(ArrayList<Point> innerPoints) {
            this.innerPoints = innerPoints;
            return this;
        }

        public Point build() {
            return new Point(this);
        }
    }
}
