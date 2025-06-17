package org.huang.langinstructor.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.huang.langinstructor.store.MemoryStore;

import java.io.File;
import java.util.List;

@Configuration
public class AssistantConfig {
    @Resource
    private MemoryStore memoryStore;
    
    // 可以持久化的内存级别的聊天内存提供者，前提是完善MemoryStore的实现
    @Bean(name = "openAiStreamingMemoryProvider")
    public ChatMemoryProvider openAiStreamingMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .maxMessages(30)
                .id(memoryId)
                .chatMemoryStore(memoryStore)
                .build();
    }
    
    // 不持久化的内存级别的聊天内存提供者
    @Bean(name="openAiStreamingMemoryProviderNoPersist")
    public ChatMemoryProvider openAiStreamingMemoryProviderNoPersist() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(50);
    }
    
    // 使用内存级别的向量存储和内置的量化嵌入模型
    @Bean(name = "qwenEmbeddingStore")
    public ContentRetriever qwenEmbeddingStore() {
        
        String filepath = "src/main/resources/VectorStore/store.json";
        
        File jsonFile = new File(filepath);
        if(jsonFile.length() > 0){
            InMemoryEmbeddingStore<TextSegment> embeddingStore = InMemoryEmbeddingStore.fromFile(filepath);
            return EmbeddingStoreContentRetriever.from(embeddingStore);
        }else{
            List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively("src/main/resources/RAG");
            InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
            EmbeddingStoreIngestor.ingest(documents, embeddingStore);
            embeddingStore.serializeToFile(filepath);
            return EmbeddingStoreContentRetriever.from(embeddingStore);
        }
    }
    
}
