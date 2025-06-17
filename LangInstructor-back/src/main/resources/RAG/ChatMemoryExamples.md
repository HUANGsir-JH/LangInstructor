# ChatMemoryExamples.java 示例

**示例目的:**
此文件演示了如何使用低层级的 `ChatMemory` API 管理对话记忆，实现多轮对话上下文记忆与控制。

**关键类和方法:**
- `TokenWindowChatMemory.withMaxTokens(...)`: 创建带最大 token 限制的窗口记忆，自动丢弃旧消息。
- `OpenAiTokenCountEstimator`: 用于估算消息 token 数，保证记忆窗口不超限。
- `chatMemory.add(...)`: 手动添加用户或 AI 消息到记忆中，可灵活控制记忆内容。
- `model.chat(chatMemory.messages())`: 传递完整历史消息，实现多轮上下文对话。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合需要自定义记忆管理、节省 token 成本、实现多轮上下文的场景。
- 高层级记忆管理可参考 `ServiceWithMemoryExample`。

```java
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class ChatMemoryExamples {

    /**
     * This example demonstrates how to use a low-level {@link ChatMemory} API.
     * For a high-level API with AI Services see {@link ServiceWithMemoryExample}.
     */

    public static void main(String[] args) {

        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenCountEstimator(GPT_4_O_MINI));

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        // You have full control over the chat memory.
        // You can decide if you want to add a particular message to the memory
        // (e.g. you might not want to store few-shot examples to save on tokens).
        // You can process/modify the message before saving if required.

        chatMemory.add(userMessage("Hello, my name is Klaus"));
        AiMessage answer = model.chat(chatMemory.messages()).aiMessage();
        System.out.println(answer.text()); // Hello Klaus! How can I assist you today?
        chatMemory.add(answer);

        chatMemory.add(userMessage("What is my name?"));
        AiMessage answerWithName = model.chat(chatMemory.messages()).aiMessage();
        System.out.println(answerWithName.text()); // Your name is Klaus.
        chatMemory.add(answerWithName);
    }
}
