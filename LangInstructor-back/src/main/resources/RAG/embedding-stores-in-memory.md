
内存存储
====

LangChain4j 提供了 `EmbeddingStore` 接口的简单内存实现： `InMemoryEmbeddingStore`。 它适用于快速原型设计和简单用例。 它在内存中保存 `Embedding` 和相关的 `TextSegment`。 搜索也在内存中执行。 它还可以被持久化并从 JSON 字符串或文件中恢复。

### Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j</artifactId>    <version>1.0.0-beta3</version></dependency>

API[​](#api "API的直接链接")
-----------------------

*   `InMemoryEmbeddingStore`

持久化[​](#持久化 "持久化的直接链接")
-----------------------

`InMemoryEmbeddingStore` 可以序列化为 json 字符串或文件：

    InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();embeddingStore.addAll(embeddings, embedded);String serializedStore = embeddingStore.serializeToJson();InMemoryEmbeddingStore<TextSegment> deserializedStore = InMemoryEmbeddingStore.fromJson(serializedStore);String filePath = "/home/me/store.json";embeddingStore.serializeToFile(filePath);InMemoryEmbeddingStore<TextSegment> deserializedStore = InMemoryEmbeddingStore.fromFile(filePath);

示例[​](#示例 "示例的直接链接")
--------------------

*   [InMemoryEmbeddingStoreExample](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/store/InMemoryEmbeddingStoreExample.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/embedding-stores/1-in-memory.md)
