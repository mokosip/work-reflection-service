package de.sipgate.konschack.work_reflection_service.appCore;

import de.sipgate.konschack.work_reflection_service.appCore.config.GitHubProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import reactor.core.publisher.Flux;

@Service
public class GitHubService {
  private final WebClient webClient;
  private final GitHubProperties properties;

  public record CommitInfo(
      String sha,
      String message,
      @JsonProperty("commit") CommitDetail commitDetail,
      @JsonProperty("html_url") String url) {}

  public record CommitDetail(
      @JsonProperty("message") String message, @JsonProperty("author") CommitAuthor author) {}

  record CommitAuthor(String name, String email, @JsonProperty("date") String date) {}

  public GitHubService(GitHubProperties properties) {
    this.properties = properties;
    this.webClient =
        WebClient.builder()
            .baseUrl(properties.getApiUrl())
            .defaultHeader("Authorization", "Bearer " + properties.getToken())
            .defaultHeader("Accept", "application/vnd.github+json")
            .build();
  }

  public Flux<CommitInfo> getUserCommits() {
    return webClient
        .get()
        .uri("/repos/{owner}/{repo}/commits", properties.getUsername(), properties.getReponame())
        .retrieve()
        .bodyToFlux(CommitInfo.class);
  }
}
