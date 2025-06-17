package org.huang.langinstructor.controller;

import org.huang.langinstructor.assistant.AiAssistant;
import org.huang.langinstructor.bean.ChatForm;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AssistantController {

    @Resource
    private AiAssistant aiAssistant;
    
    @PostMapping(value = "/chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        return aiAssistant.chat(chatForm.getMemoryId().toString(), chatForm.getUserMessage());
    }
    
    @GetMapping(value="/reset")
    public void reset(String memoryId) {
        aiAssistant.evictChatMemory(memoryId);
        // 用于重置会话
    }
}
