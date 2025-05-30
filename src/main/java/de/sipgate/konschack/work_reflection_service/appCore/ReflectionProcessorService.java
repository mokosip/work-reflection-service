package de.sipgate.konschack.work_reflection_service.appCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import de.sipgate.konschack.work_reflection_service.aiCore.MyChatClient;
import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;

@Service
public class ReflectionProcessorService {
  final MyChatClient chatClient;
  final VectorStore vectorStore;

  public ReflectionProcessorService(MyChatClient chatClient, VectorStore vectorStore) {
    this.chatClient = chatClient;
    this.vectorStore = vectorStore;
  }

  public Reflection process(ReflectionPrompt inputPrompt) {
    System.out.println("Processing " + inputPrompt);
    Reflection reflection = chatClient.chat(inputPrompt);

    persist(reflection);
    writeToMarkdownFile(reflection);
    return reflection;
  }

  public List<Reflection> findSimilar(String keyword) {
    System.out.println("#### similiarity search, disabled for now #### ' + keyword: " + keyword);
    SearchRequest searchRequest =
        SearchRequest.builder().query(keyword).topK(5).similarityThreshold(0.55).build();
    return Objects.requireNonNull(vectorStore.similaritySearch(searchRequest)).stream()
        .map(
            doc ->
                new Reflection(
                    LocalDate.parse(doc.getMetadata().get("date").toString()), doc.getText()))
        .toList();
  }

  public Reflection getReflectionForDate(LocalDate reflectionDate) {
    FilterExpressionBuilder b = new FilterExpressionBuilder();
    SearchRequest searchRequest =
        SearchRequest.builder()
            .filterExpression(b.eq("date", reflectionDate.toString()).build())
            .topK(1)
            .build();

    return new Reflection(
        reflectionDate, vectorStore.similaritySearch(searchRequest).getFirst().getText());
  }

  // TODO: FIX, GTE not working
  public List<Reflection> getReflectionsAfterDate(LocalDate reflectionDate) {
    FilterExpressionBuilder b = new FilterExpressionBuilder();
    SearchRequest searchRequest =
        SearchRequest.builder()
            .filterExpression(b.eq("date", reflectionDate.toString()).build())
            .topK(100)
            .build();

    return Objects.requireNonNull(vectorStore.similaritySearch(searchRequest)).stream()
        .map(
            doc ->
                new Reflection(
                    LocalDate.parse(doc.getMetadata().get("date").toString()), doc.getText()))
        .toList();
  }

  public Reflection getTodaysReflection() {
    return getReflectionForDate(LocalDate.now());
  }

  public List<Reflection> getAll() {
    return Objects.requireNonNull(vectorStore.similaritySearch("*")).stream()
        .map(
            doc ->
                new Reflection(
                    LocalDate.parse(doc.getMetadata().get("date").toString()), doc.getText()))
        .toList();
  }

  private void persist(Reflection reflection) {
    System.out.println("#### PERSIST REFLECTION RESULT FOR DATE #### " + reflection.date());
    Map<String, Object> metadata = Map.of("date", reflection.date().toString());
    vectorStore.add(
        Collections.singletonList(
            Document.builder().text(reflection.content()).metadata(metadata).build()));
  }

  private void writeToMarkdownFile(Reflection reflection) {
    try {
      // Create reflections directory if it doesn't exist
      Path reflectionsDir = Paths.get("reflections");
      if (!Files.exists(reflectionsDir)) {
        Files.createDirectories(reflectionsDir);
      }

      // Format the date for the filename
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String filename = "reflection-" + reflection.date().format(formatter) + ".md";
      Path filePath = reflectionsDir.resolve(filename);

      Files.writeString(filePath, reflection.content());
      System.out.println("Reflection written to file: " + filePath.toAbsolutePath());
    } catch (IOException e) {
      System.err.println("Error writing reflection to markdown file: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
