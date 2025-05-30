package de.sipgate.konschack.work_reflection_service.appCore.domain;

import java.time.LocalDate;

public record ReflectionPrompt(LocalDate date, String prompt) {}
