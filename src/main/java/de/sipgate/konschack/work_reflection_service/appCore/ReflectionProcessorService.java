package de.sipgate.konschack.work_reflection_service.appCore;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ReflectionProcessorService {
  final de.sipgate.konschack.work_reflection_service.aiCore.OllamaApiClient ollamaApiClient;
  final VectorStore vectorStore;

  public ReflectionProcessorService(
      de.sipgate.konschack.work_reflection_service.aiCore.OllamaApiClient ollamaApiClient,
      VectorStore vectorStore) {
    this.ollamaApiClient = ollamaApiClient;
    this.vectorStore = vectorStore;
  }

  public String process(String input) {
    System.out.println("Processing " + input);
    String reflection = ollamaApiClient.chat(input);
    Document document = new Document(reflection);
    vectorStore.add(List.of(document));
    return reflection;
  }

  private void testVectorStore() {
    System.out.println("Testing vector store");
    List<Document> documents =
        List.of(
            new Document(
                "Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!",
                Map.of("meta1", "meta1")),
            new Document("The World is Big and Salvation Lurks Around the Corner"),
            new Document(
                "You walk forward facing the past and you turn back toward the future.",
                Map.of("meta2", "meta2")));

    vectorStore.add(documents);
    List<Document> results = vectorStore.similaritySearch("Spring");
    System.out.println(results);
  }

  public List<String> findSimilar(String keyword) {
    return vectorStore.similaritySearch(keyword).stream().map(Document::toString).toList();
  }
}
