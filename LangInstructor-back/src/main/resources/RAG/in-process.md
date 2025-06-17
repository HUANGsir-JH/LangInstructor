
进程内 (ONNX)
==========

LangChain4j 提供了几个流行的本地嵌入模型，打包为 maven 依赖项。 它们由 [ONNX 运行时](https://onnxruntime.ai/docs/get-started/with-java.html) 提供支持， 并在同一个 Java 进程中运行。

每个模型提供两种版本：原始版本和量化版本（在 maven 构件名称中带有 `-q` 后缀，类名中带有 `Quantized`）。

例如：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>    <version>1.0.0-beta3</version></dependency>

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();Response<Embedding> response = embeddingModel.embed("test");Embedding embedding = response.content();

或量化版本：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-embeddings-all-minilm-l6-v2-q</artifactId>    <version>1.0.0-beta3</version></dependency>

    EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();Response<Embedding> response = embeddingModel.embed("test");Embedding embedding = response.content();

所有嵌入模型的完整列表可以在[这里](https://github.com/langchain4j/langchain4j-embeddings)找到。

并行化[​](#并行化 "并行化的直接链接")
-----------------------

默认情况下，嵌入过程使用所有可用的 CPU 核心进行并行处理， 因此每个 `TextSegment` 都在单独的线程中进行嵌入。

并行化是通过使用 `Executor` 完成的。 默认情况下，进程内嵌入模型使用缓存线程池， 线程数等于可用处理器的数量。 线程缓存 1 秒。

您可以在创建模型时提供自定义的 `Executor` 实例：

    Executor = ...;EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel(executor);

目前不支持使用 GPU 进行嵌入。

自定义模型[​](#自定义模型 "自定义模型的直接链接")
-----------------------------

许多模型（例如来自 [Hugging Face](https://huggingface.co/) 的模型）都可以使用， 只要它们是 ONNX 格式。

有关如何将模型转换为 ONNX 格式的信息可以在[这里](https://huggingface.co/docs/optimum/exporters/onnx/usage_guides/export_a-model)找到。

许多已经转换为 ONNX 格式的模型可以在[这里](https://huggingface.co/Xenova)找到。

使用自定义嵌入模型的示例：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-embeddings</artifactId>    <version>1.0.0-beta3</version></dependency>

    String pathToModel = "/home/langchain4j/model.onnx";String pathToTokenizer = "/home/langchain4j/tokenizer.json";PoolingMode poolingMode = PoolingMode.MEAN;EmbeddingModel embeddingModel = new OnnxEmbeddingModel(pathToModel, pathToTokenizer, poolingMode);Response<Embedding> response = embeddingModel.embed("test");Embedding embedding = response.content();

示例[​](#示例 "示例的直接链接")
--------------------

*   [InProcessEmbeddingModelExamples](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/model/InProcessEmbeddingModelExamples.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/embedding-models/1-in-process.md)
