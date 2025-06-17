
日志记录
====

LangChain4j 使用 [SLF4J](https://www.slf4j.org/) 进行日志记录， 允许你插入任何你喜欢的日志后端， 例如 [Logback](https://logback.qos.ch/) 或 [Log4j](https://logging.apache.org/log4j/2.x/index.html))。

纯 Java[​](#纯-java "纯 Java的直接链接")
--------------------------------

你可以通过在创建模型实例时设置 `.logRequests(true)` 和 `.logResponses(true)` 来启用对 LLM 的每个请求和响应的日志记录：

    OpenAiChatModel.builder()    ...    .logRequests(true)    .logResponses(true)    .build();

确保你的依赖中有一个 SLF4J 日志后端，例如 Logback：

    <dependency>    <groupId>ch.qos.logback</groupId>    <artifactId>logback-classic</artifactId>    <version>1.5.8</version></dependency>

Quarkus[​](#quarkus "Quarkus的直接链接")
-----------------------------------

当使用 [Quarkus 集成](/tutorials/quarkus-integration) 时， 日志记录在 `application.properties` 文件中配置：

    ...quarkus.langchain4j.openai.chat-model.log-requests = truequarkus.langchain4j.openai.chat-model.log-responses = truequarkus.log.console.enable = truequarkus.log.file.enable = false

这些属性也可以在 Quarkus Dev UI 中设置和更改， 当在开发模式下运行应用程序时（`mvn quarkus:dev`）。 Dev UI 可在 `http://localhost:8080/q/dev-ui` 访问。

Spring Boot[​](#spring-boot "Spring Boot的直接链接")
-----------------------------------------------

当使用 [Spring Boot 集成](/tutorials/spring-boot-integration) 时， 日志记录在 `application.properties` 文件中配置：

    ...langchain4j.open-ai.chat-model.log-requests = truelangchain4j.open-ai.chat-model.log-responses = truelogging.level.dev.langchain4j = DEBUG

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/logging.md)
