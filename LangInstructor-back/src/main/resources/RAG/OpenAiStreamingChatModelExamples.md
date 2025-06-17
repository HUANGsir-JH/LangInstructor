# OpenAiStreamingChatModelExamples.java 示例

**示例目的:**
此文件演示了如何使用 OpenAI 流式聊天模型，实现对用户输入的流式响应（如分词输出笑话）。

**关键类和方法:**
- `OpenAiStreamingChatModel`: 用于与 OpenAI 流式聊天模型交互的核心类。
- `StreamingChatModel.chat(String, StreamingChatResponseHandler)`: 发送用户消息并以流式方式处理响应。
- `StreamingChatResponseHandler`: 响应处理接口，包含：
    - `onPartialResponse(String)`: 接收部分响应（如新生成的 token）。
    - `onCompleteResponse(ChatResponse)`: 响应生成完成时调用。
    - `onError(Throwable)`: 发生错误时调用。
- `CompletableFuture<ChatResponse>`: 用于异步等待完整响应。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合需要实时输出、流式生成内容的场景，如长文本、对话等。

```java
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.concurrent.CompletableFuture;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class OpenAiStreamingChatModelExamples {

    public static void main(String[] args) {

        StreamingChatModel chatModel = OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        CompletableFuture<ChatResponse> futureChatResponse = new CompletableFuture<>();

        chatModel.chat("Tell me a joke about Java", new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureChatResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureChatResponse.completeExceptionally(error);
            }
        });

        futureChatResponse.join();
    }
}
