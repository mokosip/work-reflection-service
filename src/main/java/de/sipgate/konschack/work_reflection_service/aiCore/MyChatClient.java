package de.sipgate.konschack.work_reflection_service.aiCore;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.sipgate.konschack.work_reflection_service.appCore.domain.Reflection;
import de.sipgate.konschack.work_reflection_service.appCore.domain.ReflectionPrompt;

// Define a PODO (Plain Old Java Object) or Record to hold the structured output
record ProcessedInput(String elaboratedText, java.util.List<String> keywords) {}

@Component
public class MyChatClient {
  private static final Logger log = LoggerFactory.getLogger(MyChatClient.class);
  public static final String CONV_ID = "1";
  private final VectorStore vectorStore;
  private final PromptTemplate promptTemplate;
  private final ChatMemory chatMemory;
  private final Tools tools;
  private final ChatClient chatClient;

  public MyChatClient(
      @Qualifier("ollamaChatClient") ChatClient chatClient,
      VectorStore vectorStore,
      PromptTemplate promptTemplate,
      ChatMemory chatMemory,
      Tools tools) {
    this.chatClient = chatClient;
    this.vectorStore = vectorStore;
    this.promptTemplate = promptTemplate;
    this.chatMemory = chatMemory;
    this.tools = tools;
  }

  public Reflection chat(ReflectionPrompt input) {
    // CALL 1: Combined Elaboration and Keyword Extraction
    ProcessedInput processedData =
        chatClient
            .prompt()
            .user(
                "Take the following user input: '"
                    + input.prompt()
                    + "'. "
                    + "First, expand it into a coherent initial reflection draft. "
                    + "Second, from that draft, extract a concise list of the most relevant keywords. "
                    + "Return your response as a JSON object with two fields: 'elaboratedText' (string) and 'keywords' (list of strings).")
            .advisors(a -> a.param(CONVERSATION_ID, CONV_ID))
            .call()
            .entity(ProcessedInput.class);

    assert processedData != null;
    StringBuilder secondUserPrompt = new StringBuilder(processedData.elaboratedText());
    String keywordsForSearch =
        String.join(
            ",",
            processedData.keywords()); // Convert list to comma-separated string if needed by search
    if (processedData.elaboratedText().split("[.!?]\\s+").length < 4) {
      System.out.println("Processed Text too short - checking git commit history now...");
      secondUserPrompt.append(
          "\nPlease consider the provided git commit history for the repository, particularly for identifying what was learned today.");
    }

    chatMemory.clear(CONV_ID);
    // CALL 2: Final reflection generation with RAG
    ChatClient.ChatClientRequestSpec request =
        chatClient
            .prompt()
            .system(sp -> sp.param("itemCount", input.options().get("itemCount")))
            .advisors(
                QuestionAnswerAdvisor.builder(vectorStore)
                    .promptTemplate(promptTemplate)
                    .searchRequest(
                        SearchRequest.builder()
                            .query(keywordsForSearch)
                            .similarityThreshold(0.7)
                            .build())
                    .build())
            .tools(tools)
            .user(secondUserPrompt.toString());

    return new Reflection(input.date(), request.call().content());
  }
}
