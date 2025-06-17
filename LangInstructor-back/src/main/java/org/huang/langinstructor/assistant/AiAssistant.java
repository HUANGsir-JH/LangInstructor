package org.huang.langinstructor.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.memory.ChatMemoryAccess;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        streamingChatModel = "openAiStreamingChatModel",
//        streamingChatModel = "qwenStreamingChatModel",
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatMemoryProvider = "openAiStreamingMemoryProviderNoPersist",
        contentRetriever = "qwenEmbeddingStore",
        tools = "aiTool"
)
public interface AiAssistant extends ChatMemoryAccess {
    @SystemMessage(fromResource = "SystemPrompt.txt")
    Flux<String> chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
