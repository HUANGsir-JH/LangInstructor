# AzureOpenAiEmbeddingModelExamples.java 示例

**示例目的:**
此文件演示了如何使用 Azure OpenAI 的嵌入模型，将文本转换为向量（Embedding）。通过设置 API Key、Endpoint、部署名称等参数，调用 `embed` 方法获取文本的嵌入向量。

**关键类和方法:**
- `AzureOpenAiEmbeddingModel`: 用于与 Azure OpenAI 嵌入模型交互的核心类。
- `builder()`: 构建模型实例，支持链式设置参数。
    - `apiKey(String)`: 设置 Azure OpenAI 的 API Key。
    - `endpoint(String)`: 设置 Azure OpenAI 服务的 Endpoint。
    - `deploymentName(String)`: 设置嵌入模型部署名称。
    - `logRequestsAndResponses(boolean)`: 是否记录请求和响应日志。
- `embed(String text)`: 对输入文本进行嵌入，返回 `Response<Embedding>`。

**注意事项:**
- 运行此示例需要设置环境变量：`AZURE_OPENAI_KEY`、`AZURE_OPENAI_ENDPOINT`、`AZURE_OPENAI_EMBEDDING_DEPLOYMENT_NAME`。
- 示例输出为输入文本的嵌入向量结果，适合测试文本向量化流程。

```java
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

public class AzureOpenAiEmbeddingModelExamples {

    static class Simple_Embedding {

        public static void main(String[] args) {

            AzureOpenAiEmbeddingModel model = AzureOpenAiEmbeddingModel.builder()
                    .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                    .deploymentName(System.getenv("AZURE_OPENAI_EMBEDDING_DEPLOYMENT_NAME"))
                    .logRequestsAndResponses(true)
                    .build();

            Response<Embedding> response = model.embed("Please embed this sentence.");

            System.out.println(response);
        }
    }
}
