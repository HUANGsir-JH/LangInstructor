# HelloWorldExample.java 示例

**示例目的:**
这是最基础的 LangChain4j 聊天模型调用示例，演示如何用 OpenAI 聊天模型实现“Hello world”对话。

**关键类和方法:**
- `OpenAiChatModel`: 用于与 OpenAI 聊天模型交互的核心类。
- `chat(String)`: 发送用户输入并获取模型回复。
- `GPT_4_O_MINI`: OpenAI 最新的轻量级聊天模型名称常量。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合新手快速体验 LangChain4j 聊天模型的基本用法。

```java
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class HelloWorldExample {

    public static void main(String[] args) {

        // Create an instance of a model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        // Start interacting
        String answer = model.chat("Hello world!");

        System.out.println(answer); // Hello! How can I assist you today?
    }
}
