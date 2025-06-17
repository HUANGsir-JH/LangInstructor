
OpenAI
======

备注

这是 `OpenAI` 集成的文档，它使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。

LangChain4j 提供了 4 种不同的与 OpenAI 集成的方式来使用嵌入模型，这是第 1 种：

*   [OpenAI](/integrations/language-models/open-ai) 使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。
*   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official) 使用官方 OpenAI Java SDK。
*   [Azure OpenAI](/integrations/language-models/azure-open-ai) 使用来自 Microsoft 的 Azure SDK，如果您使用 Microsoft Java 技术栈，包括高级 Azure 认证机制，它会工作得最好。
*   [GitHub Models](/integrations/language-models/github-models) 使用 Azure AI 推理 API 访问 GitHub Models。

*   [https://platform.openai.com/docs/guides/embeddings](https://platform.openai.com/docs/guides/embeddings)
*   [https://platform.openai.com/docs/api-reference/embeddings](https://platform.openai.com/docs/api-reference/embeddings)

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

### 纯 Java[​](#纯-java "纯 Java的直接链接")

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-open-ai</artifactId>    <version>1.0.0-beta3</version></dependency>

### Spring Boot[​](#spring-boot "Spring Boot的直接链接")

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>    <version>1.0.0-beta3</version></dependency>

创建 `OpenAiEmbeddingModel`[​](#创建-openaiembeddingmodel "创建-openaiembeddingmodel的直接链接")
-------------------------------------------------------------------------------------

### 纯 Java[​](#纯-java-1 "纯 Java的直接链接")

    EmbeddingModel model = OpenAiEmbeddingModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("text-embedding-3-small")        .build();

### Spring Boot[​](#spring-boot-1 "Spring Boot的直接链接")

在 `application.properties` 中添加：

    # 必需属性：langchain4j.open-ai.embedding-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.embedding-model.model-name=text-embedding-3-small# 可选属性：langchain4j.open-ai.embedding-model.base-url=...langchain4j.open-ai.embedding-model.custom-headers=...langchain4j.open-ai.embedding-model.dimensions=...langchain4j.open-ai.embedding-model.log-requests=...langchain4j.open-ai.embedding-model.log-responses=...langchain4j.open-ai.embedding-model.max-retries=...langchain4j.open-ai.embedding-model.organization-id=...langchain4j.open-ai.embedding-model.project-id=...langchain4j.open-ai.embedding-model.timeout=...langchain4j.open-ai.embedding-model.user=...

示例[​](#示例 "示例的直接链接")
--------------------

*   [OpenAiEmbeddingModelExamples](https://github.com/langchain4j/langchain4j-examples/blob/main/open-ai-examples/src/main/java/OpenAiEmbeddingModelExamples.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/embedding-models/open-ai.md)
