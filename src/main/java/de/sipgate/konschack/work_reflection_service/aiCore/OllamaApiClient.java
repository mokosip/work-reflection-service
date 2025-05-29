package de.sipgate.konschack.work_reflection_service.aiCore;

import java.util.List;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OllamaApiClient {
  public static final String SYSTEM_PROMPT =
      "You are a helpful assistant. Your ta sk is to summarize all key learnings of the users current work day. "
          + "Emphasize on personal takeaways, do not ever come up with new information. If there is not a lot of information, return a few questions."
          + "Provide clear, concise responses in a structured format (use bulletpoints where applicable).";
  private final OllamaApi ollamaApi;
  private final String model;
  @Autowired private OllamaChatModel chatModel;

  public OllamaApiClient(
      OllamaApi ollamaApi, @Value("${spring.ai.ollama.model:mistral}") String model) {
    this.ollamaApi = ollamaApi;
    this.model = model;
  }

  public String chat(String prompt) {
    List<OllamaApi.Message> messages =
        List.of(
            // System message to set the context and behavior
            OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM).content(SYSTEM_PROMPT).build(),
            // User's actual prompt
            OllamaApi.Message.builder(OllamaApi.Message.Role.USER).content(prompt).build());

    OllamaApi.ChatRequest chatRequest =
        OllamaApi.ChatRequest.builder(model).messages(messages).stream(
                false) // Set to true if you want streaming responses
            .options(
                OllamaOptions.builder()
                    .temperature(0.7) // Controls randomness (0.0 to 1.0)
                    .topK(40) // Limits vocabulary for next token selection
                    .topP(0.9) // Nucleus sampling threshold
                    .build())
            .build();

    var response = ollamaApi.chat(chatRequest);
    return response.message().content();
  }
}
