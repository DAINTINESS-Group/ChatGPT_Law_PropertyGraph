package org.uoi.lawtopropertygraphgenerator;
import java.nio.file.Paths;

public class TestUtils {

    public static String readResourceFile(String path) throws Exception {
        return Paths.get(TestUtils.class.getClassLoader().getResource(path).toURI()).toString();
    }
}
