
Pinecone
========

[https://www.pinecone.io/](https://www.pinecone.io/)

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-pinecone</artifactId>    <version>1.0.0-beta3</version></dependency>

已知问题[​](#已知问题 "已知问题的直接链接")
--------------------------

*   [https://github.com/langchain4j/langchain4j/issues/1948](https://github.com/langchain4j/langchain4j/issues/1948) Pinecone 将所有数字存储为[浮点值](https://docs.pinecone.io/guides/data/filter-with-metadata#supported-metadata-types)， 这意味着存储在 `Metadata` 中的 `Integer` 和 `Long` 值（例如，1746714878034235396） 可能会被损坏并返回为不正确的数字！ 可能的解决方法：在将整数/双精度值存储在 `Metadata` 中之前，将它们转换为 `String`。 请注意，在这种情况下，元数据过滤可能无法正常工作！

API[​](#api "API的直接链接")
-----------------------

*   `PineconeEmbeddingStore`

示例[​](#示例 "示例的直接链接")
--------------------

*   [PineconeEmbeddingStoreExample](https://github.com/langchain4j/langchain4j-examples/blob/main/pinecone-example/src/main/java/PineconeEmbeddingStoreExample.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/embedding-stores/pinecone.md)
