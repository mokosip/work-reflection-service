package de.sipgate.konschack.work_reflection_service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;
import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;

class ReflectionProcessorServiceIT extends IntegrationTestBase {
  @Autowired private ReflectionProcessorService componentUnderTest;

  @Nested
  class add {
    @Test
    @DisplayName(
        "should store new Reflection Input (Prompt) as a Reflection Result in VectorStore for correct Date")
    void reflectionWithCorrectDate() throws IOException {
      // Arrange
      String promptText = "Test reflection prompt";
      ReflectionPrompt prompt = new ReflectionPrompt(testDate, promptText);

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
      String promptText1 = "First reflection prompt for " + testDate;
      String promptText2 = "Second reflection prompt for " + testDate;
      ReflectionPrompt prompt1 = new ReflectionPrompt(testDate, promptText1);
      ReflectionPrompt prompt2 = new ReflectionPrompt(testDate, promptText2);

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

  @Nested
  class list {
    @Test
    @DisplayName("should list all stored reflections without filtering")
    void allReflections() {
      // Arrange
      String promptText1 = "First reflection prompt for " + testDate;
      String promptText2 = "Second reflection prompt for " + testDate;
      ReflectionPrompt prompt1 = new ReflectionPrompt(testDate, promptText1);
      ReflectionPrompt prompt2 = new ReflectionPrompt(testDate, promptText2);
      componentUnderTest.process(prompt1);
      componentUnderTest.process(prompt2);

      // Act
      List<Reflection> allReflections = componentUnderTest.getAll();
      assertEquals(2, allReflections.size(), "Should have two reflections");
    }
  }

  @DisplayName("Might be flappy!")
  @Nested
  class sim {
    @Test
    @DisplayName("should find all reflections that are similar to a specific keyword")
    void toSpecificKeyword() {
      // Arrange - Add a reflection with specific content
      String promptToFind = "Something about programming";
      String otherPrompt = "Something about cats";
      ReflectionPrompt similarPrompt = new ReflectionPrompt(testDate, promptToFind);
      ReflectionPrompt other = new ReflectionPrompt(otherDate, otherPrompt);
      componentUnderTest.process(similarPrompt);
      componentUnderTest.process(other);

      // Act
      List<Reflection> result = componentUnderTest.findSimilar("programming");

      // Assert
      assertEquals(1, result.size(), "Should have exactly one reflection");
      assertEquals(testDate, result.getFirst().date(), "Should have exactly one reflection");
      assertFalse(otherDate.equals(result.getFirst().date()), "Should have exactly one reflection");
    }
  }
}
