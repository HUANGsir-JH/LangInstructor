# ServiceWithDynamicToolsExample.java 示例

**示例目的:**
此文件演示了如何在 AI 服务中集成动态工具（如 JavaScript 代码执行器），让 LLM 能够执行计算、字符串处理、日期计算等复杂任务。

**关键类和方法:**
- `Judge0JavaScriptExecutionTool`: JavaScript 代码执行工具，基于 Judge0 API 实现。
- `AiServices.builder(...).tools(...)`: 为 AI 服务配置工具。
- `MessageWindowChatMemory.withMaxMessages(...)`: 配置消息窗口记忆，保持多轮对话上下文。
- `timeout(ofSeconds(...))`: 设置聊天模型超时时间，适合复杂计算场景。

**注意事项:**
- 需要有效的 OpenAI API Key 和 RapidAPI Key（用于 Judge0 服务）。
- 适合需要复杂计算、代码执行、数据处理的 AI 助手场景。
- LLM 会自动判断何时需要使用工具，并生成相应的 JavaScript 代码。

```java
import dev.langchain4j.code.judge0.Judge0JavaScriptExecutionTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import static java.time.Duration.ofSeconds;

public class ServiceWithDynamicToolsExample {

    interface Assistant {

        String chat(String message);
    }

    public static void main(String[] args) {

        Judge0JavaScriptExecutionTool judge0Tool = new Judge0JavaScriptExecutionTool(ApiKeys.RAPID_API_KEY);

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .temperature(0.0)
                .timeout(ofSeconds(60))
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(judge0Tool)
                .build();

        interact(assistant, "What is the square root of 49506838032859?");
        interact(assistant, "Capitalize every third letter: abcabc");
        interact(assistant, "What is the number of hours between 17:00 on 21 Feb 1988 and 04:00 on 12 Apr 2014?");
    }

    private static void interact(Assistant assistant, String userMessage) {
        System.out.println("[User]: " + userMessage);
        String answer = assistant.chat(userMessage);
        System.out.println("[Assistant]: " + answer);
        System.out.println();
        System.out.println();
    }
}
