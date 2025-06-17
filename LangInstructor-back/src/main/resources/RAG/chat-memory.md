

聊天记忆
====

手动维护和管理`ChatMessage`是很麻烦的。 因此，LangChain4j提供了`ChatMemory`抽象以及多种开箱即用的实现。

`ChatMemory`可以作为独立的低级组件使用， 或者作为高级组件（如[AI服务](/tutorials/ai-services)）的一部分。

`ChatMemory`作为`ChatMessage`的容器（由`List`支持），具有以下额外功能：

*   淘汰策略
*   持久化
*   对`SystemMessage`的特殊处理
*   对[工具](/tutorials/tools)消息的特殊处理

记忆与历史[​](#记忆与历史 "记忆与历史的直接链接")
-----------------------------

请注意，"记忆"和"历史"是相似但不同的概念。

*   历史保持用户和AI之间**所有**消息**完整无缺**。历史是用户在UI中看到的内容。它代表实际对话内容。
*   记忆保存**一些信息**，这些信息呈现给LLM，使其表现得好像"记住"了对话。 记忆与历史有很大不同。根据使用的记忆算法，它可以以各种方式修改历史： 淘汰一些消息，总结多条消息，总结单独的消息，从消息中删除不重要的细节， 向消息中注入额外信息（例如，用于RAG）或指令（例如，用于结构化输出）等等。

LangChain4j目前只提供"记忆"，而不是"历史"。如果您需要保存完整的历史记录，请手动进行。

淘汰策略[​](#淘汰策略 "淘汰策略的直接链接")
--------------------------

淘汰策略是必要的，原因如下：

*   为了适应LLM的上下文窗口。LLM一次可以处理的令牌数量是有上限的。 在某些时候，对话可能会超过这个限制。在这种情况下，应该淘汰一些消息。 通常，最旧的消息会被淘汰，但如果需要，可以实现更复杂的算法。
*   控制成本。每个令牌都有成本，使每次调用LLM的费用逐渐增加。 淘汰不必要的消息可以降低成本。
*   控制延迟。发送给LLM的令牌越多，处理它们所需的时间就越长。

目前，LangChain4j提供了2种开箱即用的实现：

*   较简单的一种，`MessageWindowChatMemory`，作为滑动窗口运行， 保留最近的`N`条消息，并淘汰不再适合的旧消息。 然而，由于每条消息可能包含不同数量的令牌， `MessageWindowChatMemory`主要用于快速原型设计。
*   更复杂的选项是`TokenWindowChatMemory`， 它也作为滑动窗口运行，但专注于保留最近的`N`个**令牌**， 根据需要淘汰旧消息。 消息是不可分割的。如果一条消息不适合，它会被完全淘汰。 `TokenWindowChatMemory`需要一个`Tokenizer`来计算每个`ChatMessage`中的令牌数。

持久化[​](#持久化 "持久化的直接链接")
-----------------------

默认情况下，`ChatMemory`实现在内存中存储`ChatMessage`。

如果需要持久化，可以实现自定义的`ChatMemoryStore`， 将`ChatMessage`存储在您选择的任何持久化存储中：

    class PersistentChatMemoryStore implements ChatMemoryStore {        @Override        public List<ChatMessage> getMessages(Object memoryId) {          // TODO: 实现通过内存ID从持久化存储中获取所有消息。          // 可以使用ChatMessageDeserializer.messageFromJson(String)和          // ChatMessageDeserializer.messagesFromJson(String)辅助方法          // 轻松地从JSON反序列化聊天消息。        }        @Override        public void updateMessages(Object memoryId, List<ChatMessage> messages) {            // TODO: 实现通过内存ID更新持久化存储中的所有消息。            // 可以使用ChatMessageSerializer.messageToJson(ChatMessage)和            // ChatMessageSerializer.messagesToJson(List<ChatMessage>)辅助方法            // 轻松地将聊天消息序列化为JSON。        }        @Override        public void deleteMessages(Object memoryId) {          // TODO: 实现通过内存ID删除持久化存储中的所有消息。        }    }ChatMemory chatMemory = MessageWindowChatMemory.builder()        .id("12345")        .maxMessages(10)        .chatMemoryStore(new PersistentChatMemoryStore())        .build();

每当向`ChatMemory`添加新的`ChatMessage`时，都会调用`updateMessages()`方法。 这通常在与LLM的每次交互中发生两次： 一次是添加新的`UserMessage`时，另一次是添加新的`AiMessage`时。 `updateMessages()`方法预期会更新与给定内存ID关联的所有消息。 `ChatMessage`可以单独存储（例如，每条消息一条记录/行/对象） 或一起存储（例如，整个`ChatMemory`一条记录/行/对象）。

备注

请注意，从`ChatMemory`中淘汰的消息也将从`ChatMemoryStore`中淘汰。 当消息被淘汰时，会调用`updateMessages()`方法， 传入不包含被淘汰消息的消息列表。

当`ChatMemory`的用户请求所有消息时，会调用`getMessages()`方法。 这通常在与LLM的每次交互中发生一次。 `Object memoryId`参数的值对应于创建`ChatMemory`时指定的`id`。 它可以用来区分多个用户和/或对话。 `getMessages()`方法预期会返回与给定内存ID关联的所有消息。

当调用`ChatMemory.clear()`时，会调用`deleteMessages()`方法。 如果您不使用此功能，可以将此方法留空。

对`SystemMessage`的特殊处理[​](#对systemmessage的特殊处理 "对systemmessage的特殊处理的直接链接")
-------------------------------------------------------------------------

`SystemMessage`是一种特殊类型的消息，因此它的处理方式与其他消息类型不同：

*   一旦添加，`SystemMessage`总是被保留。
*   一次只能保存一条`SystemMessage`。
*   如果添加了具有相同内容的新`SystemMessage`，它会被忽略。
*   如果添加了具有不同内容的新`SystemMessage`，它会替换之前的消息。

对工具消息的特殊处理[​](#对工具消息的特殊处理 "对工具消息的特殊处理的直接链接")
--------------------------------------------

如果包含`ToolExecutionRequest`的`AiMessage`被淘汰， 随后的孤立`ToolExecutionResultMessage`也会自动被淘汰， 以避免与某些LLM提供商（如OpenAI）出现问题， 这些提供商禁止在请求中发送孤立的`ToolExecutionResultMessage`。

示例[​](#示例 "示例的直接链接")
--------------------

*   使用`AiServices`：
    *   [聊天记忆](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithMemoryExample.java)
    *   [为每个用户提供单独的聊天记忆](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithMemoryForEachUserExample.java)
    *   [持久化聊天记忆](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithPersistentMemoryExample.java)
    *   [为每个用户提供持久化聊天记忆](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithPersistentMemoryForEachUserExample.java)
*   使用传统的`Chain`：
    *   [ConversationalChain的聊天记忆](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ChatMemoryExamples.java)
    *   [ConversationalRetrievalChain的聊天记忆](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ChatWithDocumentsExamples.java)

相关教程[​](#相关教程 "相关教程的直接链接")
--------------------------

*   [使用LangChain4j ChatMemory的生成式AI对话](https://www.sivalabs.in/generative-ai-conversations-using-langchain4j-chat-memory/)，作者：[Siva](https://www.sivalabs.in/)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/chat-memory.md)
