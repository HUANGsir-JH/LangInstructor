
聊天和语言模型
=======

备注

本页描述的是低级 LLM API。 有关高级 LLM API，请参阅 [AI 服务](/tutorials/ai-services)。

备注

所有支持的 LLM 可以在[这里](/integrations/language-models)找到。

LLM 目前有两种 API 类型：

*   `LanguageModel`。它们的 API 非常简单 - 接受 `String` 作为输入并返回 `String` 作为输出。 这种 API 现在正在被聊天 API（第二种 API 类型）所取代。
*   `ChatLanguageModel`。这些接受多个 `ChatMessage` 作为输入并返回单个 `AiMessage` 作为输出。 `ChatMessage` 通常包含文本，但某些 LLM 也支持其他模态（例如，图像、音频等）。 这类聊天模型的例子包括 OpenAI 的 `gpt-4o-mini` 和 Google 的 `gemini-1.5-pro`。

LangChain4j 不会再扩展对 `LanguageModel` 的支持， 因此在所有新功能中，我们将使用 `ChatLanguageModel` API。

`ChatLanguageModel` 是 LangChain4j 中与 LLM 交互的低级 API，提供最大的能力和灵活性。 还有一个高级 API（[AI 服务](/tutorials/ai-services)），我们将在介绍完基础知识后再讨论。

除了 `ChatLanguageModel` 和 `LanguageModel` 外，LangChain4j 还支持以下类型的模型：

*   `EmbeddingModel` - 这种模型可以将文本转换为 `Embedding`。
*   `ImageModel` - 这种模型可以生成和编辑 `Image`。
*   `ModerationModel` - 这种模型可以检查文本是否包含有害内容。
*   `ScoringModel` - 这种模型可以对查询的多个文本片段进行评分（或排名）， 本质上确定每个文本片段与查询的相关性。这对 [RAG](/tutorials/rag) 很有用。 这些将在后面介绍。

现在，让我们仔细看看 `ChatLanguageModel` API。

    public interface ChatLanguageModel {    String chat(String userMessage);        ...}

如您所见，有一个简单的 `chat` 方法，它接受 `String` 作为输入并返回 `String` 作为输出，类似于 `LanguageModel`。 这只是一个便捷方法，让您可以快速轻松地进行试验，而无需将 `String` 包装在 `UserMessage` 中。

以下是其他聊天 API 方法：

        ...        ChatResponse chat(ChatMessage... messages);    ChatResponse chat(List<ChatMessage> messages);            ...

这些版本的 `chat` 方法接受一个或多个 `ChatMessage` 作为输入。 `ChatMessage` 是表示聊天消息的基本接口。 下一节将提供有关聊天消息的更多详细信息。

如果您希望自定义请求（例如，指定温度、工具或 JSON 模式等）， 您可以使用 `chat(ChatRequest)` 方法：

        ...        ChatResponse chat(ChatRequest chatRequest);            ...

    ChatRequest request = ChatRequest.builder()    .messages(UserMessage.from())    .parameters(ChatRequestParameters.builder()        .temperature(0.5)        .toolSpecifications(toolSpecifications)        .build())    .build();

### `ChatMessage` 的类型[​](#chatmessage-的类型 "chatmessage-的类型的直接链接")

目前有四种类型的聊天消息，每种对应消息的一个"来源"：

*   `UserMessage`：这是来自用户的消息。 用户可以是您应用程序的最终用户（人类）或您的应用程序本身。 根据 LLM 支持的模态，`UserMessage` 可以只包含文本（`String`）， 或[其他模态](/tutorials/chat-and-language-models#multimodality)。
*   `AiMessage`：这是由 AI 生成的消息，通常是对 `UserMessage` 的回应。 正如您可能已经注意到的，generate 方法返回一个包装在 `Response` 中的 `AiMessage`。 `AiMessage` 可以包含文本响应（`String`）或执行工具的请求（`ToolExecutionRequest`）。 我们将在[另一节](/tutorials/tools)中探讨工具。
*   `ToolExecutionResultMessage`：这是 `ToolExecutionRequest` 的结果。
*   `SystemMessage`：这是来自系统的消息。 通常，您作为开发人员应该定义此消息的内容。 通常，您会在这里写入关于 LLM 角色是什么、它应该如何行为、以什么风格回答等指令。 LLM 被训练为比其他类型的消息更加关注 `SystemMessage`， 所以要小心，最好不要让最终用户自由定义或在 `SystemMessage` 中注入一些输入。 通常，它位于对话的开始。
*   `CustomMessage`：这是一个可以包含任意属性的自定义消息。这种消息类型只能由 支持它的 `ChatLanguageModel` 实现使用（目前只有 Ollama）。

现在我们了解了所有类型的 `ChatMessage`，让我们看看如何在对话中组合它们。

在最简单的情况下，我们可以向 `chat` 方法提供单个 `UserMessage` 实例。 这类似于第一个版本的 `chat` 方法，它接受 `String` 作为输入。 主要区别在于它现在返回的不是 `String`，而是 `ChatResponse`。 除了 `AiMessage` 外，`ChatResponse` 还包含 `ChatResponseMetadata`。 `ChatResponseMetadata` 包含 `TokenUsage`，其中包含有关输入包含多少令牌的统计信息 （您提供给 generate 方法的所有 `ChatMessages`）， 输出（在 `AiMessage` 中）生成了多少令牌，以及总计（输入 + 输出）。 您需要这些信息来计算给定 LLM 调用的成本。 然后，`ChatResponseMetadata` 还包含 `FinishReason`， 这是一个枚举，包含生成停止的各种原因。 通常，如果 LLM 自己决定停止生成，它将是 `FinishReason.STOP`。

创建 `UserMessage` 有多种方法，取决于内容。 最简单的是 `new UserMessage("Hi")` 或 `UserMessage.from("Hi")`。

### 多个 `ChatMessage`[​](#多个-chatmessage "多个-chatmessage的直接链接")

现在，为什么您需要提供多个 `ChatMessage` 作为输入，而不是只有一个？ 这是因为 LLM 本质上是无状态的，意味着它们不维护对话的状态。 因此，如果您想支持多轮对话，您应该负责管理对话的状态。

假设您想构建一个聊天机器人。想象一个用户和聊天机器人（AI）之间的简单多轮对话：

*   用户：你好，我的名字是 Klaus
*   AI：嗨 Klaus，我能帮你什么忙？
*   用户：我的名字是什么？
*   AI：Klaus

这就是与 `ChatLanguageModel` 交互的样子：

    UserMessage firstUserMessage = UserMessage.from("Hello, my name is Klaus");AiMessage firstAiMessage = model.chat(firstUserMessage).aiMessage(); // Hi Klaus, how can I help you?UserMessage secondUserMessage = UserMessage.from("What is my name?");AiMessage secondAiMessage = model.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage(); // Klaus

如您所见，在第二次调用 `chat` 方法时，我们提供的不仅仅是单个 `secondUserMessage`， 还有对话中之前的消息。

手动维护和管理这些消息很麻烦。 因此，存在 `ChatMemory` 的概念，我们将在[下一节](/tutorials/chat-memory)中探讨。

### 多模态[​](#多模态 "多模态的直接链接")

`UserMessage` 不仅可以包含文本，还可以包含其他类型的内容。 `UserMessage` 包含 `List<Content> contents`。 `Content` 是一个接口，有以下实现：

*   `TextContent`
*   `ImageContent`
*   `AudioContent`
*   `VideoContent`
*   `PdfFileContent`

您可以在[这里](/integrations/language-models)的比较表中查看哪些 LLM 提供商支持哪些模态。

以下是向 LLM 发送文本和图像的示例：

    UserMessage userMessage = UserMessage.from(    TextContent.from("Describe the following image"),    ImageContent.from("https://example.com/cat.jpg"));ChatResponse response = model.chat(userMessage);

#### 文本内容[​](#文本内容 "文本内容的直接链接")

`TextContent` 是最简单的 `Content` 形式，表示纯文本并包装单个 `String`。 `UserMessage.from(TextContent.from("Hello!"))` 等同于 `UserMessage.from("Hello!")`。

可以在 `UserMessage` 中提供一个或多个 `TextContent`：

    UserMessage userMessage = UserMessage.from(    TextContent.from("Hello!"),    TextContent.from("How are you?"));

#### 图像内容[​](#图像内容 "图像内容的直接链接")

根据 LLM 提供商的不同，`ImageContent` 可以从**远程**图像的 URL 创建（见上面的示例）， 或从 Base64 编码的二进制数据创建：

    byte[] imageBytes = readBytes("/home/me/cat.jpg");String base64Data = Base64.getEncoder().encodeToString(imageBytes);ImageContent imageContent = ImageContent.from(base64Data, "image/jpg");UserMessage userMessage = UserMessage.from(imageContent);

还可以指定 `DetailLevel` 枚举（带有 `LOW`/`HIGH`/`AUTO` 选项）来控制模型如何处理图像。 更多详情请参见[这里](https://platform.openai.com/docs/guides/vision#low-or-high-fidelity-image-understanding)。

#### 音频内容[​](#音频内容 "音频内容的直接链接")

`AudioContent` 类似于 `ImageContent`，但表示音频内容。

#### 视频内容[​](#视频内容 "视频内容的直接链接")

`VideoContent` 类似于 `ImageContent`，但表示视频内容。

#### PDF 文件内容[​](#pdf-文件内容 "PDF 文件内容的直接链接")

`PdfFileContent` 类似于 `ImageContent`，但表示 PDF 文件的二进制内容。

### Kotlin 扩展[​](#kotlin-扩展 "Kotlin 扩展的直接链接")

`ChatLanguageModel` [Kotlin 扩展](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-core/src/main/kotlin/dev/langchain4j/model/chat/ChatLanguageModelExtensions.kt)提供了用于处理与语言模型聊天交互的异步方法，利用 Kotlin 的[协程](https://kotlinlang.org/docs/coroutines-guide.html)功能。`chatAsync` 方法允许非阻塞处理 `ChatRequest` 或 `ChatRequest.Builder` 配置，返回带有模型回复的 `ChatResponse`。类似地，`generateAsync` 处理聊天消息的异步响应生成。这些扩展简化了在 Kotlin 应用程序中高效构建聊天请求和处理对话的过程。请注意，这些方法被标记为实验性，可能会随时间演变。

**`ChatLanguageModel.chatAsync(request: ChatRequest)`**：专为 Kotlin 协程设计，这个_异步_扩展函数使用 `Dispatchers.IO` 在协程作用域内包装同步 `chat` 方法。这使得非阻塞操作成为可能，对于保持应用程序响应性至关重要。它被命名为 `chatAsync` 专门是为了避免与现有的同步 `chat` 冲突。其函数签名是：`suspend fun ChatLanguageModel.chatAsync(request: ChatRequest): ChatResponse`。关键字 `suspend` 将其指定为协程函数。

**`ChatLanguageModel.chat(block: ChatRequestBuilder.() -> Unit)`**：这个 `chat` 变体通过使用 Kotlin 的类型安全构建器 DSL 提供了更简化的方法。它简化了构建 `ChatRequest` 对象的过程，同时内部使用 `chatAsync` 进行异步执行。这个版本通过协程提供了简洁性和非阻塞行为。

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/chat-and-language-models.mdx)
