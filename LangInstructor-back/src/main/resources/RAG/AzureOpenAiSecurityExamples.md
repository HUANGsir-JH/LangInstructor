# AzureOpenAiSecurityExamples.java 示例

**示例目的:**
此文件演示了 Azure OpenAI API 的安全接入方式，包括 API Key 方式和推荐的 Azure 身份凭证（DefaultAzureCredential）方式。

**关键类和方法:**
- `AzureOpenAiChatModel`: 用于与 Azure OpenAI 聊天模型交互的核心类。
- `apiKey(String)`: 使用 API Key 进行身份验证（不推荐，部分资源会被禁用）。
- `tokenCredential(TokenCredential)`: 推荐方式，使用 Azure 身份凭证进行安全认证。
- `DefaultAzureCredentialBuilder`: Azure 官方推荐的凭证构建器，支持本地开发和生产环境的多种认证方式自动切换。
- `endpoint(String)`、`deploymentName(String)`、`temperature(double)`、`logRequestsAndResponses(boolean)`: 常规参数设置。

**注意事项:**
- 某些资源已禁用 API Key 认证，必须使用 Azure 身份凭证，否则会收到 403 错误。
- 推荐在生产环境和企业级应用中统一采用 `DefaultAzureCredential` 认证方式，安全性更高。
- 运行此示例前可执行脚本 `deploy-azure-openai-security.sh` 进行相关配置。

```java
import com.azure.identity.DefaultAzureCredentialBuilder;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;

/**
 * This sample demonstrates how to secure Azure OpenAI API models.
 * To run this sample, please execute the script deploy-azure-openai-security.sh.
 */
public class AzureOpenAiSecurityExamples {

    /**
     * This sample demonstrates that accessing a model using an API key does not work.
     * You should get the following error message:
     * Status code 403, "{"error":{"code":"AuthenticationTypeDisabled","message": "Key based authentication is disabled for this resource."}}"
     */
    static class ApiKey_Example {

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

    /**
     * This sample demonstrates that you need to use Azure Credentials (DefaultAzureCredentialBuilder) instead of an API Key.
     * DefaultAzureCredential combines credentials that are commonly used to authenticate when deployed, with credentials that are used to authenticate in a development environment. 
     */
    static class Azure_Credential_Example {

        public static void main(String[] args) {

            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                    .tokenCredential(new DefaultAzureCredentialBuilder().build())
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
