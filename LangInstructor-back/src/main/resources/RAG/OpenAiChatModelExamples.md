# OpenAiChatModelExamples.java 示例

**示例目的:**
此文件演示了如何使用 OpenAI 聊天模型进行文本对话、图像输入、多参数配置等多种用法，适用于多样化的对话场景和高级参数控制。

**关键类和方法:**
- `OpenAiChatModel`: 用于与 OpenAI 聊天模型交互的核心类。
- `chat(String)` / `chat(UserMessage)` / `chat(ChatRequest)`: 支持文本、图片、多参数请求。
- `ChatRequestParameters`、`OpenAiChatRequestParameters`: 支持通用和 OpenAI 专属参数（如 seed）。
- `defaultRequestParameters(...)`：设置全局默认参数，支持后续请求覆盖。
- `logRequests(true)`: 启用请求日志，便于调试。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 支持多种模型（如 gpt-4o、gpt-4o-mini）。
- 支持图片输入和高级参数合并，适合复杂对话和测试场景。

```java
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class OpenAiChatModelExamples {

    static class Simple_Prompt {

        public static void main(String[] args) {

            ChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY)
                    .modelName(GPT_4_O_MINI)
                    .build();

            String joke = chatModel.chat("Tell me a joke about Java");

            System.out.println(joke);
        }
    }

    static class Image_Inputs {

        public static void main(String[] args) {

            ChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY) // Please use your own OpenAI API key
                    .modelName(GPT_4_O_MINI)
                    .maxTokens(50)
                    .build();

            UserMessage userMessage = UserMessage.from(
                    TextContent.from("What do you see?"),
                    ImageContent.from("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
            );

            ChatResponse chatResponse = chatModel.chat(userMessage);

            System.out.println(chatResponse.aiMessage().text());
        }
    }

    static class Setting_Common_ChatRequestParameters {

        public static void main(String[] args) {

            ChatRequestParameters defaultParameters = ChatRequestParameters.builder()
                    .modelName("gpt-4o")
                    .temperature(0.7)
                    .maxOutputTokens(100)
                    // there are many more common parameters, see ChatRequestParameters for more info
                    .build();

            ChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY)
                    .defaultRequestParameters(defaultParameters)
                    .logRequests(true)
                    .build();

            ChatRequestParameters parameters = ChatRequestParameters.builder()
                    .modelName("gpt-4o-mini")
                    .temperature(1.0)
                    .maxOutputTokens(50)
                    .build();

            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(UserMessage.from("Tell me a funny story about Java"))
                    .parameters(parameters) // merges with and overrides default parameters
                    .build();

            ChatResponse chatResponse = chatModel.chat(chatRequest);

            System.out.println(chatResponse);
        }
    }

    static class Setting_OpenAI_Specific_ChatRequestParameters {

        public static void main(String[] args) {

            OpenAiChatRequestParameters defaultParameters = OpenAiChatRequestParameters.builder()
                    .seed(12345) // OpenAI-specific parameter
                    // there are many more OpenAI-specific parameters, see OpenAiChatRequestParameters for more info
                    .modelName("gpt-4o") // common parameter
                    .temperature(0.7) // common parameter
                    .maxOutputTokens(100) // common parameter
                    .build();

            ChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY)
                    .defaultRequestParameters(defaultParameters)
                    .logRequests(true)
                    .build();

            OpenAiChatRequestParameters parameters = OpenAiChatRequestParameters.builder()
                    .seed(67890) // OpenAI-specific parameter
                    .modelName("gpt-4o-mini") // common parameter
                    .temperature(1.0) // common parameter
                    .maxOutputTokens(50) // common parameter
                    .build();

            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(UserMessage.from("Tell me a funny story about Java"))
                    .parameters(parameters) // merges with and overrides default parameters
                    .build();

            ChatResponse chatResponse = chatModel.chat(chatRequest);

            System.out.println(chatResponse);
        }
    }
}
