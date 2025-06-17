# CustomerSupportAgentConfiguration.java 示例

**示例目的:**
此文件是客户支持代理 Spring Boot 应用程序的配置类，负责定义和配置 LangChain4j 的核心组件，包括聊天记忆提供器、嵌入模型、嵌入存储、内容检索器和 Token 计数器。

**关键类和方法:**
- `@Configuration`: Spring 框架注解，表示这是一个配置类。
- `ChatMemoryProvider chatMemoryProvider(TokenCountEstimator tokenizer)`: 配置聊天记忆提供器，为每个用户提供独立的 `TokenWindowChatMemory`。
- `EmbeddingModel embeddingModel()`: 配置嵌入模型，这里使用 `AllMiniLmL6V2EmbeddingModel`。
- `EmbeddingStore<TextSegment> embeddingStore(...)`: 配置嵌入存储，这里使用 `InMemoryEmbeddingStore`，并演示了如何加载、分割、嵌入和存储文档。
- `ContentRetriever contentRetriever(...)`: 配置内容检索器，用于从嵌入存储中检索相关文档片段。
- `TokenCountEstimator tokenCountEstimator()`: 配置 Token 计数器，用于估算消息和文档的 Token 数量。

**注意事项:**
- 这是一个 Spring 配置类，所有 `@Bean` 方法都会被 Spring 自动管理和注入。
- 嵌入存储的初始化过程包括加载 `miles-of-smiles-terms-of-use.txt` 文档，并将其分割、嵌入后存储。
- `maxTokens`、`maxResults` 和 `minScore` 等参数需要根据实际数据和模型进行调优。
- `AllMiniLmL6V2EmbeddingModel` 是一个轻量级嵌入模型，适合演示目的。

```java
package dev.langchain4j.example;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Configuration
public class CustomerSupportAgentConfiguration {

    @Bean
    ChatMemoryProvider chatMemoryProvider(TokenCountEstimator tokenizer) {
        return memoryId -> TokenWindowChatMemory.builder()
                .id(memoryId)
                .maxTokens(5000, tokenizer)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        // Not the best embedding model, but good enough for this demo
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, ResourceLoader resourceLoader, TokenCountEstimator tokenizer) throws IOException {

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
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, tokenizer);
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

        return embeddingStore;
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

        // You will need to adjust these parameters to find the optimal setting,
        // which will depend on multiple factors, for example:
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
    TokenCountEstimator tokenCountEstimator() {
        return new OpenAiTokenCountEstimator(GPT_4_O_MINI);
    }
}
