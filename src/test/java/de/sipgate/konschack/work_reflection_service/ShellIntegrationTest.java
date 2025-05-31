package de.sipgate.konschack.work_reflection_service;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;
import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.shell.ReflectionCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ShellIntegrationTest extends IntegrationTestBase {
  @Autowired private ReflectionCommands reflectionCommands;

  @Autowired private ReflectionProcessorService reflectionProcessorService;

  @Test
  @DisplayName("should add a reflection using the add command")
  void addReflection() {
    // Arrange
    String reflectionText = "Test reflection from shell command";

    // Act
    String result = reflectionCommands.addReflection(reflectionText, testDate);

    // Assert
    assertTrue(
        result.contains("Reflection added for " + testDate),
        "Result should confirm reflection was added");

    // Verify reflection was stored
    Reflection retrieved = reflectionProcessorService.getReflectionForDate(testDate);
    assertEquals(testDate, retrieved.date(), "Retrieved date should match test date");
    assertFalse(retrieved.content().isEmpty(), "Retrieved content should not be empty");

    // Verify file was created
    assertTrue(Files.exists(testFilePath), "Markdown file should exist");
  }

  @Test
  @DisplayName("should get a reflection using the get command")
  void getReflection() {
    // Arrange - Add a reflection first
    String reflectionText = "Test reflection for get command";
    reflectionCommands.addReflection(reflectionText, testDate);

    // Act
    String result = reflectionCommands.getReflection(testDate);

    // Assert
    assertTrue(result.contains("Reflection for " + testDate), "Result should contain the date");
    assertTrue(
        result.contains("found actual reflection with date: " + testDate),
        "Result should confirm the reflection was found");

    assertFalse(result.isEmpty(), "Result should not be empty");
  }

  @Test
  @DisplayName("should list all reflections using the list command")
  void listReflections() {
    // Arrange - Add a reflection first
    String reflectionText = "Test reflection for list command";
    String otherReflectionText = "Another test reflection for list command";
    LocalDate otherDate = LocalDate.now().minusDays(1);
    reflectionCommands.addReflection(reflectionText, testDate);
    reflectionCommands.addReflection(otherReflectionText, otherDate);

    // Act
    String result = reflectionCommands.listReflections(null);

    // Assert
    assertTrue(result.contains("Recorded reflections:"), "Result should have the header");
    assertTrue(result.contains("- " + testDate), "Result should list the test date");
  }

  @Test
  @DisplayName("should find similar reflections using the sim command")
  void findSimilarReflections() {
    // Arrange - Add a reflection with specific content
    String reflectionText = "Test reflection about programming and Java";
    String otherReflectionText = "This reflection has nothing to do with the search keyword";
    reflectionCommands.addReflection(reflectionText, testDate);
    LocalDate otherDate = LocalDate.now().minusDays(1);
    reflectionCommands.addReflection(otherReflectionText, testDate);

    // Act
    String result = reflectionCommands.listSimilarReflections("programming");

    // Assert
    assertTrue(result.contains("Similar reflections:"), "Result should have the header");
    // The exact matches will depend on the vector search, but we can check the format
    assertFalse(result.isEmpty(), "Result should not be empty");
    assertTrue(
        result.contains(testDate.toString()),
        "Result should contain the date of the similar reflection");
    assertFalse(result.contains(otherDate.toString()), "Result should not contain the other date");
  }

  @Test
  @DisplayName("should return version information")
  void getVersion() {
    // Act
    String result = reflectionCommands.version();

    // Assert
    assertTrue(
        result.contains("Work Reflection Service"), "Result should contain the service name");
    assertTrue(result.contains("v"), "Result should contain a version number");
  }

  @Test
  @DisplayName("should handle empty reflection text")
  void handleEmptyReflection() {
    // Act
    String result = reflectionCommands.addReflection("", testDate);

    // Assert
    assertTrue(
        result.contains("Reflection text cannot be empty"),
        "Result should contain error message about empty text");

    // Verify no file was created
    assertFalse(Files.exists(testFilePath), "No markdown file should be created");
  }
}
