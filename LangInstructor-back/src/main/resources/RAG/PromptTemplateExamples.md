# PromptTemplateExamples.java 示例

**示例目的:**
此文件演示了如何使用 LangChain4j 的提示词模板功能，支持单变量和多变量替换，便于动态生成提示词。

**关键类和方法:**
- `PromptTemplate.from(String)`: 根据模板字符串创建提示词模板，支持 `{{variable}}` 变量占位符。
- `apply(String)`: 单变量替换，使用 `{{it}}` 作为默认变量名。
- `apply(Map<String, Object>)`: 多变量替换，通过 Map 提供变量名和值的映射。
- `Prompt.text()`: 获取替换后的最终提示词文本。

**注意事项:**
- 适合需要动态生成提示词、模板化交互的场景。
- 支持单变量简化语法和多变量复杂模板。
- 变量替换使用双大括号 `{{}}` 语法。

```java
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

public class PromptTemplateExamples {

    static class PromptTemplate_with_One_Variable_Example {

        public static void main(String[] args) {

            PromptTemplate promptTemplate = PromptTemplate.from("Say 'hi' in {{it}}.");

            Prompt prompt = promptTemplate.apply("German");

            System.out.println(prompt.text()); // Say 'hi' in German.
        }
    }

    static class PromptTemplate_With_Multiple_Variables_Example {

        public static void main(String[] args) {

            PromptTemplate promptTemplate = PromptTemplate.from("Say '{{text}}' in {{language}}.");

            Map<String, Object> variables = new HashMap<>();
            variables.put("text", "hi");
            variables.put("language", "German");

            Prompt prompt = promptTemplate.apply(variables);

            System.out.println(prompt.text()); // Say 'hi' in German.
        }
    }
}
