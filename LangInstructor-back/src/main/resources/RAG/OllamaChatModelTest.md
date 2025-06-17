# OllamaChatModelTest.java 示例

**示例目的:**
此文件演示了如何使用 Ollama 本地大模型进行多种聊天与结构化数据抽取，包括普通对话、JSON Schema 结构化输出、AI 服务接口集成等。

**关键类和方法:**
- `OllamaChatModel`: 用于与 Ollama 本地大模型交互的核心类。
- `chat(String)` / `chat(ChatRequest)`: 支持普通文本和结构化请求。
- `responseFormat(ResponseFormat)`: 支持 JSON Schema、JSON 模式等结构化输出。
- `AiServices.create`: 将模型能力封装为接口，便于直接调用。
- `UserMessage`、`ChatRequestParameters`、`JsonSchema` 等：用于构建复杂请求和响应格式。
- `toMap(String json)`: 辅助方法，将 JSON 字符串转为 Map 便于断言和处理。

**注意事项:**
- 支持通过环境变量 `OLLAMA_BASE_URL` 指定本地 Ollama 服务地址，否则会自动拉取 Docker 镜像启动容器。
- 支持多种结构化输出方式，适合信息抽取、数据结构化等场景。
- 断言和日志输出便于测试和调试。

```java
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import utils.AbstractOllamaInfrastructure;

import java.util.Map;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;

class OllamaChatModelTest extends AbstractOllamaInfrastructure {

    /**
     * If you have Ollama running locally,
     * please set the OLLAMA_BASE_URL environment variable (e.g., http://localhost:11434).
     * If you do not set the OLLAMA_BASE_URL environment variable,
     * Testcontainers will download and start Ollama Docker container.
     * It might take a few minutes.
     */

    @Test
    void simple_example() {

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName(MODEL_NAME)
                .logRequests(true)
                .build();

        String answer = chatModel.chat("Provide 3 short bullet points explaining why Java is awesome");
        System.out.println(answer);

        assertThat(answer).isNotBlank();
    }

    @Test
    void json_schema_with_AI_Service_example() {

        record Person(String name, int age) {
        }

        interface PersonExtractor {

            Person extractPersonFrom(String text);
        }

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName(MODEL_NAME)
                .temperature(0.0)
                .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
                .logRequests(true)
                .build();

        PersonExtractor personExtractor = AiServices.create(PersonExtractor.class, chatModel);

        Person person = personExtractor.extractPersonFrom("John Doe is 42 years old");
        System.out.println(person);

        assertThat(person).isEqualTo(new Person("John Doe", 42));
    }

    @Test
    void json_schema_with_low_level_chat_api_example() {

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName(MODEL_NAME)
                .temperature(0.0)
                .logRequests(true)
                .build();

        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchema.builder()
                        .rootElement(JsonObjectSchema.builder()
                                .addStringProperty("name")
                                .addIntegerProperty("age")
                                .build())
                        .build())
                .build();

        ChatRequestParameters parameters = ChatRequestParameters.builder()
                .responseFormat(responseFormat)
                .build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from("John Doe is 42 years old"))
                .parameters(parameters)
                .build();

        ChatResponse chatResponse = chatModel.chat(chatRequest);
        System.out.println(chatResponse);

        assertThat(toMap(chatResponse.aiMessage().text())).isEqualTo(Map.of("name", "John Doe", "age", 42));
    }

    @Test
    void json_schema_with_low_level_model_builder_example() {

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName(MODEL_NAME)
                .temperature(0.0)
                .responseFormat(ResponseFormat.builder()
                        .type(ResponseFormatType.JSON)
                        .jsonSchema(JsonSchema.builder()
                                .rootElement(JsonObjectSchema.builder()
                                        .addStringProperty("name")
                                        .addIntegerProperty("age")
                                        .build())
                                .build())
                        .build())
                .logRequests(true)
                .build();

        String json = chatModel.chat("Extract: John Doe is 42 years old");
        System.out.println(json);

        assertThat(toMap(json)).isEqualTo(Map.of("name", "John Doe", "age", 42));
    }

    @Test
    void json_mode_with_low_level_model_builder_example() {

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName(MODEL_NAME)
                .temperature(0.0)
                .responseFormat(ResponseFormat.JSON)
                .logRequests(true)
                .build();

        String json = chatModel.chat("Give me a JSON object with 2 fields: name and age of a John Doe, 42");
        System.out.println(json);

        assertThat(toMap(json)).isEqualTo(Map.of("name", "John Doe", "age", 42));
    }

    private static Map<String, Object> toMap(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
