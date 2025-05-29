package de.sipgate.konschack.work_reflection_service.aiCore;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MyChatClient {
  ChatClient chatClient;

  public MyChatClient(@Qualifier("ollamaChatClient") ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public String chat(String message) {
    return chatClient.prompt(message).call().content();
  }
}
