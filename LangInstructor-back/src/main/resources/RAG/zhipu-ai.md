
智谱 AI
=====

[智谱 AI](https://www.zhipuai.cn/) 是一个提供模型服务的平台，包括文本生成、文本嵌入、图像生成等。您可以参考 [智谱 AI 开放平台](https://open.bigmodel.cn/) 了解更多详情。 LangChain4j 通过使用 [HTTP 端点](https://bigmodel.cn/dev/api/normal-model/glm-4) 与智谱 AI 集成。我们正在考虑将其从 HTTP 端点迁移到官方 SDK，并感谢任何帮助！

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

您可以在纯 Java 或 Spring Boot 应用程序中使用智谱 AI 和 LangChain4j。

### 纯 Java[​](#纯-java "纯 Java的直接链接")

备注

自 `1.0.0-alpha1` 起，`langchain4j-zhipu-ai` 已迁移到 `langchain4j-community` 并更名为 `langchain4j-community-zhipu-ai`

`1.0.0-alpha1` 之前：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-zhipu-ai</artifactId>    <version>${previous version here}</version></dependency>

`1.0.0-alpha1` 及之后：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-community-zhipu-ai</artifactId>    <version>1.0.0-beta3</version></dependency>

或者，您可以使用 BOM 来一致地管理依赖项：

    <dependencyManagement>    <dependency>        <groupId>dev.langchain4j</groupId>        <artifactId>langchain4j-community-bom</artifactId>        <version>1.0.0-beta3</version>        <typ>pom</typ>        <scope>import</scope>    </dependency></dependencyManagement>

可配置参数[​](#可配置参数 "可配置参数的直接链接")
-----------------------------

属性

描述

默认值

baseUrl

连接的 URL。您可以使用 HTTP 或 websocket 连接到 DashScope

[https://open.bigmodel.cn/](https://open.bigmodel.cn/)

apiKey

API 密钥

model

要使用的模型。

glm-4-flash

topP

核采样的概率阈值控制模型生成文本的多样性。top\_p 越高，生成的文本越多样，反之亦然。取值范围：(0, 1.0\]。我们通常建议更改此参数或温度，但不要同时更改两者。

maxRetries

请求的最大重试次数

3

temperature

采样温度控制模型生成文本的多样性。温度越高，生成的文本越多样，反之亦然。取值范围：\[0, 2)

0.7

stops

使用 stop 参数，模型将在即将包含指定字符串或 token\_id 时自动停止生成文本。

maxToken

此请求返回的最大令牌数。

512

listeners

监听请求、响应和错误的监听器。

callTimeout

OKHttp 请求的超时配置

connectTimeout

OKHttp 请求的超时配置

writeTimeout

OKHttp 请求的超时配置

readTimeout

OKHttp 请求的超时配置

logRequests

是否记录请求

false

logResponses

是否记录响应

false

### `ZhipuAiStreamingChatModel`[​](#zhipuaistreamingchatmodel "zhipuaistreamingchatmodel的直接链接")

与 `ZhipuAiChatModel` 相同，除了 `maxRetries`。

示例[​](#示例 "示例的直接链接")
--------------------

### 纯 Java[​](#纯-java-1 "纯 Java的直接链接")

您可以使用以下代码初始化 `ZhipuAiChatModel`：

    ChatLanguageModel qwenModel = ZhipuAiChatModel.builder()                    .apiKey("您的 API 密钥")                    .callTimeout(Duration.ofSeconds(60))                    .connectTimeout(Duration.ofSeconds(60))                    .writeTimeout(Duration.ofSeconds(60))                    .readTimeout(Duration.ofSeconds(60))                    .build();

或者为其他参数进行更多自定义：

    ChatLanguageModel qwenModel = ZhipuAiChatModel.builder()                    .apiKey("您的 API 密钥")                    .model("glm-4")                    .temperature(0.6)                    .maxToken(1024)                    .maxRetries(1)                    .callTimeout(Duration.ofSeconds(60))                    .connectTimeout(Duration.ofSeconds(60))                    .writeTimeout(Duration.ofSeconds(60))                    .readTimeout(Duration.ofSeconds(60))                    .build();

### 更多示例[​](#更多示例 "更多示例的直接链接")

您可以在以下位置查看更多示例：

*   [ZhipuAiChatModelIT](https://github.com/langchain4j/langchain4j-community/blob/main/models/langchain4j-community-zhipu-ai/src/test/java/dev/langchain4j/community/model/zhipu/ZhipuAiChatModelIT.java)
*   [ZhipuAiStreamingChatModelIT](https://github.com/langchain4j/langchain4j-community/blob/main/models/langchain4j-community-zhipu-ai/src/test/java/dev/langchain4j/community/model/zhipu/ZhipuAiStreamingChatModelIT.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/zhipu-ai.md)
