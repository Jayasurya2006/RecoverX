# 🚀 RecoverX

> **Intelligent payment recovery for failed transactions.**

RecoverX is a full-stack payment recovery platform designed to **detect failed transactions, diagnose failure reasons, recommend controlled recovery actions, enforce server-side guardrails, and maintain a complete audit trail** for every recovery decision.

The system follows a structured recovery pipeline:

**Detect → Diagnose → Decide → Gate → Execute → Audit**

🌐 **Live Demo:** https://recoverx-09cg.onrender.com/

💻 **GitHub Repository:** https://github.com/Jayasurya2006/RecoverX

---

## 🎯 Problem Statement

Failed payments can result in lost revenue and require manual investigation.

RecoverX demonstrates an automated recovery workflow that evaluates failed transactions and determines whether they should be:

* 🔄 Retried
* 💳 Sent for payment-method update
* 👤 Escalated for human review
* 🚫 Blocked by recovery guardrails

The goal is to make payment recovery **controlled, explainable, and traceable** rather than blindly retrying failed transactions.

---

## ✨ Key Features

### 🔍 Payment Failure Detection

Identifies failed transactions and determines which payments require recovery processing.

### 🧠 Intelligent Failure Diagnosis

Analyzes payment failure reasons and produces a diagnosis with an associated confidence score.

Example:

```text
Failure Reason: NETWORK_ISSUE
Diagnosis: temporary_failure
Confidence: 0.95
```

### 🎯 Recovery Decision Engine

Based on the diagnosed failure, the system recommends an appropriate recovery action.

Examples:

```text
CARD_EXPIRED
      ↓
REQUEST_UPDATE

NETWORK_ISSUE
      ↓
RETRY

INSUFFICIENT_FUNDS
      ↓
ESCALATE
```

### 🛡️ Recovery Guardrails

Recovery actions are not executed blindly.

The system applies deterministic server-side rules before execution, including:

* Maximum **2 recovery attempts** per payment
* Low-confidence decisions can be blocked
* Unfamiliar failure patterns can be routed to human review
* Recovery actions are bounded by the original transaction amount

### 👤 Human Review

Transactions that cannot be safely recovered automatically can be escalated for human review.

### 🔄 Controlled Recovery Execution

Approved recovery actions are executed through controlled recovery flows such as:

* Payment retry
* Payment-method update request
* Escalation

### 📜 Complete Audit Trail

Every recovery stage is recorded:

```text
DETECT
  ↓
DIAGNOSE
  ↓
DECIDE
  ↓
GATE
  ↓
EXECUTE
  ↓
AUDIT
```

This provides traceability for how and why a recovery decision was made.

### 📊 Recovery Metrics

The dashboard tracks operational metrics including:

* Revenue at Risk
* Revenue Recovered
* Recovery Rate
* Successful Recoveries
* Unrecovered Transactions
* Human Review Cases

### ⚡ Live Recovery Activity

The dashboard provides an event stream showing the recovery lifecycle of individual transactions in real time.

Example:

```text
PAY_1306
DETECT     → Payment failed
DIAGNOSE   → temporary_failure
DECIDE     → RETRY
GATE       → APPROVED
EXECUTE    → Retry #1 FAILED
EXECUTE    → Retry #2 SUCCESS
AUDIT      → RECOVERED
```

---

## 🤖 AI / Intelligent Diagnosis

RecoverX includes an **AI-based diagnosis component** intended to assist with identifying payment failure patterns and recommending suitable recovery actions.

The AI component produces a diagnosis and confidence score, which can then be evaluated by the recovery guardrails before an action is executed.

### AI + Policy Architecture

RecoverX follows a **"AI recommends, policy controls"** approach.

```text
Failed Payment
      │
      ▼
 AI Diagnosis
      │
      ├── Diagnosis
      └── Confidence
      │
      ▼
Deterministic Guardrails
      │
      ├── APPROVED
      │
      └── BLOCKED
             │
             ▼
       Human Review
```

> **Production Note:** The AI detection/diagnosis functionality depends on a paid third-party service and is currently disabled in the production deployment. The recovery architecture and integration point are implemented in the project.

This separation ensures that AI recommendations do not independently execute financial recovery actions.

---

## 🛡️ Recovery Guardrails

RecoverX is designed around controlled recovery rather than unrestricted automated retries.

### Retry Protection

Each payment has a maximum of **2 recovery attempts** before automatic escalation.

### AI Recommendations vs. Policy

AI can recommend a recovery action, but the action must pass deterministic server-side validation before execution.

### Low-Confidence Handling

Low-confidence or unfamiliar diagnoses can be blocked and routed to human review.

### Amount Protection

Recovery actions are constrained by the original transaction amount.

---

## 🏗️ Recovery Workflow

```text
┌───────────────┐
│    DETECT     │
│ Failed Payment│
└───────┬───────┘
        │
        ▼
┌───────────────┐
│   DIAGNOSE    │
│ Failure Reason│
│ + Confidence  │
└───────┬───────┘
        │
        ▼
┌───────────────┐
│    DECIDE     │
│ Recovery      │
│ Recommendation│
└───────┬───────┘
        │
        ▼
┌───────────────┐
│     GATE      │
│ Policy / Risk │
│ Validation    │
└───────┬───────┘
        │
    ┌───┴────┐
    │        │
 APPROVED  BLOCKED
    │        │
    ▼        ▼
 EXECUTE   HUMAN
    │      REVIEW
    └───┬────┘
        │
        ▼
┌───────────────┐
│     AUDIT     │
│ Final Outcome │
└───────────────┘
```

---

## 🧩 Example Recovery Scenarios

### 💳 Expired Card

```text
Payment
PAY_1302
₹4,620

Failure:
CARD_EXPIRED

Diagnosis:
card_expired
Confidence: 0.99

Decision:
REQUEST_UPDATE

Gate:
APPROVED

Execution:
Payment-method update request
→ Payment retried
→ SUCCESS

Final:
RECOVERED
```

### 🔄 Temporary Network Failure

```text
Payment
PAY_1306
₹2,460

Failure:
NETWORK_ISSUE

Diagnosis:
temporary_failure
Confidence: 0.95

Decision:
RETRY

Gate:
APPROVED

Execution:
Retry #1 → FAILED
Retry #2 → SUCCESS

Final:
RECOVERED
```

### 👤 Human Review

```text
Payment
PAY_1305
₹1,170

Failure:
INSUFFICIENT_FUNDS

Decision:
ESCALATE

Gate:
BLOCKED

Final:
HUMAN REVIEW
```

---

## 🛠️ Technology Stack

| Layer                | Technologies               |
| -------------------- | -------------------------- |
| **Backend**          | Java, Spring Boot          |
| **API**              | REST APIs                  |
| **Persistence**      | Spring Data JPA            |
| **ORM**              | Hibernate                  |
| **Database**         | PostgreSQL                 |
| **Build Tool**       | Maven                      |
| **Frontend**         | HTML, CSS, JavaScript      |
| **AI Component**     | AI-based failure diagnosis |
| **Containerization** | Docker                     |
| **Cloud Deployment** | Render                     |

---

## 🏛️ Backend Architecture

RecoverX follows a layered backend architecture:

```text
                REST Request
                     │
                     ▼
             ┌───────────────┐
             │   Controller  │
             └───────┬───────┘
                     │
                     ▼
             ┌───────────────┐
             │    Service    │
             │ Recovery Logic│
             └───────┬───────┘
                     │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
 ┌────────────────┐    ┌─────────────────┐
 │ AI / Diagnosis │    │ Recovery Policy │
 └────────────────┘    └─────────────────┘
          │                     │
          └──────────┬──────────┘
                     ▼
             ┌───────────────┐
             │   Repository  │
             │ Spring Data   │
             │     JPA       │
             └───────┬───────┘
                     │
                     ▼
             ┌───────────────┐
             │  PostgreSQL    │
             └───────────────┘
```

---

## 📊 Dashboard

The RecoverX dashboard provides visibility into the payment recovery pipeline.

### Payment Operations

The dashboard displays:

* Payment ID
* Transaction amount
* Payment status
* Failure reason
* Recovery status
* Recovery attempts

### Recovery Metrics

```text
Revenue at Risk
Revenue Recovered
Recovery Rate
Successful Recoveries
Unrecovered
Human Review
```

### Recovery Activity

A live event stream provides visibility into each stage of the recovery lifecycle.

---

## 📂 Project Structure

```text
RecoverX/
│
├── recoverx-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── recoverx/
│   │   │   │
│   │   │   └── resources/
│   │   │
│   │   └── test/
│   │
│   ├── Dockerfile
│   └── pom.xml
│
├── README.md
└── ...
```

---

## 🔌 REST API

The Spring Boot backend exposes REST endpoints for payment recovery operations.

Example recovery flow:

```text
/api/recovery/process/{paymentId}
```

The backend processes the transaction through the recovery workflow and records the resulting state.

> Refer to the backend source code for the complete API implementation and available endpoints.

---

## ⚙️ Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/Jayasurya2006/RecoverX.git
cd RecoverX
```

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```text
recoverx
```

Configure the required environment variables:

```properties
DB_URL=jdbc:postgresql://localhost:5432/recoverx
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

> ⚠️ Never commit real database credentials, API keys, or other secrets to GitHub.

### 3. Run the application

```bash
cd recoverx-backend
mvn spring-boot:run
```

The application will be available at:

```text
http://localhost:8080
```

---

## 🐳 Docker

RecoverX is containerized using Docker.

### Build

```bash
docker build -t recoverx .
```

### Run

```bash
docker run -p 8080:8080 recoverx
```

The application will be available at:

```text
http://localhost:8080
```

---

## ☁️ Deployment

RecoverX is deployed using **Docker and Render**.

### Deployment Flow

```text
GitHub
   │
   ▼
Docker Build
   │
   ▼
Render
   │
   ▼
Spring Boot
   │
   ▼
PostgreSQL
```

### Production Configuration

Production secrets and database credentials are supplied through environment variables rather than being committed to the repository.

### 🌐 Live Application

**RecoverX:**
https://recoverx-09cg.onrender.com/

> The application may take some time to respond after inactivity because the deployment uses a free cloud instance.

---

## 🔒 Security & Reliability

RecoverX applies several safeguards around automated recovery:

* 🔐 Sensitive credentials are stored outside source control.
* 🛡️ Recovery actions pass server-side validation.
* 🔄 Retry attempts are limited.
* 👤 Uncertain cases can be escalated for human review.
* 📜 Recovery decisions are recorded for traceability.
* 💰 Recovery actions are constrained by the original transaction amount.

---

## 🎓 What I Learned

Building RecoverX provided hands-on experience with:

* Designing backend systems using **Java and Spring Boot**
* Developing **RESTful APIs**
* Building layered application architecture
* Working with **Spring Data JPA and Hibernate**
* Integrating **PostgreSQL**
* Implementing controlled transaction-recovery workflows
* Designing deterministic **policy and guardrail systems**
* Integrating an AI-based decision-support component
* Containerizing applications with **Docker**
* Deploying backend applications to **Render**
* Managing production environment variables
* Debugging real-world database and deployment issues
* Designing systems where **AI recommendations are separated from final policy enforcement**

---

## 🔮 Future Enhancements

Potential future improvements include:

* 🤖 Re-enable AI diagnosis using a cost-effective or self-hosted model
* 📊 Advanced recovery analytics
* 🔔 Automated customer notifications
* 📈 Recovery-performance dashboards
* 🧠 Improved failure-pattern detection
* 🔐 Role-based access control
* 🏦 Integration with real payment-provider webhooks
* 📍 More sophisticated transaction-risk analysis

---

## 👨‍💻 Developer

### Jayasurya P

**BE Computer Science & Engineering (IoT)**

🔗 **GitHub:**
https://github.com/Jayasurya2006

🔗 **Portfolio:**
https://jayasurya2006.github.io/Jayasurya-Portfolio/

---

## ⭐ Project Status

🟢 **Live and deployed**

RecoverX is currently deployed as a full-stack payment recovery application.

The core recovery workflow, dashboard, database integration, and deployment infrastructure are available in the project.

The AI diagnosis component is implemented as part of the architecture but is currently disabled in production because of its paid third-party service dependency.

---

