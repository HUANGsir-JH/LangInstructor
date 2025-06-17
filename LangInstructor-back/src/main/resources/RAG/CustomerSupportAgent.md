# CustomerSupportAgent.java 示例

**示例目的:**
此文件定义了客户支持代理的 AI 服务接口。它通过 `@AiService` 注解将其标记为一个 AI 服务，并通过 `@SystemMessage` 注解定义了代理的角色、行为约束和一些动态信息（如当前日期）。

**关键类和方法:**
- `@AiService`: LangChain4j Spring 扩展注解，将接口标记为 AI 服务，Spring 会自动为其创建实现并注册为 Bean。
- `@SystemMessage`: 用于为 AI 服务定义系统消息。系统消息通常用于设置 LLM 的角色、行为准则和提供上下文信息。
- `answer(String userMessage)`: 代理的核心方法，用于接收用户消息并生成响应。

**注意事项:**
- `@SystemMessage` 中的 `{{current_date}}` 是一个占位符，LangChain4j 会在运行时自动填充当前日期。
- 代理被设定为“Miles of Smiles”汽车租赁公司的客户支持代理，并且在提供预订信息或取消预订之前，必须检查预订号、客户姓名和姓氏。这体现了通过系统消息对 LLM 行为进行约束。
- 这是一个 Spring 接口，通常会与 Spring Boot 应用程序结合使用。

```java
package dev.langchain4j.example;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
interface CustomerSupportAgent {

    @SystemMessage("""
            You are a customer support agent of a car rental company named 'Miles of Smiles'.
            Before providing information about booking or cancelling booking, you MUST always check:
            booking number, customer name and surname.
            Today is {{current_date}}.
            """)
    String answer(String userMessage);
}
```
