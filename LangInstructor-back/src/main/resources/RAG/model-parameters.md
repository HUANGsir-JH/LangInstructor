
模型参数
====

根据您选择的模型和提供商，您可以调整许多参数，这些参数将定义：

*   模型的输出：生成内容（文本、图像）的创造性或确定性水平，生成内容的数量等。
*   连接性：基础 URL、授权密钥、超时、重试、日志记录等。

通常，您可以在模型提供商的网站上找到所有参数及其含义。 例如，OpenAI API 的参数可以在 [https://platform.openai.com/docs/api-reference/chat](https://platform.openai.com/docs/api-reference/chat) 找到 （最新版本），包括以下选项：

参数

描述

类型

`modelName`

要使用的模型名称（例如，gpt-4o、gpt-4o-mini 等）

`String`

`temperature`

使用的采样温度，介于 0 和 2 之间。较高的值如 0.8 会使输出更随机，而较低的值如 0.2 会使其更集中和确定性。

`Double`

`maxTokens`

在聊天完成中可以生成的最大令牌数。

`Integer`

`frequencyPenalty`

介于 -2.0 和 2.0 之间的数字。正值会根据文本中已有的频率惩罚新令牌，降低模型逐字重复相同行的可能性。

`Double`

`...`

...

`...`

有关 OpenAI LLM 中的完整参数列表，请参阅 [OpenAI 语言模型页面](/integrations/language-models/open-ai)。 每个模型的参数完整列表和默认值可以在各个模型页面下找到（在集成、语言模型和图像模型下）。

您可以通过两种方式创建 `*Model`：

*   一个静态工厂，只接受必需的参数，如 API 密钥，其他所有必需参数都设置为合理的默认值。
*   构建器模式：在这里，您可以为每个参数指定值。

模型构建器[​](#模型构建器 "模型构建器的直接链接")
-----------------------------

我们可以使用构建器模式设置模型的每个可用参数，如下所示：

    OpenAiChatModel model = OpenAiChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("gpt-4o-mini")        .temperature(0.3)        .timeout(ofSeconds(60))        .logRequests(true)        .logResponses(true)        .build();

在 Quarkus 中设置参数[​](#在-quarkus-中设置参数 "在 Quarkus 中设置参数的直接链接")
-----------------------------------------------------------

Quarkus 应用程序中的 LangChain4j 参数可以在 `application.properties` 文件中设置，如下所示：

    quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}quarkus.langchain4j.openai.chat-model.temperature=0.5quarkus.langchain4j.openai.timeout=60s

有趣的是，为了调试、调整或甚至只是了解所有可用的参数， 可以查看 quarkus DEV UI。 在这个仪表板中，您可以进行更改，这些更改将立即反映在您正在运行的实例中， 并且您的更改会自动移植到代码中。 可以通过使用命令 `quarkus dev` 运行您的 Quarkus 应用程序来访问 DEV UI， 然后您可以在 localhost:8080/q/dev-ui（或您部署应用程序的任何位置）找到它。

[![](/assets/images/quarkus-dev-ui-parameters-9180e5f216694b88abbc90aa8255ed27.png)](/tutorials/model-parameters)

有关 Quarkus 集成的更多信息可以在[这里](/tutorials/quarkus-integration)找到。

在 Spring Boot 中设置参数[​](#在-spring-boot-中设置参数 "在 Spring Boot 中设置参数的直接链接")
-----------------------------------------------------------------------

如果您使用我们的 [Spring Boot 启动器](https://github.com/langchain4j/langchain4j-spring) 之一， 您可以在 `application.properties` 文件中配置模型参数，如下所示：

    langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.chat-model.model-name=gpt-4-1106-preview...

支持的属性完整列表可以在 [这里](https://github.com/langchain4j/langchain4j-spring/blob/main/langchain4j-open-ai-spring-boot-starter/src/main/java/dev/langchain4j/openai/spring/AutoConfig.java)找到。

有关 Spring Boot 集成的更多信息可以在[这里](/tutorials/spring-boot-integration)找到。

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/model-parameters.md)
