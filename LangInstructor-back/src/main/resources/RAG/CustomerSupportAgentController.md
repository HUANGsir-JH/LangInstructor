# CustomerSupportAgentController.java 示例

**示例目的:**
此文件是客户支持代理 Spring Boot 应用程序的 REST 控制器，提供了一个 HTTP 端点，允许通过 Web 请求与客户支持代理进行交互。

**关键类和方法:**
- `@RestController`: Spring 框架注解，表示这是一个 RESTful 控制器。
- `CustomerSupportAgentController(CustomerSupportAgent customerSupportAgent)`: 构造函数，通过 Spring 自动注入 `CustomerSupportAgent` 实例。
- `@GetMapping("/customerSupportAgent")`: 定义一个 HTTP GET 请求的端点。
- `@RequestParam String sessionId`: 从请求参数中获取会话 ID，用于区分不同用户的对话。
- `@RequestParam String userMessage`: 从请求参数中获取用户消息。
- `customerSupportAgent.answer(sessionId, userMessage)`: 调用 `CustomerSupportAgent` 的 `answer` 方法处理用户消息。
- `Result<String>`: LangChain4j 服务返回的结果封装类。

**注意事项:**
- 这是一个 Spring Web 控制器，需要 Spring Boot Web 环境才能正常运行。
- `sessionId` 参数允许为每个用户维护独立的聊天记忆。
- 通过 HTTP GET 请求即可与 AI 代理进行交互，方便集成到前端应用或进行测试。

```java
package dev.langchain4j.example;

import dev.langchain4j.service.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerSupportAgentController {

    private final CustomerSupportAgent customerSupportAgent;

    public CustomerSupportAgentController(CustomerSupportAgent customerSupportAgent) {
        this.customerSupportAgent = customerSupportAgent;
    }

    @GetMapping("/customerSupportAgent")
    public String customerSupportAgent(@RequestParam String sessionId, @RequestParam String userMessage) {
        Result<String> result = customerSupportAgent.answer(sessionId, userMessage);
        return result.content();
    }
}
