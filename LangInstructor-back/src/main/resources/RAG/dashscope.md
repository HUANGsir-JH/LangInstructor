
DashScope (通义千问)
================

[DashScope](https://dashscope.aliyun.com/) 是由[阿里云](https://www.alibabacloud.com/)开发的平台。 它提供了模型可视化、监控和调试的界面，特别是在生产环境中使用 AI/ML 模型时。该平台允许用户可视化性能指标、跟踪模型行为，并在部署周期的早期识别潜在问题。

[通义千问](https://tongyi.aliyun.com/)模型是由[阿里云](https://www.alibabacloud.com/)开发的一系列生成式 AI 模型。通义千问系列模型专为文本生成、摘要、问答和各种 NLP 任务而设计。

您可以参考[DashScope 文档](https://help.aliyun.com/zh/model-studio/getting-started/?spm=a2c4g.11186623.help-menu-2400256.d_0.6655453aLIyxGp)了解更多详情。LangChain4j 通过使用 [DashScope Java SDK](https://help.aliyun.com/zh/dashscope/java-sdk-best-practices?spm=a2c4g.11186623.0.0.272a1507Ne69ja) 与 DashScope 集成。

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

您可以在纯 Java 或 Spring Boot 应用程序中使用 DashScope 与 LangChain4j。

### 纯 Java[​](#纯-java "纯 Java的直接链接")

备注

自 `1.0.0-alpha1` 起，`langchain4j-dashscope` 已迁移到 `langchain4j-community` 并更名为 `langchain4j-community-dashscope`。

`1.0.0-alpha1` 之前：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-dashscope</artifactId>    <version>${previous version here}</version></dependency>

`1.0.0-alpha1` 及之后：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-community-dashscope</artifactId>    <version>${latest version here}</version></dependency>

### Spring Boot[​](#spring-boot "Spring Boot的直接链接")

备注

自 `1.0.0-alpha1` 起，`langchain4j-dashscope-spring-boot-starter` 已迁移到 `langchain4j-community` 并更名为 `langchain4j-community-dashscope-spring-boot-starter`。

`1.0.0-alpha1` 之前：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-dashscope-spring-boot-starter</artifactId>    <version>${previous version here}</version></dependency>

`1.0.0-alpha1` 及之后：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-community-dashscope-spring-boot-starter</artifactId>    <version>${latest version here}</version></dependency>

或者，您可以使用 BOM 来一致地管理依赖项：

    <dependencyManagement>    <dependency>        <groupId>dev.langchain4j</groupId>        <artifactId>langchain4j-community-bom</artifactId>        <version>${latest version here}</version>        <typ>pom</typ>        <scope>import</scope>    </dependency></dependencyManagement>

可配置参数[​](#可配置参数 "可配置参数的直接链接")
-----------------------------

`langchain4j-community-dashscope` 有 4 个可用模型：

*   `QwenChatModel`
*   `QwenStreamingChatModel`
*   `QwenLanguageModel`
*   `QwenStreamingLanguageModel`

`langchain4j-dashscope` 提供文本生成图像模型

*   `WanxImageModel`

### `QwenChatModel`[​](#qwenchatmodel "qwenchatmodel的直接链接")

初始化 `QwenChatModel` 时可以配置以下参数：

属性

描述

默认值

baseUrl

连接的 URL。您可以使用 HTTP 或 websocket 连接到 DashScope

[文本推理](https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation) 和 [多模态](https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation)

apiKey

API 密钥

modelName

要使用的模型

qwen-plus

topP

核采样的概率阈值控制模型生成文本的多样性。`top_p` 越高，生成的文本越多样化，反之亦然。取值范围：(0, 1.0\]。我们通常建议调整此参数或温度，但不要同时调整两者。

topK

生成过程中采样候选集的大小。

enableSearch

模型在生成文本时是否使用互联网搜索结果作为参考。

seed

设置种子参数将使文本生成过程更具确定性，通常用于使结果保持一致。

repetitionPenalty

模型生成过程中连续序列的重复。增加 `repetition_penalty` 会减少模型生成中的重复，1.0 表示没有惩罚。取值范围：(0, +inf)

temperature

控制模型生成文本多样性的采样温度。温度越高，生成的文本越多样化，反之亦然。取值范围：\[0, 2)

stops

使用 stop 参数，当模型即将包含指定的字符串或 token\_id 时，会自动停止生成文本。

maxTokens

此请求返回的最大令牌数。

listeners

监听请求、响应和错误的监听器。

### `QwenStreamingChatModel`[​](#qwenstreamingchatmodel "qwenstreamingchatmodel的直接链接")

与 `QwenChatModel` 相同

### `QwenLanguageModel`[​](#qwenlanguagemodel "qwenlanguagemodel的直接链接")

与 `QwenChatModel` 相同，除了 `listeners`。

### `QwenStreamingLanguageModel`[​](#qwenstreaminglanguagemodel "qwenstreaminglanguagemodel的直接链接")

与 `QwenChatModel` 相同，除了 `listeners`。

示例[​](#示例 "示例的直接链接")
--------------------

### 纯 Java[​](#纯-java-1 "纯 Java的直接链接")

您可以使用以下代码初始化 `QwenChatModel`：

    ChatLanguageModel qwenModel = QwenChatModel.builder()                    .apiKey("您的 API 密钥")                    .modelName("qwen-max")                    .build();

或者为其他参数进行更多自定义：

    ChatLanguageModel qwenModel = QwenChatModel.builder()                    .apiKey("您的 API 密钥")                    .modelName("qwen-max")                    .enableSearch(true)                    .temperature(0.7)                    .maxTokens(4096)                    .stops(List.of("Hello"))                    .build();

如何调用文本生成图片：

    WanxImageModel wanxImageModel = WanxImageModel.builder()                    .modelName("wanx2.1-t2i-plus")                     .apiKey("阿里云百炼apikey")                         .build();Response<Image> response = wanxImageModel.generate("美女");System.out.println(response.content().url());

### Spring Boot[​](#spring-boot-1 "Spring Boot的直接链接")

引入 `langchain4j-community-dashscope-spring-boot-starter` 依赖后，您可以通过使用以下配置简单地注册 `QwenChatModel` bean：

    langchain4j.community.dashscope.api-key=<您的 API 密钥>langchain4j.community.dashscope.model-name=qwen-max# 属性与 `QwenChatModel` 相同# 例如# langchain4j.community.dashscope.temperature=0.7# langchain4j.community.dashscope.max-tokens=4096

### 更多示例[​](#更多示例 "更多示例的直接链接")

您可以在 [LangChain4j Community](https://github.com/langchain4j/langchain4j-community/blob/main/models/langchain4j-community-dashscope) 中查看更多详情

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/dashscope.md)
