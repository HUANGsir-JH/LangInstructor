## LangInstructor
一个用Langchain4j构建的帮助人用Langchain4j构建AI的AI，基于RAG和大模型实现

## 介绍
使用springboot，基于langchain4j，结合内置的easy-rag实现一个基于知识库进行的框架使用指导AI。可以使用`deepseek`或者`qwen-plus`作为驱动的AI。可以通过Vue构建的页面进行正常的流式输出对话。

## 使用
在配置文件中填写对应的`apikey`，在 `AiAssistant.java`文件中取消相应注释，默认是`qwen-plus`（因为他有免费额度），使用IDEA启动。  
在前端，使用`npm`下载所需依赖，可以查看`packages.json`进行下载，使用`npm run dev`即可启动，后端已经配置了跨域允许（允许localhost的访问）

## 展示
![演示1](https://github.com/HUANGsir-JH/LangInstructor/blob/master/img/img2.png)
![演示2](https://github.com/HUANGsir-JH/LangInstructor/blob/master/img/img1.png)

## 改进
实际上，这个实现和Langchain4j官方的[ChatBot](https://chat.langchain4j.dev/)类似，但这是一个简单的原型，可以进行如下改进：
- 更多的RAG指示内容，为了快速开发出原型，并没有完全爬取langchain4j的所有文档和示例代码
- RAG的存储可以更改为云存储，比如[Pinecone](https://app.pinecone.io/)，就可以向量化更多内容和使用更加强悍的嵌入模型（就是免费额度不多，要钱）
- 配置Tools，在`@AiService`中，已经配置类对应的工具类，但是没有具体实现，实际上可以为当前应用添加一个网络搜索工具（但需要购买api）
- 聊天记录的持久化存储，比如使用MongoDB存储，在`AssiatantConfig.java`中，有一个被注释的`ChatMemoryProvider`，实现`MemoeyStore`并且修改`@AiService`即可实现聊天记录的本地持久化
