Anthropic | LangChain4j 中文文档  function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag("js",new Date),gtag("config","G-46M4FH02G5",{anonymize_ip:!0})  !function(){function t(t){document.documentElement.setAttribute("data-theme",t)}var e=function(){try{return new URLSearchParams(window.location.search).get("docusaurus-theme")}catch(t){}}()||function(){try{return window.localStorage.getItem("theme")}catch(t){}}();t(null!==e?e:"light")}(),function(){try{const n=new URLSearchParams(window.location.search).entries();for(var[t,e]of n)if(t.startsWith("docusaurus-data-")){var a=t.replace("docusaurus-data-","data-");document.documentElement.setAttribute(a,e)}}catch(t){}}()

[跳到主要内容](#__docusaurus_skipToContent_fallback)

[

![LangChain4j Logo](/img/logo.svg)![LangChain4j Logo](/img/logo.svg)

**LangChain4j**](/)[介绍](/intro)[快速开始](/get-started)[教程](/category/教程)[集成](/category/集成)[实用资料](/useful-materials)[Examples](https://github.com/langchain4j/langchain4j-examples)[Docu chatbot](https://chat.langchain4j.dev/)[Javadoc](https://docs.langchain4j.dev/apidocs/index.html)

[GitHub](https://github.com/langchain4j/langchain4j)[Twitter](https://twitter.com/langchain4j)[Discord](https://discord.com/invite/JzTFvyjG6R)

*   [介绍](/intro)
*   [开始使用](/get-started)
*   [教程](/category/教程)
    
*   [集成](/category/集成)
    
    *   [Language Models](/category/language-models)
        
        *   [所有支持的语言模型比较表](/integrations/language-models/)
        *   [Amazon Bedrock](/integrations/language-models/amazon-bedrock)
        *   [Anthropic](/integrations/language-models/anthropic)
        *   [Azure OpenAI](/integrations/language-models/azure-open-ai)
        *   [ChatGLM](/integrations/language-models/chatglm)
        *   [DashScope (通义千问)](/integrations/language-models/dashscope)
        *   [GitHub Models](/integrations/language-models/github-models)
        *   [Google AI Gemini](/integrations/language-models/google-ai-gemini)
        *   [Google Vertex AI Gemini](/integrations/language-models/google-vertex-ai-gemini)
        *   [Google Vertex AI PaLM 2](/integrations/language-models/google-palm)
        *   [Hugging Face](/integrations/language-models/hugging-face)
        *   [Jlama](/integrations/language-models/jlama)
        *   [LocalAI](/integrations/language-models/local-ai)
        *   [MistralAI](/integrations/language-models/mistral-ai)
        *   [Ollama](/integrations/language-models/ollama)
        *   [OpenAI](/integrations/language-models/open-ai)
        *   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official)
        *   [千帆](/integrations/language-models/qianfan)
        *   [Cloudflare Workers AI](/integrations/language-models/workers-ai)
        *   [智谱 AI](/integrations/language-models/zhipu-ai)
        *   [Xinference](/integrations/language-models/xinference)
    *   [Embedding Models](/category/embedding-models)
        
    *   [Embedding Stores](/category/embedding-stores)
        
    *   [Image Models](/category/image-models)
        
    *   [文档加载器](/category/文档加载器)
        
    *   [Document Parsers](/category/document-parsers)
        
    *   [Scoring (Reranking) Models](/category/scoring-reranking-models)
        
    *   [代码执行引擎](/category/代码执行引擎)
        
    *   [Frameworks](/category/frameworks)
        
    *   [Web Search Engines](/category/web-search-engines)
        
*   [实用资料](/category/实用资料)
    
*   [Latest Release Notes](/latest-release-notes)

*   [](/)
*   [集成](/category/集成)
*   [Language Models](/category/language-models)
*   Anthropic

本页总览

Anthropic
=========

*   [Anthropic 文档](https://docs.anthropic.com/claude/docs)
*   [Anthropic API 参考](https://docs.anthropic.com/claude/reference)

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-anthropic</artifactId>    <version>1.0.0-beta3</version></dependency>

AnthropicChatModel[​](#anthropicchatmodel "AnthropicChatModel的直接链接")
--------------------------------------------------------------------

    AnthropicChatModel model = AnthropicChatModel.builder()    .apiKey(System.getenv("ANTHROPIC_API_KEY"))    .modelName(CLAUDE_3_5_SONNET_20240620)    .build();String answer = model.chat("Say 'Hello World'");System.out.println(answer);

### 自定义 AnthropicChatModel[​](#自定义-anthropicchatmodel "自定义 AnthropicChatModel的直接链接")

    AnthropicChatModel model = AnthropicChatModel.builder()    .baseUrl(...)    .apiKey(...)    .version(...)    .beta(...)    .modelName(...)    .temperature(...)    .topP(...)    .topK(...)    .maxTokens(...)    .stopSequences(...)    .cacheSystemMessages(...)    .cacheTools(...)    .thinkingType(...)    .thinkingBudgetTokens(...)    .timeout(...)    .maxRetries(...)    .logRequests(...)    .logResponses(...)    .build();

上述部分参数的描述可以在[这里](https://docs.anthropic.com/claude/reference/messages_post)找到。

AnthropicStreamingChatModel[​](#anthropicstreamingchatmodel "AnthropicStreamingChatModel的直接链接")
-----------------------------------------------------------------------------------------------

    AnthropicStreamingChatModel model = AnthropicStreamingChatModel.builder()    .apiKey(System.getenv("ANTHROPIC_API_KEY"))    .modelName(CLAUDE_3_5_SONNET_20240620)    .build();model.chat("Say 'Hello World'", new StreamingChatResponseHandler() {    @Override    public void onPartialResponse(String partialResponse) {        // 当有新的部分响应可用时调用此方法。它可以由一个或多个令牌组成。    }    @Override    public void onCompleteResponse(ChatResponse completeResponse) {        // 当模型完成响应时调用此方法    }    @Override    public void onError(Throwable error) {        // 当发生错误时调用此方法    }});

### 自定义 AnthropicStreamingChatModel[​](#自定义-anthropicstreamingchatmodel "自定义 AnthropicStreamingChatModel的直接链接")

与 `AnthropicChatModel` 相同，请参见上文。

工具[​](#工具 "工具的直接链接")
--------------------

Anthropic 在流式和非流式模式下都支持[工具](/tutorials/tools)。

Anthropic 关于工具的文档可以在[这里](https://docs.anthropic.com/claude/docs/tool-use)找到。

缓存[​](#缓存 "缓存的直接链接")
--------------------

`AnthropicChatModel` 和 `AnthropicStreamingChatModel` 支持系统消息和工具的缓存。 默认情况下，缓存是禁用的。 可以通过分别设置 `cacheSystemMessages` 和 `cacheTools` 参数来启用它。

启用后，`cache_control` 块将分别添加到所有系统消息和工具中。

要使用缓存，请设置 `beta("prompt-caching-2024-07-31")`。

`AnthropicChatModel` 和 `AnthropicStreamingChatModel` 在响应中返回 `AnthropicTokenUsage`， 其中包含 `cacheCreationInputTokens` 和 `cacheReadInputTokens`。

有关缓存的更多信息可以在[这里](https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching)找到。

思考[​](#思考 "思考的直接链接")
--------------------

`AnthropicChatModel` 和 `AnthropicStreamingChatModel` 对[思考](https://docs.anthropic.com/en/docs/build-with-claude/extended-thinking)功能有**_有限的_**支持。 可以通过设置 `thinkingType` 和 `thinkingBudgetTokens` 参数来启用它：

    ChatLanguageModel model = AnthropicChatModel.builder()        .apiKey(System.getenv("ANTHROPIC_API_KEY"))        .modelName(CLAUDE_3_7_SONNET_20250219)        .thinkingType("enabled")        .thinkingBudgetTokens(1024)        .maxTokens(1024 + 100)        .logRequests(true)        .logResponses(true)        .build();

当前不支持的内容：

*   无法从 LC4j API 获取思考内容。它只在日志中可见。
*   在多轮对话（使用[记忆](/tutorials/chat-memory)）中，思考内容不会被[保留](https://docs.anthropic.com/en/docs/build-with-claude/extended-thinking#preserving-thinking-blocks)
*   等等

有关思考的更多信息可以在[这里](https://docs.anthropic.com/en/docs/build-with-claude/extended-thinking)找到。

Quarkus[​](#quarkus "Quarkus的直接链接")
------------------------------------

更多详情请参见[这里](https://docs.quarkiverse.io/quarkus-langchain4j/dev/anthropic.html)。

Spring Boot[​](#spring-boot "Spring Boot的直接链接")
-----------------------------------------------

导入 Anthropic 的 Spring Boot 启动器：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-anthropic-spring-boot-starter</artifactId>    <version>1.0.0-beta3</version></dependency>

配置 `AnthropicChatModel` bean：

    langchain4j.anthropic.chat-model.api-key = ${ANTHROPIC_API_KEY}

配置 `AnthropicStreamingChatModel` bean：

    langchain4j.anthropic.streaming-chat-model.api-key = ${ANTHROPIC_API_KEY}

示例[​](#示例 "示例的直接链接")
--------------------

*   [AnthropicChatModelTest](https://github.com/langchain4j/langchain4j-examples/blob/main/anthropic-examples/src/main/java/AnthropicChatModelTest.java)
*   [AnthropicStreamingChatModelTest](https://github.com/langchain4j/langchain4j-examples/blob/main/anthropic-examples/src/main/java/AnthropicStreamingChatModelTest.java)
*   [AnthropicToolsTest](https://github.com/langchain4j/langchain4j-examples/blob/main/anthropic-examples/src/main/java/AnthropicToolsTest.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/anthropic.md)

[

上一页

Amazon Bedrock

](/integrations/language-models/amazon-bedrock)[

下一页

Azure OpenAI

](/integrations/language-models/azure-open-ai)

- [Anthropic](#anthropic)
  - [Maven 依赖​](#maven-依赖)
  - [AnthropicChatModel​](#anthropicchatmodel)
    - [自定义 AnthropicChatModel​](#自定义-anthropicchatmodel)
  - [AnthropicStreamingChatModel​](#anthropicstreamingchatmodel)
    - [自定义 AnthropicStreamingChatModel​](#自定义-anthropicstreamingchatmodel)
  - [工具​](#工具)
  - [缓存​](#缓存)
  - [思考​](#思考)
  - [Quarkus​](#quarkus)
  - [Spring Boot​](#spring-boot)
  - [示例​](#示例)

Docs

*   [介绍](/intro)
*   [快速开始](/get-started)
*   [教程](/category/教程)
*   [集成](/category/集成)
*   [实用资料](/useful-materials)
*   [Examples](https://github.com/langchain4j/langchain4j-examples)
*   [Documentation chatbot (experimental)](https://chat.langchain4j.dev/)
*   [Javadoc](https://docs.langchain4j.dev/apidocs/index.html)

Community

*   [GitHub](https://github.com/langchain4j/langchain4j)
*   [Twitter](https://twitter.com/langchain4j)
*   [Discord](https://discord.com/invite/JzTFvyjG6R)
*   [Stack Overflow](https://stackoverflow.com/questions/tagged/langchain4j)

LangChain4j Documentation 2025. Built with Docusaurus.
