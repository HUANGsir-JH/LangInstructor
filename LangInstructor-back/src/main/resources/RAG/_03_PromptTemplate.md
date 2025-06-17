# _03_PromptTemplate.java 示例

**示例目的:**
这是 LangChain4j 教程系列的第四个示例，演示如何使用简单提示词模板和结构化提示词模板，实现动态、复杂的提示词生成。

**关键类和方法:**
- `PromptTemplate.from(String)`: 创建简单提示词模板，支持 `{{variable}}` 变量占位符。
- `apply(Map<String, Object>)`: 使用变量映射替换模板中的占位符。
- `@StructuredPrompt`: 注解定义复杂的结构化提示词模板，支持多行和嵌套结构。
- `StructuredPromptProcessor.toPrompt(Object)`: 将结构化提示词对象转换为 Prompt。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 简单模板适合基础变量替换，结构化模板适合复杂、多段落的提示词。
- 结构化提示词支持更清晰的组织和维护。

```java
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;

public class _03_PromptTemplate {

    static class Simple_Prompt_Template_Example {

        public static void main(String[] args) {

            ChatModel model = OpenAiChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY)
                    .modelName(GPT_4_O_MINI)
                    .timeout(ofSeconds(60))
                    .build();

            String template = "Create a recipe for a {{dishType}} with the following ingredients: {{ingredients}}";
            PromptTemplate promptTemplate = PromptTemplate.from(template);

            Map<String, Object> variables = new HashMap<>();
            variables.put("dishType", "oven dish");
            variables.put("ingredients", "potato, tomato, feta, olive oil");

            Prompt prompt = promptTemplate.apply(variables);

            String response = model.chat(prompt.text());

            System.out.println(response);
        }

    }

    static class Structured_Prompt_Template_Example {
        @StructuredPrompt({
                "Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}.",
                "Structure your answer in the following way:",

                "Recipe name: ...",
                "Description: ...",
                "Preparation time: ...",

                "Required ingredients:",
                "- ...",
                "- ...",

                "Instructions:",
                "- ...",
                "- ..."
        })
        static class CreateRecipePrompt {

            String dish;
            List<String> ingredients;

            CreateRecipePrompt(String dish, List<String> ingredients) {
                this.dish = dish;
                this.ingredients = ingredients;
            }
        }

        public static void main(String[] args) {

            ChatModel model = OpenAiChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY)
                    .modelName(GPT_4_O_MINI)
                    .timeout(ofSeconds(60))
                    .build();

            Structured_Prompt_Template_Example.CreateRecipePrompt createRecipePrompt = new Structured_Prompt_Template_Example.CreateRecipePrompt(
                    "salad",
                    asList("cucumber", "tomato", "feta", "onion", "olives")
            );

            Prompt prompt = StructuredPromptProcessor.toPrompt(createRecipePrompt);

            String recipe = model.chat(prompt.text());

            System.out.println(recipe);
        }
    }
}
