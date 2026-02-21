# Troubleshooting & FAQ Guide

## 1. Common Issues

### 1.1 "Connection Refused" or "Port 8080 in use"
- **Symptom**: Application fails to start.
- **Cause**: Another application is using port 8080.
- **Solution**:
  - Kill the process using port 8080 (`netstat -ano | findstr :8080`).
  - Or change the port in `application.yml`: `server.port: 8081`.

### 1.2 "401 Unauthorized" from OpenAI
- **Symptom**: Chat responds with an error about authentication.
- **Cause**: `OPENAI_API_KEY` is missing or invalid.
- **Solution**: Check your `.env` file or system environment variables. Ensure the key starts with `sk-...`.

### 1.3 "Pinecone Connection Failed"
- **Symptom**: RAG retrieval fails or startup hangs.
- **Cause**: Network firewall or incorrect index name.
- **Solution**: Verify Pinecone API key and Index name in `application.yml`. Ensure outbound traffic on port 443 is allowed.

### 1.4 "Microphone not detected"
- **Symptom**: Voice chat button doesn't work.
- **Cause**: Browser permission denied.
- **Solution**: Click the "Lock" icon in the browser address bar and allow Microphone access for `localhost`.

## 2. Frequently Asked Questions (FAQ)

**Q: Can I run this offline?**
A: No, Sarva AI requires an internet connection for OpenAI and Pinecone services. 

**Q: How do I add a new Agent?**
A: Create a new class implementing `SarvaAgent`, add it to the `agents` list in `AgentRouter`, and restart.

**Q: Is my data saved?**
A: Currently, chat history is stored in-memory and lost on restart.
