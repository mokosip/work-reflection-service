package de.sipgate.konschack.work_reflection_service.appCore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "github")
@Configuration
public class GitHubProperties {
  private String token;
  private String apiUrl = "https://api.github.com";
  private final String username = "mokosip";
  private final String reponame = "work-reflection-service";

  // getters and setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getReponame() {
    return reponame;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }
}
