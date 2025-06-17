# AbstractOllamaInfrastructure.java 示例

**示例目的:**
此文件为 Ollama 测试环境的基础设施类，自动管理本地 Ollama 容器的启动、模型加载和服务地址解析，便于在测试用例中统一使用 Ollama 服务。

**关键类和方法:**
- `OLLAMA_BASE_URL`: 通过环境变量指定 Ollama 服务地址，若未设置则自动拉起本地 Docker 容器。
- `MODEL_NAME`: 默认使用的模型名称（如 LLAMA_3_1）。
- `LangChain4jOllamaContainer`: Ollama 容器管理类，支持模型加载、启动和镜像提交。
- `ollamaBaseUrl(LangChain4jOllamaContainer)`: 根据环境变量或容器状态返回 Ollama 服务的实际访问地址。
- 静态代码块：自动检测环境变量并启动本地 Ollama 容器，确保测试环境一致。

**注意事项:**
- 适用于自动化测试和本地开发环境，无需手动管理 Ollama 服务。
- 支持自定义模型镜像和本地缓存，提升测试效率。
- 依赖 `utils.OllamaImage`、`LangChain4jOllamaContainer` 等工具类。

```java
package utils;

import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static utils.OllamaImage.LLAMA_3_1;
import static utils.OllamaImage.localOllamaImage;

public class AbstractOllamaInfrastructure {

    public static final String OLLAMA_BASE_URL = System.getenv("OLLAMA_BASE_URL");
    public static final String MODEL_NAME = LLAMA_3_1;

    public static LangChain4jOllamaContainer ollama;

    static {
        if (isNullOrEmpty(OLLAMA_BASE_URL)) {
            String localOllamaImage = localOllamaImage(MODEL_NAME);
            ollama = new LangChain4jOllamaContainer(OllamaImage.resolve(OllamaImage.OLLAMA_IMAGE, localOllamaImage))
                    .withModel(MODEL_NAME);
            ollama.start();
            ollama.commitToImage(localOllamaImage);
        }
    }

    public static String ollamaBaseUrl(LangChain4jOllamaContainer ollama) {
        if (isNullOrEmpty(OLLAMA_BASE_URL)) {
            return ollama.getEndpoint();
        } else {
            return OLLAMA_BASE_URL;
        }
    }
}
