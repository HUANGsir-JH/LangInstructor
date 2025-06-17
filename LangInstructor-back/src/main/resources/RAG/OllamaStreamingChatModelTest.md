# OllamaStreamingChatModelTest.java 示例

**示例目的:**
此文件演示了如何使用 Ollama 本地大模型实现流式对话，实时输出模型生成内容，适合长文本和需要边生成边消费的场景。

**关键类和方法:**
- `OllamaStreamingChatModel`: Ollama 本地流式聊天模型核心类。
- `StreamingChatModel.chat(String, StreamingChatResponseHandler)`: 发送用户消息并以流式方式处理响应。
- `StreamingChatResponseHandler`: 响应处理接口，包含：
    - `onPartialResponse(String)`: 接收部分响应（如新生成的 token）。
    - `onCompleteResponse(ChatResponse)`: 响应生成完成时调用。
    - `onError(Throwable)`: 发生错误时调用。
- `CompletableFuture<ChatResponse>`: 用于异步等待完整响应。

**注意事项:**
- 支持通过环境变量 `OLLAMA_BASE_URL` 指定本地 Ollama 服务地址，否则会自动拉取 Docker 镜像启动容器。
- 适合需要实时输出、流式生成内容的场景，如长文本、对话等。

```java
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.AbstractOllamaInfrastructure;

import java.util.concurrent.CompletableFuture;

@Testcontainers
class OllamaStreamingChatModelTest extends AbstractOllamaInfrastructure {

    /**
     * If you have Ollama running locally,
     * please set the OLLAMA_BASE_URL environment variable (e.g., http://localhost:11434).
     * If you do not set the OLLAMA_BASE_URL environment variable,
     * Testcontainers will download and start Ollama Docker container.
     * It might take a few minutes.
     */

    @Test
    void streaming_example() {

        StreamingChatModel model = OllamaStreamingChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName(MODEL_NAME)
                .build();

        String userMessage = "Write a 100-word poem about Java and AI";

        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();

        model.chat(userMessage, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });

        futureResponse.join();
    }
}
