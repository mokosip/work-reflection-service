// package de.sipgate.konschack.work_reflection_service;
//
// import org.junit.jupiter.api.Disabled;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
// import org.springframework.context.annotation.Bean;
// import org.testcontainers.chromadb.ChromaDBContainer;
// import org.testcontainers.ollama.OllamaContainer;
//
// @Disabled
// @TestConfiguration(proxyBeanMethods = false)
// public class TestContainerConfig {
//
//  @Bean
//  @ServiceConnection
//  public ChromaDBContainer chromaDB() {
//    return new ChromaDBContainer("chromadb/chroma:0.5.20");
//  }
//
//  @Bean
//  @ServiceConnection
//  public OllamaContainer ollama() {
//    return new OllamaContainer("ollama/ollama:0.4.5");
//  }
// }
