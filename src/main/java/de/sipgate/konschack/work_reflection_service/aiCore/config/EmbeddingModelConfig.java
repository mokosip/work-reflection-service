package de.sipgate.konschack.work_reflection_service.aiCore.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;

@Configuration
public class EmbeddingModelConfig {
  @Bean
  public EmbeddingModel embeddingModel(
      @Value("${spring.ai.ollama.base-url}") String baseUrl,
      @Value("${spring.ai.ollama.embedding.options.model}") String model) {
    return new OllamaEmbeddingModel(
        OllamaApi.builder().baseUrl(baseUrl).build(),
        OllamaOptions.builder().model(model).build(),
        ObservationRegistry.create(),
        ModelManagementOptions.builder().build());
  }
}
