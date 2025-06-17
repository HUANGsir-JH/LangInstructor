
可自定义的 HTTP 客户端
==============

一些 LangChain4j 模块（目前包括 OpenAI 和 Ollama）支持自定义用于调用 LLM 提供商 API 的 HTTP 客户端。

`langchain4j-http-client` 模块实现了一个 `HttpClient` SPI，这些模块使用它来调用 LLM 提供商的 REST API。 这意味着底层 HTTP 客户端可以被自定义， 并且通过实现 `HttpClient` SPI，可以集成任何其他 HTTP 客户端。

目前，有 2 个开箱即用的实现：

*   `JdkHttpClient`，位于 `langchain4j-http-client-jdk` 模块中。 当使用支持的模块（例如 `langchain4j-open-ai`）时，它是默认使用的。
*   `SpringRestClient`，位于 `langchain4j-http-client-spring-restclient` 中。 当使用支持模块的 Spring Boot 启动器（例如 `langchain4j-open-ai-spring-boot-starter`）时，它是默认使用的。

自定义 JDK 的 `HttpClient`[​](#自定义-jdk-的-httpclient "自定义-jdk-的-httpclient的直接链接")
----------------------------------------------------------------------------

    HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()        .sslContext(...);JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()        .httpClientBuilder(httpClientBuilder);OpenAiChatModel model = OpenAiChatModel.builder()        .httpClientBuilder(jdkHttpClientBuilder)        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("gpt-4o-mini")        .build();

自定义 Spring 的 `RestClient`[​](#自定义-spring-的-restclient "自定义-spring-的-restclient的直接链接")
-------------------------------------------------------------------------------------

    RestClient.Builder restClientBuilder = RestClient.builder()        .requestFactory(new HttpComponentsClientHttpRequestFactory());SpringRestClientBuilder springRestClientBuilder = SpringRestClient.builder()        .restClientBuilder(restClientBuilder)        .streamingRequestExecutor(new VirtualThreadTaskExecutor());OpenAiChatModel model = OpenAiChatModel.builder()        .httpClientBuilder(springRestClientBuilder)        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("gpt-4o-mini")        .build();

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/customizable-http-client.md)
