package de.sipgate.konschack.work_reflection_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest(
    properties = {
      "spring.shell.interactive.enabled=false",
      "spring.shell.command.script.enabled=false"
    })
@Import(TestContainerConfig.class)
public class IntegrationTestBase {
  @BeforeEach
  void setUp() {
    testDate = LocalDate.now();
    testFilePath =
        reflectionsDir.resolve(
            "reflection-" + testDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".md");
  }

  @AfterEach
  void cleanup() throws IOException {
    // Clean up any test files created during the test
    if (testFilePath != null && Files.exists(testFilePath)) {
      Files.delete(testFilePath);
      System.out.println("Cleanup: Deleted test file: " + testFilePath);
    }
  }

  final Path reflectionsDir = Paths.get("reflections");
  Path testFilePath;
  LocalDate testDate;
}
