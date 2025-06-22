package org.uoi.legislativetextparser.config;

public class Config {

    private String lawPdfPath;
    private String lawJsonPath;
    private static final String CHAPTERS_DIR = "src/main/resources/output/chapters/";
    private static final String SELECTED_LAW = "src/main/resources/output/selectedLaw.txt";
    private static final String CLEANED_LAW = "src/main/resources/output/cleanedSelectedLaw.txt";

    public Config(String lawPdfPath, String lawJsonPath) {
        this.lawPdfPath = lawPdfPath;
        this.lawJsonPath = lawJsonPath;
    }

    public String getLawPdfPath() {
        return lawPdfPath;
    }

    public void setLawPdfPath(String lawPdfPath) {
        this.lawPdfPath = lawPdfPath;
    }

    public String getLawJsonPath() {
        return lawJsonPath;
    }

    public void setLawJsonPath(String lawJsonPath) {
        this.lawJsonPath = lawJsonPath;
    }

    public static String getChaptersDir() {
        return CHAPTERS_DIR;
    }

    public static String getSelectedLaw() {
        return SELECTED_LAW;
    }

    public static String getCleanedLaw() {
        return CLEANED_LAW;
    }
}
