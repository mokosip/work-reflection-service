package de.sipgate.konschack.work_reflection_service.appCore.domain;

import java.time.LocalDate;
import java.util.Map;

public record ReflectionPrompt(LocalDate date, String prompt, Map<String, Object> options) {
  public ReflectionPrompt(LocalDate date, String prompt) {
    this(date, prompt, Map.of("itemCount", 3));
  }
}
