AI Services | LangChain4j 中文文档  function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag("js",new Date),gtag("config","G-46M4FH02G5",{anonymize_ip:!0})  !function(){function t(t){document.documentElement.setAttribute("data-theme",t)}var e=function(){try{return new URLSearchParams(window.location.search).get("docusaurus-theme")}catch(t){}}()||function(){try{return window.localStorage.getItem("theme")}catch(t){}}();t(null!==e?e:"light")}(),function(){try{const n=new URLSearchParams(window.location.search).entries();for(var[t,e]of n)if(t.startsWith("docusaurus-data-")){var a=t.replace("docusaurus-data-","data-");document.documentElement.setAttribute(a,e)}}catch(t){}}()

[跳到主要内容](#__docusaurus_skipToContent_fallback)

[

![LangChain4j Logo](/img/logo.svg)![LangChain4j Logo](/img/logo.svg)

**LangChain4j**](/)[介绍](/intro)[快速开始](/get-started)[教程](/category/教程)[集成](/category/集成)[实用资料](/useful-materials)[Examples](https://github.com/langchain4j/langchain4j-examples)[Docu chatbot](https://chat.langchain4j.dev/)[Javadoc](https://docs.langchain4j.dev/apidocs/index.html)

[GitHub](https://github.com/langchain4j/langchain4j)[Twitter](https://twitter.com/langchain4j)[Discord](https://discord.com/invite/JzTFvyjG6R)

*   [介绍](/intro)
*   [开始使用](/get-started)
*   [教程](/category/教程)
    
    *   [聊天和语言模型](/tutorials/chat-and-language-models)
    *   [聊天记忆](/tutorials/chat-memory)
    *   [模型参数](/tutorials/model-parameters)
    *   [响应流式传输](/tutorials/response-streaming)
    *   [AI Services](/tutorials/ai-services)
    *   [Agents](/tutorials/agents)
    *   [工具（函数调用）](/tutorials/tools)
    *   [RAG (检索增强生成)](/tutorials/rag)
    *   [结构化输出](/tutorials/structured-outputs)
    *   [分类](/tutorials/classification)
    *   [嵌入（向量）存储](/tutorials/embedding-stores)
    *   [图像模型](/tutorials/image-models)
    *   [Quarkus 集成](/tutorials/quarkus-integration)
    *   [Spring Boot 集成](/tutorials/spring-boot-integration)
    *   [Kotlin 支持](/tutorials/kotlin)
    *   [日志记录](/tutorials/logging)
    *   [可观测性](/tutorials/observability)
    *   [可自定义的 HTTP 客户端](/tutorials/customizable-http-client)
    *   [测试与评估](/tutorials/testing-and-evaluation)
    *   [模型上下文协议 (MCP)](/tutorials/mcp)
*   [集成](/category/集成)
    
*   [实用资料](/category/实用资料)
    
*   [Latest Release Notes](/latest-release-notes)

*   [](/)
*   [教程](/category/教程)
*   AI Services

本页总览

AI Services
===========

到目前为止，我们一直在介绍底层组件，如 `ChatLanguageModel`、`ChatMessage`、`ChatMemory` 等。 在这个层面上工作非常灵活，给予您完全的自由，但也迫使您编写大量的样板代码。 由于 LLM 驱动的应用程序通常不仅需要单个组件，还需要多个组件协同工作 （例如，提示模板、聊天记忆、LLM、输出解析器、RAG 组件：嵌入模型和存储） 并且经常涉及多次交互，协调所有这些组件变得更加繁琐。

我们希望您专注于业务逻辑，而不是低级实现细节。 因此，LangChain4j 中目前有两个高级概念可以帮助您：AI 服务和链。

Chains (legacy)[​](#chains-legacy "Chains (legacy)的直接链接")
---------------------------------------------------------

链的概念源自 Python 的 LangChain（在引入 LCEL 之前）。 其思想是为每个常见用例（如聊天机器人、RAG 等）提供一个 `Chain`。 链组合多个低级组件并协调它们之间的交互。 它们的主要问题是，如果您需要自定义某些内容，它们过于僵化。 LangChain4j 只实现了两个链（`ConversationalChain` 和 `ConversationalRetrievalChain`）， 目前我们不打算添加更多。

AI Services[​](#ai-services-1 "AI Services的直接链接")
-------------------------------------------------

我们提出了另一种为 Java 量身定制的解决方案，称为 AI 服务。 其思想是将与 LLM 和其他组件交互的复杂性隐藏在简单的 API 后面。

这种方法与 Spring Data JPA 或 Retrofit 非常相似：您以声明方式定义具有所需 API 的接口， 然后 LangChain4j 提供实现该接口的对象（代理）。 您可以将 AI 服务视为应用程序服务层中的组件。 它提供 _AI_ 服务。因此得名。

AI 服务处理最常见的操作：

*   为 LLM 格式化输入
*   解析 LLM 的输出

它们还支持更高级的功能：

*   聊天记忆
*   工具
*   RAG

AI 服务可用于构建有状态的聊天机器人，促进来回交互， 也可用于自动化每次调用 LLM 都是独立的流程。

让我们看一下最简单的 AI 服务。之后，我们将探索更复杂的示例。

最简单的 AI 服务[​](#最简单的-ai-服务 "最简单的 AI 服务的直接链接")
--------------------------------------------

首先，我们定义一个带有单个方法 `chat` 的接口，该方法接受 `String` 作为输入并返回 `String`。

    interface Assistant {    String chat(String userMessage);}

然后，我们创建低级组件。这些组件将在我们的 AI 服务底层使用。 在这种情况下，我们只需要 `ChatLanguageModel`：

    ChatLanguageModel model = OpenAiChatModel.builder()    .apiKey(System.getenv("OPENAI_API_KEY"))    .modelName(GPT_4_O_MINI)    .build();

最后，我们可以使用 `AiServices` 类创建我们的 AI 服务实例：

    Assistant assistant = AiServices.create(Assistant.class, model);

备注

在 [Quarkus](https://docs.quarkiverse.io/quarkus-langchain4j/dev/ai-services.html) 和 [Spring Boot](/tutorials/spring-boot-integration#spring-boot-starter-for-declarative-ai-services) 应用程序中， 自动配置会处理创建 `Assistant` bean。 这意味着您不需要调用 `AiServices.create(...)`，只需在需要的地方注入/自动装配 `Assistant` 即可。

现在我们可以使用 `Assistant`：

    String answer = assistant.chat("Hello");System.out.println(answer); // Hello, how can I help you?

它是如何工作的？[​](#它是如何工作的 "它是如何工作的？的直接链接")
-------------------------------------

您将接口的 `Class` 与低级组件一起提供给 `AiServices`， 然后 `AiServices` 创建一个实现该接口的代理对象。 目前，它使用反射，但我们也在考虑其他替代方案。 这个代理对象处理所有输入和输出的转换。 在这种情况下，输入是单个 `String`，但我们使用的是 `ChatLanguageModel`，它接受 `ChatMessage` 作为输入。 因此，`AiService` 会自动将其转换为 `UserMessage` 并调用 `ChatLanguageModel`。 由于 `chat` 方法的输出类型是 `String`，在 `ChatLanguageModel` 返回 `AiMessage` 后， 它将在从 `chat` 方法返回之前转换为 `String`。

Quarkus 应用程序中的 AI 服务[​](#quarkus-应用程序中的-ai-服务 "Quarkus 应用程序中的 AI 服务的直接链接")
--------------------------------------------------------------------------

[LangChain4j Quarkus 扩展](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html) 极大地简化了在 Quarkus 应用程序中使用 AI 服务。

更多信息可以在[这里](https://docs.quarkiverse.io/quarkus-langchain4j/dev/ai-services.html)找到。

Spring Boot 应用程序中的 AI 服务[​](#spring-boot-应用程序中的-ai-服务 "Spring Boot 应用程序中的 AI 服务的直接链接")
--------------------------------------------------------------------------------------

[LangChain4j Spring Boot starter](/tutorials/spring-boot-integration/#spring-boot-starter-for-declarative-ai-services) 极大地简化了在 Spring Boot 应用程序中使用 AI 服务。

@SystemMessage[​](#systemmessage "@SystemMessage的直接链接")
-------------------------------------------------------

现在，让我们看一个更复杂的例子。 我们将强制 LLM 使用俚语回复 😉

这通常通过在 `SystemMessage` 中提供指令来实现。

    interface Friend {    @SystemMessage("You are a good friend of mine. Answer using slang.")    String chat(String userMessage);}Friend friend = AiServices.create(Friend.class, model);String answer = friend.chat("Hello"); // Hey! What's up?

在这个例子中，我们添加了 `@SystemMessage` 注解，其中包含我们想要使用的系统提示模板。 这将在幕后转换为 `SystemMessage` 并与 `UserMessage` 一起发送给 LLM。

`@SystemMessage` 也可以从资源加载提示模板： `@SystemMessage(fromResource = "my-prompt-template.txt")`

### 系统消息提供者[​](#系统消息提供者 "系统消息提供者的直接链接")

系统消息也可以通过系统消息提供者动态定义：

    Friend friend = AiServices.builder(Friend.class)    .chatLanguageModel(model)    .systemMessageProvider(chatMemoryId -> "You are a good friend of mine. Answer using slang.")    .build();

如您所见，您可以根据聊天记忆 ID（用户或对话）提供不同的系统消息。

@UserMessage[​](#usermessage "@UserMessage的直接链接")
-------------------------------------------------

现在，假设我们使用的模型不支持系统消息， 或者我们只想为此目的使用 `UserMessage`。

    interface Friend {    @UserMessage("You are a good friend of mine. Answer using slang. {{it}}")    String chat(String userMessage);}Friend friend = AiServices.create(Friend.class, model);String answer = friend.chat("Hello"); // Hey! What's shakin'?

我们将 `@SystemMessage` 注解替换为 `@UserMessage`， 并指定了一个包含变量 `it` 的提示模板，该变量指的是唯一的方法参数。

也可以用 `@V` 注解 `String userMessage`， 并为提示模板变量分配自定义名称：

    interface Friend {    @UserMessage("You are a good friend of mine. Answer using slang. {{message}}")    String chat(@V("message") String userMessage);}

备注

请注意，在使用 Quarkus 或 Spring Boot 的 LangChain4j 时，不需要使用 `@V`。 只有在 Java 编译期间_未_启用 `-parameters` 选项时，才需要此注解。

`@UserMessage` 也可以从资源加载提示模板： `@UserMessage(fromResource = "my-prompt-template.txt")`

有效的 AI 服务方法示例[​](#有效的-ai-服务方法示例 "有效的 AI 服务方法示例的直接链接")
-----------------------------------------------------

以下是一些有效的 AI 服务方法示例。

`UserMessage`

    String chat(String userMessage);String chat(@UserMessage String userMessage);String chat(@UserMessage String userMessage, @V("country") String country); // userMessage 包含 "{{country}}" 模板变量@UserMessage("What is the capital of Germany?")String chat();@UserMessage("What is the capital of {{it}}?")String chat(String country);@UserMessage("What is the capital of {{country}}?")String chat(@V("country") String country);@UserMessage("What is the {{something}} of {{country}}?")String chat(@V("something") String something, @V("country") String country);@UserMessage("What is the capital of {{country}}?")String chat(String country); // 这仅在 Quarkus 和 Spring Boot 应用程序中有效

`SystemMessage` 和 `UserMessage`

    @SystemMessage("Given a name of a country, answer with a name of it's capital")String chat(String userMessage);@SystemMessage("Given a name of a country, answer with a name of it's capital")String chat(@UserMessage String userMessage);@SystemMessage("Given a name of a country, {{answerInstructions}}")String chat(@V("answerInstructions") String answerInstructions, @UserMessage String userMessage);@SystemMessage("Given a name of a country, answer with a name of it's capital")String chat(@UserMessage String userMessage, @V("country") String country); // userMessage 包含 "{{country}}" 模板变量@SystemMessage("Given a name of a country, {{answerInstructions}}")String chat(@V("answerInstructions") String answerInstructions, @UserMessage String userMessage, @V("country") String country); // userMessage 包含 "{{country}}" 模板变量@SystemMessage("Given a name of a country, answer with a name of it's capital")@UserMessage("Germany")String chat();@SystemMessage("Given a name of a country, {{answerInstructions}}")@UserMessage("Germany")String chat(@V("answerInstructions") String answerInstructions);@SystemMessage("Given a name of a country, answer with a name of it's capital")@UserMessage("{{it}}")String chat(String country);@SystemMessage("Given a name of a country, answer with a name of it's capital")@UserMessage("{{country}}")String chat(@V("country") String country);@SystemMessage("Given a name of a country, {{answerInstructions}}")@UserMessage("{{country}}")String chat(@V("answerInstructions") String answerInstructions, @V("country") String country);

多模态[​](#多模态 "多模态的直接链接")
-----------------------

AI 服务目前不支持多模态， 请使用[低级 API](/tutorials/chat-and-language-models#multimodality)。

返回类型[​](#返回类型 "返回类型的直接链接")
--------------------------

AI 服务方法可以返回以下类型之一：

*   `String` - 在这种情况下，LLM 生成的输出将不经任何处理/解析直接返回
*   [结构化输出](/tutorials/structured-outputs#supported-types)支持的任何类型 - 在这种情况下， AI 服务将在返回之前将 LLM 生成的输出解析为所需类型

任何类型都可以额外包装在 `Result<T>` 中，以获取有关 AI 服务调用的额外元数据：

*   `TokenUsage` - AI 服务调用期间使用的令牌总数。如果 AI 服务对 LLM 进行了多次调用 （例如，因为执行了工具），它将汇总所有调用的令牌使用情况。
*   Sources - 在 [RAG](/tutorials/ai-services#rag) 检索期间检索到的 `Content`
*   已执行的[工具](/tutorials/ai-services#tools-function-calling)
*   `FinishReason`

示例：

    interface Assistant {        @UserMessage("Generate an outline for the article on the following topic: {{it}}")    Result<List<String>> generateOutlineFor(String topic);}Result<List<String>> result = assistant.generateOutlineFor("Java");List<String> outline = result.content();TokenUsage tokenUsage = result.tokenUsage();List<Content> sources = result.sources();List<ToolExecution> toolExecutions = result.toolExecutions();FinishReason finishReason = result.finishReason();

结构化输出[​](#结构化输出 "结构化输出的直接链接")
-----------------------------

如果您想从 LLM 接收结构化输出（例如，复杂的 Java 对象，而不是 `String` 中的非结构化文本）， 您可以将 AI 服务方法的返回类型从 `String` 更改为其他类型。

备注

有关结构化输出的更多信息可以在[这里](/tutorials/structured-outputs)找到。

几个例子：

### 返回类型为 `boolean`[​](#返回类型为-boolean "返回类型为-boolean的直接链接")

    interface SentimentAnalyzer {    @UserMessage("Does {{it}} has a positive sentiment?")    boolean isPositive(String text);}SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);boolean positive = sentimentAnalyzer.isPositive("It's wonderful!");// true

### 返回类型为 `Enum`[​](#返回类型为-enum "返回类型为-enum的直接链接")

    enum Priority {    CRITICAL, HIGH, LOW}interface PriorityAnalyzer {        @UserMessage("Analyze the priority of the following issue: {{it}}")    Priority analyzePriority(String issueDescription);}PriorityAnalyzer priorityAnalyzer = AiServices.create(PriorityAnalyzer.class, model);Priority priority = priorityAnalyzer.analyzePriority("The main payment gateway is down, and customers cannot process transactions.");// CRITICAL

### 返回类型为 POJO[​](#返回类型为-pojo "返回类型为 POJO的直接链接")

    class Person {    @Description("first name of a person") // 您可以添加可选描述，帮助 LLM 更好地理解    String firstName;    String lastName;    LocalDate birthDate;    Address address;}@Description("an address") // 您可以添加可选描述，帮助 LLM 更好地理解class Address {    String street;    Integer streetNumber;    String city;}interface PersonExtractor {    @UserMessage("Extract information about a person from {{it}}")    Person extractPersonFrom(String text);}PersonExtractor personExtractor = AiServices.create(PersonExtractor.class, model);String text = """            In 1968, amidst the fading echoes of Independence Day,            a child named John arrived under the calm evening sky.            This newborn, bearing the surname Doe, marked the start of a new journey.            He was welcomed into the world at 345 Whispering Pines Avenue            a quaint street nestled in the heart of Springfield            an abode that echoed with the gentle hum of suburban dreams and aspirations.            """;Person person = personExtractor.extractPersonFrom(text);System.out.println(person); // Person { firstName = "John", lastName = "Doe", birthDate = 1968-07-04, address = Address { ... } }

JSON 模式[​](#json-模式 "JSON 模式的直接链接")
-----------------------------------

在提取自定义 POJO（实际上是 JSON，然后解析为 POJO）时， 建议在模型配置中启用"JSON 模式"。 这样，LLM 将被强制以有效的 JSON 格式响应。

备注

请注意，JSON 模式和工具/函数调用是类似的功能， 但有不同的 API，用于不同的目的。

当您\_始终\_需要 LLM 以结构化格式（有效的 JSON）响应时，JSON 模式很有用。 此外，通常不需要状态/记忆，因此与 LLM 的每次交互都独立于其他交互。 例如，您可能想从文本中提取信息，如文本中提到的人员列表， 或将自由格式的产品评论转换为结构化形式，包含 `String productName`、`Sentiment sentiment`、`List<String> claimedProblems` 等字段。

另一方面，当 LLM 应该能够执行某些操作时，工具/函数很有用 （例如，查询数据库、搜索网络、取消用户预订等）。 在这种情况下，向 LLM 提供带有预期 JSON 模式的工具列表，它会自主决定 是否调用其中任何一个来满足用户请求。

早期，函数调用经常用于结构化数据提取， 但现在我们有了 JSON 模式功能，它更适合这个目的。

以下是如何启用 JSON 模式：

*   对于 OpenAI：
    
    *   对于支持[结构化输出](https://openai.com/index/introducing-structured-outputs-in-the-api/)的较新模型（例如，`gpt-4o-mini`、`gpt-4o-2024-08-06`）：
        
            OpenAiChatModel.builder()    ...    .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)    .strictJsonSchema(true)    .build();
        
        查看[此处](/integrations/language-models/open-ai#structured-outputs)了解更多详情。
    *   对于较旧的模型（例如 gpt-3.5-turbo、gpt-4）：
        
            OpenAiChatModel.builder()    ...    .responseFormat("json_object")    .build();
        
*   对于 Azure OpenAI：
    

    AzureOpenAiChatModel.builder()    ...    .responseFormat(new ChatCompletionsJsonResponseFormat())    .build();

*   对于 Vertex AI Gemini：

    VertexAiGeminiChatModel.builder()    ...    .responseMimeType("application/json")    .build();

或通过从 Java 类指定显式模式：

    VertexAiGeminiChatModel.builder()    ...    .responseSchema(SchemaHelper.fromClass(Person.class))    .build();

从 JSON 模式：

    VertexAiGeminiChatModel.builder()    ...    .responseSchema(Schema.builder()...build())    .build();

*   对于 Google AI Gemini：

    GoogleAiGeminiChatModel.builder()    ...    .responseFormat(ResponseFormat.JSON)    .build();

或通过从 Java 类指定显式模式：

    GoogleAiGeminiChatModel.builder()    ...    .responseFormat(ResponseFormat.builder()        .type(JSON)        .jsonSchema(JsonSchemas.jsonSchemaFrom(Person.class).get())        .build())    .build();

从 JSON 模式：

    GoogleAiGeminiChatModel.builder()    ...    .responseFormat(ResponseFormat.builder()        .type(JSON)        .jsonSchema(JsonSchema.builder()...build())        .build())    .build();

*   对于 Mistral AI：

    MistralAiChatModel.builder()    ...    .responseFormat(MistralAiResponseFormatType.JSON_OBJECT)
    .build();

*   对于 Ollama：

    OllamaChatModel.builder()    ...    .responseFormat(JSON)    .build();

*   对于其他模型提供商：如果底层模型提供商不支持 JSON 模式， 提示工程是您最好的选择。此外，尝试降低 `temperature` 以获得更确定性的结果。

[更多示例](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/OtherServiceExamples.java)

流式处理[​](#流式处理 "流式处理的直接链接")
--------------------------

AI 服务可以使用 `TokenStream` 返回类型[逐个令牌流式处理响应](/tutorials/response-streaming)：

    interface Assistant {    TokenStream chat(String message);}StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()    .apiKey(System.getenv("OPENAI_API_KEY"))    .modelName(GPT_4_O_MINI)    .build();Assistant assistant = AiServices.create(Assistant.class, model);TokenStream tokenStream = assistant.chat("Tell me a joke");tokenStream.onPartialResponse((String partialResponse) -> System.out.println(partialResponse))    .onRetrieved((List<Content> contents) -> System.out.println(contents))    .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))    .onCompleteResponse((ChatResponse response) -> System.out.println(response))    .onError((Throwable error) -> error.printStackTrace())    .start();

### Flux[​](#flux "Flux的直接链接")

您也可以使用 `Flux<String>` 代替 `TokenStream`。 为此，请导入 `langchain4j-reactor` 模块：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-reactor</artifactId>    <version>1.0.0-beta3</version></dependency>

    interface Assistant {  Flux<String> chat(String message);}

[流式处理示例](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithStreamingExample.java)

聊天记忆[​](#聊天记忆 "聊天记忆的直接链接")
--------------------------

AI 服务可以使用[聊天记忆](/tutorials/chat-memory)来"记住"之前的交互：

    Assistant assistant = AiServices.builder(Assistant.class)    .chatLanguageModel(model)    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))    .build();

在这种情况下，所有 AI 服务调用都将使用相同的 `ChatMemory` 实例。 然而，如果您有多个用户，这种方法将不起作用， 因为每个用户都需要自己的 `ChatMemory` 实例来维护各自的对话。

解决这个问题的方法是使用 `ChatMemoryProvider`：

    interface Assistant  {    String chat(@MemoryId int memoryId, @UserMessage String message);}Assistant assistant = AiServices.builder(Assistant.class)    .chatLanguageModel(model)    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))    .build();String answerToKlaus = assistant.chat(1, "Hello, my name is Klaus");String answerToFrancine = assistant.chat(2, "Hello, my name is Francine");

在这种情况下，`ChatMemoryProvider` 将提供两个不同的 `ChatMemory` 实例，每个记忆 ID 一个。

以这种方式使用 `ChatMemory` 时，重要的是要清除不再需要的对话记忆，以避免内存泄漏。要使 AI 服务内部使用的聊天记忆可访问，只需让定义它的接口扩展 `ChatMemoryAccess` 接口即可。

    interface Assistant extends ChatMemoryAccess {    String chat(@MemoryId int memoryId, @UserMessage String message);}

这使得可以访问单个对话的 `ChatMemory` 实例，并在对话终止时删除它。

    String answerToKlaus = assistant.chat(1, "Hello, my name is Klaus");String answerToFrancine = assistant.chat(2, "Hello, my name is Francine");List<ChatMessage> messagesWithKlaus = assistant.getChatMemory(1).messages();boolean chatMemoryWithFrancineEvicted = assistant.evictChatMemory(2);

备注

请注意，如果 AI 服务方法没有用 `@MemoryId` 注解的参数， `ChatMemoryProvider` 中 `memoryId` 的值将默认为字符串 `"default"`。

备注

请注意，不应对同一个 `@MemoryId` 并发调用 AI 服务， 因为这可能导致 `ChatMemory` 损坏。 目前，AI 服务没有实现任何机制来防止对同一 `@MemoryId` 的并发调用。

*   [单个 ChatMemory 示例](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithMemoryExample.java)
*   [每个用户 ChatMemory 示例](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithMemoryForEachUserExample.java)
*   [单个持久化 ChatMemory 示例](https://github.langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithPersistentMemoryExample.java)
*   [每个用户持久化 ChatMemory 示例](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithPersistentMemoryForEachUserExample.java)

工具（函数调用）[​](#工具函数调用 "工具（函数调用）的直接链接")
------------------------------------

AI 服务可以配置 LLM 可以使用的工具：

    class Tools {        @Tool    int add(int a, int b) {        return a + b;    }    @Tool    int multiply(int a, int b) {        return a * b;    }}Assistant assistant = AiServices.builder(Assistant.class)    .chatLanguageModel(model)    .tools(new Tools())    .build();String answer = assistant.chat("What is 1+2 and 3*4?");

在这种情况下，LLM 将在提供最终答案之前请求执行 `add(1, 2)` 和 `multiply(3, 4)` 方法。 LangChain4j 将自动执行这些方法。

有关工具的更多详细信息可以在[这里](/tutorials/tools#high-level-tool-api)找到。

RAG[​](#rag "RAG的直接链接")
-----------------------

AI 服务可以配置 `ContentRetriever` 以启用[简单 RAG](/tutorials/rag#naive-rag)：

    EmbeddingStore embeddingStore  = ...EmbeddingModel embeddingModel = ...ContentRetriever contentRetriever = new EmbeddingStoreContentRetriever(embeddingStore, embeddingModel);Assistant assistant = AiServices.builder(Assistant.class)    .chatLanguageModel(model)    .contentRetriever(contentRetriever)    .build();

配置 `RetrievalAugmentor` 提供更大的灵活性， 启用[高级 RAG](/tutorials/rag#advanced-rag)功能，如查询转换、重新排序等：

    RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()        .queryTransformer(...)        .queryRouter(...)        .contentAggregator(...)        .contentInjector(...)        .executor(...)        .build();Assistant assistant = AiServices.builder(Assistant.class)    .chatLanguageModel(model)    .retrievalAugmentor(retrievalAugmentor)    .build();

有关 RAG 的更多详细信息可以在[这里](/tutorials/rag)找到。

更多 RAG 示例可以在[这里](https://github.com/langchain4j/langchain4j-examples/tree/main/rag-examples/src/main/java)找到。

自动审核[​](#自动审核 "自动审核的直接链接")
--------------------------

[示例](https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithAutoModerationExample.java)

链接多个 AI 服务[​](#链接多个-ai-服务 "链接多个 AI 服务的直接链接")
--------------------------------------------

随着您的 LLM 驱动应用程序逻辑变得越来越复杂， 将其分解成更小的部分变得越来越重要，这也是软件开发中的常见做法。

例如，在系统提示中塞入大量指令以应对所有可能的场景， 容易出错且效率低下。如果指令太多，LLM 可能会忽略一些指令。 此外，指令呈现的顺序也很重要，这使得整个过程更具挑战性。

这一原则也适用于工具、RAG 以及模型参数，如 `temperature`、`maxTokens` 等。

您的聊天机器人可能不需要始终了解您拥有的每一个工具。 例如，当用户只是向聊天机器人打招呼或说再见时， 让 LLM 访问数十或数百个工具既昂贵又有时甚至危险 （每个包含在 LLM 调用中的工具都会消耗大量的 token） 并可能导致意外结果（LLM 可能会产生幻觉或被操纵以使用非预期输入调用工具）。

关于 RAG：同样，有时需要向 LLM 提供一些上下文， 但并非总是如此，因为这会增加额外成本（更多上下文 = 更多 token） 并增加响应时间（更多上下文 = 更高延迟）。

关于模型参数：在某些情况下，您可能需要 LLM 具有高度确定性， 因此您会设置较低的 `temperature`。在其他情况下，您可能会选择较高的 `temperature`，等等。

重点是，更小、更具体的组件更容易且更便宜地开发、测试、维护和理解。

另一个需要考虑的方面涉及两个极端：

*   您是否希望您的应用程序高度确定性， 应用程序控制流程，而 LLM 只是组件之一？
*   或者您是否希望 LLM 拥有完全自主权并驱动您的应用程序？

或者根据情况混合使用两者？ 当您将应用程序分解为更小、更易管理的部分时，所有这些选项都是可能的。

AI 服务可以作为常规（确定性）软件组件使用并与之结合：

*   您可以一个接一个地调用 AI 服务（即链接）。
*   您可以使用确定性和 LLM 驱动的 `if`/`else` 语句（AI 服务可以返回 `boolean`）。
*   您可以使用确定性和 LLM 驱动的 `switch` 语句（AI 服务可以返回 `enum`）。
*   您可以使用确定性和 LLM 驱动的 `for`/`while` 循环（AI 服务可以返回 `int` 和其他数值类型）。
*   您可以在单元测试中模拟 AI 服务（因为它是一个接口）。
*   您可以单独对每个 AI 服务进行集成测试。
*   您可以单独评估并找到每个 AI 服务的最佳参数。
*   等等

让我们考虑一个简单的例子。 我想为我的公司构建一个聊天机器人。 如果用户向聊天机器人打招呼， 我希望它用预定义的问候语回应，而不依赖 LLM 生成问候语。 如果用户提出问题，我希望 LLM 使用公司的内部知识库生成回应（即 RAG）。

以下是如何将此任务分解为 2 个独立的 AI 服务：

    interface GreetingExpert {    @UserMessage("Is the following text a greeting? Text: {{it}}")    boolean isGreeting(String text);}interface ChatBot {    @SystemMessage("You are a polite chatbot of a company called Miles of Smiles.")    String reply(String userMessage);}class MilesOfSmiles {    private final GreetingExpert greetingExpert;    private final ChatBot chatBot;        ...        public String handle(String userMessage) {        if (greetingExpert.isGreeting(userMessage)) {            return "Greetings from Miles of Smiles! How can I make your day better?";        } else {            return chatBot.reply(userMessage);        }    }}GreetingExpert greetingExpert = AiServices.create(GreetingExpert.class, llama2);ChatBot chatBot = AiServices.builder(ChatBot.class)    .chatLanguageModel(gpt4)    .contentRetriever(milesOfSmilesContentRetriever)    .build();MilesOfSmiles milesOfSmiles = new MilesOfSmiles(greetingExpert, chatBot);String greeting = milesOfSmiles.handle("Hello");System.out.println(greeting); // Greetings from Miles of Smiles! How can I make your day better?String answer = milesOfSmiles.handle("Which services do you provide?");System.out.println(answer); // At Miles of Smiles, we provide a wide range of services ...

注意我们如何使用更便宜的 Llama2 来完成识别文本是否为问候语这一简单任务， 而使用更昂贵的 GPT-4 和内容检索器（RAG）来完成更复杂的任务。

这是一个非常简单且有些幼稚的例子，但希望它能说明这个想法。

现在，我可以模拟 `GreetingExpert` 和 `ChatBot`，并单独测试 `MilesOfSmiles`。 我还可以分别对 `GreetingExpert` 和 `ChatBot` 进行集成测试。 我可以分别评估它们，并为每个子任务找到最优参数， 或者从长远来看，甚至可以为每个特定子任务微调一个小型专用模型。

测试[​](#测试 "测试的直接链接")
--------------------

*   [客户支持代理集成测试示例](https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/test/java/dev/langchain4j/example/CustomerSupportAgentIT.java)

相关教程[​](#相关教程 "相关教程的直接链接")
--------------------------

*   [Siva](https://www.sivalabs.in/) 的 [LangChain4j AiServices 教程](https://www.sivalabs.in/langchain4j-ai-services-tutorial/)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/ai-services.mdx)

[

上一页

响应流式传输

](/tutorials/response-streaming)[

下一页

Agents

](/tutorials/agents)

- [AI Services](#ai-services)
  - [Chains (legacy)​](#chains-legacy)
  - [AI Services​](#ai-services-1)
  - [最简单的 AI 服务​](#最简单的-ai-服务)
  - [它是如何工作的？​](#它是如何工作的)
  - [Quarkus 应用程序中的 AI 服务​](#quarkus-应用程序中的-ai-服务)
  - [Spring Boot 应用程序中的 AI 服务​](#spring-boot-应用程序中的-ai-服务)
  - [@SystemMessage​](#systemmessage)
    - [系统消息提供者​](#系统消息提供者)
  - [@UserMessage​](#usermessage)
  - [有效的 AI 服务方法示例​](#有效的-ai-服务方法示例)
  - [多模态​](#多模态)
  - [返回类型​](#返回类型)
  - [结构化输出​](#结构化输出)
    - [返回类型为 `boolean`​](#返回类型为-boolean)
    - [返回类型为 `Enum`​](#返回类型为-enum)
    - [返回类型为 POJO​](#返回类型为-pojo)
  - [JSON 模式​](#json-模式)
  - [流式处理​](#流式处理)
    - [Flux​](#flux)
  - [聊天记忆​](#聊天记忆)
  - [工具（函数调用）​](#工具函数调用)
  - [RAG​](#rag)
  - [自动审核​](#自动审核)
  - [链接多个 AI 服务​](#链接多个-ai-服务)
  - [测试​](#测试)
  - [相关教程​](#相关教程)
- [VSCode Visible Files](#vscode-visible-files)
- [VSCode Open Tabs](#vscode-open-tabs)
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

IMPORTANT: For any future changes to this file, use the final_file_content shown above as your reference. This content reflects the current state of the file, including any auto-formatting (e.g., if you used single quotes but the formatter converted them to double quotes). Always base your SEARCH/REPLACE operations on this final version to ensure accuracy.

<environment_details>
# VSCode Visible Files
rags/ai-services.md

# VSCode Open Tabs
rags/intro.md
rags/get-started.md
rags/chat-and-language-models.md
rags/chat-memory.md
rags/model-parameters.md
rags/response-streaming.md
rags/ai-services.md

# Current Time
2025/6/13 下午5:57:30 (Asia/Shanghai, UTC+8:00)

# Context Window Usage
152,247 / 1,048.576K tokens used (15%)

# Current Mode
ACT MODE
</environment_details>
