package org.uoi.legislativetextparser.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EntityTest {

    @Test
    public void testBuilderCreatesEntityCorrectly() {
        
        String name = "AI system";
        String definition = "A machine-based system designed to operate with varying levels of autonomy.";
        Entity entity = new Entity.Builder(name, definition).build();

        assertEquals(name, entity.getName());
        assertEquals(definition, entity.getDefinition());
    }

    @Test
    public void testBuilderWithChainedMethods() {
        
        String name = "Provider";
        String definition = "An entity that supplies AI systems.";

        Entity entity = new Entity.Builder("", "")
                .name(name)
                .definition(definition)
                .build();

        assertEquals(name, entity.getName());
        assertEquals(definition, entity.getDefinition());
    }

    @Test
    public void testEntityWithNullValues() {
        
        Entity entity = new Entity.Builder(null, null).build();
        assertNull(entity.getName());
        assertNull(entity.getDefinition());
    }

    @Test
    public void testEntityWithEmptyValues() {
        
        Entity entity = new Entity.Builder("", "").build();
        assertEquals("", entity.getName());
        assertEquals("", entity.getDefinition());
    }

    @Test
    public void testBuilderUpdatesOnlyName() {
        
        String name = "Updated Name";
        String definition = "Original Definition";

        Entity entity = new Entity.Builder("Old Name", definition)
                .name(name)
                .build();

        assertEquals(name, entity.getName());
        assertEquals(definition, entity.getDefinition());
    }

    @Test
    public void testBuilderUpdatesOnlyDefinition() {
        
        String name = "Original Name";
        String definition = "Updated Definition";

        Entity entity = new Entity.Builder(name, "Old Definition")
                .definition(definition)
                .build();

        assertEquals(name, entity.getName());
        assertEquals(definition, entity.getDefinition());
    }
}
