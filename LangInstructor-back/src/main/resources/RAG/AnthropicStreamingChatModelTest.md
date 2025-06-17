# AnthropicStreamingChatModelTest.java 示例

**示例目的:**
此示例演示了 `AnthropicStreamingChatModel` 的流式聊天功能。它展示了如何逐个接收模型生成的令牌，并在完成时处理完整的响应。

**关键类和方法:**
- `AnthropicStreamingChatModel`: LangChain4j 中用于与 Anthropic Claude 模型进行流式聊天交互的模型。
- `chat(String userMessage, StreamingChatResponseHandler handler)`: 用于发送文本消息并以流式方式接收响应。
- `StreamingChatResponseHandler`: 一个接口，定义了处理流式响应的方法：
    - `onPartialResponse(String partialResponse)`: 当接收到部分响应（新令牌）时调用。
    - `onCompleteResponse(ChatResponse completeResponse)`: 当模型完成生成所有响应时调用。
    - `onError(Throwable error)`: 当流式传输过程中发生错误时调用。
- `CompletableFuture`: 用于异步处理流式响应的完成或错误。

**注意事项:**
- 运行此示例需要有效的 `ANTHROPIC_API_KEY` 环境变量。
- `modelName` 可以根据需要选择不同的 Anthropic Claude 模型。
- `logRequests(true)` 可以启用请求的日志记录，便于调试。
- `CompletableFuture` 用于等待流式响应的完成，确保测试在收到完整响应后才结束。

```java
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class AnthropicStreamingChatModelTest {

    StreamingChatModel model = AnthropicStreamingChatModel.builder()
            // API key can be created here: https://console.anthropic.com/settings/keys
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName("claude-3-haiku-20240307")
            .logRequests(true)
            // Other parameters can be set as well
            .build();

    @Test
    void AnthropicChatModel_Example() throws ExecutionException, InterruptedException {

        CompletableFuture<ChatResponse> future = new CompletableFuture<>();

        model.chat("What is the capital of Germany?", new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println("New token: '" + partialResponse + "'");
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("Streaming completed: " + completeResponse);
                future.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                future.completeExceptionally(error);
            }
        });

        assertThat(future.get().aiMessage().text()).containsIgnoringCase("Berlin");
    }
}
