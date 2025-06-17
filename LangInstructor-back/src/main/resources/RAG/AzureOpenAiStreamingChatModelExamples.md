# AzureOpenAiStreamingChatModelExamples.java 示例

**示例目的:**
此文件演示了如何使用 Azure OpenAI 流式聊天模型，实现对用户输入的流式响应（如分词输出诗歌）。

**关键类和方法:**
- `AzureOpenAiStreamingChatModel`: 用于与 Azure OpenAI 流式聊天模型交互的核心类。
- `builder()`: 构建模型实例，支持链式设置参数。
    - `apiKey(String)`、`endpoint(String)`、`deploymentName(String)`、`temperature(double)`、`logRequestsAndResponses(boolean)`：常规参数设置。
- `chat(String, StreamingChatResponseHandler)`: 发送用户消息并以流式方式处理响应。
- `StreamingChatResponseHandler`: 响应处理接口，包含：
    - `onPartialResponse(String)`: 接收部分响应（如新生成的 token）。
    - `onCompleteResponse(ChatResponse)`: 响应生成完成时调用。
    - `onError(Throwable)`: 发生错误时调用。
- `CompletableFuture<ChatResponse>`: 用于异步等待完整响应。

**注意事项:**
- 运行此示例需要设置环境变量：`AZURE_OPENAI_KEY`、`AZURE_OPENAI_ENDPOINT`、`AZURE_OPENAI_DEPLOYMENT_NAME`。
- 适合需要实时输出、流式生成内容的场景，如长文本、对话等。

```java
import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

import java.util.concurrent.CompletableFuture;

public class AzureOpenAiStreamingChatModelExamples {

    static class Simple_Streaming_Prompt {

        public static void main(String[] args) {

            AzureOpenAiStreamingChatModel model = AzureOpenAiStreamingChatModel.builder()
                    .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                    .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
                    .temperature(0.3)
                    .logRequestsAndResponses(true)
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
}
