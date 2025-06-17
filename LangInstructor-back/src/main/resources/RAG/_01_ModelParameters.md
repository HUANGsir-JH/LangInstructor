# _01_ModelParameters.java 示例

**示例目的:**
这是 LangChain4j 教程系列的第二个示例，演示如何配置聊天模型的各种参数，包括温度、超时、日志等，以优化模型行为和调试体验。

**关键类和方法:**
- `OpenAiChatModel.builder()`: 构建器模式配置聊天模型。
- `temperature(double)`: 控制生成内容的随机性，0-1 之间，值越小越确定。
- `timeout(Duration)`: 设置请求超时时间，避免长时间等待。
- `logRequests(boolean)`: 启用请求日志记录，便于调试。
- `logResponses(boolean)`: 启用响应日志记录，便于调试。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 参数详细说明可参考 OpenAI 官方 API 文档。
- 适合学习模型参数调优和调试技巧。

```java
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;

public class _01_ModelParameters {

    public static void main(String[] args) {

        // OpenAI parameters are explained here: https://platform.openai.com/docs/api-reference/chat/create

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .temperature(0.3)
                .timeout(ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();

        String prompt = "Explain in three lines how to make a beautiful painting";

        String response = model.chat(prompt);

        System.out.println(response);
    }
}
