# AnthropicToolsTest.java 示例

**示例目的:**
此示例演示了 `AnthropicChatModel` 如何与工具（函数调用）集成。它展示了如何定义 Java 方法作为工具，并让 LLM 在需要时调用这些工具来获取信息。

**关键类和方法:**
- `@Tool`: 用于注解 Java 方法，将其暴露为 LLM 可以调用的工具。
- `AiServices.builder().chatModel().tools().build()`: 构建一个 AI 服务，将聊天模型和工具绑定在一起。
- `LocalDate currentDate()`: 一个工具方法，返回当前日期。
- `LocalTime currentTime()`: 一个工具方法，返回当前时间。
- `AiService` 接口: 定义了与 AI 服务交互的方法。

**注意事项:**
- 运行此示例需要有效的 `ANTHROPIC_API_KEY` 环境变量。
- `modelName` 可以根据需要选择不同的 Anthropic Claude 模型。
- `logRequests(true)` 和 `logResponses(true)` 可以启用请求和响应的日志记录，便于调试。
- `AiServices` 会自动处理工具的注册和调用，简化了与 LLM 的交互。

```java
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_HAIKU_20240307;
import static org.assertj.core.api.Assertions.assertThat;

class AnthropicToolsTest {

    ChatModel model = AnthropicChatModel.builder()
            // API key can be created here: https://console.anthropic.com/settings/keys
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(CLAUDE_3_HAIKU_20240307)
            .logRequests(true)
            .logResponses(true)
            // Other parameters can be set as well
            .build();

    static class Tools {

        @Tool
        LocalDate currentDate() {
            System.out.println("Called currentDate()");
            return LocalDate.now();
        }

        @Tool
        LocalTime currentTime() {
            System.out.println("Called currentTime()");
            return LocalTime.now();
        }
    }

    interface AiService {

        String chat(String userMessage);
    }

    @Test
    void AnthropicChatModel_Tools_Example() {

        AiService aiService = AiServices.builder(AiService.class)
                .chatModel(model)
                .tools(new Tools())
                .build();

        String answer = aiService.chat("What is the date today?");
        System.out.println(answer);

        assertThat(answer).contains(String.valueOf(LocalDate.now().getDayOfMonth()));
    }
}
