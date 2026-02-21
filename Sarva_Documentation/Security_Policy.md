# Security Policy & Data Privacy

## 1. API Key Management
- **OpenAI Keys**: Must never be hardcoded in source code. Use environment variables (`OPENAI_API_KEY`).
- **Pinecone Keys**: Managed similarly via environment configuration.
- **Git**: `.env` files are added to `.gitignore` to prevent accidental commits.

## 2. Data Privacy (PII)
- **Logging**: The application logs query intents but **scripts** filter out PII (Personally Identifiable Information) like phone numbers or email addresses before storage.
- **Memory**: Conversation history is stored in-memory (RAM) and is cleared when the application restarts. No persistent database is currently used for chat logs.

## 3. Network Security
- **HTTPS**: Recommended for production deployments.
- **CORS**: Configured to allow requests only from trusted origins (like `localhost` for dev, or specific domains for prod).
- **Input Validation**: All user inputs are sanitized to prevent injection attacks.

## 4. Reporting Vulnerabilities
If you find a security issue, please email `security@sarva.ai` (fictional) instead of opening a public GitHub issue.
