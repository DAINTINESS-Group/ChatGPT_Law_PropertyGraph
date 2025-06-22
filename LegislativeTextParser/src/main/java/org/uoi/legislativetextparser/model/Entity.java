package org.uoi.legislativetextparser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Entity {

    @JsonProperty("name")
    private String name;

    @JsonProperty("definition")
    private String definition;

    public Entity(Builder builder) {
        this.name = builder.name;
        this.definition = builder.definition;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public static class Builder {
        private String name;
        private String definition;

        public Builder(String name, String definition) {
            this.name = name;
            this.definition = definition;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder definition(String definition) {
            this.definition = definition;
            return this;
        }

        public Entity build() {return new Entity(this);}
    }
}
