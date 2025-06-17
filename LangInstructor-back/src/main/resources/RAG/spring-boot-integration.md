
Spring Boot 集成
==============

LangChain4j 为以下内容提供了 [Spring Boot 启动器](https://github.com/langchain4j/langchain4j-spring)：

*   流行的集成
*   声明式 [AI 服务](/tutorials/ai-services)

Spring Boot 启动器[​](#spring-boot-启动器 "Spring Boot 启动器的直接链接")
-----------------------------------------------------------

Spring Boot 启动器通过属性配置帮助创建和配置 [语言模型](/category/language-models)、 [嵌入模型](/category/embedding-models)、 [嵌入存储](/category/embedding-stores) 和其他 LangChain4j 核心组件。

要使用 [Spring Boot 启动器](https://github.com/langchain4j/langchain4j-spring) 之一， 请导入相应的依赖项。

Spring Boot 启动器依赖项的命名约定是：`langchain4j-{integration-name}-spring-boot-starter`。

例如，对于 OpenAI (`langchain4j-open-ai`)，依赖项名称为 `langchain4j-open-ai-spring-boot-starter`：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>    <version>1.0.0-beta3</version></dependency>

然后，您可以在 `application.properties` 文件中配置模型参数，如下所示：

    langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.chat-model.model-name=gpt-4olangchain4j.open-ai.chat-model.log-requests=truelangchain4j.open-ai.chat-model.log-responses=true...

在这种情况下，将自动创建 `OpenAiChatModel`（`ChatLanguageModel` 的实现）的实例， 您可以在需要的地方自动装配它：

    @RestControllerpublic class ChatController {    ChatLanguageModel chatLanguageModel;    public ChatController(ChatLanguageModel chatLanguageModel) {        this.chatLanguageModel = chatLanguageModel;    }    @GetMapping("/chat")    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {        return chatLanguageModel.chat(message);    }}

如果您需要 `StreamingChatLanguageModel` 的实例， 请使用 `streaming-chat-model` 而不是 `chat-model` 属性：

    langchain4j.open-ai.streaming-chat-model.api-key=${OPENAI_API_KEY}...

声明式 AI 服务的 Spring Boot 启动器[​](#声明式-ai-服务的-spring-boot-启动器 "声明式 AI 服务的 Spring Boot 启动器的直接链接")
--------------------------------------------------------------------------------------------

LangChain4j 提供了一个 Spring Boot 启动器，用于自动配置 [AI 服务](/tutorials/ai-services)、[RAG](/tutorials/rag)、[工具](/tutorials/tools) 等。

假设您已经导入了其中一个集成启动器（见上文）， 导入 `langchain4j-spring-boot-starter`：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-spring-boot-starter</artifactId>    <version>1.0.0-beta3</version></dependency>

现在您可以定义 AI 服务接口并使用 `@AiService` 注解它：

    @AiServiceinterface Assistant {    @SystemMessage("You are a polite assistant")    String chat(String userMessage);}

将其视为标准的 Spring Boot `@Service`，但具有 AI 功能。

当应用程序启动时，LangChain4j 启动器将扫描类路径 并找到所有带有 `@AiService` 注解的接口。 对于找到的每个 AI 服务，它将使用应用程序上下文中可用的所有 LangChain4j 组件创建此接口的实现，并将其注册为 bean， 这样您就可以在需要的地方自动装配它：

    @RestControllerclass AssistantController {    @Autowired    Assistant assistant;    @GetMapping("/chat")    public String chat(String message) {        return assistant.chat(message);    }}

### 自动组件装配[​](#自动组件装配 "自动组件装配的直接链接")

如果以下组件在应用程序上下文中可用，它们将自动装配到 AI 服务中：

*   `ChatLanguageModel`
*   `StreamingChatLanguageModel`
*   `ChatMemory`
*   `ChatMemoryProvider`
*   `ContentRetriever`
*   `RetrievalAugmentor`
*   任何 `@Component` 或 `@Service` 类中带有 `@Tool` 注解的所有方法 示例：

    @Componentpublic class BookingTools {    private final BookingService bookingService;    public BookingTools(BookingService bookingService) {        this.bookingService = bookingService;    }    @Tool    public Booking getBookingDetails(String bookingNumber, String customerName, String customerSurname) {        return bookingService.getBookingDetails(bookingNumber, customerName, customerSurname);    }    @Tool    public void cancelBooking(String bookingNumber, String customerName, String customerSurname) {        bookingService.cancelBooking(bookingNumber, customerName, customerSurname);    }}

备注

如果应用程序上下文中存在多个相同类型的组件，应用程序将无法启动。 在这种情况下，请使用显式装配模式（下面解释）。

### 显式组件装配[​](#显式组件装配 "显式组件装配的直接链接")

如果您有多个 AI 服务，并希望将不同的 LangChain4j 组件装配到每个服务中， 您可以使用显式装配模式（`@AiService(wiringMode = EXPLICIT)`）指定要使用的组件。

假设我们配置了两个 `ChatLanguageModel`：

    # OpenAIlangchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}langchain4j.open-ai.chat-model.model-name=gpt-4o-mini# Ollamalangchain4j.ollama.chat-model.base-url=http://localhost:11434langchain4j.ollama.chat-model.model-name=llama3.1

    @AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel")interface OpenAiAssistant {    @SystemMessage("You are a polite assistant")    String chat(String userMessage);}@AiService(wiringMode = EXPLICIT, chatModel = "ollamaChatModel")interface OllamaAssistant {    @SystemMessage("You are a polite assistant")    String chat(String userMessage);}

备注

在这种情况下，您必须显式指定**所有**组件。

更多详细信息可以在[这里](https://github.com/langchain4j/langchain4j-spring/blob/main/langchain4j-spring-boot-starter/src/main/java/dev/langchain4j/service/spring/AiService.java)找到。

### 监听 AI 服务注册事件[​](#监听-ai-服务注册事件 "监听 AI 服务注册事件的直接链接")

在以声明方式完成 AI 服务的开发后，您可以通过实现 `ApplicationListener<AiServiceRegisteredEvent>` 接口来监听 `AiServiceRegisteredEvent`。 当 AI 服务在 Spring 上下文中注册时，会触发此事件， 使您能够在运行时获取有关所有已注册的 AI 服务及其工具的信息。 以下是一个示例：

    @Componentclass AiServiceRegisteredEventListener implements ApplicationListener<AiServiceRegisteredEvent> {    @Override    public void onApplicationEvent(AiServiceRegisteredEvent event) {        Class<?> aiServiceClass = event.aiServiceClass();        List<ToolSpecification> toolSpecifications = event.toolSpecifications();        for (int i = 0; i < toolSpecifications.size(); i++) {            System.out.printf("[%s]: [Tool-%s]: %s%n", aiServiceClass.getSimpleName(), i + 1, toolSpecifications.get(i));        }    }}

Flux[​](#flux "Flux的直接链接")
--------------------------

在流式传输时，您可以使用 `Flux<String>` 作为 AI 服务的返回类型：

    @AiServiceinterface Assistant {    @SystemMessage("You are a polite assistant")    Flux<String> chat(String userMessage);}

为此，请导入 `langchain4j-reactor` 模块。 更多详细信息请参见[此处](/tutorials/ai-services#flux)。

可观察性[​](#可观察性 "可观察性的直接链接")
--------------------------

要为 `ChatLanguageModel` 或 `StreamingChatLanguageModel` bean 启用可观察性， 您需要声明一个或多个 `ChatModelListener` bean：

    @Configurationclass MyConfiguration {        @Bean    ChatModelListener chatModelListener() {        return new ChatModelListener() {            private static final Logger log = LoggerFactory.getLogger(ChatModelListener.class);            @Override            public void onRequest(ChatModelRequestContext requestContext) {                log.info("onRequest(): {}", requestContext.chatRequest());            }            @Override            public void onResponse(ChatModelResponseContext responseContext) {                log.info("onResponse(): {}", responseContext.chatResponse());            }            @Override            public void onError(ChatModelErrorContext errorContext) {                log.info("onError(): {}", errorContext.error().getMessage());            }        };    }}

应用程序上下文中的每个 `ChatModelListener` bean 都将自动 注入到由我们的 Spring Boot 启动器之一创建的所有 `ChatLanguageModel` 和 `StreamingChatLanguageModel` bean 中。

测试[​](#测试 "测试的直接链接")
--------------------

*   [客户支持代理的集成测试示例](https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/test/java/dev/langchain4j/example/CustomerSupportAgentIT.java)

支持的版本[​](#支持的版本 "支持的版本的直接链接")
-----------------------------

LangChain4j Spring Boot 集成需要 Java 17 和 Spring Boot 3.2。

示例[​](#示例 "示例的直接链接")
--------------------

*   \[使用 [ChatLanguageModel API](/tutorials/chat-and-language-models) 的低级 Spring Boot 示例\]([https://github.com/langchain4j/langchain4j-examples/blob/main/spring-boot-example/src/main/java/dev/langchain4j/example/lowlevel/ChatLanguageModelController.java](https://github.com/langchain4j/langchain4j-examples/blob/main/spring-boot-example/src/main/java/dev/langchain4j/example/lowlevel/ChatLanguageModelController.java))
*   \[使用 [AI 服务](/tutorials/ai-services) 的高级 Spring Boot 示例\]([https://github.com/langchain4j/langchain4j-examples/blob/main/spring-boot-example/src/main/java/dev/langchain4j/example/aiservice/AssistantController.java](https://github.com/langchain4j/langchain4j-examples/blob/main/spring-boot-example/src/main/java/dev/langchain4j/example/aiservice/AssistantController.java))
*   [使用 Spring Boot 的客户支持代理示例](https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/main/java/dev/langchain4j/example/CustomerSupportAgentApplication.java)

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/spring-boot-integration.md)
