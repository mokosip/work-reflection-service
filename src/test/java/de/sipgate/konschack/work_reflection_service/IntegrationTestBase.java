package de.sipgate.konschack.work_reflection_service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import de.sipgate.konschack.work_reflection_service.config.TestContainerConfig;

@SpringBootTest(
    properties = {
      "spring.shell.interactive.enabled=false",
      "spring.shell.command.script.enabled=false"
    })
@Import({TestContainerConfig.class})
public class IntegrationTestBase {
  @Autowired private ChromaApi chromaApi;

  @Value("${spring.ai.vectorstore.chroma.collection-name}")
  private String collectionName;

  @Value("${output.filePath:./pathNotFound}") // Provide default value
  String outputPath;

  Path reflectionsDir;
  Path testFilePath;
  Path otherFilePath;

  LocalDate testDate = LocalDate.EPOCH;
  LocalDate otherDate = testDate.plusDays(1);
  @Autowired private VectorStore vectorStore;

  @BeforeEach
  void setUp() {
    reflectionsDir = Paths.get(outputPath);
    try {
      Files.createDirectories(reflectionsDir);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create test directories", e);
    }
    testFilePath = getTestFilePath(testDate);
    otherFilePath = getTestFilePath(otherDate);
  }

  private @NotNull Path getTestFilePath(LocalDate date) {
    return reflectionsDir.resolve(
        "reflection-" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".md");
  }

  @AfterEach
  void cleanup() throws IOException {
    deleteMarkdownFiles();
    clearVectorStoreEmbeddings();
  }

  private void clearVectorStoreEmbeddings() {
    try {
      System.out.println("Clearing vector store collection: " + collectionName);
      int bucketSize = 100;
      String springAiTenant = "SpringAiTenant";
      String springAiDatabase = "SpringAiDatabase";
      String collectionId =
          Objects.requireNonNull(
                  chromaApi.getCollection(springAiTenant, springAiDatabase, collectionName))
              .id();
      // Create a search request that will return all documents
      SearchRequest searchRequest =
          SearchRequest.builder().similarityThreshold(0.0).topK(bucketSize).build();
      List<Document> documents = vectorStore.similaritySearch(searchRequest);
      Assertions.assertNotNull(documents);
      List<String> ids = documents.stream().map(Document::getId).toList();

      if (!documents.isEmpty()) {
        System.out.println("Found " + documents.size() + " document(s) to clear ...");

        ChromaApi.DeleteEmbeddingsRequest deleteRequest =
            new ChromaApi.DeleteEmbeddingsRequest(ids);
        int i =
            chromaApi.deleteEmbeddings(
                springAiTenant, springAiDatabase, collectionId, deleteRequest);
        if (i == 200) {
          System.out.println("Deleted " + documents.size() + " document(s)");
        } else {
          System.out.println("Failed to delete " + documents.size() + " document(s)");
        }
      } else {
        System.out.println("No documents found in collection");
      }
    } catch (Exception e) {
      System.out.println("Warning during collection cleanup: " + e.getMessage());
    }
  }

  private void deleteMarkdownFiles() throws IOException {
    // Clean up any test files created during the test
    if (testFilePath != null && Files.exists(testFilePath)) {
      Files.delete(testFilePath);
      System.out.println("Cleanup: Deleted test file: " + testFilePath);
    }
    if (otherFilePath != null && Files.exists(otherFilePath)) {
      Files.delete(otherFilePath);
      System.out.println("Cleanup: Deleted test file: " + otherFilePath);
    }
  }
}
