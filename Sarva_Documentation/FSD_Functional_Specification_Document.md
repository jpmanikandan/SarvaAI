# Functional Specification Document (FSD)

## 1. Introduction
**Project**: Sarva AI Core
**Version**: 1.0
**Date**: 2026-02-13

This document details the functional specifications for the Sarva AI system, focusing on the behavior of individual agents and the routing logic.

## 2. General System Behavior

### 2.1 User Input Processing
- **Input**: The system accepts text strings (via chat) or audio files (via voice).
- **Language Detection**: The system automatically detects the input language using `TranslationService`.
- **Normalization**: All non-English queries are translated to English before processing by agents.

### 2.2 Routing Logic
The `AgentRouter` classifies the intent into one of the following categories:
- **LAW** (Legal)
- **FINANCE** (Economics/Gold/Silver)
- **HEALTH** (Wellness/Diet)
- **EDUCATION** (Tutoring/Exams)
- **MATRIMONY** (Relationship Advice)
- **IOT** (Hardware/Smart Home)
- **COMMERCE** (Shopping)
- **DEV** (Coding)
- **GENERAL** (Fallback)

## 3. Agent Specifications

### 3.1 Law Agent (Sarva Lawbotix)
- **Primary Function**: Provide legal advice based on Indian Penal Code (IPC) and other relevant acts.
- **Data Source**: RAG over `law.txt`.
- **Example Inputs**:
    - "Can I be arrested without a warrant?"
    - "What is IPC Section 420?"
- **Output**: Detailed legal explanation with disclaimers.

### 3.2 Finance Agent (Sarva ArthAI)
- **Primary Function**:
    1.  **Investment Advice**: General financial planning.
    2.  **Gold/Silver Rates**: Real-time prices for major TN cities.
    3.  **Price Prediction**: Forecast tomorrow's rates.
- **Tools**: `GoldSilverService`.
- **Example Inputs**:
    - "What is the gold rate in Trichy today?"
    - "Will silver price go up tomorrow?"
- **Output**:
    - Markdown table for prices.
    - Graphical trend analysis (text descriptions like 📈/📉).

### 3.3 Health Agent (Sarva Dhanvantari)
- **Primary Function**: Provide general wellness, diet, and yoga advice.
- **Constraints**: Must always include "Not a doctor" disclaimer.
- **Example Inputs**:
    - "What foods are good for diabetes?"
    - "Suggest a yoga pose for back pain."

### 3.4 Education Agent (Sarva Gurukul)
- **Primary Function**: Tutor for school subjects (Math, Science) and language learning.
- **Persona**: "Miss Nova" for English teaching.
- **Example Inputs**:
    - "Teach me Present Perfect Tense."
    - "Solve this quadratic equation: x^2 + 5x + 6 = 0."

### 3.5 Developer Agent (Sarva DevAI)
- **Primary Function**: Generate and explain code snippets.
- **Supported Languages**: Java, Python, JavaScript, HTML/CSS.
- **Example Inputs**:
    - "Create a Spring Boot REST Controller."
    - "Write a Python script to scrape a website."
- **Output**: Markdown code blocks with syntax highlighting.

### 3.6 IoT Agent (Sarva IoT/Yantra)
- **Primary Function**: Explain IoT concepts and suggest hardware configurations.
- **Example Inputs**:
    - "How do I connect an ESP8266 to WiFi?"

### 3.7 Other Agents
-   **Matrimony Agent**: Advice on compatibility and relationship conflict resolution (based on cultural context).
-   **Commerce Agent**: Product recommendations and comparisons.

## 4. Voice Interaction Specs
### 4.1 Speech-to-Text (STT)
-   **Endpoint**: `/api/voice/chat`
-   **Format**: Accepts `.wav` or `.webm` audio blobs.
-   **Transcription**: Uses OpenAI Whisper model.

### 4.2 Text-to-Speech (TTS)
-   **Response**: The system returns binary audio data.
-   **Voice**: Neutral, clear Indian-accented English (if available) or standard global voice.

## 5. Error Handling
-   **Network Failure**: "I am currently unable to connect to the server. Please check your internet."
-   **Unknown Intent**: "I'm not sure which expert to ask. Could you please rephrase?"
-   **Empty Input**: "Please say something!"
