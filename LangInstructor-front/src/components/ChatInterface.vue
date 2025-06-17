<template>
  <div class="chat-container">
    <!-- 标题栏 -->
    <header class="chat-header">
      <h1 class="chat-title">AI 助手</h1>
      <button 
        @click="resetChat" 
        class="reset-btn"
        :disabled="isLoading"
        title="重置对话"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/>
          <path d="M21 3v5h-5"/>
          <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/>
          <path d="M3 21v-5h5"/>
        </svg>
        重置
      </button>
    </header>

    <!-- 消息区域 -->
    <div class="messages-container" ref="messagesRef">
      <div class="messages-wrapper">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-icon">🤖</div>
          <h2>欢迎使用 AI 助手</h2>
          <p>您可以向我提问Langchain4j的问题，我会尽力为您解答。</p>
        </div>
        
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          class="message-item"
          :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
        >
          <div class="message-avatar">
            <div v-if="message.type === 'user'" class="user-avatar">👤</div>
            <div v-else class="ai-avatar">🤖</div>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="message.content"></div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <!-- 加载指示器 -->
        <div v-if="isLoading" class="message-item ai-message">
          <div class="message-avatar">
            <div class="ai-avatar">🤖</div>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-container">
      <div class="input-wrapper">
        <textarea
          v-model="inputMessage"
          @keydown="handleKeyDown"
          placeholder="输入您的问题..."
          class="message-input"
          :disabled="isLoading"
          rows="1"
          ref="textareaRef"
        ></textarea>
        <button 
          @click="sendMessage" 
          class="send-btn"
          :disabled="!inputMessage.trim() || isLoading"
        >
          <svg v-if="!isLoading" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22,2 15,22 11,13 2,9"></polygon>
          </svg>
          <div v-else class="loading-spinner"></div>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import axios from 'axios'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

// 配置marked
const renderer = new marked.Renderer()
marked.setOptions({
  breaks: true,
  gfm: true
})

// 处理代码高亮的函数
function highlightCode(code: string, language?: string): string {
  if (language && hljs.getLanguage(language)) {
    try {
      return hljs.highlight(code, { language }).value
    } catch (err) {
      console.error('代码高亮错误:', err)
    }
  }
  return hljs.highlightAuto(code).value
}

// 消息接口
interface Message {
  type: 'user' | 'ai'
  content: string
  timestamp: Date
}

// 聊天表单接口
interface ChatForm {
  memoryId: string
  userMessage: string
}

// 响应式数据
const messages = reactive<Message[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const memoryId = ref(generateMemoryId())
const messagesRef = ref<HTMLElement>()
const textareaRef = ref<HTMLTextAreaElement>()

// API基础URL
const API_BASE_URL = 'http://localhost:8080'

// 生成内存ID
function generateMemoryId(): string {
  return Date.now().toString() + Math.random().toString(36).substr(2, 9)
}

// 格式化时间
function formatTime(date: Date): string {
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

// 自动调整textarea高度
function autoResizeTextarea() {
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = 'auto'
      textareaRef.value.style.height = Math.min(textareaRef.value.scrollHeight, 120) + 'px'
    }
  })
}

// 处理键盘事件
function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
  autoResizeTextarea()
}

// 处理markdown渲染
async function renderMarkdown(text: string): Promise<string> {
  // 使用自定义的代码高亮处理
  const html = await marked.parse(text)
  
  // 使用DOM解析器处理代码块高亮
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = html
  
  // 处理所有代码块
  const codeBlocks = tempDiv.querySelectorAll('pre code')
  codeBlocks.forEach((block) => {
    const codeElement = block as HTMLElement
    const language = codeElement.className.replace('language-', '')
    const code = codeElement.textContent || ''
    
    try {
      if (language && hljs.getLanguage(language)) {
        const highlightedCode = hljs.highlight(code, { language }).value
        codeElement.innerHTML = highlightedCode
      } else {
        const highlightedCode = hljs.highlightAuto(code).value
        codeElement.innerHTML = highlightedCode
      }
    } catch (err) {
      console.error('代码高亮错误:', err)
    }
  })
  
  return tempDiv.innerHTML
}

// 发送消息
async function sendMessage() {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  autoResizeTextarea()

  // 添加用户消息
  messages.push({
    type: 'user',
    content: userMessage,
    timestamp: new Date()
  })

  scrollToBottom()
  isLoading.value = true

  try {
    // 创建AI消息占位符
    const aiMessageIndex = messages.length
    messages.push({
      type: 'ai',
      content: '',
      timestamp: new Date()
    })

    const chatForm: ChatForm = {
      memoryId: memoryId.value,
      userMessage: userMessage
    }

    // 使用fetch API处理流式响应
    const response = await fetch(`${API_BASE_URL}/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/stream;charset=UTF-8'
      },
      body: JSON.stringify(chatForm)
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    // 处理流式响应
    const reader = response.body?.getReader()
    if (!reader) {
      throw new Error('无法获取响应流')
    }

    const decoder = new TextDecoder()
    let accumulatedText = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      const chunk = decoder.decode(value, { stream: true })
      accumulatedText += chunk
      
      // 实时更新AI消息内容（渲染markdown）
      messages[aiMessageIndex].content = await renderMarkdown(accumulatedText)
      scrollToBottom()
    }

  } catch (error) {
    console.error('发送消息失败:', error)
    
    // 移除可能添加的空消息
    if (messages[messages.length - 1]?.content === '') {
      messages.pop()
    }
    
    // 显示错误消息
    messages.push({
      type: 'ai',
      content: '抱歉，发送消息时出现错误，请稍后重试。错误信息：' + (error as Error).message,
      timestamp: new Date()
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// 重置对话
async function resetChat() {
  try {
    await axios.get(`${API_BASE_URL}/reset`, {
      params: { memoryId: memoryId.value }
    })
    
    // 清空消息并生成新的memoryId
    messages.length = 0
    memoryId.value = generateMemoryId()
    
    console.log('对话已重置')
  } catch (error) {
    console.error('重置对话失败:', error)
  }
}

// 组件挂载后的初始化
onMounted(() => {
  autoResizeTextarea()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 1400px;
  margin: 0 auto;
  background: #f8fafc;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  border-radius: 0;
}

@media (min-width: 1024px) {
  .chat-container {
    margin: 20px auto;
    height: calc(100vh - 40px);
    border-radius: 20px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
}

/* 标题栏 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.chat-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.2rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  backdrop-filter: blur(10px);
}

.reset-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.reset-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 消息区域 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 0;
  scroll-behavior: smooth;
}

.messages-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.welcome-message {
  text-align: center;
  padding: 4rem 2rem;
  color: #64748b;
}

.welcome-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.welcome-message h2 {
  margin: 0 0 1rem 0;
  color: #334155;
  font-weight: 600;
}

.welcome-message p {
  margin: 0;
  font-size: 1.1rem;
  line-height: 1.6;
}

.message-item {
  display: flex;
  margin-bottom: 2rem;
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 1rem;
}

.user-avatar,
.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
}

.user-avatar {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
}

.ai-avatar {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.user-message .message-content {
  text-align: right;
}

.message-text {
  background: white;
  padding: 1rem 1.5rem;
  border-radius: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  line-height: 1.6;
  word-wrap: break-word;
}

.user-message .message-text {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.ai-message .message-text {
  border-bottom-left-radius: 6px;
}

.message-time {
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #94a3b8;
  padding: 0 0.5rem;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  background: white;
  border-radius: 18px;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94a3b8;
  margin: 0 2px;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 输入区域 */
.input-container {
  padding: 1rem 1.5rem;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.input-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  align-items: flex-end;
  gap: 1rem;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 28px;
  padding: 0.75rem;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.input-wrapper:focus-within {
  border-color: #4f46e5;
}

.message-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  line-height: 1.5;
  resize: none;
  min-height: 24px;
  max-height: 120px;
  font-family: inherit;
}

.message-input::placeholder {
  color: #94a3b8;
}

.send-btn {
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.4);
}

.send-btn:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* markdown样式 */
.message-text :deep(pre) {
  background: #f8fafc;
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1rem 0;
  border-left: 4px solid #4f46e5;
}

.message-text :deep(code) {
  background: #f1f5f9;
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
  font-size: 0.9em;
}

.message-text :deep(pre code) {
  background: transparent;
  padding: 0;
}

.user-message .message-text :deep(pre) {
  background: rgba(255, 255, 255, 0.1);
  border-left-color: rgba(255, 255, 255, 0.3);
}

.user-message .message-text :deep(code) {
  background: rgba(255, 255, 255, 0.2);
}

.message-text :deep(blockquote) {
  border-left: 4px solid #e2e8f0;
  padding-left: 1rem;
  margin: 1rem 0;
  color: #64748b;
  font-style: italic;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  padding-left: 1.5rem;
  margin: 0.5rem 0;
}

.message-text :deep(li) {
  margin: 0.25rem 0;
}

.message-text :deep(h1),
.message-text :deep(h2),
.message-text :deep(h3),
.message-text :deep(h4),
.message-text :deep(h5),
.message-text :deep(h6) {
  margin: 1rem 0 0.5rem 0;
  font-weight: 600;
}

.message-text :deep(p) {
  margin: 0.5rem 0;
}

.message-text :deep(a) {
  color: #4f46e5;
  text-decoration: none;
}

.message-text :deep(a:hover) {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    height: 100vh;
  }
  
  .chat-header {
    padding: 0.75rem 1rem;
  }
  
  .chat-title {
    font-size: 1.25rem;
  }
  
  .messages-wrapper {
    padding: 0 0.75rem;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .input-container {
    padding: 0.75rem 1rem;
  }
  
  .welcome-message {
    padding: 2rem 1rem;
  }
  
  .welcome-icon {
    font-size: 3rem;
  }
}

/* 滚动条样式 */
.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

.messages-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
