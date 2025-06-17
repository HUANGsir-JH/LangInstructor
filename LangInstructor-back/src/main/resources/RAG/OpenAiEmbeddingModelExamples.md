# OpenAiEmbeddingModelExamples.java 示例

**示例目的:**
此文件演示了如何使用 OpenAI 嵌入模型将文本转换为向量（Embedding），适用于文本相似度、检索增强等场景。

**关键类和方法:**
- `OpenAiEmbeddingModel`: 用于与 OpenAI 嵌入模型交互的核心类。
- `embed(String)`: 对输入文本进行嵌入，返回 `Response<Embedding>`。
- `EmbeddingModel`: 通用嵌入模型接口，便于后续模型切换。
- `TEXT_EMBEDDING_3_SMALL`: OpenAI 最新的小型嵌入模型名称常量。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 嵌入结果可用于文本检索、语义搜索、聚类等多种场景。
- 输出为输入文本的向量表示。

```java
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class OpenAiEmbeddingModelExamples {

    public static void main(String[] args) {

        EmbeddingModel model = OpenAiEmbeddingModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .build();

        Response<Embedding> response = model.embed("I love Java");
        Embedding embedding = response.content();

        System.out.println(embedding);
    }
}
