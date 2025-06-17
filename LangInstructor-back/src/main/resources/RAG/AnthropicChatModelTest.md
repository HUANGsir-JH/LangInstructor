# AnthropicChatModelTest.java 示例

**文件路径:** `rags/langchain4j-examples/anthropic-examples/src/main/java/AnthropicChatModelTest.java`

**示例目的:**
此示例演示了 `AnthropicChatModel` 的基本用法，包括：
1.  **文本聊天:** 如何使用 `AnthropicChatModel` 进行简单的文本问答。
2.  **视觉能力:** 如何结合文本和图像内容进行多模态聊天，利用模型的视觉理解能力。
3.  **系统消息缓存:** 如何启用和使用系统消息缓存功能，以优化性能和成本。

**关键类和方法:**
- `AnthropicChatModel`: LangChain4j 中用于与 Anthropic Claude 模型交互的聊天模型。
- `chat(String userMessage)`: 用于发送文本消息并获取响应。
- `chat(ChatMessage... messages)`: 用于发送包含多种消息类型（如用户消息、系统消息、图像内容）的复杂聊天请求。
- `UserMessage.from(...)`: 创建用户消息，可以包含文本和图像内容。
- `ImageContent.from(...)`: 创建图像内容，支持 Base64 编码的图像。
- `SystemMessage.from(...)`: 创建系统消息，用于设置模型的行为或角色。
- `AnthropicTokenUsage`: 响应元数据中包含的 Anthropic 特定的令牌使用信息，包括缓存相关的令牌计数。

**注意事项:**
- 运行此示例需要有效的 `ANTHROPIC_API_KEY` 环境变量。
- `modelName` 可以根据需要选择不同的 Anthropic Claude 模型，例如 `claude-3-haiku-20240307`。
- `logRequests(true)` 和 `logResponses(true)` 可以启用请求和响应的日志记录，便于调试。
- 系统消息缓存功能通过 `beta("prompt-caching-2024-07-31")` 和 `cacheSystemMessages(true)` 启用，可以显著减少重复系统消息的令牌使用。

```java
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicTokenUsage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static dev.langchain4j.internal.Utils.readBytes;
import static org.assertj.core.api.Assertions.assertThat;

class AnthropicChatModelTest {

    ChatModel model = AnthropicChatModel.builder()
            // API key can be created here: https://console.anthropic.com/settings/keys
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName("claude-3-haiku-20240307")
            .logRequests(true)
            .logResponses(true)
            // Other parameters can be set as well
            .build();

    @Test
    void AnthropicChatModel_Example() {

        String answer = model.chat("What is the capital of Germany?");

        assertThat(answer).containsIgnoringCase("Berlin");
    }

    @Test
    void AnthropicChatModel_with_vision_Example() {

        byte[] image = readBytes("https://docs.langchain4j.dev/img/langchain4j-components.png");
        String base64EncodedImage = Base64.getEncoder().encodeToString(image);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("What do you see?"),
                ImageContent.from(base64EncodedImage, "image/png")
        );

        ChatResponse chatResponse = model.chat(userMessage);

        assertThat(chatResponse.aiMessage().text()).containsIgnoringCase("RAG");
    }

    @Test
    void AnthropicChatModel_with_cache_system_message_Example() {
        ChatModel modelWithCache = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .beta("prompt-caching-2024-07-31")
                .modelName(AnthropicChatModelName.CLAUDE_3_HAIKU_20240307)
                .cacheSystemMessages(true)
                .logRequests(true)
                .logResponses(true)
                .build();

        // Now cache has in beta
        // You can send up to 4 systemMessages/Tools

        // create cache
        SystemMessage systemMessage = SystemMessage.from("What types of messages are supported in LangChain?".repeat(187));
        UserMessage userMessage = UserMessage.userMessage("what result it calcule 5x2 + 2x + 2 = 0?");
        ChatResponse response = modelWithCache.chat(systemMessage, userMessage);

        AnthropicTokenUsage createCacheTokenUsage = (AnthropicTokenUsage) response.metadata().tokenUsage();
        assertThat(createCacheTokenUsage.cacheCreationInputTokens()).isGreaterThan(0);

        // read cache created
        ChatResponse responseToReadCache = modelWithCache.chat(systemMessage, userMessage);
        AnthropicTokenUsage readCacheTokenUsage = (AnthropicTokenUsage) responseToReadCache.metadata().tokenUsage();
        assertThat(readCacheTokenUsage.cacheReadInputTokens()).isGreaterThan(0);
    }
}
