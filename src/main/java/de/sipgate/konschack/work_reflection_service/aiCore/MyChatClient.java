package de.sipgate.konschack.work_reflection_service.aiCore;

import java.util.Collections;
import java.util.Map;

import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
                    // .searchRequest(SearchRequest.builder().filterExpression("filterExpr").build())
                    .build());
    System.out.println("#### REQUEST with QA-ADVISOR: ####");
    System.out.println(request.call().chatClientResponse().chatResponse());
    Reflection reflection = new Reflection(prompt.date(), request.call().content());
    return reflection;
  }
}
