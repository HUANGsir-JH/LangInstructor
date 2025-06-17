# _00_HelloWorld.java 示例

**示例目的:**
这是 LangChain4j 教程系列的第一个示例，演示最基础的聊天模型调用，实现"Hello World"级别的 AI 对话。

**关键类和方法:**
- `OpenAiChatModel`: 用于与 OpenAI 聊天模型交互的核心类。
- `chat(String)`: 发送用户输入并获取模型回复。
- `GPT_4_O_MINI`: OpenAI 最新的轻量级聊天模型名称常量。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 这是学习 LangChain4j 的第一步，适合新手快速体验。

```java
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class _00_HelloWorld {

    public static void main(String[] args) {

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        String answer = model.chat("Say Hello World");

        System.out.println(answer);
    }
}
