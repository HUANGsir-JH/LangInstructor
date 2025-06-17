# LangChain4jOllamaContainer.java 示例

**示例目的:**
此文件扩展了 Testcontainers 的 Ollama 容器，支持自动拉取指定大模型镜像，便于在自动化测试或本地开发中动态管理 Ollama 模型环境。

**关键类和方法:**
- `LangChain4jOllamaContainer(DockerImageName)`: 构造函数，指定 Docker 镜像名称。
- `withModel(String model)`: 设置需要自动拉取的模型名称，支持链式调用。
- `containerIsStarted(InspectContainerResponse)`: 容器启动后自动拉取指定模型，拉取过程有日志提示。
- `execInContainer(...)`: 在容器内执行 `ollama pull` 命令，确保模型可用。
- `Logger`: 日志输出，便于跟踪模型拉取进度和异常。

**注意事项:**
- 依赖 Testcontainers 和 Ollama 官方镜像，适合自动化测试和本地一键环境搭建。
- 支持异常处理，拉取失败会抛出运行时异常。
- 通常与 `AbstractOllamaInfrastructure` 配合使用，实现自动化模型环境管理。

```java
package utils;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

public class LangChain4jOllamaContainer extends OllamaContainer {

    private static final Logger log = LoggerFactory.getLogger(LangChain4jOllamaContainer.class);

    private String model;

    public LangChain4jOllamaContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public LangChain4jOllamaContainer withModel(String model) {
        this.model = model;
        return this;
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        if (this.model != null) {
            try {
                log.info("Start pulling the '{}' model ... would take several minutes ...", this.model);
                ExecResult r = execInContainer("ollama", "pull", this.model);
                log.info("Model pulling competed! {}", r);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error pulling model", e);
            }
        }
    }
}
