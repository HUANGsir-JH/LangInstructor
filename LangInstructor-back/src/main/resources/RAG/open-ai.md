
OpenAI
======

备注

这是 `OpenAI` 集成的文档，它使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。

如果您使用 Quarkus，请参考 [Quarkus LangChain4j 文档](https://docs.quarkiverse.io/quarkus-langchain4j/dev/openai.html)。

LangChain4j 提供了 4 种不同的与 OpenAI 集成的方式来使用聊天模型，这是第 1 种：

*   [OpenAI](/integrations/language-models/open-ai) 使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。
*   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official) 使用官方 OpenAI Java SDK。
*   [Azure OpenAI](/integrations/language-models/azure-open-ai) 使用来自 Microsoft 的 Azure SDK，如果您使用 Microsoft Java 技术栈，包括高级 Azure 认证机制，它会工作得最好。
*   [GitHub Models](/integrations/language-models/github-models) 使用 Azure AI Inference API 访问 GitHub Models。

OpenAI 文档[​](#openai-文档 "OpenAI 文档的直接链接")
-----------------------------------------

*   [OpenAI API 文档](https://platform.openai.com/docs/introduction)
*   [OpenAI API 参考](https://platform.openai.com/docs/api-reference)

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

### 纯 Java[​](#纯-java "纯 Java的直接链接")

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-open-ai</artifactId>    <version>1.0.0-beta3</version></dependency>

### Spring Boot[​](#spring-boot "Spring Boot的直接链接")

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>    <version>1.0.0-beta3</version></dependency>

API 密钥[​](#api-密钥 "API 密钥的直接链接")
--------------------------------

要使用 OpenAI 模型，您需要一个 API 密钥。 您可以在[这里](https://platform.openai.com/api-keys)创建一个。

如果我没有 API 密钥怎么办？

如果您没有自己的 OpenAI API 密钥，不用担心。 您可以临时使用 `demo` 密钥，我们免费提供它用于演示目的。 请注意，当使用 `demo` 密钥时，所有对 OpenAI API 的请求都需要通过我们的代理， 该代理在转发您的请求到 OpenAI API 之前注入真实的密钥。 我们不会以任何方式收集或使用您的数据。 `demo` 密钥有配额限制，仅限于 `gpt-4o-mini` 模型，并且应该只用于演示目的。

    OpenAiChatModel model = OpenAiChatModel.builder()    .baseUrl("http://langchain4j.dev/demo/openai/v1")    .apiKey("demo")    .modelName("gpt-4o-mini")    .build();

创建 `OpenAiChatModel`[​](#创建-openaichatmodel "创建-openaichatmodel的直接链接")
----------------------------------------------------------------------

### 纯 Java[​](#纯-java-1 "纯 Java的直接链接")

    ChatLanguageModel model = OpenAiChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("gpt-4o-mini")        .build();// 您也可以使用 ChatRequestParameters 或 OpenAiChatRequestParameters 指定默认聊天请求参数ChatLanguageModel model = OpenAiChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .defaultRequestParameters(OpenAiChatRequestParameters.builder()                .modelName("gpt-4o-mini")                .build())        .build();

这将创建一个具有指定默认参数的 `OpenAiChatModel` 实例。

### Spring Boot[​](#spring-boot-1 "Spring Boot的直接链接")

添加到 `application.properties`：

    # 必需属性：langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.chat-model.model-name=gpt-4o-mini# 可选属性：langchain4j.open-ai.chat-model.base-url=...langchain4j.open-ai.chat-model.custom-headers=...langchain4j.open-ai.chat-model.frequency-penalty=...langchain4j.open-ai.chat-model.log-requests=...langchain4j.open-ai.chat-model.log-responses=...langchain4j.open-ai.chat-model.logit-bias=...langchain4j.open-ai.chat-model.max-retries=...langchain4j.open-ai.chat-model.max-completion-tokens=...langchain4j.open-ai.chat-model.max-tokens=...langchain4j.open-ai.chat-model.metadata=...langchain4j.open-ai.chat-model.organization-id=...langchain4j.open-ai.chat-model.parallel-tool-calls=...langchain4j.open-ai.chat-model.presence-penalty=...langchain4j.open-ai.chat-model.project-id=...langchain4j.open-ai.chat-model.reasoning-effort=...langchain4j.open-ai.chat-model.response-format=...langchain4j.open-ai.chat-model.seed=...langchain4j.open-ai.chat-model.service-tier=...langchain4j.open-ai.chat-model.stop=...langchain4j.open-ai.chat-model.store=...langchain4j.open-ai.chat-model.strict-schema=...langchain4j.open-ai.chat-model.strict-tools=...langchain4j.open-ai.chat-model.supported-capabilities=...langchain4j.open-ai.chat-model.temperature=...langchain4j.open-ai.chat-model.timeout=...langchain4j.open-ai.chat-model.top-p=langchain4j.open-ai.chat-model.user=...

大多数参数的描述可以在[这里](https://platform.openai.com/docs/api-reference/chat/create)找到。

此配置将创建一个 `OpenAiChatModel` bean， 它可以由 [AI Service](https://docs.langchain4j.dev/tutorials/spring-boot-integration/#langchain4j-spring-boot-starter) 使用， 或在需要的地方自动装配，例如：

    @RestControllerclass ChatLanguageModelController {    ChatLanguageModel chatLanguageModel;    ChatLanguageModelController(ChatLanguageModel chatLanguageModel) {        this.chatLanguageModel = chatLanguageModel;    }    @GetMapping("/model")    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {        return chatLanguageModel.chat(message);    }}

结构化输出[​](#结构化输出 "结构化输出的直接链接")
-----------------------------

[结构化输出](https://openai.com/index/introducing-structured-outputs-in-the-api/)功能支持 [工具](/tutorials/tools)和[响应格式](/tutorials/ai-services#json-mode)。

有关结构化输出的更多信息，请参见[此处](/tutorials/structured-outputs)。

### 工具的结构化输出[​](#工具的结构化输出 "工具的结构化输出的直接链接")

要为工具启用结构化输出功能，在构建模型时设置`.strictTools(true)`：

    OpenAiChatModel.builder()    ...    .strictTools(true)    .build(),

请注意，这将自动使所有工具参数成为必需的（json schema中的`required`） 并为json schema中的每个`object`设置`additionalProperties=false`。这是由于当前OpenAI的限制。

### 响应格式的结构化输出[​](#响应格式的结构化输出 "响应格式的结构化输出的直接链接")

要在使用AI服务时为响应格式启用结构化输出功能， 在构建模型时设置`.supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)`和`.strictJsonSchema(true)`：

    OpenAiChatModel.builder()    ...    .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)    .strictJsonSchema(true)    .build();

在这种情况下，AI服务将自动从给定的POJO生成JSON schema并将其传递给LLM。

创建 `OpenAiStreamingChatModel`[​](#创建-openaistreamingchatmodel "创建-openaistreamingchatmodel的直接链接")
-------------------------------------------------------------------------------------------------

### 纯 Java[​](#纯-java-2 "纯 Java的直接链接")

    StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("gpt-4o-mini")        .build();// 您也可以使用 ChatRequestParameters 或 OpenAiChatRequestParameters 指定默认聊天请求参数StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .defaultRequestParameters(OpenAiChatRequestParameters.builder()                .modelName("gpt-4o-mini")                .build())        .build();

### Spring Boot[​](#spring-boot-2 "Spring Boot的直接链接")

添加到 `application.properties`：

    # 必需属性：langchain4j.open-ai.streaming-chat-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.streaming-chat-model.model-name=gpt-4o-mini# 可选属性：langchain4j.open-ai.streaming-chat-model.base-url=...langchain4j.open-ai.streaming-chat-model.custom-headers=...langchain4j.open-ai.streaming-chat-model.frequency-penalty=...langchain4j.open-ai.streaming-chat-model.log-requests=...langchain4j.open-ai.streaming-chat-model.log-responses=...langchain4j.open-ai.streaming-chat-model.logit-bias=...langchain4j.open-ai.streaming-chat-model.max-retries=...langchain4j.open-ai.streaming-chat-model.max-completion-tokens=...langchain4j.open-ai.streaming-chat-model.max-tokens=...langchain4j.open-ai.streaming-chat-model.metadata=...langchain4j.open-ai.streaming-chat-model.organization-id=...langchain4j.open-ai.streaming-chat-model.parallel-tool-calls=...langchain4j.open-ai.streaming-chat-model.presence-penalty=...langchain4j.open-ai.streaming-chat-model.project-id=...langchain4j.open-ai.streaming-chat-model.reasoning-effort=...langchain4j.open-ai.streaming-chat-model.response-format=...langchain4j.open-ai.streaming-chat-model.seed=...langchain4j.open-ai.streaming-chat-model.service-tier=...langchain4j.open-ai.streaming-chat-model.stop=...langchain4j.open-ai.streaming-chat-model.store=...langchain4j.open-ai.streaming-chat-model.strict-schema=...langchain4j.open-ai.streaming-chat-model.strict-tools=...langchain4j.open-ai.streaming-chat-model.temperature=...langchain4j.open-ai.streaming-chat-model.timeout=...langchain4j.open-ai.streaming-chat-model.top-p=...langchain4j.open-ai.streaming-chat-model.user=...

创建 `OpenAiModerationModel`[​](#创建-openaimoderationmodel "创建-openaimoderationmodel的直接链接")
----------------------------------------------------------------------------------------

### 纯 Java[​](#纯-java-3 "纯 Java的直接链接")

    ModerationModel model = OpenAiModerationModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("text-moderation-stable")        .build();

### Spring Boot[​](#spring-boot-3 "Spring Boot的直接链接")

添加到 `application.properties`：

    # 必需属性：langchain4j.open-ai.moderation-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.moderation-model.model-name=text-moderation-stable# 可选属性：langchain4j.open-ai.moderation-model.base-url=...langchain4j.open-ai.moderation-model.custom-headers=...langchain4j.open-ai.moderation-model.log-requests=...langchain4j.open-ai.moderation-model.log-responses=...langchain4j.open-ai.moderation-model.max-retries=...langchain4j.open-ai.moderation-model.organization-id=...langchain4j.open-ai.moderation-model.project-id=...langchain4j.open-ai.moderation-model.timeout=...

创建 `OpenAiTokenizer`[​](#创建-openaitokenizer "创建-openaitokenizer的直接链接")
----------------------------------------------------------------------

    Tokenizer tokenizer = new OpenAiTokenizer("gpt-4o-mini");

HTTP 客户端[​](#http-客户端 "HTTP 客户端的直接链接")
--------------------------------------

### 纯 Java[​](#纯-java-4 "纯 Java的直接链接")

当使用 `langchain4j-open-ai` 模块时， JDK 的 `java.net.http.HttpClient` 被用作默认 HTTP 客户端。

您可以自定义它或使用您选择的任何其他 HTTP 客户端。 更多信息可以在[这里](/tutorials/customizable-http-client)找到。

### Spring Boot[​](#spring-boot-4 "Spring Boot的直接链接")

当使用 `langchain4j-open-ai-spring-boot-starter` Spring Boot 启动器时， Spring 的 `RestClient` 被用作默认 HTTP 客户端。

您可以自定义它或使用您选择的任何其他 HTTP 客户端。 更多信息可以在[这里](/tutorials/customizable-http-client)找到。

示例[​](#示例 "示例的直接链接")
--------------------

*   [OpenAI 示例](https://github.com/langchain4j/langchain4j-examples/tree/main/open-ai-examples/src/main/java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/open-ai.md)
