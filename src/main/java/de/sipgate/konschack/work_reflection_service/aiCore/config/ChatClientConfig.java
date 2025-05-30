package de.sipgate.konschack.work_reflection_service.aiCore.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
  public static final String SYSTEM_PROMPT =
      "You are a helpful assistant. Your task is to summarize all key learnings of the users current work day. "
          + "Emphasize on personal takeaways, do not ever come up with new information. If there is not a lot of information, return a few questions."
          + "Provide clear, concise responses in a structured format (use bullet points where applicable).";
  private final ChatMemory chatMemory;

  public ChatClientConfig(ChatMemory chatMemory) {
    this.chatMemory = chatMemory;
  }

  @Bean
  public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .defaultAdvisors(
            new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
  }
}
