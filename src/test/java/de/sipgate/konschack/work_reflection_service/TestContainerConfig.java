package de.sipgate.konschack.work_reflection_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.testcontainers.chromadb.ChromaDBContainer;
import org.testcontainers.ollama.OllamaContainer;

@TestConfiguration
public class TestContainerConfig {

  private final Environment environment;

  public TestContainerConfig(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public ChromaDBContainer chromaDB() {
    String chromaUrl =
        environment.getProperty("spring.ai.vectorstore.chroma.base-url", "http://localhost:8000");

    if (isRunningInCi()) {
      // In CI, use a fresh test container
      return new ChromaDBContainer("chromadb/chroma:0.5.20");
    } else {
      // In local development, use existing container
      return new ExistingChromaDBContainer(chromaUrl);
    }
  }

  @Bean
  public OllamaContainer ollama() {
    String ollamaUrl =
        environment.getProperty("spring.ai.ollama.base-url", "http://localhost:11434");

    if (isRunningInCi()) {
      // In CI, use a fresh test container
      return new OllamaContainer("ollama/ollama:0.4.5");
    } else {
      // In local development, use existing container
      return new ExistingOllamaContainer(ollamaUrl);
    }
  }

  private boolean isRunningInCi() {
    return Boolean.parseBoolean(System.getProperty("CI", "false"));
  }
}

// Helper classes to wrap existing containers
class ExistingChromaDBContainer extends ChromaDBContainer {
  private final String url;

  public ExistingChromaDBContainer(String url) {
    super("chromadb/chroma:0.5.20");
    this.url = url;
  }

  @Override
  public void start() {
    // Don't actually start a container
  }

  @Override
  public String getEndpoint() {
    return url;
  }
}

class ExistingOllamaContainer extends OllamaContainer {
  private final String url;

  public ExistingOllamaContainer(String url) {
    super("ollama/ollama:0.4.5");
    this.url = url;
  }

  @Override
  public void start() {
    // Don't actually start a container
  }

  @Override
  public String getEndpoint() {
    return url;
  }
}
