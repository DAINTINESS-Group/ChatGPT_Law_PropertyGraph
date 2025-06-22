package org.uoi.lawtopropertygraphgenerator.model.law;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Entity {

    @JsonProperty("name")
    private String name;

    @JsonProperty("definition")
    private String definition;

    public Entity(String name, String definition) {
        this.name = name;
        this.definition = definition;
    }

    public Entity() {}

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

}
