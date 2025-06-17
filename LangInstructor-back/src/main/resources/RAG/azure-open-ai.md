Azure OpenAI | LangChain4j 中文文档  function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag("js",new Date),gtag("config","G-46M4FH02G5",{anonymize_ip:!0})  !function(){function t(t){document.documentElement.setAttribute("data-theme",t)}var e=function(){try{return new URLSearchParams(window.location.search).get("docusaurus-theme")}catch(t){}}()||function(){try{return window.localStorage.getItem("theme")}catch(t){}}();t(null!==e?e:"light")}(),function(){try{const n=new URLSearchParams(window.location.search).entries();for(var[t,e]of n)if(t.startsWith("docusaurus-data-")){var a=t.replace("docusaurus-data-","data-");document.documentElement.setAttribute(a,e)}}catch(t){}}()

[跳到主要内容](#__docusaurus_skipToContent_fallback)

[

![LangChain4j Logo](/img/logo.svg)![LangChain4j Logo](/img/logo.svg)

**LangChain4j**](/)[介绍](/intro)[快速开始](/get-started)[教程](/category/教程)[集成](/category/集成)[实用资料](/useful-materials)[Examples](https://github.com/langchain4j/langchain4j-examples)[Docu chatbot](https://chat.langchain4j.dev/)[Javadoc](https://docs.langchain4j.dev/apidocs/index.html)

[GitHub](https://github.com/langchain4j/langchain4j)[Twitter](https://twitter.com/langchain4j)[Discord](https://discord.com/invite/JzTFvyjG6R)

*   [介绍](/intro)
*   [开始使用](/get-started)
*   [教程](/category/教程)
    
*   [集成](/category/集成)
    
    *   [Language Models](/category/language-models)
        
        *   [所有支持的语言模型比较表](/integrations/language-models/)
        *   [Amazon Bedrock](/integrations/language-models/amazon-bedrock)
        *   [Anthropic](/integrations/language-models/anthropic)
        *   [Azure OpenAI](/integrations/language-models/azure-open-ai)
        *   [ChatGLM](/integrations/language-models/chatglm)
        *   [DashScope (通义千问)](/integrations/language-models/dashscope)
        *   [GitHub Models](/integrations/language-models/github-models)
        *   [Google AI Gemini](/integrations/language-models/google-ai-gemini)
        *   [Google Vertex AI Gemini](/integrations/language-models/google-vertex-ai-gemini)
        *   [Google Vertex AI PaLM 2](/integrations/language-models/google-palm)
        *   [Hugging Face](/integrations/language-models/hugging-face)
        *   [Jlama](/integrations/language-models/jlama)
        *   [LocalAI](/integrations/language-models/local-ai)
        *   [MistralAI](/integrations/language-models/mistral-ai)
        *   [Ollama](/integrations/language-models/ollama)
        *   [OpenAI](/integrations/language-models/open-ai)
        *   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official)
        *   [千帆](/integrations/language-models/qianfan)
        *   [Cloudflare Workers AI](/integrations/language-models/workers-ai)
        *   [智谱 AI](/integrations/language-models/zhipu-ai)
        *   [Xinference](/integrations/language-models/xinference)
    *   [Embedding Models](/category/embedding-models)
        
    *   [Embedding Stores](/category/embedding-stores)
        
    *   [Image Models](/category/image-models)
        
    *   [文档加载器](/category/文档加载器)
        
    *   [Document Parsers](/category/document-parsers)
        
    *   [Scoring (Reranking) Models](/category/scoring-reranking-models)
        
    *   [代码执行引擎](/category/代码执行引擎)
        
    *   [Frameworks](/category/frameworks)
        
    *   [Web Search Engines](/category/web-search-engines)
        
*   [实用资料](/category/实用资料)
    
*   [Latest Release Notes](/latest-release-notes)

*   [](/)
*   [集成](/category/集成)
*   [Language Models](/category/language-models)
*   Azure OpenAI

本页总览

Azure OpenAI
============

备注

这是 `Azure OpenAI` 集成的文档，它使用来自微软的 Azure SDK，如果您使用的是微软 Java 技术栈（包括高级 Azure 身份验证机制），它会工作得最好。

LangChain4j 提供了 4 种不同的 OpenAI 集成方式来使用聊天模型，这是第 3 种：

*   [OpenAI](/integrations/language-models/open-ai) 使用 OpenAI REST API 的自定义 Java 实现，与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）配合最佳。
*   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official) 使用官方 OpenAI Java SDK。
*   [Azure OpenAI](/integrations/language-models/azure-open-ai) 使用来自微软的 Azure SDK，如果您使用的是微软 Java 技术栈（包括高级 Azure 身份验证机制），它会工作得最好。
*   [GitHub Models](/integrations/language-models/github-models) 使用 Azure AI 推理 API 访问 GitHub 模型。

Azure OpenAI 提供托管在 Azure 上的 OpenAI 语言模型（`gpt-4`、`gpt-4o` 等），使用 [Azure OpenAI Java SDK](https://learn.microsoft.com/en-us/java/api/overview/azure/ai-openai-readme)。

Azure OpenAI 文档[​](#azure-openai-文档 "Azure OpenAI 文档的直接链接")
-----------------------------------------------------------

*   [Azure OpenAI 文档](https://learn.microsoft.com/en-us/azure/ai-services/openai/)

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

### 纯 Java[​](#纯-java "纯 Java的直接链接")

`langchain4j-azure-open-ai` 库可在 Maven Central 上获取。

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-azure-open-ai</artifactId>    <version>1.0.0-beta3</version></dependency>

### Spring Boot[​](#spring-boot "Spring Boot的直接链接")

Spring Boot 启动器可用于更轻松地配置 `langchain4j-azure-open-ai` 库。

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-azure-open-ai-spring-boot-starter</artifactId>    <version>1.0.0-beta3</version></dependency>

备注

在使用任何 Azure OpenAI 模型之前，您需要[部署](https://learn.microsoft.com/en-us/azure/ai-services/openai/how-to/create-resource?pivots=web-portal)它们。

使用 API 密钥创建 `AzureOpenAiChatModel`[​](#使用-api-密钥创建-azureopenaichatmodel "使用-api-密钥创建-azureopenaichatmodel的直接链接")
----------------------------------------------------------------------------------------------------------------

### 纯 Java[​](#纯-java-1 "纯 Java的直接链接")

    ChatLanguageModel model = AzureOpenAiChatModel.builder()        .endpoint(System.getenv("AZURE_OPENAI_URL"))        .apiKey(System.getenv("AZURE_OPENAI_KEY"))        .deploymentName("gpt-4o")        ...        .build();

这将创建一个具有默认模型参数（例如 `0.7` 温度等）的 `AzureOpenAiChatModel` 实例， 并使用存储在 `AZURE_OPENAI_KEY` 环境变量中的 API 密钥。 可以通过在构建器中提供值来自定义默认模型参数。

### Spring Boot[​](#spring-boot-1 "Spring Boot的直接链接")

在 `application.properties` 中添加：

    langchain4j.azure-open-ai.chat-model.endpoint=${AZURE_OPENAI_URL}langchain4j.azure-open-ai.chat-model.service-version=...langchain4j.azure-open-ai.chat-model.api-key=${AZURE_OPENAI_KEY}langchain4j.azure-open-ai.chat-model.non-azure-api-key=${OPENAI_API_KEY}langchain4j.azure-open-ai.chat-model.deployment-name=gpt-4olangchain4j.azure-open-ai.chat-model.max-tokens=...langchain4j.azure-open-ai.chat-model.temperature=...langchain4j.azure-open-ai.chat-model.top-p=langchain4j.azure-open-ai.chat-model.logit-bias=...langchain4j.azure-open-ai.chat-model.user=langchain4j.azure-open-ai.chat-model.stop=...langchain4j.azure-open-ai.chat-model.presence-penalty=...langchain4j.azure-open-ai.chat-model.frequency-penalty=...langchain4j.azure-open-ai.chat-model.seed=...langchain4j.azure-open-ai.chat-model.strict-json-schema=...langchain4j.azure-open-ai.chat-model.timeout=...langchain4j.azure-open-ai.chat-model.max-retries=...langchain4j.azure-open-ai.chat-model.log-requests-and-responses=...langchain4j.azure-open-ai.chat-model.user-agent-suffix=langchain4j.azure-open-ai.chat-model.custom-headers=...

上述部分参数的描述可以在[这里](https://learn.microsoft.com/en-us/azure/ai-services/openai/reference#completions)找到。

此配置将创建一个 `AzureOpenAiChatModel` bean（具有默认模型参数）， 可以由 [AI 服务](/tutorials/spring-boot-integration/#langchain4j-spring-boot-starter)使用， 或在需要的地方自动装配，例如：

    @RestControllerclass ChatLanguageModelController {    ChatLanguageModel chatLanguageModel;    ChatLanguageModelController(ChatLanguageModel chatLanguageModel) {        this.chatLanguageModel = chatLanguageModel;    }    @GetMapping("/model")    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {        return chatLanguageModel.chat(message);    }}

使用 Azure 凭证创建 `AzureOpenAiChatModel`[​](#使用-azure-凭证创建-azureopenaichatmodel "使用-azure-凭证创建-azureopenaichatmodel的直接链接")
-----------------------------------------------------------------------------------------------------------------------

API 密钥可能存在一些安全问题（可能被提交、传递等）。 如果您想提高安全性，建议使用 Azure 凭证。 为此，需要将 `azure-identity` 依赖项添加到项目中。

    <dependency>    <groupId>com.azure</groupId>    <artifactId>azure-identity</artifactId>    <scope>compile</scope></dependency>

然后，您可以使用 [DefaultAzureCredentialBuilder](https://learn.microsoft.com/en-us/java/api/com.azure.identity.defaultazurecredentialbuilder?view=azure-java-stable) API 创建 `AzureOpenAiChatModel`：

    ChatLanguageModel model = AzureOpenAiChatModel.builder()        .deploymentName("gpt-4o")        .endpoint(System.getenv("AZURE_OPENAI_URL"))        .tokenCredential(new DefaultAzureCredentialBuilder().build())        .build();

备注

请注意，您需要使用托管身份部署模型。查看 [Azure CLI 部署脚本](https://github.com/langchain4j/langchain4j-examples/blob/main/azure-open-ai-examples/src/main/script/deploy-azure-openai-security.sh)获取更多信息。

工具[​](#工具 "工具的直接链接")
--------------------

工具，也称为"函数调用"，允许模型调用 Java 代码中的方法，包括并行工具调用。 "函数调用"在 OpenAI 文档中有[描述](https://platform.openai.com/docs/guides/function-calling)。

备注

LangChain4j 中有关如何使用"函数调用"的完整教程在[这里](/tutorials/tools/)。

函数可以使用 `ToolSpecification` 类指定，或者更简单地使用 `@Tool` 注解，如下例所示：

    class StockPriceService {    private Logger log = Logger.getLogger(StockPriceService.class.getName());    @Tool("获取公司的股票价格，通过其股票代码")    public double getStockPrice(@P("公司股票代码") String ticker) {        log.info("获取 " + ticker + " 的股票价格");        if (Objects.equals(ticker, "MSFT")) {            return 400.0;        } else {            return 0.0;        }    }}

然后，您可以在 AI `Assistant` 中使用 `StockPriceService`，如下所示：

    interface Assistant {    String chat(String userMessage);}public class Demo {    String functionCalling(Model model) {        String question = "当前微软股票价格是否高于 450 美元？";        StockPriceService stockPriceService = new StockPriceService();        Assistant assistant = AiServices.builder(Assistant.class)                .chatLanguageModel(model)                .tools(stockPriceService)                .build();        String answer = assistant.chat(question);        model.addAttribute("answer", answer);        return "demo";    }}

结构化输出[​](#结构化输出 "结构化输出的直接链接")
-----------------------------

结构化输出确保模型的响应符合 JSON 模式。

备注

LangChain4j 中使用结构化输出的文档可在[这里](/tutorials/structured-outputs)获取，下面的部分将提供 Azure OpenAI 特定的信息。

需要将模型配置为 `strictJsonSchema` 参数设置为 `true`，以强制遵守 JSON 模式：

    ChatLanguageModel model = AzureOpenAiChatModel.builder()        .endpoint(System.getenv("AZURE_OPENAI_URL"))        .apiKey(System.getenv("AZURE_OPENAI_KEY"))        .deploymentName("gpt-4o")        .strictJsonSchema(true)        .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))        .build();

备注

如果 `strictJsonSchema` 设置为 `false` 并且您提供了 JSON 模式，模型仍会尝试生成符合该模式的响应，但如果响应不符合模式，它不会失败。这样做的一个原因是为了获得更好的性能。

然后，您可以将此模型与高级 `Assistant` API 或低级 `ChatLanguageModel` API 一起使用，如下所述。 当与高级 `Assistant` API 一起使用时，配置 `supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))` 以启用带有 JSON 模式的结构化输出。

### 使用高级 `Assistant` API[​](#使用高级-assistant-api "使用高级-assistant-api的直接链接")

与前面部分中的工具类似，结构化输出可以自动与 AI `Assistant` 一起使用：

    interface PersonAssistant {    Person extractPerson(String message);}class Person {    private final String name;    private final List<String> favouriteColors;    public Person(String name, List<String> favouriteColors) {        this.name = name;        this.favouriteColors = favouriteColors;    }    public String getName() {        return name;    }    public List<String> getFavouriteColors() {        return favouriteColors;    }}

这个 `Assistant` 将确保响应符合与 `Person` 类对应的 JSON 模式，如下例所示：

    String question = "Julien 喜欢蓝色、白色和红色";PersonAssistant assistant = AiServices.builder(PersonAssistant.class)                .chatLanguageModel(chatLanguageModel)                .build();Person person = assistant.extractPerson(question);

### 使用低级 `ChatLanguageModel` API[​](#使用低级-chatlanguagemodel-api "使用低级-chatlanguagemodel-api的直接链接")

这个过程与高级 API 类似，但这次需要手动配置 JSON 模式，以及将 JSON 响应映射到 Java 对象。

一旦模型配置完成，必须在每个请求的 `ChatRequest` 对象中指定 JSON 模式。 然后，模型将生成符合该模式的响应，如下例所示：

    ChatRequest chatRequest = ChatRequest.builder()    .messages(UserMessage.from("Julien 喜欢蓝色、白色和红色"))    .responseFormat(ResponseFormat.builder()        .type(JSON)        .jsonSchema(JsonSchema.builder()            .name("Person")            .rootElement(JsonObjectSchema.builder()                .addStringProperty("name")                .addProperty("favouriteColors", JsonArraySchema.builder()                    .items(new JsonStringSchema())                    .build())                .required("name", "favouriteColors")                .build())            .build())        .build())    .build();String answer = chatLanguageModel.chat(chatRequest).aiMessage().text();

在此示例中，`answer` 将是：

    {  "name": "Julien",  "favouriteColors": ["蓝色", "白色", "红色"]}

然后，通常会使用 Jackson 等库将此 JSON 响应反序列化为 Java 对象。

创建 `AzureOpenAiStreamingChatModel` 以流式传输结果[​](#创建-azureopenaistreamingchatmodel-以流式传输结果 "创建-azureopenaistreamingchatmodel-以流式传输结果的直接链接")
----------------------------------------------------------------------------------------------------------------------------------------

此实现类似于上面的 `AzureOpenAiChatModel`，但它逐个令牌地流式传输响应。

### 纯 Java[​](#纯-java-2 "纯 Java的直接链接")

    StreamingChatLanguageModel model = AzureOpenAiStreamingChatModel.builder()        .endpoint(System.getenv("AZURE_OPENAI_URL"))        .apiKey(System.getenv("AZURE_OPENAI_KEY"))        .deploymentName("gpt-4o")        ...        .build();

### Spring Boot[​](#spring-boot-2 "Spring Boot的直接链接")

在 `application.properties` 中添加：

    langchain4j.azure-open-ai.streaming-chat-model.endpoint=${AZURE_OPENAI_URL}langchain4j.azure-open-ai.streaming-chat-model.service-version=...langchain4j.azure-open-ai.streaming-chat-model.api-key=${AZURE_OPENAI_KEY}langchain4j.azure-open-ai.streaming-chat-model.deployment-name=gpt-4olangchain4j.azure-open-ai.streaming-chat-model.max-tokens=...langchain4j.azure-open-ai.streaming-chat-model.temperature=...langchain4j.azure-open-ai.streaming-chat-model.top-p=...langchain4j.azure-open-ai.streaming-chat-model.logit-bias=...langchain4j.azure-open-ai.streaming-chat-model.user=...langchain4j.azure-open-ai.streaming-chat-model.stop=...langchain4j.azure-open-ai.streaming-chat-model.presence-penalty=...langchain4j.azure-open-ai.streaming-chat-model.frequency-penalty=...langchain4j.azure-open-ai.streaming-chat-model.seed=...langchain4j.azure-open-ai.streaming-chat-model.timeout=...langchain4j.azure-open-ai.streaming-chat-model.max-retries=...langchain4j.azure-open-ai.streaming-chat-model.log-requests-and-responses=...langchain4j.azure-open-ai.streaming-chat-model.user-agent-suffix=...langchain4j.azure-open-ai.streaming-chat-model.customHeaders=...

示例[​](#示例 "示例的直接链接")
--------------------

*   [Azure OpenAI 示例](https://github.com/langchain4j/langchain4j-examples/tree/main/azure-open-ai-examples/src/main/java)
*   [AzureOpenAiSecurityExamples](https://github.com/langchain4j/langchain4j-examples/blob/main/azure-open-ai-examples/src/main/java/AzureOpenAiSecurityExamples.java) 及其 [Azure CLI 部署脚本](https://github.com/langchain4j/langchain4j-examples/blob/main/azure-open-ai-examples/src/main/script/deploy-azure-openai-security.sh)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/azure-open-ai.md)

[

上一页

Anthropic

](/integrations/language-models/anthropic)[

下一页

ChatGLM

](/integrations/language-models/chatglm)

- [Azure OpenAI](#azure-openai)
  - [Azure OpenAI 文档​](#azure-openai-文档)
  - [Maven 依赖​](#maven-依赖)
    - [纯 Java​](#纯-java)
    - [Spring Boot​](#spring-boot)
  - [使用 API 密钥创建 `AzureOpenAiChatModel`​](#使用-api-密钥创建-azureopenaichatmodel)
    - [纯 Java​](#纯-java-1)
    - [Spring Boot​](#spring-boot-1)
  - [使用 Azure 凭证创建 `AzureOpenAiChatModel`​](#使用-azure-凭证创建-azureopenaichatmodel)
  - [工具​](#工具)
  - [结构化输出​](#结构化输出)
    - [使用高级 `Assistant` API​](#使用高级-assistant-api)
    - [使用低级 `ChatLanguageModel` API​](#使用低级-chatlanguagemodel-api)
  - [创建 `AzureOpenAiStreamingChatModel` 以流式传输结果​](#创建-azureopenaistreamingchatmodel-以流式传输结果)
    - [纯 Java​](#纯-java-2)
    - [Spring Boot​](#spring-boot-2)
  - [示例​](#示例)
- [VSCode Visible Files](#vscode-visible-files)
- [VSCode Open Tabs](#vscode-open-tabs)
- [Recently Modified Files](#recently-modified-files)
- [Current Time](#current-time)
- [Context Window Usage](#context-window-usage)
- [Current Mode](#current-mode)

Docs

*   [介绍](/intro)
*   [快速开始](/get-started)
*   [教程](/category/教程)
*   [集成](/category/集成)
*   [实用资料](/useful-materials)
*   [Examples](https://github.com/langchain4j/langchain4j-examples)
*   [Documentation chatbot (experimental)](https://chat.langchain4j.dev/)
*   [Javadoc](https://docs.langchain4j.dev/apidocs/index.html)

Community

*   [GitHub](https://github.com/langchain4j/langchain4j)
*   [Twitter](https://twitter.com/langchain4j)
*   [Discord](https://discord.com/invite/JzTFvyjG6R)
*   [Stack Overflow](https://stackoverflow.com/questions/tagged/langchain4j)

LangChain4j Documentation 2025. Built with Docusaurus.

</final_file_content>

Please note:
1. You do not need to re-write the file with these changes, as they have already been applied.
2. Proceed with the task using this updated file content as the new baseline.
3. If the user's edits have addressed part of the task or changed the requirements, adjust your approach accordingly.4. IMPORTANT: For any future changes to this file, use the final_file_content shown above as your reference. This content reflects the current state of the file, including both user edits and any auto-formatting (e.g., if you used single quotes but the formatter converted them to double quotes). Always base your SEARCH/REPLACE operations on this final version to ensure accuracy.

<environment_details>
# VSCode Visible Files
rags/azure-open-ai.md

# VSCode Open Tabs
rags/anthropic.md
rags/azure-open-ai.md

# Recently Modified Files
These files have been modified since you last accessed them (file was just edited so you may need to re-read it before editing):
rags/azure-open-ai.md

# Current Time
2025/6/13 下午6:15:08 (Asia/Shanghai, UTC+8:00)

# Context Window Usage
242,008 / 1,048.576K tokens used (23%)

# Current Mode
ACT MODE
</environment_details>
