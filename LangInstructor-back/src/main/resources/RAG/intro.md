
介绍
==

欢迎！

LangChain4j 的目标是简化将 LLM 集成到 Java 应用程序中的过程。

具体方式如下：

1.  **统一 API：** LLM 提供商（如 OpenAI 或 Google Vertex AI）和嵌入（向量）存储（如 Pinecone 或 Milvus） 使用专有 API。LangChain4j 提供统一的 API，避免了学习和实现每个特定 API 的需求。 要尝试不同的 LLM 或嵌入存储，您可以在它们之间轻松切换，无需重写代码。 LangChain4j 目前支持 [15+ 个流行的 LLM 提供商](/integrations/language-models/) 和 [20+ 个嵌入存储](/integrations/embedding-stores/)。
2.  **全面的工具箱：** 自 2023 年初以来，社区一直在构建众多 LLM 驱动的应用程序， 识别常见的抽象、模式和技术。LangChain4j 将这些提炼成一个即用型包。 我们的工具箱包含从低级提示模板、聊天记忆管理和函数调用 到高级模式如代理和 RAG 的工具。 对于每个抽象，我们提供一个接口以及基于常见技术的多个即用型实现。 无论您是在构建聊天机器人还是开发包含从数据摄取到检索完整管道的 RAG， LangChain4j 都提供多种选择。
3.  **丰富的示例：** 这些[示例](https://github.com/langchain4j/langchain4j-examples)展示了如何开始创建各种 LLM 驱动的应用程序， 提供灵感并使您能够快速开始构建。

LangChain4j 始于 2023 年初 ChatGPT 热潮期间。 我们注意到与众多 Python 和 JavaScript LLM 库和框架相比，缺少 Java 对应物， 我们必须解决这个问题！ 虽然我们的名字中有"LangChain"，但该项目是 LangChain、Haystack、 LlamaIndex 和更广泛社区的想法和概念的融合，并加入了我们自己的创新。

我们积极关注社区发展，旨在快速整合新技术和集成， 确保您保持最新状态。 该库正在积极开发中。虽然一些功能仍在开发中， 但核心功能已经就位，让您现在就可以开始构建 LLM 驱动的应用程序！

为了更容易集成，LangChain4j 还包括与 [Quarkus](/tutorials/quarkus-integration) 和 [Spring Boot](/tutorials/spring-boot-integration) 的集成。

LangChain4j 功能[​](#langchain4j-功能 "LangChain4j 功能的直接链接")
--------------------------------------------------------

*   集成 [15+ 个 LLM 提供商](/integrations/language-models)
*   集成 [20+ 个嵌入（向量）存储](/integrations/embedding-stores)
*   集成 [15+ 个嵌入模型](/category/embedding-models)
*   集成 [5 个图像生成模型](/category/image-models)
*   集成 [2 个评分（重排序）模型](/category/scoring-reranking-models)
*   集成一个审核模型（OpenAI）
*   支持文本和图像作为输入（多模态）
*   [AI 服务](/tutorials/ai-services)（高级 LLM API）
*   提示模板
*   实现持久化和内存中的[聊天记忆](/tutorials/chat-memory)算法：消息窗口和令牌窗口
*   [从 LLM 流式传输响应](/tutorials/response-streaming)
*   用于常见 Java 类型和自定义 POJO 的输出解析器
*   [工具（函数调用）](/tutorials/tools)
*   动态工具（执行动态生成的 LLM 代码）
*   [RAG（检索增强生成）](/tutorials/rag)：
    *   摄取：
        *   从多个来源（文件系统、URL、GitHub、Azure Blob Storage、Amazon S3 等）导入各种类型的文档（TXT、PDF、DOC、PPT、XLS 等）
        *   使用多种分割算法将文档分割成更小的段落
        *   文档和段落的后处理
        *   使用嵌入模型嵌入段落
        *   在嵌入（向量）存储中存储嵌入
    *   检索（简单和高级）：
        *   查询转换（扩展、压缩）
        *   查询路由
        *   从向量存储和/或任何自定义源检索
        *   重排序
        *   倒数排名融合
        *   自定义 RAG 流程中的每个步骤
*   文本分类
*   用于分词和估算令牌数的工具
*   [Kotlin 扩展](/tutorials/kotlin)：使用 Kotlin 的协程功能异步非阻塞处理聊天交互。

2 个抽象层次[​](#2-个抽象层次 "2 个抽象层次的直接链接")
-----------------------------------

LangChain4j 在两个抽象层次上运行：

*   低层次。在这个层次上，您拥有最大的自由度和访问所有低级组件的权限，如 [ChatLanguageModel](/tutorials/chat-and-language-models)、`UserMessage`、`AiMessage`、`EmbeddingStore`、`Embedding` 等。 这些是您的 LLM 驱动应用程序的"原语"。 您可以完全控制如何组合它们，但需要编写更多的粘合代码。
*   高层次。在这个层次上，您使用高级 API（如 [AI 服务](/tutorials/ai-services)）与 LLM 交互， 它隐藏了所有复杂性和样板代码。 您仍然可以灵活地调整和微调行为，但是以声明式方式完成。

[![](/assets/images/langchain4j-components-76269e10e1cf4146cdf0cfe552ab6c4d.png)](/intro)

LangChain4j 库结构[​](#langchain4j-库结构 "LangChain4j 库结构的直接链接")
-----------------------------------------------------------

LangChain4j 采用模块化设计，包括：

*   `langchain4j-core` 模块，定义核心抽象（如 `ChatLanguageModel` 和 `EmbeddingStore`）及其 API。
*   主要的 `langchain4j` 模块，包含有用的工具，如文档加载器、[聊天记忆](/tutorials/chat-memory)实现以及 [AI 服务](/tutorials/ai-services)等高级功能。
*   大量的 `langchain4j-{integration}` 模块，每个模块提供与各种 LLM 提供商和嵌入存储的集成到 LangChain4j。 您可以独立使用 `langchain4j-{integration}` 模块。要获得额外功能，只需导入主要的 `langchain4j` 依赖项。

LangChain4j 仓库[​](#langchain4j-仓库 "LangChain4j 仓库的直接链接")
--------------------------------------------------------

*   [主仓库](https://github.com/langchain4j/langchain4j)
*   [Quarkus 扩展](https://github.com/quarkiverse/quarkus-langchain4j)
*   [Spring Boot 集成](https://github.com/langchain4j/langchain4j-spring)
*   [社区集成](https://github.com/langchain4j/langchain4j-community)
*   [示例](https://github.com/langchain4j/langchain4j-examples)
*   [社区资源](https://github.com/langchain4j/langchain4j-community-resources)
*   [进程内嵌入](https://github.com/langchain4j/langchain4j-embeddings)

使用场景[​](#使用场景 "使用场景的直接链接")
--------------------------

您可能会问为什么需要这些？ 以下是一些示例：

*   您想实现一个自定义的 AI 驱动的聊天机器人，它可以访问您的数据并按照您想要的方式行事：
    *   客户支持聊天机器人可以：
        *   礼貌地回答客户问题
        *   接受/更改/取消订单
    *   教育助手可以：
        *   教授各种科目
        *   解释不清楚的部分
        *   评估用户的理解/知识
*   您想处理大量非结构化数据（文件、网页等）并从中提取结构化信息。 例如：
    *   从客户评论和支持聊天历史中提取见解
    *   从竞争对手的网站提取有趣信息
    *   从求职者的简历中提取见解
*   您想生成信息，例如：
    *   为每个客户定制的电子邮件
    *   为您的应用/网站生成内容：
        *   博客文章
        *   故事
*   您想转换信息，例如：
    *   总结
    *   校对和重写
    *   翻译

社区集成[​](#社区集成 "社区集成的直接链接")
--------------------------

LangChain4j 在[社区仓库](https://github.com/langchain4j/langchain4j-community)中维护一些集成。 它们支持与主仓库中的集成相同的功能。 它们之间的唯一区别是社区集成具有不同的构件和包名（即构件和包名中有 `community` 前缀）。 创建社区是为了分离某些集成的维护工作，从而使主仓库更容易维护。

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/intro.md)
