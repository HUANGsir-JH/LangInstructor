
可观测性
====

聊天模型可观测性[​](#聊天模型可观测性 "聊天模型可观测性的直接链接")
--------------------------------------

[某些](/integrations/language-models) `ChatLanguageModel` 和 `StreamingChatLanguageModel` 的实现 （参见"可观测性"列）允许配置 `ChatModelListener`（多个）来监听事件，例如：

*   向 LLM 发送的请求
*   来自 LLM 的响应
*   错误

这些事件包括各种属性，如 [OpenTelemetry 生成式 AI 语义约定](https://opentelemetry.io/docs/specs/semconv/gen-ai/)中所述，例如：

*   请求：
    *   消息
    *   模型
    *   温度
    *   Top P
    *   最大令牌数
    *   工具
    *   响应格式
    *   等等
*   响应：
    *   助手消息
    *   ID
    *   模型
    *   令牌使用情况
    *   完成原因
    *   等等

以下是使用 `ChatModelListener` 的示例：

    ChatModelListener listener = new ChatModelListener() {    @Override    public void onRequest(ChatModelRequestContext requestContext) {        ChatRequest chatRequest = requestContext.chatRequest();        List<ChatMessage> messages = chatRequest.messages();        System.out.println(messages);        ChatRequestParameters parameters = chatRequest.parameters();        System.out.println(parameters.modelName());        System.out.println(parameters.temperature());        System.out.println(parameters.topP());        System.out.println(parameters.topK());        System.out.println(parameters.frequencyPenalty());        System.out.println(parameters.presencePenalty());        System.out.println(parameters.maxOutputTokens());        System.out.println(parameters.stopSequences());        System.out.println(parameters.toolSpecifications());        System.out.println(parameters.toolChoice());        System.out.println(parameters.responseFormat());        if (parameters instanceof OpenAiChatRequestParameters openAiParameters) {            System.out.println(openAiParameters.maxCompletionTokens());            System.out.println(openAiParameters.logitBias());            System.out.println(openAiParameters.parallelToolCalls());            System.out.println(openAiParameters.seed());            System.out.println(openAiParameters.user());            System.out.println(openAiParameters.store());            System.out.println(openAiParameters.metadata());            System.out.println(openAiParameters.serviceTier());            System.out.println(openAiParameters.reasoningEffort());        }        System.out.println(requestContext.modelProvider());        Map<Object, Object> attributes = requestContext.attributes();        attributes.put("my-attribute", "my-value");    }    @Override    public void onResponse(ChatModelResponseContext responseContext) {        ChatResponse chatResponse = responseContext.chatResponse();        AiMessage aiMessage = chatResponse.aiMessage();        System.out.println(aiMessage);        ChatResponseMetadata metadata = chatResponse.metadata();        System.out.println(metadata.id());        System.out.println(metadata.modelName());        System.out.println(metadata.finishReason());        if (metadata instanceof OpenAiChatResponseMetadata openAiMetadata) {            System.out.println(openAiMetadata.created());            System.out.println(openAiMetadata.serviceTier());            System.out.println(openAiMetadata.systemFingerprint());        }        TokenUsage tokenUsage = metadata.tokenUsage();        System.out.println(tokenUsage.inputTokenCount());        System.out.println(tokenUsage.outputTokenCount());        System.out.println(tokenUsage.totalTokenCount());        if (tokenUsage instanceof OpenAiTokenUsage openAiTokenUsage) {            System.out.println(openAiTokenUsage.inputTokensDetails().cachedTokens());            System.out.println(openAiTokenUsage.outputTokensDetails().reasoningTokens());        }        ChatRequest chatRequest = responseContext.chatRequest();        System.out.println(chatRequest);        System.out.println(responseContext.modelProvider());        Map<Object, Object> attributes = responseContext.attributes();        System.out.println(attributes.get("my-attribute"));    }    @Override    public void onError(ChatModelErrorContext errorContext) {        Throwable error = errorContext.error();        error.printStackTrace();        ChatRequest chatRequest = errorContext.chatRequest();        System.out.println(chatRequest);        System.out.println(errorContext.modelProvider());        Map<Object, Object> attributes = errorContext.attributes();        System.out.println(attributes.get("my-attribute"));    }};ChatLanguageModel model = OpenAiChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName(GPT_4_O_MINI)        .listeners(List.of(listener))        .build();model.chat("Tell me a joke about Java");

`attributes` 映射允许在同一 `ChatModelListener` 的 `onRequest`、`onResponse` 和 `onError` 方法之间传递信息，以及在多个 `ChatModelListener` 之间传递信息。

监听器如何工作[​](#监听器如何工作 "监听器如何工作的直接链接")
-----------------------------------

*   监听器被指定为 `List<ChatModelListener>`，并按照迭代顺序调用。
*   监听器同步调用，并在同一线程中调用。有关流式处理情况的更多详细信息，参见下文。 第二个监听器直到第一个监听器返回后才会被调用。
*   `ChatModelListener.onRequest()` 方法在调用 LLM 提供商 API 之前立即调用。
*   `ChatModelListener.onRequest()` 方法每个请求只调用一次。 如果在调用 LLM 提供商 API 时发生错误并进行重试， `ChatModelListener.onRequest()` 将**_不会_**为每次重试调用。
*   `ChatModelListener.onResponse()` 方法只调用一次， 在从 LLM 提供商收到成功响应后立即调用。
*   `ChatModelListener.onError()` 方法只调用一次。 如果在调用 LLM 提供商 API 时发生错误并进行重试， `ChatModelListener.onError()` 将**_不会_**为每次重试调用。
*   如果从 `ChatModelListener` 方法之一抛出异常， 它将以 `WARN` 级别记录。后续监听器的执行将照常继续。
*   通过 `ChatModelRequestContext`、`ChatModelResponseContext` 和 `ChatModelErrorContext` 提供的 `ChatRequest` 是最终请求，包含在 `ChatLanguageModel` 上配置的默认 `ChatRequestParameters` 和特定于请求的 `ChatRequestParameters` 合并在一起。
*   对于 `StreamingChatLanguageModel`，`ChatModelListener.onResponse()` 和 `ChatModelListener.onError()` 在与 `ChatModelListener.onRequest()` 不同的线程上调用。 线程上下文目前不会自动传播，因此您可能希望使用 `attributes` 映射 从 `ChatModelListener.onRequest()` 传播任何必要的数据到 `ChatModelListener.onResponse()` 或 `ChatModelListener.onError()`。
*   对于 `StreamingChatLanguageModel`，`ChatModelListener.onResponse()` 在 `StreamingChatResponseHandler.onCompleteResponse()` 被调用之前调用。`ChatModelListener.onError()` 在 `StreamingChatResponseHandler.onError()` 被调用之前调用。

Spring Boot 应用程序中的可观测性[​](#spring-boot-应用程序中的可观测性 "Spring Boot 应用程序中的可观测性的直接链接")
--------------------------------------------------------------------------------

更多详细信息请参见[此处](/tutorials/spring-boot-integration#observability)。

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/observability.md)