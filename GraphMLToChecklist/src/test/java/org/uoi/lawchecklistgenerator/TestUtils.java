package org.uoi.lawchecklistgenerator;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {

    public static String readResourceFile(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(
                TestUtils.class.getClassLoader().getResource(path).toURI()
        )));
    }
}
