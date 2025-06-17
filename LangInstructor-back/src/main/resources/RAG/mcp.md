
模型上下文协议 (MCP)
=============

LangChain4j 支持模型上下文协议 (MCP)，用于与符合 MCP 的服务器通信，这些服务器可以提供和执行工具。有关该协议的一般信息可以在 [MCP 网站](https://modelcontextprotocol.io/) 上找到。

该协议指定了两种传输类型，两种都受支持：

*   `HTTP`：客户端请求一个 SSE 通道来接收来自服务器的事件，然后通过 HTTP POST 请求发送命令。
*   `stdio`：客户端可以将 MCP 服务器作为本地子进程运行，并通过标准输入/输出直接与其通信。

要让你的聊天模型或 AI 服务运行 MCP 服务器提供的工具，你需要创建一个 MCP 工具提供者实例。

创建 MCP 工具提供者[​](#创建-mcp-工具提供者 "创建 MCP 工具提供者的直接链接")
--------------------------------------------------

### MCP 传输[​](#mcp-传输 "MCP 传输的直接链接")

首先，你需要一个 MCP 传输实例。

对于 stdio - 这个例子展示了如何从 NPM 包启动一个服务器作为子进程：

    McpTransport transport = new StdioMcpTransport.Builder()    .command(List.of("/usr/bin/npm", "exec", "@modelcontextprotocol/server-everything@0.6.2"))    .logEvents(true) // 仅当你想在日志中查看流量时    .build();

对于 HTTP，你需要两个 URL，一个用于启动 SSE 通道，一个用于通过 `POST` 提交命令：

    McpTransport transport = new HttpMcpTransport.Builder()    .sseUrl("http://localhost:3001/sse")    .logRequests(true) // 如果你想在日志中查看流量    .logResponses(true)    .build();

### MCP 客户端[​](#mcp-客户端 "MCP 客户端的直接链接")

从传输创建 MCP 客户端：

    McpClient mcpClient = new DefaultMcpClient.Builder()    .transport(transport)    .build();

### MCP 工具提供者[​](#mcp-工具提供者 "MCP 工具提供者的直接链接")

最后，从客户端创建 MCP 工具提供者：

    ToolProvider toolProvider = McpToolProvider.builder()    .mcpClients(List.of(mcpClient))    .build();

请注意，一个 MCP 工具提供者可以同时使用多个客户端。 如果你利用这一点，你还可以指定工具提供者在从特定服务器检索工具失败的情况下的行为 - 这是通过 `builder.failIfOneServerFails(boolean)` 方法完成的。默认值为 `false`， 这意味着工具提供者将忽略来自一个服务器的错误并继续使用其他服务器。如果你将其设置为 `true`，任何服务器的失败都将导致工具提供者抛出异常。

要将工具提供者绑定到 AI 服务，只需使用 AI 服务构建器的 `toolProvider` 方法：

    Bot bot = AiServices.builder(Bot.class)    .chatLanguageModel(model)    .toolProvider(toolProvider)    .build();

有关 LangChain4j 中工具支持的更多信息可以在[这里](/tutorials/tools)找到。

日志记录[​](#日志记录 "日志记录的直接链接")
--------------------------

MCP 协议还定义了服务器向客户端发送日志消息的方式。默认情况下，客户端的行为是转换这些日志消息并使用 SLF4J 记录器记录它们。如果你想更改此行为，有一个名为 `dev.langchain4j.mcp.client.logging.McpLogMessageHandler` 的接口，它作为接收日志消息的回调。如果你创建了自己的 `McpLogMessageHandler` 实现，将其传递给 MCP 客户端构建器：

    McpClient mcpClient = new DefaultMcpClient.Builder()    .transport(transport)    .logMessageHandler(new MyLogMessageHandler())    .build();

资源[​](#资源 "资源的直接链接")
--------------------

要获取服务器上的 [MCP 资源](https://modelcontextprotocol.io/docs/concepts/resources) 列表，使用 `client.listResources()` 或者在资源模板的情况下使用 `client.listResourceTemplates()`。 这将返回一个 `McpResource` 对象列表（或相应的 `McpResourceTemplate`）。这些包含资源的元数据，最重要的是 URI。

要获取资源的实际内容，使用 `client.readResource(uri)`，提供资源的 URI。 这将返回一个 `McpReadResourceResult`，其中包含 `McpResourceContents` 对象的列表（在单个 URI 上可能有多个资源内容，例如如果 URI 表示一个目录）。每个 `McpResourceContents` 对象表示二进制 blob（`McpBlobResourceContents`）或文本（`McpTextResourceContents`）。

提示[​](#提示 "提示的直接链接")
--------------------

要从服务器获取 [MCP 提示](https://modelcontextprotocol.io/docs/concepts/prompts) 列表，使用 `client.listPrompts()`。此方法返回 `McpPrompt` 的列表。`McpPrompt` 包含有关提示的名称和参数的信息。

要渲染提示的实际内容，使用 `client.getPrompt(name, arguments)`。渲染的提示可以包含一到多个消息，这些消息表示为 `McpPromptMessage` 对象。每个 `McpPromptMessage` 包含消息的角色（`user`、`assistant`...）和消息的实际内容。目前支持的消息内容类型有：`McpTextContent`、`McpImageContent` 和 `McpEmbeddedResource`。

你可以使用 `McpPromptMessage.toChatMessage()` 将其转换为 LangChain4j 核心 API 中的通用 `dev.langchain4j.data.message.ChatMessage`。但这并非在所有情况下都可行。例如，如果提示消息的 `role` 是 `assistant` 并且它包含文本以外的内容，它将抛出异常。无论角色如何，将带有二进制 blob 内容的消息转换为 `ChatMessage` 都不受支持。

通过 Docker 使用 GitHub MCP 服务器[​](#通过-docker-使用-github-mcp-服务器 "通过 Docker 使用 GitHub MCP 服务器的直接链接")
-----------------------------------------------------------------------------------------------

现在让我们看看如何使用模型上下文协议 (MCP) 以标准化的方式将 AI 模型与外部工具连接起来。 以下示例将通过 LangChain4j MCP 客户端与 GitHub 交互，获取并总结公共 GitHub 仓库的最新提交。 为此，无需重新发明轮子，我们可以使用 [MCP GitHub 仓库](https://github.com/modelcontextprotocol) 中现有的 [GitHub MCP 服务器实现](https://github.com/modelcontextprotocol/servers/tree/main/src/github)。

我们的想法是构建一个 Java 应用程序，连接到本地 Docker 中运行的 GitHub MCP 服务器，获取并总结最新的提交。 该示例使用 MCP 的 stdio 传输机制在我们的 Java 应用程序和 GitHub MCP 服务器之间进行通信。

在 Docker 中打包和执行 GitHub MCP 服务器[​](#在-docker-中打包和执行-github-mcp-服务器 "在 Docker 中打包和执行 GitHub MCP 服务器的直接链接")
----------------------------------------------------------------------------------------------------------

要与 GitHub 交互，我们首先需要在 Docker 中设置 GitHub MCP 服务器。 GitHub MCP 服务器提供了一个标准化的接口，通过模型上下文协议与 GitHub 交互。 它支持文件操作、仓库管理和搜索功能。

要为我们的 GitHub MCP 服务器构建 Docker 镜像，你需要通过克隆仓库或下载代码从 [MCP servers GitHub 仓库](https://github.com/modelcontextprotocol/servers/tree/main/src/github) 获取代码。 然后，导航到根目录并执行以下 Docker 命令：

    docker build -t mcp/github -f src/github/Dockerfile .

`Dockerfile` 设置了必要的环境并安装了 GitHub MCP 服务器实现。 构建完成后，镜像将在本地可用，名为 `mcp/github`。

    docker image lsREPOSITORY   TAG         IMAGE ID        SIZEmcp/github   latest      b141704170b1    173MB

开发工具提供者[​](#开发工具提供者 "开发工具提供者的直接链接")
-----------------------------------

让我们创建一个名为 `McpGithubToolsExample` 的 Java 类，使用 LangChain4j 连接到我们的 GitHub MCP 服务器。这个类将：

*   在 Docker 容器中启动 GitHub MCP 服务器（`docker` 命令位于 `/usr/local/bin/docker`）
*   使用 stdio 传输建立连接
*   使用 LLM 总结 LangChain4j GitHub 仓库的最后 3 个提交

> **注意**：在下面的代码中，我们在环境变量 `GITHUB_PERSONAL_ACCESS_TOKEN` 中传递 GitHub 令牌。但这对于不需要身份验证的公共仓库的某些操作是可选的。

以下是实现：

    public static void main(String[] args) throws Exception {    ChatLanguageModel model = OpenAiChatModel.builder()        .apiKey(System.getenv("OPENAI_API_KEY"))        .modelName("gpt-4o-mini")        .logRequests(true)        .logResponses(true)        .build();    McpTransport transport = new StdioMcpTransport.Builder()        .command(List.of("/usr/local/bin/docker", "run", "-e", "GITHUB_PERSONAL_ACCESS_TOKEN", "-i", "mcp/github"))        .logEvents(true)        .build();    McpClient mcpClient = new DefaultMcpClient.Builder()        .transport(transport)        .build();    ToolProvider toolProvider = McpToolProvider.builder()        .mcpClients(List.of(mcpClient))        .build();    Bot bot = AiServices.builder(Bot.class)        .chatLanguageModel(model)        .toolProvider(toolProvider)        .build();    try {        String response = bot.chat("Summarize the last 3 commits of the LangChain4j GitHub repository");        System.out.println("RESPONSE: " + response);    } finally {        mcpClient.close();    }}

> **注意**：此示例使用 Docker，因此执行位于 `/usr/local/bin/docker` 的 Docker 命令（根据你的操作系统更改路径）。如果你想使用 Podman 而不是 Docker，请相应地更改命令。

执行代码[​](#执行代码 "执行代码的直接链接")
--------------------------

要运行示例，请确保 Docker 在你的系统上运行。 另外，在环境变量 `OPENAI_API_KEY` 中设置你的 OpenAI API 密钥。

然后运行 Java 应用程序。你应该会得到一个总结 LangChain4j GitHub 仓库最后 3 个提交的响应，例如：

    以下是 LangChain4j GitHub 仓库最后三个提交的摘要：1. **提交 [36951f9](https://github.com/langchain4j/langchain4j/commit/36951f9649c1beacd8b9fc2d910a2e23223e0d93)** (日期: 2025-02-05)   - **作者:** Dmytro Liubarskyi   - **消息:** 更新到 `upload-pages-artifact@v3`。   - **详情:** 此提交将用于上传页面构件的 GitHub Action 更新到版本 3。2. **提交 [6fcd19f](https://github.com/langchain4j/langchain4j/commit/6fcd19f50c8393729a0878d6125b0bb1967ac055)** (日期: 2025-02-05)   - **作者:** Dmytro Liubarskyi   - **消息:** 更新到 `checkout@v4`、`deploy-pages@v4` 和 `upload-pages-artifact@v4`。   - **详情:** 此提交将多个 GitHub Actions 更新到它们的版本 4。3. **提交 [2e74049](https://github.com/langchain4j/langchain4j/commit/2e740495d2aa0f16ef1c05cfcc76f91aef6f6599)** (日期: 2025-02-05)   - **作者:** Dmytro Liubarskyi   - **消息:** 更新到 `setup-node@v4` 和 `configure-pages@v4`。   - **详情:** 此提交将 `setup-node` 和 `configure-pages` GitHub Actions 更新到版本 4。所有提交都由同一作者 Dmytro Liubarskyi 在同一天完成，专注于将各种 GitHub Actions 更新到更新的版本。

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/tutorials/mcp.md)
