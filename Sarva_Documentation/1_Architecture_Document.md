# Sarva AI - Architecture Document

## 1. Overview
Sarva AI is a Spring Boot-based application designed to provide a universal conversational interface. It routes user queries to specialized agents (e.g., Law, Finance) and supports multi-modal interaction (Text and Voice).

## 2. High-Level Architecture
The system follows a standard **Layered Architecture**:

### 2.1 Presentation Layer (Frontend)
- **Technology**: HTML5, CSS3, Vanilla JavaScript.
- **Key Components**:
  - `index.html`: The single-page chat interface.
  - `script.js`: Handles WebSocket/HTTP communication, Markdown rendering, and UI logic.
  - `style.css`: Manages visual themes and responsiveness.
- **Interaction**: Communicates with the backend via REST APIs (`/api/chat`, `/api/voice`).

### 2.2 Application Layer (Backend)
- **Technology**: Java 17, Spring Boot 3.3.5.
- **Key Components**:
  - **Controllers**: 
    - `ChatController`: endpoints for text chat.
    - `VoiceController`: endpoints for audio processing.
  - **Router**:
    - `AgentRouter`: Analyzes query intent and routes to the appropriate specific agent.
  - **Agents**:
    - `SarvaAgent` (Base/Orchestrator)
    - `LawAgent`, `FinanceAgent`, `MatrimonyAgent`, `IoTAgent` (Domain Specialists).

### 2.3 AI & Integration Layer
- **Framework**: Spring AI (1.0.1).
- **Models**:
  - **LLM**: OpenAI GPT models for text generation and reasoning.
  - **Audio**: OpenAI Whisper (STT) and TTS models.
- **Data Stores**:
  - **Vector Store**: Pinecone (configured for RAG - Retrieval Augmented Generation).

## 3. Data Flow
1.  **User Input**: Text or Audio is captured in the browser.
2.  **Transmission**: Sent to Spring Boot backend via HTTP POST.
3.  **Routing**: 
    - `AgentRouter` classifies the intent.
    - Selects the best agent (e.g., "Legal advice" -> `LawAgent`).
4.  **Processing**:
    - Agent constructs a prompt including context/memory.
    - Calls OpenAI API.
5.  **Streaming**: Response is streamed back chunk-by-chunk to the frontend.
6.  **Rendering**: Frontend parses Markdown and displays the response dynamically.

## 4. Key Design Patterns
- **Strategy Pattern**: For selecting different Agents based on intent.
- **Observer/Reactive**: Using `Flux` for streaming responses.
- **MVC**: Standard Spring Web MVC for API handling.
