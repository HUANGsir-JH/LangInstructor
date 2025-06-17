Agents | LangChain4j 中文文档  function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag("js",new Date),gtag("config","G-46M4FH02G5",{anonymize_ip:!0})  !function(){function t(t){document.documentElement.setAttribute("data-theme",t)}var e=function(){try{return new URLSearchParams(window.location.search).get("docusaurus-theme")}catch(t){}}()||function(){try{return window.localStorage.getItem("theme")}catch(t){}}();t(null!==e?e:"light")}(),function(){try{const n=new URLSearchParams(window.location.search).entries();for(var[t,e]of n)if(t.startsWith("docusaurus-data-")){var a=t.replace("docusaurus-data-","data-");document.documentElement.setAttribute(a,e)}}catch(t){}}()

[跳到主要内容](#__docusaurus_skipToContent_fallback)

[

![LangChain4j Logo](/img/logo.svg)![LangChain4j Logo](/img/logo.svg)

**LangChain4j**](/)[介绍](/intro)[快速开始](/get-started)[教程](/category/教程)[集成](/category/集成)[实用资料](/useful-materials)[Examples](https://github.com/langchain4j/langchain4j-examples)[Docu chatbot](https://chat.langchain4j.dev/)[Javadoc](https://docs.langchain4j.dev/apidocs/index.html)

[GitHub](https://github.com/langchain4j/langchain4j)[Twitter](https://twitter.com/langchain4j)[Discord](https://discord.com/invite/JzTFvyjG6R)

*   [介绍](/intro)
*   [开始使用](/get-started)
*   [教程](/category/教程)
    
    *   [聊天和语言模型](/tutorials/chat-and-language-models)
    *   [聊天记忆](/tutorials/chat-memory)
    *   [模型参数](/tutorials/model-parameters)
    *   [响应流式传输](/tutorials/response-streaming)
    *   [AI Services](/tutorials/ai-services)
    *   [Agents](/tutorials/agents)
    *   [工具（函数调用）](/tutorials/tools)
    *   [RAG (检索增强生成)](/tutorials/rag)
    *   [结构化输出](/tutorials/structured-outputs)
    *   [分类](/tutorials/classification)
    *   [嵌入（向量）存储](/tutorials/embedding-stores)
    *   [图像模型](/tutorials/image-models)
    *   [Quarkus 集成](/tutorials/quarkus-integration)
    *   [Spring Boot 集成](/tutorials/spring-boot-integration)
    *   [Kotlin 支持](/tutorials/kotlin)
    *   [日志记录](/tutorials/logging)
    *   [可观测性](/tutorials/observability)
    *   [可自定义的 HTTP 客户端](/tutorials/customizable-http-client)
    *   [测试与评估](/tutorials/testing-and-evaluation)
    *   [模型上下文协议 (MCP)](/tutorials/mcp)
*   [集成](/category/集成)
    
*   [实用资料](/category/实用资料)
    
*   [Latest Release Notes](/latest-release-notes)

*   [](/)
*   [教程](/category/教程)
*   Agents

本页总览

Agents
======

备注

请注意，"Agent"（智能代理）是一个非常广泛的术语，有多种定义。

推荐阅读[​](#推荐阅读 "推荐阅读的直接链接")
--------------------------

*   Anthropic 的 [构建有效的智能代理](https://www.anthropic.com/research/building-effective-agents)

Agent[​](#agent "Agent的直接链接")
-----------------------------

大多数基本的"Agent"功能可以使用 _high-level_ [AI Service](/tutorials/ai-services)和[Tool](/tutorials/tools#high-level-tool-api) API 构建。

如果您需要更多灵活性，可以使用low-level [ChatLanguageModel](/tutorials/chat-and-language-models)、[ToolSpecification](/tutorials/tools#low-level-tool-api) 和 [ChatMemory](/tutorials/chat-memory) API。

Multi-Agent[​](#multi-agent "Multi-Agent的直接链接")
-----------------------------------------------

LangChain4j 不支持像 [AutoGen](https://github.com/microsoft/autogen) 或 [CrewAI](https://www.crewai.com/) 中的 _high-level_ 抽象概念（如"agent"）来构建多代理系统。

然而，您仍然可以通过使用低级 [ChatLanguageModel](/tutorials/chat-and-language-models)、[ToolSpecification](/tutorials/tools#low-level-tool-api) 和 [ChatMemory](/tutorials/chat-memory) API 来构建多代理系统。

示例[​](#示例 "示例的直接链接")
--------------------

*   [客户支持代理](https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/test/java/dev/langchain4j/example/CustomerSupportAgentIT.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/agents.mdx)

[

上一页

AI Services

](/tutorials/ai-services)[

下一页

工具（函数调用）

](/tutorials/tools)

- [Agents](#agents)
  - [推荐阅读​](#推荐阅读)
  - [Agent​](#agent)
  - [Multi-Agent​](#multi-agent)
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
