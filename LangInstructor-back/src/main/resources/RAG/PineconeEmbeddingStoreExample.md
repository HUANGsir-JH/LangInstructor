# PineconeEmbeddingStoreExample.java 示例

**示例目的:**
此文件演示了如何使用 LangChain4j 与 Pinecone 向量数据库集成，实现文本嵌入的存储和相似性搜索。

**关键类和方法:**
- `PineconeEmbeddingStore.builder()`: 构建 Pinecone 嵌入存储实例。
    - `apiKey(String)`: Pinecone API Key。
    - `index(String)`: Pinecone 索引名称。
    - `nameSpace(String)`: 命名空间，用于隔离数据。
    - `createIndex(PineconeServerlessIndexConfig)`: 自动创建无服务器索引，配置云提供商、区域和维度。
- `AllMiniLmL6V2EmbeddingModel`: 用于生成文本嵌入的嵌入模型。
- `embeddingModel.embed(TextSegment)`: 将文本片段转换为嵌入向量。
- `embeddingStore.add(Embedding, TextSegment)`: 将嵌入向量和原始文本片段添加到 Pinecone。
- `embeddingStore.search(EmbeddingSearchRequest)`: 执行相似性搜索，查找最相关的文本片段。
- `EmbeddingMatch`: 搜索结果，包含匹配的文本片段和相似度分数。

**注意事项:**
- 需要有效的 Pinecone API Key。
- 首次运行时，如果索引不存在，会自动创建。
- 嵌入数据写入 Pinecone 后，需要短暂延迟（如 `Thread.sleep(5_000)`）才能进行检索，因为数据同步需要时间。
- 适合需要持久化存储和高效检索大量文本嵌入的 RAG 应用。

```java
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeServerlessIndexConfig;

import static dev.langchain4j.internal.Utils.randomUUID;

public class PineconeEmbeddingStoreExample {

    public static void main(String[] args) throws Exception {

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey(System.getenv("PINECONE_API_KEY"))
                .index("test")
                .nameSpace(randomUUID())
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS")
                        .region("us-east-1")
                        .dimension(embeddingModel.dimension())
                        .build())
                .build();

        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("The weather is good today.");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        Thread.sleep(5_000); // it takes some time for Pinecone to persist

        Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        EmbeddingMatch<TextSegment> embeddingMatch = searchResult.matches().get(0);
        System.out.println(embeddingMatch.score()); // 0.8144288515898701
        System.out.println(embeddingMatch.embedded().text()); // I like football.
    }
}
