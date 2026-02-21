# Deployment & Operations Guide

## 1. Prerequisites
- **Java**: JDK 17 or higher (`java -version`).
- **Maven**: Apache Maven 3.8+ (`mvn -v`).
- **API Keys**: OpenAI API Key (Required).

## 2. Environment Configuration
Create a `.env` file or set system environment variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `OPENAI_API_KEY` | Required for LLM/STT/TTS | `sk-proj-...` |
| `SERVER_PORT` | Port for the application | `8080` |

## 3. Building the Application
Run the following command in the project root:

```bash
mvn clean package -DskipTests
```
*Artifact created*: `target/sarva-ai-0.0.1-SNAPSHOT.jar`

## 4. Running the Application

### 4.1 Local Run (Dev)
```bash
mvn spring-boot:run
```

### 4.2 Production Run
```bash
java -jar target/sarva-ai-0.0.1-SNAPSHOT.jar
```

## 5. Docker Deployment (Optional)
If a `Dockerfile` is present:

1.  **Build Image**:
    ```bash
    docker build -t sarva-ai .
    ```
2.  **Run Container**:
    ```bash
    docker run -p 8080:8080 -e OPENAI_API_KEY=sk-... sarva-ai
    ```

## 6. Logs & Monitoring
- **Logs**: Written to `console` and `logs/application.log` (if configured).
- **Health Check**: `GET /actuator/health` (if Actuator is enabled).
