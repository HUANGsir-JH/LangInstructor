# CustomerSupportAgentApplication.java 示例

**示例目的:**
此文件是客户支持代理 Spring Boot 应用程序的入口点。它演示了如何集成 LangChain4j 的 RAG（检索增强生成）组件和聊天记忆，以构建一个能够回答基于特定文档的问题的智能代理。

**关键类和方法:**
- `@SpringBootApplication`: Spring Boot 应用程序的启动类注解。
- `ApplicationRunner`: Spring Boot 接口，用于在应用程序启动后执行特定代码，这里用于实现交互式聊天。
- `ChatMemory`: 配置聊天记忆，这里使用 `TokenWindowChatMemory` 来限制记忆的令牌数量。
- `ContentRetriever`: 配置内容检索器，用于从嵌入存储中检索相关文档片段。这里使用 `EmbeddingStoreContentRetriever`。
- `EmbeddingModel`: 配置嵌入模型，用于将文本转换为向量。这里使用 `AllMiniLmL6V2EmbeddingModel`（一个进程内模型）。
- `EmbeddingStore<TextSegment> embeddingStore`: 配置嵌入存储，用于存储文档片段的嵌入。这里使用 `InMemoryEmbeddingStore`。
- `EmbeddingStoreIngestor`: 用于自动化文档加载、分割、嵌入和存储的过程。
- `loadDocument()`: 从文件系统加载文档。
- `DocumentSplitters.recursive()`: 用于将文档分割成更小的文本片段。

**注意事项:**
- 应用程序通过 `@Bean` 注解配置了多个 LangChain4j 组件，Spring 会自动管理它们的生命周期和依赖注入。
- `interactiveRunner` Bean 提供了一个简单的命令行界面，允许用户与代理进行交互。
- `embeddingStore` Bean 中演示了如何加载一个本地的 `miles-of-smiles-terms-of-use.txt` 文档，并将其处理后存储到内存嵌入存储中，作为代理的知识库。
- `TokenCountEstimator` 用于估算令牌数量，确保聊天记忆和文档分割符合模型限制。
- `maxResults` 和 `minScore` 参数用于配置内容检索器，以控制检索到的相关文档片段的数量和相关性阈值。

```java
package dev.langchain4j.example;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.azure.AzureOpenAiTokenCountEstimator;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Scanner;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@SpringBootApplication
public class CustomerSupportAgentApplication {

    private static final Logger log = LoggerFactory.getLogger(CustomerSupportAgentApplication.class);

    /**
     * Either run this class (CustomerSupportAgentApplication) and have an interactive conversation
     * or run a CustomerSupportAgentApplicationTest and see a simulated conversation
     */

    @Bean
    ApplicationRunner interactiveRunner(CustomerSupportAgent agent) {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    log.info("==================================================");
                    log.info("User: ");
                    String userQuery = scanner.nextLine();
                    log.info("==================================================");

                    if ("exit".equalsIgnoreCase(userQuery)) {
                        break;
                    }

                    String agentAnswer = agent.answer(userQuery);
                    log.info("==================================================");
                    log.info("Agent: " + agentAnswer);
                }
            }
        };
    }

    @Bean
    ChatMemory chatMemory(TokenCountEstimator tokenizer) {
        return TokenWindowChatMemory.withMaxTokens(1000, tokenizer);
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

        // You will need to adjust these parameters to find the optimal setting, which will depend on two main factors:
        // - The nature of your data
        // - The embedding model you are using
        int maxResults = 1;
        double minScore = 0.6;

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, ResourceLoader resourceLoader) throws IOException {

        // Normally, you would already have your embedding store filled with your data.
        // However, for the purpose of this demonstration, we will:

        // 1. Create an in-memory embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 2. Load an example document ("Miles of Smiles" terms of use)
        Resource resource = resourceLoader.getResource("classpath:miles-of-smiles-terms-of-use.txt");
        Document document = loadDocument(resource.getFile().toPath(), new TextDocumentParser());

        // 3. Split the document into segments 100 tokens each
        // 4. Convert segments into embeddings
        // 5. Store embeddings into embedding store
        // All this can be done manually, but we will use EmbeddingStoreIngestor to automate this:
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, new AzureOpenAiTokenCountEstimator("gpt-4o-mini"));
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

        return embeddingStore;
    }

    public static void main(String[] args) {
        SpringApplication.run(CustomerSupportAgentApplication.class, args);
    }
}
