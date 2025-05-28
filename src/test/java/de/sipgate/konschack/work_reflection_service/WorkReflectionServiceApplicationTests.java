package de.sipgate.konschack.work_reflection_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WorkReflectionServiceApplicationTests.TestApplication.class)
class WorkReflectionServiceApplicationTests {

  @Test
  void contextLoads() {}

  @SpringBootApplication(scanBasePackages = "de.sipgate.konschack.work_reflection_service.shell")
  static class TestApplication {}
}
