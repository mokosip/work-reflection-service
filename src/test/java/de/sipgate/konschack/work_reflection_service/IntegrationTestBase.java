package de.sipgate.konschack.work_reflection_service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.sipgate.konschack.work_reflection_service.config.TestContainerConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(
    properties = {
      "spring.shell.interactive.enabled=false",
      "spring.shell.command.script.enabled=false"
    })
@Import({TestContainerConfig.class})
public class IntegrationTestBase {
  @Value("${output.filePath:./pathNotFound}") // Provide default value
  String outputPath;

  Path reflectionsDir;
  Path testFilePath;
  Path otherFilePath;

  LocalDate testDate = LocalDate.EPOCH;
  LocalDate otherDate = testDate.plusDays(1);

  @BeforeEach
  void setUp() {
    reflectionsDir = Paths.get(outputPath);
    try {
      Files.createDirectories(reflectionsDir);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create test directories", e);
    }
    testFilePath = getTestFilePath(testDate);
    otherFilePath = getTestFilePath(otherDate);
  }

  private @NotNull Path getTestFilePath(LocalDate date) {
    return reflectionsDir.resolve(
        "reflection-" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".md");
  }

  @AfterEach
  void cleanup() throws IOException {
    // Clean up any test files created during the test
    if (testFilePath != null && Files.exists(testFilePath)) {
      Files.delete(testFilePath);
      System.out.println("Cleanup: Deleted test file: " + testFilePath);
    }
    if (otherFilePath != null && Files.exists(otherFilePath)) {
      Files.delete(otherFilePath);
      System.out.println("Cleanup: Deleted test file: " + otherFilePath);
    }
  }
}
