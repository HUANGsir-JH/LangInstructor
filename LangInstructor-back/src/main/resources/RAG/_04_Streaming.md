# _04_Streaming.java 示例

**示例目的:**
这是 LangChain4j 教程系列的第五个示例，演示如何使用流式聊天模型实时接收生成内容，并展示 token 计数功能。

**关键类和方法:**
- `OpenAiStreamingChatModel`: OpenAI 流式聊天模型，支持实时输出。
- `StreamingChatResponseHandler`: 流式响应处理器接口：
    - `onPartialResponse(String)`: 接收部分响应（如新生成的 token）。
    - `onCompleteResponse(ChatResponse)`: 响应生成完成时调用。
    - `onError(Throwable)`: 发生错误时调用。
- `OpenAiTokenCountEstimator`: Token 计数估算器，用于统计文本的 token 数量。
- `CompletableFuture<ChatResponse>`: 异步等待完整响应。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合需要实时输出、流式生成内容的场景，如长文本、诗歌创作等。
- Token 计数有助于成本控制和输入长度管理。

```java
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

import java.util.concurrent.CompletableFuture;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class _04_Streaming {

    public static void main(String[] args) {

        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        String prompt = "Write a short funny poem about developers and null-pointers, 10 lines maximum";

        System.out.println("Nr of chars: " + prompt.length());
        System.out.println("Nr of tokens: " + new OpenAiTokenCountEstimator(GPT_4_O_MINI).estimateTokenCountInText(prompt));

        CompletableFuture<ChatResponse> futureChatResponse = new CompletableFuture<>();

        model.chat(prompt, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("\n\nDone streaming");
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
