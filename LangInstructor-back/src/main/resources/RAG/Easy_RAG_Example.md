# Easy_RAG_Example.java 示例

**示例目的:**
此文件演示了如何实现一个“简易 RAG”（检索增强生成）应用，通过 LangChain4j 快速构建一个能够基于文档回答问题的 AI 助手。

**关键类和方法:**
- `loadDocuments(Path, PathMatcher)`: 从文件系统加载文档。
- `AiServices.builder()`: 构建 AI 服务，集成聊天模型、聊天记忆和内容检索器。
- `chatModel(ChatModel)`: 配置聊天模型（这里使用 OpenAI）。
- `chatMemory(ChatMemory)`: 配置聊天记忆（这里使用 `MessageWindowChatMemory`）。
- `contentRetriever(ContentRetriever)`: 配置内容检索器，用于从文档中检索相关信息。
- `InMemoryEmbeddingStore<TextSegment>`: 内存嵌入存储，用于存储文档的嵌入向量。
- `EmbeddingStoreIngestor.ingest(documents, embeddingStore)`: 将文档摄取到嵌入存储中，自动处理文档分割、嵌入等。
- `EmbeddingStoreContentRetriever.from(embeddingStore)`: 从嵌入存储创建内容检索器。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 示例简化了 RAG 的实现细节，通过封装好的 API 快速搭建。
- 适合快速原型开发和初学者理解 RAG 概念。
- `documents/` 目录下应包含用于 RAG 的文本文件（如 `*.txt`）。

```java
package _1_easy;

import _2_naive.Naive_RAG_Example;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import shared.Assistant;

import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static shared.Utils.*;

public class Easy_RAG_Example {

    private static final ChatModel CHAT_MODEL = OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName(GPT_4_O_MINI)
            .build();

    /**
     * This example demonstrates how to implement an "Easy RAG" (Retrieval-Augmented Generation) application.
     * By "easy" we mean that we won't dive into all the details about parsing, splitting, embedding, etc.
     * All the "magic" is hidden inside the "langchain4j-easy-rag" module.
     * <p>
     * If you want to learn how to do RAG without the "magic" of an "Easy RAG", see {@link Naive_RAG_Example}.
     */

    public static void main(String[] args) {

        // First, let's load documents that we want to use for RAG
        List<Document> documents = loadDocuments(toPath("documents/"), glob("*.txt"));

        // Second, let's create an assistant that will have access to our documents
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(CHAT_MODEL) // it should use OpenAI LLM
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // it should remember 10 latest messages
                .contentRetriever(createContentRetriever(documents)) // it should have access to our documents
                .build();

        // Lastly, let's start the conversation with the assistant. We can ask questions like:
        // - Can I cancel my reservation?
        // - I had an accident, should I pay extra?
        startConversationWith(assistant);
    }

    private static ContentRetriever createContentRetriever(List<Document> documents) {

        // Here, we create an empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }
}
