package de.sipgate.konschack.work_reflection_service.aiCore.config;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class VectorStoreConfig {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  public RestClient.Builder builder() {
    return RestClient.builder().requestFactory(new SimpleClientHttpRequestFactory());
  }

  @Bean
  public ChromaApi chromaApi(RestClient.Builder restClientBuilder) {
    String chromaUrl = "http://localhost:8000";
    return new ChromaApi(chromaUrl, restClientBuilder, objectMapper);
  }

  @Bean
  public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
    System.out.println("EmbeddingModel: " + embeddingModel);
    return ChromaVectorStore.builder(chromaApi, embeddingModel)
        .collectionName("TestCollection")
        .initializeSchema(true)
        .build();
  }
}
