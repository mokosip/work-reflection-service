package de.sipgate.konschack.work_reflection_service.shell;

import java.time.LocalDate;
import java.util.List;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import de.sipgate.konschack.work_reflection_service.appCore.ReflectionProcessorService;
import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;

/**
 * Command-line interface for the Work Reflection Service. Provides commands for recording and
 * retrieving work reflections.
 */
@ShellComponent
public class ReflectionCommands {
  private final ReflectionProcessorService reflectionProcessorService;

  public ReflectionCommands(ReflectionProcessorService reflectionProcessorService) {
    this.reflectionProcessorService = reflectionProcessorService;
  }

  @ShellMethod(key = "add", value = "Add a reflection for today or a specific date")
  public String addReflection(
      @ShellOption(help = "Your reflection text", defaultValue = "") String reflection,
      @ShellOption(help = "Date in format DD-MM-YY (defaults to today)", defaultValue = "")
          LocalDate date) {

    LocalDate reflectionDate = date == null ? LocalDate.now() : date;

    if (reflection.isEmpty()) {
      return "Reflection text cannot be empty. Please provide some text.";
    }

    ReflectionPrompt inputPrompt = new ReflectionPrompt(reflectionDate, reflection);
    reflectionProcessorService.process(inputPrompt);
    return "Reflection added for " + reflectionDate;
  }

  @ShellMethod(key = "get", value = "Get a reflection for a specific date")
  public String getReflection(
      @ShellOption(help = "Date in format YY-MM-DD (defaults to today)", defaultValue = "")
          LocalDate date) {
    LocalDate reflectionDate = date == null ? LocalDate.now() : date;
    Reflection reflectionForDate = reflectionProcessorService.getReflectionForDate(reflectionDate);

    return "Reflection for "
        + reflectionDate
        + " found actual reflection with date: "
        + reflectionForDate.date()
        + ":\n"
        + reflectionForDate.content();
  }

  @ShellMethod(key = "list", value = "List all reflection dates")
  public String listReflections(
      @ShellOption(
              help = "Indicates whether to list all reflections after a certain date",
              defaultValue = "")
          LocalDate after) {
    List<Reflection> reflections =
        after != null
            ? reflectionProcessorService.getReflectionsAfterDate(after)
            : reflectionProcessorService.getAll();
    List<String> recordedDates = reflections.stream().map(ref -> ref.date().toString()).toList();
    StringBuilder result = new StringBuilder("Recorded reflections:\n");
    recordedDates.forEach(date -> result.append("- ").append(date).append("\n"));
    return result.toString();
  }

  @ShellMethod(key = "sim", value = "Find all similar reflections")
  public String listSimilarReflections(
      @ShellOption(help = "keyword you want to search for", defaultValue = "") String keyword) {

    StringBuilder result = new StringBuilder("Similar reflections:\n");
    List<Reflection> allReflections = reflectionProcessorService.findSimilar(keyword);
    allReflections.forEach(
        reflection -> result.append("- ").append(reflection.date()).append("\n"));
    return result.toString();
  }

  @ShellMethod(key = "version", value = "Display the application version")
  public String version() {
    return "Work Reflection Service v0.1.0";
  }
}
