# AzureOpenAIDallEExample.java 示例

**示例目的:**
此文件演示了如何使用 Azure OpenAI 的 DALL·E 图像生成模型。通过设置 API Key、Endpoint、部署名称等参数，调用 `generate` 方法生成图像，并获取远程图片的 URL。

**关键类和方法:**
- `AzureOpenAiImageModel`: 用于与 Azure OpenAI 图像生成模型（如 DALL·E）交互的核心类。
- `builder()`: 构建模型实例，支持链式设置参数。
    - `apiKey(String)`: 设置 Azure OpenAI 的 API Key。
    - `endpoint(String)`: 设置 Azure OpenAI 服务的 Endpoint。
    - `deploymentName(String)`: 设置 DALL·E 部署名称。
    - `logRequestsAndResponses(boolean)`: 是否记录请求和响应日志。
- `generate(String prompt)`: 根据提示词生成图像，返回 `Response<Image>`。
- `Image.url()`: 获取生成图片的远程 URL。

**注意事项:**
- 运行此示例需要设置环境变量：`AZURE_OPENAI_KEY`、`AZURE_OPENAI_ENDPOINT`、`AZURE_OPENAI_DALLE_DEPLOYMENT_NAME`。
- 示例输出包括完整响应内容和图片的远程访问地址。

```java
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.azure.AzureOpenAiImageModel;
import dev.langchain4j.model.output.Response;

public class AzureOpenAIDallEExample {

    static class Simple_Image {

        public static void main(String[] args) {

            AzureOpenAiImageModel model = AzureOpenAiImageModel.builder()
                    .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                    .deploymentName(System.getenv("AZURE_OPENAI_DALLE_DEPLOYMENT_NAME"))
                    .logRequestsAndResponses(true)
                    .build();

            Response<Image> response = model.generate("A coffee mug in Paris, France");

            System.out.println(response.toString());

            Image image = response.content();

            System.out.println("The remote image is here:" + image.url());
        }
    }
}
