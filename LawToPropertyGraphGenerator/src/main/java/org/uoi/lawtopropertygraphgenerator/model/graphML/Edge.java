package org.uoi.lawtopropertygraphgenerator.model.graphML;

public class    Edge {

    private String source;
    private String target;
    private String relationship;
    private String paragraphRef;

    public Edge(String source, String target, String relationship, String paragraphRef) {
        this.source = source;
        this.target = target;
        this.relationship = relationship;
        this.paragraphRef = paragraphRef;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getParagraphRef() {
        return paragraphRef;
    }

    public void setParagraphRef(String paragraphRef) {
        this.paragraphRef = paragraphRef;
    }

    @Override
    public String toString() {
        return "Edge{source='" + source + "', target='" + target + "', label='" + relationship + "', paragraphReference='" + paragraphRef + "'}";
    }
}
