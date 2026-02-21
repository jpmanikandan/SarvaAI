# Sarva AI - Functional Document

## 1. Introduction
Sarva AI is an intelligent assistant designed to democratize access to expert knowledge. It serves as a single entry point for various domains including Legal, Financial, Matrimonial, and Educational advice.

## 2. Key Features

### 2.1 Universal Expert Interaction
- **Dynamic Routing**: Users can ask any question, and the system automatically identifies the domain.
  - *Example*: "How do I file taxes?" -> Routes to **Finance Agent**.
  - *Example*: "What are the property laws?" -> Routes to **Law Agent**.
- **Context Awareness**: The system maintains conversation history to provide relevant follow-up responses.

### 2.2 Multi-Modal Interface
- **Text Chat**: Standard typing interface with rich text support.
- **Voice Interaction**:
  - **Speech-to-Text**: Users can speak their queries using the microphone.
  - **Text-to-Speech**: The AI can respond verbally for a seamless conversational experience.

### 2.3 Interactive User Interface
- **Rich Text Rendering**: Responses are formatted with Markdown (Bold, Lists, Headers).
- **Code Support**: Developer-friendly with syntax highlighting for code snippets and "Copy to Clipboard" functionality.
- **Visual Feedback**:
  - **Typing Indicators**: "..." animation while the AI processes.
  - **Agent Tags**: Explicitly shows which agent is responding (e.g., `LAW Agent Response`).

### 2.4 Multi-Language Support
- **Output Translation**: Users can select their preferred response language (e.g., Tamil, Hindi, Spanish) via the UI dropdown.
- **Cross-Lingual Processing**: Queries in English can be answered in another language, and vice-versa (depending on model capabilities).

## 3. User Roles
- **Guest/Public User**: Can access the chat interface, ask questions, and use voice features. No authentication required for basic access.

## 4. Use Cases
1.  **Legal Consultation**: A user asks about a traffic violation. The Law Agent provides a summary of relevant acts and advice.
2.  **Investment Advice**: A user asks about gold rates. The Finance Agent retrieves current data (simulated or real) and advises.
3.  **Language Learning**: A user practices conversation with "Miss Nova", the English tutor agent.
