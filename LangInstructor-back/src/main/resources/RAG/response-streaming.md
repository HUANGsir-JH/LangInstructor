
响应流式传输
======

备注

本页描述了使用低级 LLM API 的响应流式传输。 有关高级 LLM API，请参阅 [AI 服务](/tutorials/ai-services#streaming)。

LLM 一次生成一个标记（token），因此许多 LLM 提供商提供了一种方式，可以逐个标记地流式传输响应，而不是等待整个文本生成完毕。 这显著改善了用户体验，因为用户不需要等待未知的时间，几乎可以立即开始阅读响应。

对于 `ChatLanguageModel` 和 `LanguageModel` 接口，有相应的 `StreamingChatLanguageModel` 和 `StreamingLanguageModel` 接口。 这些接口有类似的 API，但可以流式传输响应。 它们接受 `StreamingChatResponseHandler` 接口的实现作为参数。

    public interface StreamingChatResponseHandler {    void onPartialResponse(String partialResponse);    void onCompleteResponse(ChatResponse completeResponse);    void onError(Throwable error);}

通过实现 `StreamingChatResponseHandler`，您可以为以下事件定义操作：

*   当生成下一个部分响应时：调用 `onPartialResponse(String partialResponse)`。 部分响应可以由单个或多个标记组成。 例如，您可以在标记可用时立即将其发送到 UI。
*   当 LLM 完成生成时：调用 `onCompleteResponse(ChatResponse completeResponse)`。 `ChatResponse` 对象包含完整的响应（`AiMessage`）以及 `ChatResponseMetadata`。
*   当发生错误时：调用 `onError(Throwable error)`。

以下是如何使用 `StreamingChatLanguageModel` 实现流式传输的示例：

    StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()    .apiKey(System.getenv("OPENAI_API_KEY"))    .modelName(GPT_4_O_MINI)    .build();String userMessage = "Tell me a joke";model.chat(userMessage, new StreamingChatResponseHandler() {    @Override    public void onPartialResponse(String partialResponse) {        System.out.println("onPartialResponse: " + partialResponse);    }    @Override    public void onCompleteResponse(ChatResponse completeResponse) {        System.out.println("onCompleteResponse: " + completeResponse);    }    @Override    public void onError(Throwable error) {        error.printStackTrace();    }});

一种更紧凑的流式传输响应的方式是使用 `LambdaStreamingResponseHandler` 类。 这个工具类提供了使用 lambda 表达式创建 `StreamingChatResponseHandler` 的静态方法。 使用 lambda 流式传输响应的方式非常简单。 您只需调用 `onPartialResponse()` 静态方法，并传入一个定义如何处理部分响应的 lambda 表达式：

    import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponse;model.chat("Tell me a joke", onPartialResponse(System.out::print));

`onPartialResponseAndError()` 方法允许您为 `onPartialResponse()` 和 `onError()` 事件定义操作：

    import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponseAndError;model.chat("Tell me a joke", onPartialResponseAndError(System.out::print, Throwable::printStackTrace));

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/response-streaming.md)
