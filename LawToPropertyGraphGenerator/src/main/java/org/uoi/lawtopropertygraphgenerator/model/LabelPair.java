package org.uoi.lawtopropertygraphgenerator.model;

public class LabelPair {
    private final String label1;
    private final String label2;
    private final double similarity;

    public LabelPair(String label1, String label2, double similarity) {
        this.label1 = label1;
        this.label2 = label2;
        this.similarity = similarity;
    }

    public String getLabel1() {
        return label1;
    }

    public String getLabel2() {
        return label2;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s) -> %.2f%%", label1, label2, similarity);
    }
}
