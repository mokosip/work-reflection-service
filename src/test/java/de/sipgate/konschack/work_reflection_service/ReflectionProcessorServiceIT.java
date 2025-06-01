package de.sipgate.konschack.work_reflection_service;

import de.sipgate.konschack.work_reflection_service.config.TestContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;

@SpringBootTest
@Import(TestContainerConfig.class)
class ReflectionProcessorServiceIT {
  @Autowired private final ReflectionProcessorService componentUnderTest;

  public ReflectionProcessorServiceIT(ReflectionProcessorService componentUnderTest) {
    this.componentUnderTest = componentUnderTest;
  }

  @Nested
  class add {
    @Test
    @DisplayName(
        "should store new Reflection Input (Prompt) as a Reflection Result in VectorStore for correct Date")
    void reflectionWithCorrectDate() {}

    @Test
    @DisplayName(
        "should store second Reflection Input (Prompt) for same date as a Reflection Result in VectorStore, but not override the first one")
    void secondReflectionWithSameDate() {}
  }

  @Nested
  class get {
    @Test
    @DisplayName("should get (all) reflections for a specific date")
    void reflectionsForDate() {}
  }

  @Nested
  class list {
    @Test
    @DisplayName("should list all stored reflections without filtering")
    void allReflections() {}
  }

  @Nested
  class sim {
    @Test
    @DisplayName("should find all reflections that are similar to a specific keyword")
    void toSpecificKeyword() {}
  }
}
