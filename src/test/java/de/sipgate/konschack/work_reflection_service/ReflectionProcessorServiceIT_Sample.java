package de.sipgate.konschack.work_reflection_service;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;
import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sample implementation of the first test method in ReflectionProcessorServiceIT. This demonstrates
 * how to implement the tests with proper assertions and cleanup.
 */
class ReflectionProcessorServiceIT_Sample extends IntegrationTestBase {
  @Autowired private ReflectionProcessorService componentUnderTest;

  @Test
  @DisplayName(
      "should store new Reflection Input (Prompt) as a Reflection Result in VectorStore for correct Date")
  void reflectionWithCorrectDate() throws IOException {
    // Arrange
    LocalDate testDate = LocalDate.now();
    String promptText = "Test reflection prompt for " + testDate;
    ReflectionPrompt prompt = new ReflectionPrompt(testDate, promptText);

    // Set the path for cleanup
    testFilePath =
        reflectionsDir.resolve(
            "reflection-" + testDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".md");

    // Act
    Reflection result = componentUnderTest.process(prompt);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals(testDate, result.date(), "Result date should match input date");
    assertFalse(result.content().isEmpty(), "Result content should not be empty");

    // Verify it was stored in vector store
    Reflection retrieved = componentUnderTest.getReflectionForDate(testDate);
    assertEquals(testDate, retrieved.date(), "Retrieved date should match input date");
    assertEquals(
        result.content(), retrieved.content(), "Retrieved content should match result content");

    // Verify markdown file was created
    assertTrue(Files.exists(testFilePath), "Markdown file should exist");
    String fileContent = Files.readString(testFilePath);
    assertEquals(result.content(), fileContent, "File content should match result content");
  }

  @Test
  @DisplayName(
      "should store second Reflection Input (Prompt) for same date as a Reflection Result in VectorStore, but not override the first one")
  void secondReflectionWithSameDate() throws IOException {
    // Arrange
    LocalDate testDate = LocalDate.now();
    String promptText1 = "First reflection prompt for " + testDate;
    String promptText2 = "Second reflection prompt for " + testDate;
    ReflectionPrompt prompt1 = new ReflectionPrompt(testDate, promptText1);
    ReflectionPrompt prompt2 = new ReflectionPrompt(testDate, promptText2);

    // Set the path for cleanup (we'll only clean up the second file)
    testFilePath =
        reflectionsDir.resolve(
            "reflection-" + testDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".md");

    // Act
    Reflection result1 = componentUnderTest.process(prompt1);
    Reflection result2 = componentUnderTest.process(prompt2);

    // Assert
    assertNotNull(result1, "First result should not be null");
    assertNotNull(result2, "Second result should not be null");

    // Verify both reflections are stored
    List<Reflection> allReflections = componentUnderTest.getAll();
    assertTrue(
        allReflections.stream()
            .anyMatch(r -> r.date().equals(testDate) && r.content().equals(result1.content())),
        "First reflection should be in the list of all reflections");
    assertTrue(
        allReflections.stream()
            .anyMatch(r -> r.date().equals(testDate) && r.content().equals(result2.content())),
        "Second reflection should be in the list of all reflections");

    // Verify getReflectionForDate returns one of them (likely the most recent)
    Reflection retrieved = componentUnderTest.getReflectionForDate(testDate);
    assertEquals(testDate, retrieved.date(), "Retrieved date should match input date");
    assertTrue(
        retrieved.content().equals(result1.content())
            || retrieved.content().equals(result2.content()),
        "Retrieved content should match one of the results");

    // Verify markdown file was created
    assertTrue(Files.exists(testFilePath), "Markdown file should exist");
  }
}
