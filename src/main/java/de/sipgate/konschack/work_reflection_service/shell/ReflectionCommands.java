package de.sipgate.konschack.work_reflection_service.shell;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Command-line interface for the Work Reflection Service. Provides commands for recording and
 * retrieving work reflections.
 */
@ShellComponent
public class ReflectionCommands {

  // Simple in-memory storage for reflections (in a real app, this would be a database)
  private final Map<LocalDate, String> reflections = new HashMap<>();
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
  private final ReflectionProcessorService reflectionProcessorService;

  public ReflectionCommands(ReflectionProcessorService reflectionProcessorService) {
    this.reflectionProcessorService = reflectionProcessorService;
  }

  @ShellMethod(key = "add", value = "Add a reflection for today or a specific date")
  public String addReflection(
      @ShellOption(help = "Your reflection text", defaultValue = "") String reflection,
      @ShellOption(help = "Date in format DD-MM-YY (defaults to today)", defaultValue = "")
          String date) {

    LocalDate reflectionDate =
        date.isEmpty() ? LocalDate.now() : LocalDate.parse(date, dateFormatter);

    if (reflection.isEmpty()) {
      return "Reflection text cannot be empty. Please provide some text.";
    }

    reflections.put(reflectionDate, reflection);
    return "Reflection added for " + reflectionDate.format(dateFormatter);
  }

  @ShellMethod(key = "get", value = "Get a reflection for a specific date")
  public String getReflection(
      @ShellOption(help = "Date in format DD-MM-YY (defaults to today)", defaultValue = "")
          String date) {

    LocalDate reflectionDate =
        date.isEmpty() ? LocalDate.now() : LocalDate.parse(date, dateFormatter);

    String input = reflections.get(reflectionDate);
    if (input == null) {
      return "No reflection found for " + reflectionDate.format(dateFormatter);
    }
    String processedReflection = reflectionProcessorService.process(input);
    System.out.println(processedReflection);

    return "Reflection for " + reflectionDate.format(dateFormatter) + ":\n" + input;
  }

  @ShellMethod(key = "list", value = "List all reflection dates")
  public String listReflections() {
    if (reflections.isEmpty()) {
      return "No reflections recorded yet.";
    }

    StringBuilder result = new StringBuilder("Recorded reflections:\n");

    reflections.keySet().stream()
        .sorted()
        .forEach(date -> result.append("- ").append(date.format(dateFormatter)).append("\n"));

    return result.toString();
  }

  @ShellMethod(key = "sim", value = "Find all similar reflections")
  public String listSimilarReflections(
      @ShellOption(help = "keyword you want to search for", defaultValue = "") String keyword) {

    if (reflections.isEmpty()) {
      return "No reflections recorded yet.";
    }

    StringBuilder result = new StringBuilder("Similar reflections:\n");
    List<String> allReflections = reflectionProcessorService.findSimilar(keyword);

    allReflections.stream().sorted().forEach(text -> result.append("- ").append(text).append("\n"));

    return result.toString();
  }

  @ShellMethod(key = "version", value = "Display the application version")
  public String version() {
    return "Work Reflection Service v0.1.0";
  }
}
