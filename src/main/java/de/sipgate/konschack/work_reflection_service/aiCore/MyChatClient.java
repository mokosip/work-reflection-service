package de.sipgate.konschack.work_reflection_service.aiCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;

@Component
public class MyChatClient {
  private static final Logger log = LoggerFactory.getLogger(MyChatClient.class);
  private final VectorStore vectorStore;
  private final PromptTemplate promptTemplate;
  ChatClient chatClient;

  public MyChatClient(
      @Qualifier("ollamaChatClient") ChatClient chatClient,
      VectorStore vectorStore,
      PromptTemplate promptTemplate) {
    this.chatClient = chatClient;
    this.vectorStore = vectorStore;
    this.promptTemplate = promptTemplate;
  }

  // TODO: refactor: using 3 LLM calls here...
  public Reflection chat(ReflectionPrompt input) {
    var initialLLMResponse = chatClient.prompt().user(input.prompt()).call().content();
    assert initialLLMResponse != null;
    var keywords =
        chatClient
            .prompt()
            .system(
                "Extract a concise, comma-separated list of the most relevant keywords from the following text. Give the answer only and no further information. If no keywords are found, return nothing.")
            .user(initialLLMResponse)
            .call()
            .content();
    assert keywords != null;
    ChatClient.ChatClientRequestSpec request =
        chatClient
            .prompt()
            // TODO: itemCount not really working.
            .system(sp -> sp.param("itemCount", input.options().get("itemCount")))
            .user(input.prompt())
            .advisors(
                QuestionAnswerAdvisor.builder(vectorStore)
                    .promptTemplate(promptTemplate)
                    .searchRequest(
                        SearchRequest.builder().query(keywords).similarityThreshold(0.5).build())
                    .build());

    return new Reflection(input.date(), request.call().content());
  }
}
