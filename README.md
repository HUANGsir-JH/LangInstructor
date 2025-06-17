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
