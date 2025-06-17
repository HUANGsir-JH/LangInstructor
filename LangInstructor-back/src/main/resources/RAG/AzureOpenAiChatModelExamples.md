# AzureOpenAiChatModelExamples.java 示例

**示例目的:**
此文件演示了如何使用 Azure OpenAI 聊天模型进行简单的对话请求。通过设置 API Key、Endpoint、部署名称等参数，调用 `chat` 方法获取模型回复。

**关键类和方法:**
- `AzureOpenAiChatModel`: 用于与 Azure OpenAI 聊天模型交互的核心类。
- `builder()`: 构建模型实例，支持链式设置参数。
    - `apiKey(String)`: 设置 Azure OpenAI 的 API Key。
    - `endpoint(String)`: 设置 Azure OpenAI 服务的 Endpoint。
    - `deploymentName(String)`: 设置部署名称。
    - `temperature(double)`: 设置生成温度，控制输出多样性。
    - `logRequestsAndResponses(boolean)`: 是否记录请求和响应日志。
- `chat(String prompt)`: 发送用户输入并获取模型回复。

**注意事项:**
- 运行此示例需要设置环境变量：`AZURE_OPENAI_KEY`、`AZURE_OPENAI_ENDPOINT`、`AZURE_OPENAI_DEPLOYMENT_NAME`。
- 示例输出为 Java 优势的简要要点，适合快速测试模型调用流程。

```java
import dev.langchain4j.model.azure.AzureOpenAiChatModel;

public class AzureOpenAiChatModelExamples {

    static class Simple_Prompt {

        public static void main(String[] args) {

            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                    .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                    .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
                    .temperature(0.3)
                    .logRequestsAndResponses(true)
                    .build();

            String response = model.chat("Provide 3 short bullet points explaining why Java is awesome");

            System.out.println(response);
        }
    }
}
