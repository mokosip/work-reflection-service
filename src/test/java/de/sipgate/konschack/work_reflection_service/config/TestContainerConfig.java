package de.sipgate.konschack.work_reflection_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.chromadb.ChromaDBContainer;
import org.testcontainers.ollama.OllamaContainer;

@TestConfiguration
public class TestContainerConfig {

  @Bean
  public static ChromaDBContainer chromaDB() {
    return new ChromaDBContainer("chromadb/chroma:0.5.20");
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry, ChromaDBContainer container) {
    System.out.println(
        "Dynamically registering ChromaDB container on port "
            + container.getFirstMappedPort()
            + "with endpoint "
            + container.getEndpoint());
    registry.add("spring.ai.vectorstore.chroma.base-url", container::getEndpoint);
  }

  @Bean
  public OllamaContainer ollama() {
    return new OllamaContainer("ollama/ollama:0.4.5");
  }
}
