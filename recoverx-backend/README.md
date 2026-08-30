# RecoverX — Backend

Spring Boot + PostgreSQL implementation of the Track 03 revenue recovery
pipeline: **Detect → Diagnose → Decide → Gate → Execute → Audit**, with a
bundled dashboard frontend served from the same app.

Nothing here talks to a real payment gateway. Amounts and failures are
synthetic; `RecoveryExecutionService.attempt()` is the one place to swap in a
real Razorpay test-mode API call later — everything upstream (diagnosis,
policy gate, orchestration, audit) is already written against that
abstraction and won't need to change.

## Requirements

- Java 17+
- Maven 3.9+ (or use the `mvnw` wrapper if you add one)
- PostgreSQL 14+ running locally

## 1. Create the database

```bash
createdb recoverx
# or, from psql:
# CREATE DATABASE recoverx;
```

Then edit `src/main/resources/application.properties` if your username,
password, host, or port differ from the defaults (`postgres` / `postgres` on
`localhost:5432`).

Schema is created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`) —
no manual migration needed for this demo.

## 2. Run it

```bash
mvn spring-boot:run
```

The API and the dashboard are both served from **http://localhost:8080**.
Open that URL in a browser — no separate frontend server needed.

## API reference

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/batch?count=16` | Generates a fresh synthetic batch (replaces the previous one) |
| `GET`  | `/api/transactions` | Lists all transactions in the current batch |
| `POST` | `/api/recovery/process/{externalId}` | Runs one payment through the full pipeline; returns the AI decision, the audit lines produced, and the updated transaction |
| `GET`  | `/api/transactions/{externalId}/audit` | Full persisted audit trail for one payment |
| `GET`  | `/api/metrics` | Revenue at risk / recovered / recovery rate / case counts |
| `DELETE` | `/api/reset` | Clears all transactions and audit logs |

Example:

```bash
curl -X POST 'http://localhost:8080/api/batch?count=16'
curl -X POST 'http://localhost:8080/api/recovery/process/PAY_1101'
curl 'http://localhost:8080/api/metrics'
```

## How the safety rules are enforced

The AI agent (`DiagnosisService`) only ever returns a structured `Decision` —
diagnosis, confidence, recommended action, priority. It never touches the
database or triggers an action directly. Every decision passes through
`PolicyGateService`, a deterministic set of rules (max 2 retries, unrecognized
failures escalate immediately, low-risk actions like requesting a card update
are always allowed once) before `RecoveryOrchestratorService` is permitted to
execute anything. That separation — "AI recommends, policy decides" — is what
makes every action explainable, bounded, and gated, and it's why the demo
video line ("the model can recommend an action, but it cannot override our
execution policy") is literally true of this code, not just narration.

## What's next if you extend this

- **Real AI**: replace the body of `DiagnosisService.diagnose()` with a call
  to the Anthropic API (or any model), keeping the `Decision` record shape.
- **Real gateway**: replace `RecoveryExecutionService.attempt()` with a
  Razorpay test-mode API call.
- **Auth / multi-merchant**: add a `merchant_id` column to `Transaction` and
  scope every query by it.
- **Production schema management**: switch `ddl-auto` to `validate` and
  introduce Flyway or Liquibase migrations.
