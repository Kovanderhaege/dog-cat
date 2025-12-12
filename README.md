# Dog / Cat Image Classifier — Full-Stack Training Project

This repository is a full-stack training project built to practice clean
frontend/backend architecture using a concrete use case: image upload and
classification (dog / cat / other).

---

## Project Structure (Monorepo)

- The backend exposes a REST API and serves image files.
- The frontend consumes the API and handles the UI.
- Both applications are independent and communicate via HTTP.

---

## Current Features

- Image upload
- File storage on disk (backend)
- Metadata persistence in PostgreSQL
- Image gallery in the frontend
- Image status management (`PENDING`, `DONE`, …)
- Prediction trigger (placeholder)

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot, Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL (Docker)

**Frontend**
- Angular (standalone components)
- TypeScript, RxJS
- Angular Router

---
