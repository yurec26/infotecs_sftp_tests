import org.testng.TestNG;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class TestNGRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();

        URL testngXmlUrl = TestNGRunner.class.getClassLoader().getResource("testng.xml");
        if (testngXmlUrl == null) {
            System.err.println("Ошибка: testng.xml не найден в ресурсах JAR!");
            return;
        }
        try {
            Path tempFile = Files.createTempFile("testng", ".xml");
            try (InputStream is = testngXmlUrl.openStream()) {
                Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            testNG.setTestSuites(Collections.singletonList(tempFile.toAbsolutePath().toString()));
            testNG.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
