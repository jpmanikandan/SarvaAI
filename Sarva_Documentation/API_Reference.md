# API Reference Documentation

## 1. Overview
Base URL: `http://localhost:8080`
Content-Type: `application/json`

## 2. Chat Endpoints

### 2.1 Send Message (Streamed)
Returns the agent's response as a server-sent event stream (or chunked text).

- **URL**: `/api/chat`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "query": "What is the gold rate in Chennai?",
    "inputLanguage": "English",
    "outputLanguage": "English",
    "history": [
      { "role": "user", "content": "Hi" },
      { "role": "assistant", "content": "Hello! How can I help you?" }
    ]
  }
  ```
- **Response**: `200 OK` (Stream of text)
  ```text
  The
  current
  gold
  rate...
  ```

### 2.2 Send Voice Message
Uploads an audio file for processing.

- **URL**: `/api/voice/chat`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Form Fields**:
  - `file`: The audio file (`.wav`, `.webm`, `.mp3`).
  - `outputLanguage`: (Optional) Target language for the audio response.
- **Response**: `200 OK` (Audio binary stream - `audio/wav`)

## 3. Error Codes
| Code | Description | Solution |
|------|-------------|----------|
| 400  | Bad Request | Check JSON format or missing fields. |
| 401  | Unauthorized| Invalid or missing OpenAI API Key. |
| 500  | Server Error| Internal failure (Check keys/logs). |
