
Google AI Gemini
================

[https://ai.google.dev/gemini-api/docs](https://ai.google.dev/gemini-api/docs)

Maven 依赖[​](#maven-依赖 "Maven 依赖的直接链接")
--------------------------------------

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-google-ai-gemini</artifactId>    <version>1.0.0-beta3</version></dependency>

API 密钥[​](#api-密钥 "API 密钥的直接链接")
--------------------------------

在这里免费获取 API 密钥：[https://ai.google.dev/gemini-api/docs/api-key](https://ai.google.dev/gemini-api/docs/api-key) 。

可用模型[​](#可用模型 "可用模型的直接链接")
----------------------------

查看文档中的[可用模型](https://ai.google.dev/gemini-api/docs/models/gemini)列表。

*   `gemini-2.0-flash`
*   `gemini-1.5-flash`
*   `gemini-1.5-pro`
*   `gemini-1.0-pro`

GoogleAiGeminiChatModel[​](#googleaigeminichatmodel "GoogleAiGeminiChatModel的直接链接")
-----------------------------------------------------------------------------------

常用的 `chat(...)` 方法可用：

    ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()    .apiKey(System.getenv("GEMINI_AI_KEY"))    .modelName("gemini-1.5-flash")    ...    .build();String response = gemini.chat("你好 Gemini！");

以及 `ChatResponse chat(ChatRequest req)` 方法：

    ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()    .apiKey(System.getenv("GEMINI_AI_KEY"))    .modelName("gemini-1.5-flash")    .build();ChatResponse chatResponse = gemini.chat(ChatRequest.builder()    .messages(UserMessage.from(        "单词'strawberry'中有多少个字母'R'？"))    .build());String response = chatResponse.aiMessage().text();

### 配置[​](#配置 "配置的直接链接")

    ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()    .apiKey(System.getenv("GEMINI_AI_KEY"))    .modelName("gemini-1.5-flash")    .temperature(1.0)    .topP(0.95)    .topK(64)    .maxOutputTokens(8192)    .timeout(Duration.ofSeconds(60))    .candidateCount(1)    .responseFormat(ResponseFormat.JSON) // 或 .responseFormat(ResponseFormat.builder()...build())     .stopSequences(List.of(...))    .toolConfig(GeminiFunctionCallingConfig.builder()...build()) // 或下面的方式    .toolConfig(GeminiMode.ANY, List.of("fnOne", "fnTwo"))    .allowCodeExecution(true)    .includeCodeExecution(output)    .logRequestsAndResponses(true)    .safetySettings(List<GeminiSafetySetting> or Map<GeminiHarmCategory, GeminiHarmBlockThreshold>)    .build();

GoogleAiGeminiStreamingChatModel[​](#googleaigeministreamingchatmodel "GoogleAiGeminiStreamingChatModel的直接链接")
--------------------------------------------------------------------------------------------------------------

`GoogleAiGeminiStreamingChatModel` 允许逐个令牌地流式传输响应文本。 响应必须由 `StreamingChatResponseHandler` 处理。

    StreamingChatLanguageModel gemini = GoogleAiGeminiStreamingChatModel.builder()        .apiKey(System.getenv("GEMINI_AI_KEY"))        .modelName("gemini-1.5-flash")        .build();CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();gemini.chat("讲个关于 Java 的笑话", new StreamingChatResponseHandler() {    @Override    public void onPartialResponse(String partialResponse) {        System.out.print(partialResponse);    }    @Override    public void onCompleteResponse(ChatResponse completeResponse) {        futureResponse.complete(completeResponse);    }    @Override    public void onError(Throwable error) {        futureResponse.completeExceptionally(error);    }});futureResponse.join();

函数调用[​](#函数调用 "函数调用的直接链接")
--------------------------

Gemini 支持函数调用，这意味着您可以定义一组函数，然后让 Gemini 决定何时调用它们。

    ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()    .apiKey(System.getenv("GEMINI_AI_KEY"))    .modelName("gemini-1.5-flash")    .toolConfig(GeminiFunctionCallingConfig.builder()        .addFunction(            GeminiFunction.builder()                .name("get_weather")                .description("获取给定位置的当前天气")                .addParameter("location", "string", "城市名称", true)                .addParameter("unit", "string", "温度单位，可以是'celsius'或'fahrenheit'", false)                .build()        )        .build())    .build();ChatResponse response = gemini.chat(    UserMessage.from("巴黎今天的天气如何？"));// 检查是否有函数调用if (response.aiMessage().toolExecutionRequests() != null) {    for (ToolExecutionRequest request : response.aiMessage().toolExecutionRequests()) {        System.out.println("函数名称: " + request.name());        System.out.println("参数: " + request.arguments());                // 在这里执行函数并获取结果        String result = executeFunction(request.name(), request.arguments());                // 将结果发送回 Gemini        response = gemini.chat(            UserMessage.from("巴黎今天的天气如何？"),            AiMessage.from(response.aiMessage().text())                .addToolExecutionResult(ToolExecutionResult.builder()                    .toolName(request.name())                    .content(result)                    .build())        );    }}System.out.println(response.aiMessage().text());

代码执行[​](#代码执行 "代码执行的直接链接")
--------------------------

Gemini 可以执行 Python 代码来解决问题。这对于数学问题、数据分析或任何需要计算的任务特别有用。

有两个构建器方法：

*   `allowCodeExecution(true)`：让 Gemini 知道它可以进行一些 Python 编码
*   `includeCodeExecutionOutput(true)`：如果您想看到它编写的实际 Python 脚本及其执行输出

    ChatResponse mathQuizz = gemini.chat(    SystemMessage.from("""        您是一位专家数学家。        当被问到数学问题或逻辑问题时，        您可以通过创建 Python 程序来解决它，        并执行它以返回结果。        """),    UserMessage.from("""        实现斐波那契和阿克曼函数。        `fibonacci(22)` - ackermann(3, 4) 的结果是多少？        """));

Gemini 将编写一个 Python 脚本，在其服务器上执行它，并返回结果。 由于我们要求查看代码执行的代码和输出，答案将如下所示：

    执行的代码：```pythondef fibonacci(n):    if n <= 1:        return n    else:        return fibonacci(n-1) + fibonacci(n-2)def ackermann(m, n):    if m == 0:        return n + 1    elif n == 0:        return ackermann(m - 1, 1)    else:        return ackermann(m - 1, ackermann(m, n - 1))print(fibonacci(22) - ackermann(3, 4))```输出：```17586````fibonacci(22)` - ackermann(3, 4) 的结果是 **17586**。我在 Python 中实现了斐波那契和阿克曼函数。然后我调用了 `fibonacci(22) - ackermann(3, 4)` 并打印了结果。

如果我们没有要求代码/输出，我们只会收到以下文本：

    `fibonacci(22)` - ackermann(3, 4) 的结果是 **17586**。我在 Python 中实现了斐波那彻和阿克曼函数。然后我调用了 `fibonacci(22) - ackermann(3, 4)` 并打印了结果。

多模态[​](#多模态 "多模态的直接链接")
-----------------------

Gemini 是一个多模态模型，这意味着它输出文本，但在输入中，它除了文本外还接受其他\_模态\_，如：

*   图片（`ImageContent`）
*   视频（`VideoContent`）
*   音频文件（`AudioContent`）
*   PDF 文件（`PdfFileContent`）
*   文本文档（`TextFileContent`）

下面的示例展示了如何混合文本提示、图像和 Markdown 文档：

    // LangChain4j 项目 Github 仓库中的 README.md markdown 文件String base64Text = b64encoder.encodeToString(readBytes(  "https://github.com/langchain4j/langchain4j/blob/main/README.md"));// LangChain4j 项目的可爱彩色鹦鹉吉祥物的 PNGString base64Img = b64encoder.encodeToString(readBytes(  "https://avatars.githubusercontent.com/u/132277850?v=4"));ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()    .apiKey(System.getenv("GEMINI_AI_KEY"))    .modelName("gemini-1.5-flash")    .build();ChatResponse response = gemini.chat(    UserMessage.from(        TextFileContent.from(base64Text, "text/x-markdown"),        ImageContent.from(base64Img, "image/png"),        TextContent.from("""            您认为这个标志            与项目描述匹配吗？            """)    ));

了解更多[​](#了解更多 "了解更多的直接链接")
--------------------------

如果您有兴趣了解更多关于 Google AI Gemini 模型的信息，请查看其 [文档](https://ai.google.dev/gemini-api/docs/models/gemini)。

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/integrations/language-models/google-ai-gemini.md)
