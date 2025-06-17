# _05_Memory.java 示例

**示例目的:**
这是 LangChain4j 教程系列的第六个示例，演示如何使用聊天记忆功能实现多轮对话上下文保持，结合流式输出和 token 窗口管理。

**关键类和方法:**
- `TokenWindowChatMemory.withMaxTokens(...)`: 创建带最大 token 限制的记忆窗口，自动管理历史消息。
- `OpenAiTokenCountEstimator`: Token 计数估算器，确保记忆不超过模型限制。
- `chatMemory.add(...)`: 手动添加系统消息、用户消息、AI 消息到记忆中。
- `model.chat(chatMemory.messages(), handler)`: 传递完整历史消息进行流式对话。
- `SystemMessage.from(...)`: 创建系统消息，设置对话背景和角色。
- `CompletableFuture<AiMessage>`: 异步等待 AI 响应完成。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合需要多轮上下文对话的场景，如技术咨询、代码讨论等。
- 系统消息帮助设定对话角色和背景，提升回答质量。
- Token 窗口自动管理避免超出模型上下文限制。

```java
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class _05_Memory {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenCountEstimator(GPT_4_O_MINI));

        SystemMessage systemMessage = SystemMessage.from(
                "You are a senior developer explaining to another senior developer, "
                        + "the project you are working on is an e-commerce platform with Java back-end, " +
                        "Oracle database, and Spring Data JPA");
        chatMemory.add(systemMessage);

        UserMessage userMessage1 = userMessage(
                "How do I optimize database queries for a large-scale e-commerce platform? "
                        + "Answer short in three to five lines maximum.");
        chatMemory.add(userMessage1);

        System.out.println("[User]: " + userMessage1.singleText());
        System.out.print("[LLM]: ");

        AiMessage aiMessage1 = streamChat(model, chatMemory);
        chatMemory.add(aiMessage1);

        UserMessage userMessage2 = userMessage(
                "Give a concrete example implementation of the first point? " +
                        "Be short, 10 lines of code maximum.");
        chatMemory.add(userMessage2);

        System.out.println("\n\n[User]: " + userMessage2.singleText());
        System.out.print("[LLM]: ");

        AiMessage aiMessage2 = streamChat(model, chatMemory);
        chatMemory.add(aiMessage2);
    }

    private static AiMessage streamChat(OpenAiStreamingChatModel model, ChatMemory chatMemory)
            throws ExecutionException, InterruptedException {

        CompletableFuture<AiMessage> futureAiMessage = new CompletableFuture<>();

        StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureAiMessage.complete(completeResponse.aiMessage());
            }

            @Override
            public void onError(Throwable throwable) {
            }
        };

        model.chat(chatMemory.messages(), handler);
        return futureAiMessage.get();
    }
}
