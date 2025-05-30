package de.sipgate.konschack.work_reflection_service.appCore;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.sipgate.konschack.work_reflection_service.aiCore.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;
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

  public Reflection process(ReflectionPrompt inputPrompt) {
    System.out.println("Processing " + inputPrompt);
    Reflection reflection = chatClient.chat(inputPrompt);
    //    Map<String, Object> metadata = Map.of("date", String.valueOf(inputPrompt.date()));
    //    vectorStore.add(List.of(new Document(reflection., metadata)));
    return reflection;
  }

  public List<String> findSimilar(String keyword) {
    System.out.println("#### similiarity search, disabled for now #### ' + keyword: " + keyword);
    return Collections.emptyList();
    //    return vectorStore.similaritySearch(keyword).stream().map(Document::toString).toList();
  }

  public void register(String reflection) {
    System.out.println("#### REGISTER " + reflection);
    vectorStore.add(List.of(new Document(reflection)));
  }

  public String getReflection(LocalDate reflectionDate) {
    return vectorStore.similaritySearch(reflectionDate.toString()).getFirst().getText();
  }
}
