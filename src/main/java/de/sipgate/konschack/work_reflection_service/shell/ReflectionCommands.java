package de.sipgate.konschack.work_reflection_service.shell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;
import org.springframework.ai.document.Document;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;

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
    reflectionProcessorService.register(reflection);
    return "Reflection added for " + reflectionDate.format(dateFormatter);
  }

  @ShellMethod(key = "get", value = "Get a reflection for a specific date")
  public String getReflection(
      @ShellOption(help = "Date in format DD-MM-YY (defaults to today)", defaultValue = "")
          String date) {

    LocalDate reflectionDate =
        date.isEmpty() ? LocalDate.now() : LocalDate.parse(date, dateFormatter);

    String input = reflections.get(reflectionDate);
    String reflection = reflectionProcessorService.getReflection(reflectionDate);
    System.out.println("TRYING TO FETCH REFLECTION FOR " + reflectionDate + ": " + reflection);
    if (input == null || reflection == null) {
      return "No reflection found for " + reflectionDate.format(dateFormatter);
    }
    ReflectionPrompt inputPrompt =
        new ReflectionPrompt(reflectionDate, reflections.get(reflectionDate));
    Reflection processedReflection = reflectionProcessorService.process(inputPrompt);
    System.out.println(processedReflection);

    return "Reflection for " + reflectionDate.format(dateFormatter) + ":\n" + input;
  }

  @ShellMethod(key = "list", value = "List all content dates")
  public String listReflections() {
    System.out.println("LISTING ALL REFLECTIONS BY * QUERY" + reflectionProcessorService.getAll());
    List<Document> reflections = reflectionProcessorService.getTodaysReflection();
    //    if (reflections.isEmpty()) {
    //      return "No local/ in memory reflections recorded yet.";
    //    }
    //
    //
    //    StringBuilder result = new StringBuilder("Recorded reflections:\n");
    //
    //    reflections.stream()
    //        .sorted()
    //        .forEach(doc -> result.append("- ").append(doc.getText()).append("\n"));
    //
    //    return result.toString();
    return "";
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
