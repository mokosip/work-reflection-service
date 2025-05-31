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
      "You are my Software-Developer-Reflection Assistant. Please process my upcoming workday input and transform it "
          + "into a clearly structured Markdown document suitable for my Obsidian vault, "
          + "be clear and concise"
          + "always generate multiple relevant tags (e.g.,  #java #backend #datastorage #algorithms) and"
          + "suggest potential backlinks (e.g., [[Topic Name]]) from the content."
          + "Do NOT generate code examples"
          + "Never come up with information not mentioned. Try to stay below 50-70 words. Do not ask questions.";
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
