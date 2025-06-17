# AzureOpenAiFunctionCallingExamples.java 示例

**示例目的:**
此文件演示了如何在 Azure OpenAI 聊天模型中实现函数调用（工具调用），包括工具的声明、参数推理、执行和最终响应生成的完整流程。

**关键类和方法:**
- `AzureOpenAiChatModel`: 用于与 Azure OpenAI 聊天模型交互的核心类。
- `ToolSpecification`、`ToolExecutionRequest`、`ToolExecutor`: 低层级工具 API，支持自定义工具声明与执行。
- `WeatherTools`: 示例工具类，包含天气查询、日期和温度转换等方法，部分方法通过 `@Tool` 注解暴露为 LLM 可调用工具。
- `chat(List<ChatMessage>)`：支持多轮消息和工具调用的模型接口。
- `DefaultToolExecutor`: 工具执行器，用于根据模型推理结果自动调用本地方法。

**注意事项:**
- 运行此示例需要设置环境变量：`AZURE_OPENAI_KEY`、`AZURE_OPENAI_ENDPOINT`、`AZURE_OPENAI_DEPLOYMENT_NAME`。
- 推荐实际开发中优先使用高层级工具 API，低层级 API 适合特殊自定义场景。
- 示例完整演示了 LLM 工具调用的四个步骤：声明工具、模型推理参数、执行工具、生成最终回复。

```java
import dev.langchain4j.agent.tool.*;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.langchain4j.data.message.UserMessage.userMessage;

public class AzureOpenAiFunctionCallingExamples {

    /**
     * This example demonstrates how to programmatically configure the low-level tool APIs, such as ToolSpecification,
     * ToolExecutionRequest, and ToolExecutor.
     * But it is recommended to use higher-level APIs as demonstrated here: https://docs.langchain4j.dev/tutorials/tools/#high-level-tool-api
     * <p>
     * This sample goes through 4 different steps:
     * 1. Specify the tools (WeatherTools) and the query ("What will the weather be like in London tomorrow?")
     * 2. Model generate function arguments (model decides which tools to invoke)
     * 3. User execute function to obtain tool results (using ToolExecutor)
     * 4. Model generate final response based on the query and the tool results
     */
    static class Weather_From_Manual_Configuration {

        static ChatModel azureOpenAiModel = AzureOpenAiChatModel.builder()
                .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
                .temperature(0.7)
                .logRequestsAndResponses(true)
                .build();

        public static void main(String[] args) {

            // STEP 1: User specify tools and query
            // Tools
            WeatherTools weatherTools = new WeatherTools();
            List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(weatherTools);
            // User query
            List<ChatMessage> chatMessages = new ArrayList<>();
            UserMessage userMessage = userMessage("What will the weather be like in London tomorrow?");
            chatMessages.add(userMessage);

            // STEP 2: Model generates function arguments
            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(chatMessages)
                    .parameters(ChatRequestParameters.builder()
                            .toolSpecifications(toolSpecifications)
                            .build())
                    .build();
            AiMessage aiMessage = azureOpenAiModel.chat(chatRequest).aiMessage();
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            System.out.println("Out of the " + toolSpecifications.size() + " functions declared in WeatherTools, " + toolExecutionRequests.size() + " will be invoked:");
            toolExecutionRequests.forEach(toolExecutionRequest -> {
                System.out.println("Function name: " + toolExecutionRequest.name());
                System.out.println("Function args:" + toolExecutionRequest.arguments());
            });
            chatMessages.add(aiMessage);

            // STEP 3: User execute function to obtain tool results
            toolExecutionRequests.forEach(toolExecutionRequest -> {
                ToolExecutor toolExecutor = new DefaultToolExecutor(weatherTools, toolExecutionRequest);
                System.out.println("Now let's execute the function " + toolExecutionRequest.name());
                String result = toolExecutor.execute(toolExecutionRequest, UUID.randomUUID().toString());
                ToolExecutionResultMessage toolExecutionResultMessages = ToolExecutionResultMessage.from(toolExecutionRequest, result);
                chatMessages.add(toolExecutionResultMessages);
            });

            // STEP 4: Model generate final response
            AiMessage finalResponse = azureOpenAiModel.chat(chatMessages).aiMessage();
            System.out.println(finalResponse.text()); //According to the payment data, the payment status of transaction T1005 is Pending.
        }
    }

    static class WeatherTools {

        @Tool("Returns the weather forecast for tomorrow for a given city")
        String getWeather(@P("The city for which the weather forecast should be returned") String city) {
            return "The weather tomorrow in " + city + " is 25°C";
        }

        @Tool("Returns the date for tomorrow")
        LocalDate getTomorrow() {
            return LocalDate.now().plusDays(1);
        }

        @Tool("Transforms Celsius degrees into Fahrenheit")
        double celsiusToFahrenheit(@P("The celsius degree to be transformed into fahrenheit") double celsius) {
            return (celsius * 1.8) + 32;
        }

        String iAmNotATool() {
            return "I am not a method annotated with @Tool";
        }
    }
}
