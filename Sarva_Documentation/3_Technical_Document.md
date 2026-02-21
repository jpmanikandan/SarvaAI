# Sarva AI - Technical Document

## 1. Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.3.5
- **Build Tool**: Maven
- **AI Integration**: Spring AI 1.0.1 (OpenAI, Ollama, Pinecone support)
- **Frontend**: 
  - HTML5, CSS3 (Bootstrap 5)
  - JavaScript (Vanilla + Marked.js + Highlight.js)

## 2. Prerequisites
- **Java**: JDK 17+ installed and configured (`JAVA_HOME`).
- **OpenAI API Key**: Required for chat and voice features.
- **Maven**: For building the project (wrapper included).

## 3. Configuration
The application is configured via `src/main/resources/application.yml`.

### Key Properties:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY} # Must be set in environment
      chat:
        options:
          model: gpt-3.5-turbo
server:
  port: 8080
```

## 4. Build and Run
### 4.1 Build
```bash
mvn clean install
```

### 4.2 Run
```bash
# Windows PowerShell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17.0.1'; mvn spring-boot:run
```

## 5. API Endpoints

### 5.1 Chat API
- **Endpoint**: `POST /api/chat`
- **Body**:
  ```json
  {
      "query": "User question here",
      "outputLanguage": "English"
  }
  ```
- **Response**: Streaming text (`Flux<String>`).

### 5.2 Voice API
- **Endpoint**: `POST /api/voice/chat`
- **Body**: `FormData` containing audio file (`recording.wav`).
- **Response**: Binary audio stream (WAV).

## 6. Project Structure
- `src/main/java/com/sarva`:
    - `core/`: Business logic, Agents, Router.
    - `api/`: REST Controllers.
    - `config/`: Spring Configuration.
- `src/main/resources/static`:
    - `index.html`, `js/script.js`, `css/style.css`: Frontend assets.
