package de.sipgate.konschack.work_reflection_service.appCore;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import de.sipgate.konschack.work_reflection_service.aiCore.MyChatClient;

@Service
public class ReflectionProcessorService {
  final MyChatClient chatClient;
  final VectorStore vectorStore;

  public ReflectionProcessorService(MyChatClient chatClient, VectorStore vectorStore) {
    this.chatClient = chatClient;
    this.vectorStore = vectorStore;
  }

  public String process(String input) {
    System.out.println("Processing " + input);
    String reflection = chatClient.chat(input);
    vectorStore.add(List.of(new Document(reflection)));
    return reflection;
  }

  public List<String> findSimilar(String keyword) {
    return vectorStore.similaritySearch(keyword).stream().map(Document::toString).toList();
  }
}
