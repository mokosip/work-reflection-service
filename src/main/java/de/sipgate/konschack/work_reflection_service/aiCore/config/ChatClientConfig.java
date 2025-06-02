package de.sipgate.konschack.work_reflection_service.aiCore.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
  public static final String SYSTEM_PROMPT =
      "You are my Software-Developer-Reflection Assistant. Please process my workday input reflection and transform it "
          + "into a clearly structured Markdown document suitable for my Obsidian vault, "
          + "be clear and concise. "
          + "Create exactly {itemCount} bullet point items."
          + "Always generate relevant tags (e.g., #java #backend #datastorage #algorithms). "
          + "When similar past reflections are provided in the context, create markdown backlinks to them using the format [[reflection-YYYY-MM-DD]]. "
          + "Also suggest potential backlinks (e.g., [[Topic Name]]) from the content. "
          + "Do NOT generate code examples. "
          + "Never come up with information not mentioned. Stay below 50-70 words. Do not ask questions. "
          + "Additionally check if the provided git commit history reveals anything about what might has been learned today.";
  private final ChatMemory chatMemory;
  private final VectorStore vectorStore;

  public ChatClientConfig(ChatMemory chatMemory, VectorStore vectorStore) {
    this.chatMemory = chatMemory;
    this.vectorStore = vectorStore;
  }

  @Bean
  public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .defaultAdvisors(
            new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
    //            VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
  }

  @Bean
  PromptTemplate promptTemplate() {
    return PromptTemplate.builder()
        .renderer(
            StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
        .template(
            """
                                        <query>

                                        You also have the following context information from past reflections, which might be relevant:
                                        ---------------------
                                       <question_answer_context>
                                        ---------------------

                                        Please use this context, if relevant, to enhance your response to the main request, following all instructions from the system prompt.
                                        """)
        .build();
  }
}
