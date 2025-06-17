# ServiceWithAutoModerationExample.java 示例

**示例目的:**
此文件演示了如何在 AI 服务中集成自动内容审核功能，使用 OpenAI 的审核模型自动检测和拦截违规内容。

**关键类和方法:**
- `@Moderate`: 注解标记需要进行内容审核的方法。
- `OpenAiModerationModel`: OpenAI 内容审核模型，用于检测有害、暴力、违规等内容。
- `AiServices.builder(...).moderationModel(...)`: 为 AI 服务配置审核模型。
- `ModerationException`: 当内容违反审核政策时抛出的异常。
- `TEXT_MODERATION_LATEST`: OpenAI 最新的文本审核模型名称常量。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合需要内容安全控制的应用场景，如聊天机器人、内容生成等。
- 违规内容会在处理前被拦截，确保系统安全性。

```java
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Moderate;
import dev.langchain4j.service.ModerationException;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static dev.langchain4j.model.openai.OpenAiModerationModelName.TEXT_MODERATION_LATEST;

public class ServiceWithAutoModerationExample {

    interface Chat {

        @Moderate
        String chat(String text);
    }

    public static void main(String[] args) {

        OpenAiModerationModel moderationModel = OpenAiModerationModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(TEXT_MODERATION_LATEST)
                .build();

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .build();

        Chat chat = AiServices.builder(Chat.class)
                .chatModel(chatModel)
                .moderationModel(moderationModel)
                .build();

        try {
            chat.chat("I WILL KILL YOU!!!");
        } catch (ModerationException e) {
            System.out.println(e.getMessage());
            // Text "I WILL KILL YOU!!!" violates content policy
        }
    }
}
