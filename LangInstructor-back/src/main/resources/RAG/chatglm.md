
ChatGLM
=======

[https://github.com/THUDM/ChatGLM-6B](https://github.com/THUDM/ChatGLM-6B)

ChatGLM 是由清华大学发布的开源双语对话语言模型。

对于 ChatGLM2、ChatGLM3 和 GLM4，它们的 API 与 OpenAI 兼容。您可以参考 `langchain4j-zhipu-ai` 或使用 `langchain4j-open-ai`。

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

备注

自 `1.0.0-alpha1` 起，`langchain4j-chatglm` 已迁移到 `langchain4j-community` 并更名为 `langchain4j-community-chatglm`。

`1.0.0-alpha1` 之前：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-chatglm</artifactId>    <version>${previous version here}</version></dependency>

`1.0.0-alpha1` 及之后：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-community-chatglm</artifactId>    <version>${latest version here}</version></dependency>

或者，您可以使用 BOM 来一致地管理依赖项：

    <dependencyManagement>    <dependency>        <groupId>dev.langchain4j</groupId>        <artifactId>langchain4j-community-bom</artifactId>        <version>${latest version here}</version>        <typ>pom</typ>        <scope>import</scope>    </dependency></dependencyManagement>

API[​](#api "API的直接链接")
-----------------------

您可以使用以下代码实例化 `ChatGlmChatModel`：

    ChatLanguageModel model = ChatGlmChatModel.builder()        .baseUrl(System.getenv("CHATGLM_BASE_URL"))        .logRequests(true)        .logResponses(true)        .build();

现在您可以像使用普通的 `ChatLanguageModel` 一样使用它。

备注

`ChatGlmChatModel` 不支持函数调用和结构化输出。请参见[索引](/integrations/language-models/)

示例[​](#示例 "示例的直接链接")
--------------------

*   [ChatGlmChatModelIT](https://github.com/langchain4j/langchain4j-community/blob/main/models/langchain4j-community-chatglm/src/test/java/dev/langchain4j/community/model/chatglm/ChatGlmChatModelIT.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/chatglm.md)
