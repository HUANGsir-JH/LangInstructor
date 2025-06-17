# OllamaImage.java 示例

**示例目的:**
此文件用于管理 Ollama Docker 镜像的本地缓存与解析，支持自动检测本地镜像、动态选择镜像来源，提升模型启动效率。

**关键类和方法:**
- `OLLAMA_IMAGE`: Ollama 官方基础镜像名（如 `ollama/ollama:latest`）。
- `localOllamaImage(String modelName)`: 生成本地缓存镜像名，便于区分不同模型。
- `LLAMA_3_1`: 默认模型名称常量。
- `resolve(String baseImage, String localImageName)`: 检查本地是否存在指定镜像，若有则优先使用本地镜像，否则拉取官方镜像。
- `DockerClientFactory`、`DockerImageName`: Testcontainers 工具类，用于操作 Docker 镜像。

**注意事项:**
- 适用于自动化测试和本地开发环境，提升模型镜像复用效率。
- 支持多模型本地缓存，避免重复拉取镜像。
- 通常与 `LangChain4jOllamaContainer`、`AbstractOllamaInfrastructure` 配合使用。

```java
package utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

public class OllamaImage {

    public static final String OLLAMA_IMAGE = "ollama/ollama:latest";

    public static String localOllamaImage(String modelName) {
        return String.format("tc-%s-%s", OllamaImage.OLLAMA_IMAGE, modelName);
    }

    public static final String LLAMA_3_1 = "llama3.1";

    public static DockerImageName resolve(String baseImage, String localImageName) {
        DockerImageName dockerImageName = DockerImageName.parse(baseImage);
        DockerClient dockerClient = DockerClientFactory.instance().client();
        List<Image> images = dockerClient.listImagesCmd().withReferenceFilter(localImageName).exec();
        if (images.isEmpty()) {
            return dockerImageName;
        }
        return DockerImageName.parse(localImageName).asCompatibleSubstituteFor(baseImage);
    }
}
