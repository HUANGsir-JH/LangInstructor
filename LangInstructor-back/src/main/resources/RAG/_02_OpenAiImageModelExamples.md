# _02_OpenAiImageModelExamples.java 示例

**示例目的:**
这是 LangChain4j 教程系列的第三个示例，演示如何使用 OpenAI 的 DALL·E 3 图像生成模型，根据文本描述生成创意图片。

**关键类和方法:**
- `OpenAiImageModel`: 用于与 OpenAI 图像生成模型交互的核心类。
- `generate(String prompt)`: 根据提示词生成图片，返回 `Response<Image>`。
- `Image.url()`: 获取生成图片的远程 URL。
- `DALL_E_3`: OpenAI DALL·E 3 图像生成模型名称常量。

**注意事项:**
- 需要有效的 OpenAI API Key。
- 适合文本生成图片、创意设计、AI 绘画等场景。
- 输出为图片的远程访问地址，可直接在浏览器中查看。

```java
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;

import static dev.langchain4j.model.openai.OpenAiImageModelName.DALL_E_3;

public class _02_OpenAiImageModelExamples {

    public static void main(String[] args) {

        ImageModel model = OpenAiImageModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(DALL_E_3)
                .build();

        Response<Image> response = model.generate(
                "Swiss software developers with cheese fondue, a parrot and a cup of coffee");

        System.out.println(response.content().url());
    }
}
