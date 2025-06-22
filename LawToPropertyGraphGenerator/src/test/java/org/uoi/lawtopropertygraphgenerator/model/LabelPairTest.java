package org.uoi.lawtopropertygraphgenerator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LabelPairTest {

    @Test
    void testLabelPairGetters() {
        LabelPair pair = new LabelPair("LabelA", "LabelB", 85.5);

        assertEquals("LabelA", pair.getLabel1());
        assertEquals("LabelB", pair.getLabel2());
        assertEquals(85.5, pair.getSimilarity());
    }

    @Test
    void testLabelPairToString() {
        LabelPair pair = new LabelPair("LabelA", "LabelB", 85.5);
        String expected = "(LabelA, LabelB) -> 85.50%";

        assertEquals(expected, pair.toString());
    }
}