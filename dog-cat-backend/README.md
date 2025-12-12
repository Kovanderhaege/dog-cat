
## 📁 `backend/README.md`

```md
# Backend — Spring Boot API

Spring Boot REST API responsible for image storage, persistence,
and data exposure to the Angular frontend.

---

## Responsibilities

- Receive image uploads (multipart)
- Store image files on disk
- Persist image metadata in PostgreSQL
- Expose REST endpoints
- Serve image files via HTTP

---

## Core Model

**Entity**
- `id : UUID`
- `filePath : String`
- `status : enum (PENDING, DONE, …)`
- `predictedLabel : String`
- `confidence : Double`
- `createdAt : Instant`

**DTO (API Contract)**

```java
ImagePredictionDto(
  UUID id,
  String filePath,
  String status,
  String predictedLabel,
  Double confidence,
  Instant createdAt
)
