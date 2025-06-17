
开始使用
====

备注

如果您使用 Quarkus，请参阅 [Quarkus 集成](/tutorials/quarkus-integration/)。

如果您使用 Spring Boot，请参阅 [Spring Boot 集成](/tutorials/spring-boot-integration)。

LangChain4j 提供[与多种 LLM 提供商的集成](/integrations/language-models/)。 每种集成都有自己的 Maven 依赖项。 最简单的开始方式是使用 OpenAI 集成：

*   对于 Maven 在 `pom.xml` 中：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j-open-ai</artifactId>    <version>1.0.0-beta3</version></dependency>

如果您希望使用高级 [AI 服务](/tutorials/ai-services) API，您还需要添加以下依赖项：

    <dependency>    <groupId>dev.langchain4j</groupId>    <artifactId>langchain4j</artifactId>    <version>1.0.0-beta3</version></dependency>

*   对于 Gradle 在 `build.gradle` 中：

    implementation 'dev.langchain4j:langchain4j-open-ai:1.0.0-beta3'implementation 'dev.langchain4j:langchain4j:1.0.0-beta3'

物料清单 (BOM)

    <dependencyManagement>    <dependencies>        <dependency>            <groupId>dev.langchain4j</groupId>            <artifactId>langchain4j-bom</artifactId>            <version>1.0.0-beta3</version>            <type>pom</type>            <scope>import</scope>        </dependency>    </dependencies></dependencyManagement>

SNAPSHOT 依赖项（最新功能）

如果您想在官方发布前测试最新功能， 您可以使用最新的 SNAPSHOT 依赖项：

    <repositories>    <repository>        <id>snapshots-repo</id>        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>        <snapshots>            <enabled>true</enabled>        </snapshots>    </repository></repositories><dependencies>    <dependency>        <groupId>dev.langchain4j</groupId>        <artifactId>langchain4j</artifactId>        <version>1.0.0-beta4-SNAPSHOT</version>    </dependency></dependencies>

然后，导入您的 OpenAI API 密钥。 建议将 API 密钥存储在环境变量中，以降低公开暴露的风险。

    String apiKey = System.getenv("OPENAI_API_KEY");

如果我没有 API 密钥怎么办？

如果您没有自己的 OpenAI API 密钥，不用担心。 您可以临时使用我们免费提供的 `demo` 密钥，用于演示目的。 请注意，当使用 `demo` 密钥时，所有对 OpenAI API 的请求都需要通过我们的代理， 该代理会在转发请求到 OpenAI API 之前注入真实的密钥。 我们不会以任何方式收集或使用您的数据。 `demo` 密钥有配额限制，仅限于使用 `gpt-4o-mini` 模型，并且应该仅用于演示目的。

    OpenAiChatModel model = OpenAiChatModel.builder()    .baseUrl("http://langchain4j.dev/demo/openai/v1")    .apiKey("demo")
    .modelName("gpt-4o-mini")
    .build();

设置好密钥后，让我们创建一个 `OpenAiChatModel` 实例：

    OpenAiChatModel model = OpenAiChatModel.builder()    .apiKey(apiKey)    .modelName("gpt-4o-mini")    .build();

现在，是时候开始聊天了！

    String answer = model.chat("Say 'Hello World'");System.out.println(answer); // Hello World

[编辑此页](https://github.com/langchain4j/langchain4j/blob/main/docs/docs/get-started.md)
