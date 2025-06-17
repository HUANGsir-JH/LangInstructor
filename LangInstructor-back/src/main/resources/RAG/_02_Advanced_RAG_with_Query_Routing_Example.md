# _02_Advanced_RAG_with_Query_Routing_Example.java 示例

**示例目的:**
此文件演示了如何实现一个高级 RAG 应用，通过“查询路由”（Query Routing）技术将用户查询智能地路由到最合适的 `ContentRetriever`，从而高效地从多个数据源中检索信息。

**关键类和方法:**
- `LanguageModelQueryRouter`: 基于 LLM 的查询路由器，根据查询内容和检索器描述，决定将查询发送到哪个（或哪些）检索器。
- `RetrievalAugmentor`: 检索增强器，作为 RAG 流程的入口点，负责协调查询路由和内容检索。
- `DefaultRetrievalAugmentor.builder()`: 构建检索增强器，配置查询路由器。
- `ContentRetriever`: 内容检索器，每个检索器对应一个特定的数据源（如传记、使用条款）。
- `EmbeddingStore`: 嵌入存储，用于存储不同数据源的嵌入向量。
- `embed(Path, EmbeddingModel)`: 辅助方法，用于加载文档、分割、嵌入并存储到嵌入存储中。

**工作原理:**
1.  为不同的数据源（如约翰·多伊的传记、汽车租赁条款）创建独立的 `EmbeddingStore` 和 `ContentRetriever`。
2.  `LanguageModelQueryRouter` 接收用户查询，并利用 LLM 判断哪个 `ContentRetriever` 最适合处理该查询。
3.  查询被路由到选定的 `ContentRetriever`，进行信息检索。
4.  检索到的信息被用于增强 LLM 的回答。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合数据分散在多个不同来源和格式的复杂 RAG 应用。
- 查询路由提高了检索效率和准确性，避免了对所有数据源进行不必要的搜索。
- 示例中使用了 `biography-of-john-doe.txt` 和 `miles-of-smiles-terms-of-use.txt` 两个文档作为不同的数据源。

```java
package _3_advanced;

import _2_naive.Naive_RAG_Example;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import shared.Assistant;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static shared.Utils.*;

public class _02_Advanced_RAG_with_Query_Routing_Example {

    /**
     * Please refer to {@link Naive_RAG_Example} for a basic context.
     * <p>
     * Advanced RAG in LangChain4j is described here: https://github.com/langchain4j/langchain4j/pull/538
     * <p>
     * This example showcases the implementation of a more advanced RAG application
     * using a technique known as "query routing".
     * <p>
     * Often, private data is spread across multiple sources and formats.
     * This might include internal company documentation on Confluence, your project's code in a Git repository,
     * a relational database with user data, or a search engine with the products you sell, among others.
     * In a RAG flow that utilizes data from multiple sources, you will likely have multiple
     * {@link EmbeddingStore}s or {@link ContentRetriever}s.
     * While you could route each user query to all available {@link ContentRetriever}s,
     * this approach might be inefficient and counterproductive.
     * <p>
     * "Query routing" is the solution to this challenge. It involves directing a query to the most appropriate
     * {@link ContentRetriever} (or several). Routing can be implemented in various ways:
     * - Using rules (e.g., depending on the user's privileges, location, etc.).
     * - Using keywords (e.g., if a query contains words X1, X2, X3, route it to {@link ContentRetriever} X, etc.).
     * - Using semantic similarity (see EmbeddingModelTextClassifierExample in this repository).
     * - Using an LLM to make a routing decision.
     * <p>
     * For scenarios 1, 2, and 3, you can implement a custom {@link QueryRouter}.
     * For scenario 4, this example will demonstrate how to use a {@link LanguageModelQueryRouter}.
     */

    public static void main(String[] args) {

        Assistant assistant = createAssistant();

        // First, ask "What is the legacy of John Doe?"
        // Then, ask "Can I cancel my reservation?"
        // Now, see the logs to observe how the queries are routed to different retrievers.
        startConversationWith(assistant);
    }

    private static Assistant createAssistant() {

        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        // Let's create a separate embedding store specifically for biographies.
        EmbeddingStore<TextSegment> biographyEmbeddingStore =
                embed(toPath("documents/biography-of-john-doe.txt"), embeddingModel);
        ContentRetriever biographyContentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(biographyEmbeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();

        // Additionally, let's create a separate embedding store dedicated to terms of use.
        EmbeddingStore<TextSegment> termsOfUseEmbeddingStore =
                embed(toPath("documents/miles-of-smiles-terms-of-use.txt"), embeddingModel);
        ContentRetriever termsOfUseContentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(termsOfUseEmbeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        // Let's create a query router.
        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(biographyContentRetriever, "biography of John Doe");
        retrieverToDescription.put(termsOfUseContentRetriever, "terms of use of car rental company");
        QueryRouter queryRouter = new LanguageModelQueryRouter(chatModel, retrieverToDescription);

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    private static EmbeddingStore<TextSegment> embed(Path documentPath, EmbeddingModel embeddingModel) {
        DocumentParser documentParser = new TextDocumentParser();
        Document document = loadDocument(documentPath, documentParser);

        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);
        return embeddingStore;
    }
}
