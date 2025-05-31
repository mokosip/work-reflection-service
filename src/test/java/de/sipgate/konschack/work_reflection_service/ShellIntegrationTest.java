package de.sipgate.konschack.work_reflection_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfig.class)
class ShellIntegrationTest {
  @BeforeEach
  void setUp() {}

  @Test
  void name() {
    System.out.println("Hello World");
    assert true;
  }
}
