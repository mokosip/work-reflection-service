package de.sipgate.konschack.work_reflection_service.aiCore.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Bean
  public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
    return ChatClient.create(chatModel);
  }
}
