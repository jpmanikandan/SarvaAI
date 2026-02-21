# Requirements Understanding Document (RUD)

## 1. Introduction
**Project Name**: Sarva AI Core
**Document Version**: 1.0
**Date**: 2026-02-13

## 2. Project Overview
Sarva AI is a "Universal Expert" conversational AI platform designed to democratize access to expert knowledge across multiple domains. It unifies 11 specialized products into a single core platform, allowing users to interact via text or voice in their preferred language.

## 3. Scope
The scope includes the implementation of a Spring Boot-based backend, a web-based frontend, and the integration of various AI agents to handle specific domains.

### 3.1 In-Scope
- **Unified Interface**: Single chat window for all queries.
- **Multi-Domain Agents**:
  - Law (Sarva Lawbotix)
  - Finance (Sarva ArthAI)
  - Health (Sarva Dhanvantari)
  - Education (Sarva Gurukul)
  - Matrimony (Sarva Mangalya)
  - IoT (Sarva IoT/Yantra)
  - Commerce (Sarva Bazaar)
  - Development (Sarva DevAI)
  - Language (Sarva Bhasha)
- **Multi-Modal Interaction**: Text-to-Text, Voice-to-Voice (STT/TTS).
- **Multi-Language Support**: Real-time translation for input and output.
- **RAG Integration**: Knowledge retrieval from domain-specific text files.

### 3.2 Out-of-Scope
- Physical hardware manufacturing for Sarva Yantra (only simulation/logic is in-scope).
- Medical diagnosis (Health agent provides guidance only, not prescriptions).
- Real-time stock market transactions (Finance agent provides data/predictions only).

## 4. User Requirements

### 4.1 Functional Requirements
| ID | Requirement | Description |
|----|-------------|-------------|
| FR-01 | Domain Routing | System must automatically route queries to the correct specialist agent based on intent. |
| FR-02 | Voice Interaction | Users must be able to speak queries and hear responses. |
| FR-03 | Code Generation | DevAgent must generate formatted, syntax-highlighted code. |
| FR-04 | Price Prediction | FinanceAgent must predict gold/silver rates for the next day. |
| FR-05 | Language Switching | Users must be able to switch response language dynamically. |

### 4.2 Non-Functional Requirements
| ID | Requirement | Description |
|----|-------------|-------------|
| NFR-01 | Latency | Text responses should start streaming within 2 seconds. |
| NFR-02 | Availability | System should be available 99.9% of the time during business hours. |
| NFR-03 | Scalability | Architecture must support adding new agents without core refactoring. |
| NFR-04 | Security | OpenAI API keys must be secured; no PII should be logged permanently. |

## 5. Assumptions & Constraints
- **Assumptions**: Users have a stable internet connection for API calls.
- **Constraints**: Project must run on Java 17 and Spring Boot 3.3.5.

## 6. Acceptance Criteria
- System successfully categorizes queries for all 11 domains.
- Voice/Text modes function without critical errors.
- Application passes all defined unit tests.
