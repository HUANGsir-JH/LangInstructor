# Naive_RAG_Example.java 示例

**示例目的:**
此文件演示了如何实现一个“朴素 RAG”（检索增强生成）应用，详细展示了 RAG 的核心步骤：文档加载、分割、嵌入、存储和检索，以及如何将这些组件集成到 AI 服务中。

**关键类和方法:**
- `ChatModel`: 聊天模型，用于生成回答。
- `DocumentParser`: 文档解析器，将文件内容解析为 `Document` 对象。
- `DocumentSplitter`: 文档分割器，将长文档分割成小片段（`TextSegment`）。
- `EmbeddingModel`: 嵌入模型，将文本片段转换为嵌入向量。
- `EmbeddingStore`: 嵌入存储（向量数据库），用于存储嵌入向量并进行相似性搜索。
- `ContentRetriever`: 内容检索器，根据用户查询从嵌入存储中检索相关内容。
- `ChatMemory`: 聊天记忆，用于保持多轮对话上下文。
- `AiServices.builder()`: 构建 AI 服务，将上述组件集成。

**RAG 核心步骤:**
1.  **加载文档**: 使用 `FileSystemDocumentLoader` 加载本地文档。
2.  **分割文档**: 使用 `DocumentSplitters.recursive()` 将文档分割成小片段。
3.  **嵌入片段**: 使用 `BgeSmallEnV15QuantizedEmbeddingModel` 将片段转换为嵌入向量。
4.  **存储嵌入**: 将嵌入向量存储到 `InMemoryEmbeddingStore` 中。
5.  **创建内容检索器**: 配置 `EmbeddingStoreContentRetriever`，指定检索结果数量和最小相似度分数。
6.  **配置聊天记忆**: 使用 `MessageWindowChatMemory` 保持对话上下文。
7.  **构建 AI 服务**: 将聊天模型、内容检索器和聊天记忆集成到 `AiServices` 中。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 示例详细展示了 RAG 的每个步骤，适合深入理解 RAG 工作原理。
- `documents/` 目录下应包含用于 RAG 的文本文件（如 `miles-of-smiles-terms-of-use.txt`）。
- `maxResults` 和 `minScore` 参数用于控制检索结果的质量和数量。

```java
package _2_naive;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import shared.Assistant;

import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static shared.Utils.*;

public class Naive_RAG_Example {

    /**
     * This example demonstrates how to implement a naive Retrieval-Augmented Generation (RAG) application.
     * By "naive", we mean that we won't use any advanced RAG techniques.
     * In each interaction with the Large Language Model (LLM), we will:
     * 1. Take the user's query as-is.
     * 2. Embed it using an embedding model.
     * 3. Use the query's embedding to search an embedding store (containing small segments of your documents)
     * for the X most relevant segments.
     * 4. Append the found segments to the user's query.
     * 5. Send the combined input (user query + segments) to the LLM.
     * 6. Hope that:
     * - The user's query is well-formulated and contains all necessary details for retrieval.
     * - The found segments are relevant to the user's query.
     */

    public static void main(String[] args) {

        // Let's create an assistant that will know about our document
        Assistant assistant = createAssistant("documents/miles-of-smiles-terms-of-use.txt");

        // Now, let's start the conversation with the assistant. We can ask questions like:
        // - Can I cancel my reservation?
        // - I had an accident, should I pay extra?
        startConversationWith(assistant);
    }

    private static Assistant createAssistant(String documentPath) {

        // First, let's create a chat model, also known as a LLM, which will answer our queries.
        // In this example, we will use OpenAI's gpt-4o-mini, but you can choose any supported model.
        // Langchain4j currently supports more than 10 popular LLM providers.
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        // Now, let's load a document that we want to use for RAG.
        // We will use the terms of use from an imaginary car rental company, "Miles of Smiles".
        // For this example, we'll import only a single document, but you can load as many as you need.
        // LangChain4j offers built-in support for loading documents from various sources:
        // File System, URL, Amazon S3, Azure Blob Storage, GitHub, Tencent COS.
        // Additionally, LangChain4j supports parsing multiple document types:
        // text, pdf, doc, xls, ppt.
        // However, you can also manually import your data from other sources.
        DocumentParser documentParser = new TextDocumentParser();
        Document document = loadDocument(toPath(documentPath), documentParser);

        // Now, we need to split this document into smaller segments, also known as "chunks."
        // This approach allows us to send only relevant segments to the LLM in response to a user query,
        // rather than the entire document. For instance, if a user asks about cancellation policies,
        // we will identify and send only those segments related to cancellation.
        // A good starting point is to use a recursive document splitter that initially attempts
        // to split by paragraphs. If a paragraph is too large to fit into a single segment,
        // the splitter will recursively divide it by newlines, then by sentences, and finally by words,
        // if necessary, to ensure each piece of text fits into a single segment.
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        // Now, we need to embed (also known as "vectorize") these segments.
        // Embedding is needed for performing similarity searches.
        // For this example, we'll use a local in-process embedding model, but you can choose any supported model.
        // Langchain4j currently supports more than 10 popular embedding model providers.
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Next, we will store these embeddings in an embedding store (also known as a "vector database").
        // This store will be used to search for relevant segments during each interaction with the LLM.
        // For simplicity, this example uses an in-memory embedding store, but you can choose from any supported store.
        // Langchain4j currently supports more than 15 popular embedding stores.
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        // We could also use EmbeddingStoreIngestor to hide manual steps above behind a simpler API.
        // See an example of using EmbeddingStoreIngestor in _01_Advanced_RAG_with_Query_Compression_Example.

        // The content retriever is responsible for retrieving relevant content based on a user query.
        // Currently, it is capable of retrieving text segments, but future enhancements will include support for
        // additional modalities like images, audio, and more.
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2) // on each interaction we will retrieve the 2 most relevant segments
                .minScore(0.5) // we want to retrieve segments at least somewhat similar to user query
                .build();

        // Optionally, we can use a chat memory, enabling back-and-forth conversation with the LLM
        // and allowing it to remember previous interactions.
        // Currently, LangChain4j offers two chat memory implementations:
        // MessageWindowChatMemory and TokenWindowChatMemory.
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // The final step is to build our AI Service,
        // configuring it to use the components we've created above.
        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();
    }
}
