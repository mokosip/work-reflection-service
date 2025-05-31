package de.sipgate.konschack.work_reflection_service.aiCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;

@Component
public class MyChatClient {
  private static final Logger log = LoggerFactory.getLogger(MyChatClient.class);
  private final VectorStore simpleVectorStore;
  ChatClient chatClient;

  public MyChatClient(
      @Qualifier("ollamaChatClient") ChatClient chatClient, VectorStore simpleVectorStore) {
    this.chatClient = chatClient;
    this.simpleVectorStore = simpleVectorStore;
  }

  public Reflection chat(ReflectionPrompt prompt) {
    ChatClient.ChatClientRequestSpec request =
        chatClient
            .prompt(prompt.prompt())
            .advisors(
                QuestionAnswerAdvisor.builder(simpleVectorStore)
                    .searchRequest(
                        SearchRequest.builder().similarityThreshold(0.8d).topK(1).build())
                    .build());
    return new Reflection(prompt.date(), request.call().content());
  }
}
