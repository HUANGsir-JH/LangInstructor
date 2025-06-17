package org.huang.langinstructor.store;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
// 暂时不做持久化，若要做可考虑MongoDB或Redis等
public class MemoryStore implements ChatMemoryStore {
    @Override
    public List<ChatMessage> getMessages(Object o) {
        return List.of();
    }
    
    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
    
    }
    
    @Override
    public void deleteMessages(Object o) {
    
    }
}
