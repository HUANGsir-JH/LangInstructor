
RAG (检索增强生成)
============

LLM 的知识仅限于它已经训练过的数据。 如果你想让 LLM 了解特定领域的知识或专有数据，你可以：

*   使用 RAG，我们将在本节中介绍
*   用你的数据微调 LLM
*   [结合 RAG 和微调](https://gorilla.cs.berkeley.edu/blogs/9_raft.html)

什么是 RAG？[​](#什么是-rag "什么是 RAG？的直接链接")
-------------------------------------

简单来说，RAG 是一种在发送给 LLM 之前，从你的数据中找到并注入相关信息片段到提示中的方法。 这样 LLM 将获得（希望是）相关信息，并能够使用这些信息回复， 这应该会降低产生幻觉的概率。

相关信息片段可以使用各种[信息检索](https://en.wikipedia.org/wiki/Information_retrieval)方法找到。 最流行的方法有：

*   全文（关键词）搜索。这种方法使用 TF-IDF 和 BM25 等技术， 通过匹配查询（例如，用户提问的内容）中的关键词与文档数据库进行搜索。 它根据每个文档中这些关键词的频率和相关性对结果进行排名。
*   向量搜索，也称为"语义搜索"。 文本文档使用嵌入模型转换为数字向量。 然后根据查询向量和文档向量之间的余弦相似度 或其他相似度/距离度量找到并排序文档， 从而捕捉更深层次的语义含义。
*   混合搜索。结合多种搜索方法（例如，全文 + 向量）通常可以提高搜索的有效性。

目前，本页主要关注向量搜索。 全文和混合搜索目前仅由 Azure AI Search 集成支持， 详情请参阅 `AzureAiSearchContentRetriever`。 我们计划在不久的将来扩展 RAG 工具箱，包括全文和混合搜索。

RAG 阶段[​](#rag-阶段 "RAG 阶段的直接链接")
--------------------------------

RAG 过程分为两个不同的阶段：索引和检索。 LangChain4j 为这两个阶段提供了工具。

### 索引[​](#索引 "索引的直接链接")

在索引阶段，文档会被预处理，以便在检索阶段进行高效搜索。

这个过程可能因使用的信息检索方法而异。 对于向量搜索，这通常涉及清理文档、用额外数据和元数据丰富文档、 将文档分割成更小的片段（也称为分块）、嵌入这些片段，最后将它们存储在嵌入存储（也称为向量数据库）中。

索引阶段通常是离线进行的，这意味着最终用户不需要等待其完成。 例如，可以通过定时任务在周末每周重新索引一次公司内部文档来实现。 负责索引的代码也可以是一个单独的应用程序，只处理索引任务。

然而，在某些情况下，最终用户可能希望上传自己的自定义文档，使 LLM 能够访问这些文档。 在这种情况下，索引应该在线进行，并成为主应用程序的一部分。

以下是索引阶段的简化图表：

[![](/assets/images/rag-ingestion-9b548e907df1c3c8948643795a981b95.png)](/tutorials/rag)

### 检索[​](#检索 "检索的直接链接")

检索阶段通常在线进行，当用户提交一个应该使用索引文档回答的问题时。

这个过程可能因使用的信息检索方法而异。 对于向量搜索，这通常涉及嵌入用户的查询（问题） 并在嵌入存储中执行相似度搜索。 然后将相关片段（原始文档的片段）注入到提示中并发送给 LLM。

以下是检索阶段的简化图表： [![](/assets/images/rag-retrieval-f525d2937abc08fed5cec36a7f08a4c3.png)](/tutorials/rag)

LangChain4j 中的 RAG 风格[​](#langchain4j-中的-rag-风格 "LangChain4j 中的 RAG 风格的直接链接")
-----------------------------------------------------------------------------

LangChain4j 提供了三种 RAG 风格：

*   [Easy RAG](/tutorials/rag/#easy-rag)：开始使用 RAG 的最简单方式
*   [Naive RAG](/tutorials/rag/#naive-rag)：使用向量搜索的基本 RAG 实现
*   [Advanced RAG](/tutorials/rag/#advanced-rag)：一个模块化的 RAG 框架，允许额外的步骤，如 查询转换、从多个来源检索和重新排序

Easy RAG[​](#easy-rag "Easy RAG的直接链接")
--------------------------------------

LangChain4j 有一个"Easy RAG"功能，使开始使用 RAG 变得尽可能简单。 你不必了解嵌入、选择向量存储、找到合适的嵌入模型、 弄清楚如何解析和分割文档等。 只需指向你的文档，LangChain4j 将完成其魔法。

如果你使用 Quarkus，有一种更简单的方法来实现 Easy RAG。 请阅读 [Quarkus 文档](https://docs.quarkiverse.io/quarkus-langchain4j/dev/easy-rag.html)。

备注

当然，这种"Easy RAG"的质量会低于定制的 RAG 设置。 然而，这是开始学习 RAG 和/或制作概念验证的最简单方法。 之后，你将能够平稳地从 Easy RAG 过渡到更高级的 RAG， 调整和定制更多方面。

1.  导入 `langchain4j-easy-rag` 依赖：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-easy-rag</artifactId>    <version>1.0.0-beta3</version></dependency>

2.  让我们加载你的文档：

    List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j/documentation");

这将加载指定目录中的所有文件。

幕后发生了什么？

使用 Apache Tika 库，它支持各种文档类型， 用于检测文档类型并解析它们。 由于我们没有明确指定使用哪个 `DocumentParser`， `FileSystemDocumentLoader` 将通过 SPI 加载 `ApacheTikaDocumentParser`， 由 `langchain4j-easy-rag` 依赖提供。

如何自定义加载文档？

如果你想从所有子目录加载文档，可以使用 `loadDocumentsRecursively` 方法：

    List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively("/home/langchain4j/documentation");

此外，你可以使用 glob 或正则表达式过滤文档：

    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j/documentation", pathMatcher);

备注

使用 `loadDocumentsRecursively` 方法时，你可能想在 glob 中使用双星号（而不是单个星号）： `glob:**.pdf`。

3.  现在，我们需要预处理文档并将其存储在专门的嵌入存储（也称为向量数据库）中。 这对于在用户提问时快速找到相关信息片段是必要的。 我们可以使用我们支持的 15+ [嵌入存储](/integrations/embedding-stores)中的任何一个， 但为了简单起见，我们将使用内存中的存储：

    InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();EmbeddingStoreIngestor.ingest(documents, embeddingStore);

幕后发生了什么？

1.  `EmbeddingStoreIngestor` 通过 SPI 从 `langchain4j-easy-rag` 依赖加载 `DocumentSplitter`。 每个 `Document` 被分割成更小的片段（`TextSegment`），每个片段不超过 300 个令牌， 并有 30 个令牌的重叠。
    
2.  `EmbeddingStoreIngestor` 通过 SPI 从 `langchain4j-easy-rag` 依赖加载 `EmbeddingModel`。 每个 `TextSegment` 使用 `EmbeddingModel` 转换为 `Embedding`。 ...
    

备注

We have chosen [bge-small-en-v1.5](https://huggingface.co/BAAI/bge-small-en-v1.5) as the default embedding model for Easy RAG. It has achieved an impressive score on the [MTEB leaderboard](https://huggingface.co/spaces/mteb/leaderboard), and its quantized version occupies only 24 megabytes of space. Therefore, we can easily load it into memory and run it in the same process using [ONNX Runtime](https://onnxruntime.ai/).

Yes, that's right, you can convert text into embeddings entirely offline, without any external services, in the same JVM process. LangChain4j offers 5 popular embedding models [out-of-the-box](https://github.com/langchain4j/langchain4j-embeddings).

3.  All `TextSegment`\-`Embedding` pairs are stored in the `EmbeddingStore`.

### Document[​](#document "Document的直接链接")

`Document` 类表示整个文档，如单个 PDF 文件或网页。 目前，`Document` 只能表示文本信息， 但未来的更新将使其支持图像和表格。

有用的方法

*   `Document.text()` 返回 `Document` 的文本
*   `Document.metadata()` 返回 `Document` 的 `Metadata`（参见下面的"Metadata"部分）
*   `Document.toTextSegment()` 将 `Document` 转换为 `TextSegment`（参见下面的"TextSegment"部分）
*   `Document.from(String, Metadata)` 从文本和 `Metadata` 创建 `Document`
*   `Document.from(String)` 从文本创建带有空 `Metadata` 的 `Document`

### Metadata[​](#metadata "Metadata的直接链接")

每个 `Document` 包含 `Metadata`。 它存储关于 `Document` 的元信息，如其名称、来源、最后更新日期、所有者， 或任何其他相关细节。

`Metadata` 存储为键值映射，其中键为 `String` 类型， 值可以是以下类型之一：`String`、`Integer`、`Long`、`Float`、`Double`。

`Metadata` 有几个用途：

*   在将 `Document` 的内容包含在提示中时， 元数据条目也可以包括在内，为 LLM 提供额外的信息考虑。 例如，提供 `Document` 名称和来源可以帮助提高 LLM 对内容的理解。
*   在搜索相关内容以包含在提示中时， 可以通过 `Metadata` 条目进行过滤。 例如，你可以将语义搜索范围缩小到仅属于特定所有者的 `Document`。
*   当 `Document` 的来源更新时（例如，文档的特定页面）， 可以通过其元数据条目（例如，"id"、"source"等）轻松定位相应的 `Document`， 并在 `EmbeddingStore` 中更新它以保持同步。

有用的方法

*   `Metadata.from(Map)` 从 `Map` 创建 `Metadata`
*   `Metadata.put(String key, String value)` / `put(String, int)` / 等，向 `Metadata` 添加条目
*   `Metadata.putAll(Map)` 向 `Metadata` 添加多个条目
*   `Metadata.getString(String key)` / `getInteger(String key)` / 等，返回 `Metadata` 条目的值，将其转换为所需类型
*   `Metadata.containsKey(String key)` 检查 `Metadata` 是否包含指定键的条目
*   `Metadata.remove(String key)` 通过键从 `Metadata` 中删除条目
*   `Metadata.copy()` 返回 `Metadata` 的副本 ...(省略约 191 行)... 而不是在提示中包含整个知识库：
*   LLM 有有限的上下文窗口，所以整个知识库可能无法容纳
*   在提示中提供的信息越多，LLM 处理和响应所需的时间就越长
*   在提示中提供的信息越多，你支付的费用就越多
*   提示中的不相关信息可能会分散 LLM 的注意力并增加产生幻觉的可能性
*   在提示中提供的信息越多，就越难解释 LLM 基于哪些信息做出了响应

我们可以通过将知识库分割成更小、更易于消化的片段来解决这些问题。 这些片段应该有多大？这是一个好问题。一如既往，这取决于具体情况。

目前有两种广泛使用的方法：

1.  每个文档（例如，PDF 文件、网页等）是原子的且不可分割的。 在 RAG 管道的检索过程中，检索 N 个最相关的文档并注入到提示中。 在这种情况下，你很可能需要使用长上下文 LLM，因为文档可能相当长。 如果检索完整文档很重要，例如当你不能错过一些细节时，这种方法是合适的。

*   优点：不会丢失上下文。
*   缺点：
    *   消耗更多令牌。
    *   有时，文档可能包含多个部分/主题，并非所有这些都与查询相关。
    *   向量搜索质量受到影响，因为各种大小的完整文档被压缩成单一的固定长度向量。

2.  文档被分割成更小的片段，如章节、段落，有时甚至是句子。 在 RAG 管道的检索过程中，检索 N 个最相关的片段并注入到提示中。 挑战在于确保每个片段提供足够的上下文/信息，使 LLM 能够理解它。 缺少上下文可能导致 LLM 误解给定的片段并产生幻觉。 一种常见的策略是将文档分割成有重叠的片段，但这并不能完全解决问题。 几种高级技术可以在这里提供帮助，例如，"句子窗口检索"、"自动合并检索" 和"父文档检索"。 我们不会在这里详细介绍，但本质上，这些方法有助于获取检索片段周围的更多上下文， 在检索片段之前和之后为 LLM 提供额外信息。

*   优点：
    *   更好的向量搜索质量。
    *   减少令牌消耗。
*   缺点：仍可能丢失一些上下文。

有用的方法

*   `TextSegment.text()` 返回 `TextSegment` 的文本
*   `TextSegment.metadata()` 返回 `TextSegment` 的 `Metadata`
*   `TextSegment.from(String, Metadata)` 从文本和 `Metadata` 创建 `TextSegment`
*   `TextSegment.from(String)` 从文本创建带有空 `Metadata` 的 `TextSegment`

### Document Splitter[​](#document-splitter "Document Splitter的直接链接")

LangChain4j 有一个 `DocumentSplitter` 接口，带有几个开箱即用的实现：

*   `DocumentByParagraphSplitter`
*   `DocumentByLineSplitter`
*   `DocumentBySentenceSplitter`
*   `DocumentByWordSplitter`
*   `DocumentByCharacterSplitter`
*   `DocumentByRegexSplitter`
*   递归：`DocumentSplitters.recursive(...)`

它们的工作方式如下：

1.  你实例化一个 `DocumentSplitter`，指定所需的 `TextSegment` 大小， 并可选择指定字符或令牌的重叠。
2.  你调用 `DocumentSplitter` 的 `split(Document)` 或 `splitAll(List<Document>)` 方法。
3.  `DocumentSplitter` 将给定的 `Document` 分割成更小的单元， 这些单元的性质因分割器而异。例如，`DocumentByParagraphSplitter` 将 文档分成段落（由两个或更多连续的换行符定义）， 而 `DocumentBySentenceSplitter` 使用 OpenNLP 库的句子检测器将 文档分成句子，等等。
4.  然后，`DocumentSplitter` 将这些更小的单元（段落、句子、单词等）组合成 `TextSegment`， 尝试在不超过步骤 1 中设置的限制的情况下，在单个 `TextSegment` 中包含尽可能多的单元。 如果某些单元仍然太大而无法放入 `TextSegment`，它会调用子分割器。 这是另一个 `DocumentSplitter`，能够将不适合的单元分割成更细粒度的单元。 所有 `Metadata` 条目都从 `Document` 复制到每个 `TextSegment`。 每个文本片段都添加了一个唯一的元数据条目"index"。 第一个 `TextSegment` 将包含 `index=0`，第二个 `index=1`，依此类推。

### Text Segment Transformer[​](#text-segment-transformer "Text Segment Transformer的直接链接")

`TextSegmentTransformer` 类似于 `DocumentTransformer`（上面描述的），但它转换 `TextSegment`。

与 `DocumentTransformer` 一样，没有一种通用的解决方案， 所以我们建议实现你自己的 `TextSegmentTransformer`，根据你独特的数据定制。

一种对改善检索效果很好的技术是在每个 `TextSegment` 中包含 `Document` 标题或简短摘要。

### Embedding[​](#embedding "Embedding的直接链接")

`Embedding` 类封装了一个数值向量，表示已嵌入内容（通常是文本，如 `TextSegment`）的"语义含义"。

在这里阅读更多关于向量嵌入的信息：

*   [https://www.elastic.co/what-is/vector-embedding](https://www.elastic.co/what-is/vector-embedding)
*   [https://www.pinecone.io/learn/vector-embeddings/](https://www.pinecone.io/learn/vector-embeddings/)
*   [https://cloud.google.com/blog/topics/developers-practitioners/meet-ais-multitool-vector-embeddings](https://cloud.google.com/blog/topics/developers-practitioners/meet-ais-multitool-vector-embeddings)

有用的方法

*   `Embedding.dimension()` 返回嵌入向量的维度（其长度）
*   `CosineSimilarity.between(Embedding, Embedding)` 计算两个 `Embedding` 之间的余弦相似度
*   `Embedding.normalize()` 规范化嵌入向量（就地）

### Embedding Model[​](#embedding-model "Embedding Model的直接链接")

`EmbeddingModel` 接口表示一种特殊类型的模型，将文本转换为 `Embedding`。

当前支持的嵌入模型可以在[这里](/category/embedding-models)找到。

有用的方法

*   `EmbeddingModel.embed(String)` 嵌入给定的文本
*   `EmbeddingModel.embed(TextSegment)` 嵌入给定的 `TextSegment`
*   `EmbeddingModel.embedAll(List<TextSegment>)` 嵌入所有给定的 `TextSegment`
*   `EmbeddingModel.dimension()` 返回此模型产生的 `Embedding` 的维度

...

#### Expanding Query Transformer[​](#expanding-query-transformer "Expanding Query Transformer的直接链接")

`ExpandingQueryTransformer` 使用 LLM 将给定的 `Query` 扩展为多个 `Query`。 这很有用，因为 LLM 可以以各种方式重新表述和重新表达 `Query`， 这将有助于检索更多相关内容。

### Content[​](#content "Content的直接链接")

`Content` 表示与用户 `Query` 相关的内容。 目前，它仅限于文本内容（即 `TextSegment`）， 但将来可能支持其他模态（例如，图像、音频、视频等）。

### Content Retriever[​](#content-retriever "Content Retriever的直接链接")

`ContentRetriever` 使用给定的 `Query` 从底层数据源检索 `Content`。 底层数据源可以是几乎任何东西：

*   嵌入存储
*   全文搜索引擎
*   向量和全文搜索的混合
*   网络搜索引擎
*   知识图谱
*   SQL 数据库
*   等等

`ContentRetriever` 返回的 `Content` 列表按相关性排序，从最高到最低。

#### Embedding Store Content Retriever[​](#embedding-store-content-retriever "Embedding Store Content Retriever的直接链接")

`EmbeddingStoreContentRetriever` 使用 `EmbeddingModel` 嵌入 `Query`， 从 `EmbeddingStore` 检索相关 `Content`。

以下是一个例子：

    EmbeddingStore embeddingStore = ...EmbeddingModel embeddingModel = ...ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()    .embeddingStore(embeddingStore)    .embeddingModel(embeddingModel)    .maxResults(3)     // maxResults 也可以根据查询动态指定    .dynamicMaxResults(query -> 3)    .minScore(0.75)     // minScore 也可以根据查询动态指定    .dynamicMinScore(query -> 0.75)    .filter(metadataKey("userId").isEqualTo("12345"))    // filter 也可以根据查询动态指定    .dynamicFilter(query -> {        String userId = getUserId(query.metadata().chatMemoryId());        return metadataKey("userId").isEqualTo(userId);    })    .build();

#### Web Search Content Retriever[​](#web-search-content-retriever "Web Search Content Retriever的直接链接")

`WebSearchContentRetriever` 使用 `WebSearchEngine` 从网络检索相关 `Content`。

所有支持的 `WebSearchEngine` 集成可以在[这里](/category/web-search-engines)找到。

以下是一个例子：

    WebSearchEngine googleSearchEngine = GoogleCustomWebSearchEngine.builder()        .apiKey(System.getenv("GOOGLE_API_KEY"))        .csi(System.getenv("GOOGLE_SEARCH_ENGINE_ID"))        .build();ContentRetriever contentRetriever = WebSearchContentRetriever.builder()        .webSearchEngine(googleSearchEngine)        .maxResults(3)        .build();

完整示例可以在[这里](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_08_Advanced_RAG_Web_Search_Example.java)找到。

#### SQL Database Content Retriever[​](#sql-database-content-retriever "SQL Database Content Retriever的直接链接")

`SqlDatabaseContentRetriever` 是 `ContentRetriever` 的一个实验性实现， 可以在 `langchain4j-experimental-sql` 模块中找到。

它使用 `DataSource` 和 LLM 为给定的自然语言 `Query` 生成并执行 SQL 查询。

有关更多信息，请参阅 `SqlDatabaseContentRetriever` 的 javadoc。

这里有一个[示例](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_10_Advanced_RAG_SQL_Database_Retreiver_Example.java)。

#### Azure AI Search Content Retriever[​](#azure-ai-search-content-retriever "Azure AI Search Content Retriever的直接链接")

`AzureAiSearchContentRetriever` 是与 [Azure AI Search](https://azure.microsoft.com/en-us/products/ai-services/ai-search) 的集成。 它支持全文、向量和混合搜索，以及重新排序。 它可以在 `langchain4j-azure-ai-search` 模块中找到。 请参阅 `AzureAiSearchContentRetriever` Javadoc 获取更多信息。

#### Neo4j Content Retriever[​](#neo4j-content-retriever "Neo4j Content Retriever的直接链接")

`Neo4jContentRetriever` 是与 [Neo4j](https://neo4j.com/) 图数据库的集成。 它将自然语言查询转换为 Neo4j Cypher 查询， 并通过在 Neo4j 中运行这些查询来检索相关信息。 它可以在 `langchain4j-community-neo4j-retriever` 模块中找到。

### Query Router[​](#query-router "Query Router的直接链接")

`QueryRouter` 负责将 `Query` 路由到适当的 `ContentRetriever`。

#### Default Query Router[​](#default-query-router "Default Query Router的直接链接")

`DefaultQueryRouter` 是 `DefaultRetrievalAugmentor` 中使用的默认实现。 它将每个 `Query` 路由到所有配置的 `ContentRetriever`。

#### Language Model Query Router[​](#language-model-query-router "Language Model Query Router的直接链接")

`LanguageModelQueryRouter` 使用 LLM 决定将给定的 `Query` 路由到哪里。

### Content Aggregator[​](#content-aggregator "Content Aggregator的直接链接")

`ContentAggregator` 负责聚合来自以下来源的多个排序 `Content` 列表：

*   多个 `Query`
*   多个 `ContentRetriever`
*   两者都有

#### Default Content Aggregator[​](#default-content-aggregator "Default Content Aggregator的直接链接")

`DefaultContentAggregator` 是 `ContentAggregator` 的默认实现， 它执行两阶段倒数排名融合（RRF）。 更多详细信息请参阅 `DefaultContentAggregator` 的 Javadoc。

#### Re-Ranking Content Aggregator[​](#re-ranking-content-aggregator "Re-Ranking Content Aggregator的直接链接")

`ReRankingContentAggregator` 使用 `ScoringModel`（如 Cohere）执行重新排序。 支持的评分（重新排序）模型完整列表可以在 [这里](https://docs.langchain4j.dev/category/scoring-reranking-models)找到。 更多详细信息请参阅 `ReRankingContentAggregator` 的 Javadoc。

### Content Injector[​](#content-injector "Content Injector的直接链接")

`ContentInjector` 负责将 `ContentAggregator` 返回的 `Content` 注入到 `UserMessage` 中。

#### Default Content Injector[​](#default-content-injector "Default Content Injector的直接链接")

`DefaultContentInjector` 是 `ContentInjector` 的默认实现，它简单地将 `Content` 附加到 `UserMessage` 的末尾，前缀为 `Answer using the following information:`。

你可以通过 3 种方式自定义 `Content` 如何注入到 `UserMessage` 中：

*   覆盖默认的 `PromptTemplate`：

    RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()    .contentInjector(DefaultContentInjector.builder()        .promptTemplate(PromptTemplate.from("{{userMessage}}\n{{contents}}"))        .build())    .build();

请注意，`PromptTemplate` 必须包含 `{{userMessage}}` 和 `{{contents}}` 变量。

*   扩展 `DefaultContentInjector` 并覆盖其中一个 `format` 方法
*   实现自定义 `ContentInjector`

`DefaultContentInjector` 还支持从检索到的 `Content.textSegment()` 中注入 `Metadata` 条目：

    DefaultContentInjector.builder()    .metadataKeysToInclude(List.of("source"))    .build()

在这种情况下，`TextSegment.text()` 将以 "content: " 前缀开头， 而 `Metadata` 中的每个值将以键作为前缀。 最终的 `UserMessage` 将如下所示：

    How can I cancel my reservation?Answer using the following information:content: To cancel a reservation, go to ...source: ./cancellation_procedure.htmlcontent: Cancellation is allowed for ...source: ./cancellation_policy.html

### 并行化[​](#并行化 "并行化的直接链接")

当只有一个 `Query` 和一个 `ContentRetriever` 时， `DefaultRetrievalAugmentor` 在同一线程中执行查询路由和内容检索。 否则，将使用 `Executor` 来并行处理。 默认情况下，使用修改后的（`keepAliveTime` 为 1 秒而不是 60 秒）`Executors.newCachedThreadPool()`， 但你可以在创建 `DefaultRetrievalAugmentor` 时提供自定义的 `Executor` 实例：

    DefaultRetrievalAugmentor.builder()        ...        .executor(executor)        .build;

访问来源[​](#访问来源 "访问来源的直接链接")
--------------------------

如果你希望在使用 [AI Services](/tutorials/ai-services) 时访问来源（用于增强消息的检索到的 `Content`）， 你可以通过将返回类型包装在 `Result` 类中轻松实现：

    interface Assistant {    Result<String> chat(String userMessage);}Result<String> result = assistant.chat("How to do Easy RAG with LangChain4j?");String answer = result.content();List<Content> sources = result.sources();

在流式处理时，可以使用 `onRetrieved()` 方法指定 `Consumer<List<Content>>`：

    interface Assistant {    TokenStream chat(String userMessage);}assistant.chat("How to do Easy RAG with LangChain4j?")    .onRetrieved((List<Content> sources) -> ...)    .onPartialResponse(...)    .onCompleteResponse(...)    .onError(...)    .start();

示例[​](#示例 "示例的直接链接")
--------------------

*   [Easy RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_1_easy/Easy_RAG_Example.java)
*   [Naive RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_2_naive/Naive_RAG_Example.java)
*   [使用查询压缩的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_01_Advanced_RAG_with_Query_Compression_Example.java)
*   [使用查询路由的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_02_Advanced_RAG_with_Query_Routing_Example.java)
*   [使用重新排序的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_03_Advanced_RAG_with_ReRanking_Example.java)
*   [包含元数据的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_04_Advanced_RAG_with_Metadata_Example.java)
*   [使用元数据过滤的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_05_Advanced_RAG_with_Metadata_Filtering_Examples.java)
*   [使用多个检索器的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_07_Advanced_RAG_Multiple_Retrievers_Example.java)
*   [使用网络搜索的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_08_Advanced_RAG_Web_Search_Example.java)
*   [使用 SQL 数据库的高级 RAG](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_10_Advanced_RAG_SQL_Database_Retreiver_Example.java)
*   [跳过检索](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_06_Advanced_RAG_Skip_Retrieval_Example.java)
*   [RAG + 工具](https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/test/java/dev/langchain4j/example/CustomerSupportAgentIT.java)
*   [加载文档](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/DocumentLoaderExamples.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/rag.md)