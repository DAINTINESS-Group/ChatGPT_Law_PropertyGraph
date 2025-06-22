package org.uoi.lawtopropertygraphgenerator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnionFindTest {

    @Test
    void testFindSelf() {
        UnionFind uf = new UnionFind(5);

        for (int i = 0; i < 5; i++) {
            assertEquals(i, uf.find(i));
        }
    }

    @Test
    void testUnionAndFind() {
        UnionFind uf = new UnionFind(5);

        uf.union(0, 1);
        assertEquals(uf.find(0), uf.find(1));

        uf.union(1, 2);
        assertEquals(uf.find(0), uf.find(2));

        assertNotEquals(uf.find(0), uf.find(3));
    }

    @Test
    void testUnionByRank() {
        UnionFind uf = new UnionFind(3);

        uf.union(0, 1);
        uf.union(1, 2);

        int root0 = uf.find(0);
        int root1 = uf.find(1);
        int root2 = uf.find(2);

        assertEquals(root0, root1);
        assertEquals(root1, root2);
    }
}