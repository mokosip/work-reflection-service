package de.sipgate.konschack.work_reflection_service.aiCore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import de.sipgate.konschack.work_reflection_service.appCore.GitHubService;

@Component
public class Tools {

  private final GitHubService gitHubService;

  public Tools(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  @Tool(description = "Getting commit history for repository")
  public String getCommitHistory() {
    List<GitHubService.CommitInfo> commits = gitHubService.getUserCommits().collectList().block();

    if (commits == null || commits.isEmpty()) {
      return "No commits found for ";
    }

    StringBuilder result = new StringBuilder();
    result.append("Commit history: \n");

    commits.forEach(
        commit -> {
          result
              .append("SHA: ")
              .append(commit.sha())
              .append("\nMessage: ")
              .append(commit.commitDetail().message())
              .append("\nURL: ")
              .append(commit.url())
              .append("\n\n");
        });

    return result.toString();
  }
}
