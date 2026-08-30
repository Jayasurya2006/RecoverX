RecoverX — AI-Powered Payment Recovery System

> **Intelligent payment-failure detection, diagnosis, policy-controlled recovery, and auditability — built with Spring Boot, PostgreSQL, and AI.**

RecoverX is an AI-powered payment recovery system designed to automatically handle failed transactions while maintaining **safety, explainability, and human oversight**.

Instead of treating every failed payment the same way, RecoverX analyzes the failure reason, determines the appropriate recovery strategy, passes the decision through a policy gate, executes only approved actions, and records the complete recovery journey in an audit trail.



 Key Features

AI-powered failure diagnosis

  * Classifies payment failures such as insufficient funds, expired cards, and temporary failures.
  * Produces a diagnosis with a confidence score and recommended recovery action.

 Policy Gate

  * Prevents unsafe or low-confidence automated actions.
  * Routes uncertain decisions to human review.

* Automatic payment recovery

  * Retries temporary failures automatically.
  * Supports multiple retry attempts with a strict retry limit.

* Payment-method recovery

  * Detects expired-card failures.
  * Requests a payment-method update before retrying.

*  Human escalation

  * Low-confidence or unsuitable cases are automatically escalated instead of being blindly retried.

*  Deterministic AI fallback

  * If the AI service is unavailable or quota is exhausted, RecoverX falls back to predefined recovery rules.

* Audit trail

  * Records every important stage of the recovery lifecycle:
    DETECT → DIAGNOSE → DECIDE → GATE → EXECUTE → AUDIT

* Recovery dashboard

  * Provides a visual interface for monitoring transaction processing and recovery results.

* Persistent transaction storage

  * Uses PostgreSQL with Spring Data JPA for transaction and recovery-state persistence.


#System Architecture

                    ┌──────────────────────┐
                    │   Recovery Dashboard  │
                    │     HTML / CSS / JS   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Recovery REST API   │
                    │     Spring Boot       │
                    └──────────┬───────────┘
                               │
                               ▼
                ┌──────────────────────────────┐
                │   Recovery Orchestrator      │
                │                              │
                │ Detect → Diagnose → Decide   │
                │ → Gate → Execute → Audit     │
                └──────────────┬───────────────┘
                               │
              ┌────────────────┼─────────────────┐
              ▼                ▼                 ▼
       ┌────────────┐   ┌─────────────┐   ┌─────────────┐
       │ Spring AI  │   │ Policy Gate │   │ PostgreSQL  │
       │ Diagnosis  │   │   Safety    │   │ Persistence │
       └────────────┘   └─────────────┘   └─────────────┘
              │
              ▼
       ┌────────────────┐
       │ Deterministic  │
       │ Fallback Rules │
       └────────────────┘



# Recovery Workflow

Each failed transaction follows a controlled recovery pipeline:

Payment Failure
      │
      ▼
   DETECT
      │
      ▼
  DIAGNOSE
      │
      ▼
    DECIDE
      │
      ▼
 Policy Gate
   ┌──┴───────┐
   │          │
APPROVED    BLOCKED
   │           │
   ▼           ▼
EXECUTE    Human Review
   │
   ▼
  AUDIT


# Example

A temporary bank timeout can be handled differently from an expired card:


BANK_TIMEOUT
     ↓
temporary_failure
     ↓
RETRY
     ↓
Policy Gate → APPROVED
     ↓
Retry #1
     ↓
SUCCESS
     ↓
RECOVERED


For an expired card:

CARD_EXPIRED
     ↓
card_expired
     ↓
REQUEST_UPDATE
     ↓
Policy Gate → APPROVED
     ↓
Customer updates card
     ↓
Automatic retry
     ↓
SUCCESS
     ↓
RECOVERED


For insufficient funds:

INSUFFICIENT_FUNDS
     ↓
insufficient_funds
     ↓
ESCALATE
     ↓
Human Review



# Safety-First Recovery

RecoverX does not allow the AI model to directly execute payment actions.

The AI provides a diagnosis and recommendation, but the **Policy Gate acts as the safety boundary**.

For retry-based actions, RecoverX enforces a maximum of **2 attempts**.

AI Recommendation
       ↓
   Policy Gate
       ↓
 ┌─────┴─────┐
 │           │
Approve     Block
 │           │
 ▼           ▼
Execute    Escalate


This separation makes the system easier to reason about and prevents uncontrolled AI-driven actions.



# AI + Deterministic Fallback

RecoverX uses AI for intelligent diagnosis, but the system is not completely dependent on the external AI service.

If the AI API is unavailable or the account has exhausted its API quota:

AI Request
    │
    ├── Success ───────► AI Diagnosis
    │
    └── Failure ───────► Deterministic Rules
                              │
                              ▼
                       Recovery Decision

This allows the recovery pipeline to continue operating even when the external AI dependency is unavailable.


# Auditability

Every transaction produces a structured recovery trail.

Example:

DETECT   → Payment failed
DIAGNOSE → temporary_failure / confidence=0.95
DECIDE   → RETRY
GATE     → APPROVED
EXECUTE  → Retry #1
EXECUTE  → SUCCESS
AUDIT    → Final status: RECOVERED

This makes the recovery process transparent and easier to debug, monitor, and demonstrate during incident analysis.


#Tested Recovery Scenarios

RecoverX has been tested against multiple payment-failure scenarios:

| Failure Type                     | Diagnosis              | Action           | Result             |
| -------------------------------- | ---------------------- | ---------------- | ------------------ |
| `CARD_EXPIRED`                   | `card_expired`         | `REQUEST_UPDATE` | ✅ Recovered       |
| `BANK_TIMEOUT`                   | `temporary_failure`    | `RETRY`          | ✅ Recovered       |
| `NETWORK_ISSUE`                  | `temporary_failure`    | `RETRY`          | ✅ Recovered       |
| `INSUFFICIENT_FUNDS`             | `insufficient_funds`   | `ESCALATE`       | 👨‍💼 Human Review    |
| Unknown / low-confidence failure | `unrecognized_failure` | `ESCALATE`       | 👨‍💼 Human Review    |

#Retry Safety Test

Temporary failures are limited to:

Maximum retries = 2


The system stops retry execution once the configured retry limit is reached.


# Tech Stack

#nBackend

* Java 17
* Spring Boot 3.4
* Spring Web
* Spring Data JPA
* Hibernate
* Spring AI
* Maven

# Database

* PostgreSQL

# AI

* OpenAI API
* Spring AI
* Deterministic fallback rules

# Frontend

* HTML
* CSS
* JavaScript

# Development Tools

* Git
* GitHub
* VS Code
* IntelliJ IDEA compatible project structure



# Project Structure

recoverx-backend/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/recoverx/
│       │       ├── controller/
│       │       ├── dto/
│       │       ├── model/
│       │       ├── repository/
│       │       └── service/
│       │
│       └── resources/
│           ├── static/
│           │   ├── index.html
│           │   └── logo.png
│           │
│           └── application.properties
│
├── pom.xml
├── README.md
└── .gitignore

# Setup & Installation

# 1. Clone the repository


git clone https://github.com/Jayasurya2006/RecoverX.git
cd RecoverX/recoverx-backend


# 2. Configure PostgreSQL

Create a PostgreSQL database:

sql
CREATE DATABASE recoverx;


Configure the required environment variables:

DB_PASSWORD=your_postgresql_password
OPENAI_API_KEY=your_openai_api_key

RecoverX does **not** require API keys or database passwords to be hardcoded into the source code.

# 3. Run the application

Using Maven:

bash
mvn spring-boot:run


Or use the Maven wrapper / IDE if configured.

### 4. Open the dashboard


http://localhost:8080



# Configuration

The application reads sensitive configuration from environment variables:

```properties
spring.datasource.password=${DB_PASSWORD}
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

This keeps credentials outside the Git repository.

---

##  Example Recovery Result

A successful temporary-failure recovery may look like:

```text
PAY_1112
DETECT   → BANK_TIMEOUT
DIAGNOSE → temporary_failure (0.95)
DECIDE   → RETRY
GATE     → APPROVED
EXECUTE  → Retry #1 → FAILED
EXECUTE  → Retry #2 → SUCCESS
AUDIT    → Final status: RECOVERED
```

---

##  Design Goals

RecoverX was designed around four principles:

### 1. Intelligent

Use AI to understand payment failures instead of relying only on static conditions.

### 2. Safe

AI recommendations must pass through a policy gate before execution.

### 3. Resilient

External AI failures should not completely stop the recovery workflow.

### 4. Auditable

Every important decision and execution step should be traceable.

---

##  Future Improvements

Potential extensions include:

* Real payment gateway integration
* Real-time transaction monitoring
* Admin authentication and role-based access
* Advanced recovery analytics
* Notification service for customers
* Configurable retry policies
* Model performance monitoring
* Distributed processing using Kafka
* Docker deployment
* Cloud deployment
* Automated unit and integration testing

---

## 👨‍💻 Developer

**Jayasurya P.**

BE Computer Science & Engineering (IoT)

Interested in:

* Java & Spring Boot
* Backend Development
* AI-powered applications
* IoT systems
* Full-stack development

---

## 📌 Project Status

**RecoverX — Working Prototype / Portfolio Project**

The current implementation demonstrates an end-to-end AI-assisted payment recovery workflow with automated recovery, safety controls, fallback handling, persistence, and auditability.
