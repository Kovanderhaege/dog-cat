## 📁 `frontend/README.md`

```md
# Frontend — Angular Application

Angular application responsible for the user interface.
It consumes the Spring Boot REST API and displays images and their state.

---

## Architecture

- Standalone Angular components
- Angular Router for navigation
- Dedicated services for HTTP calls
- UI state handled exclusively by components

---

## Routes

- `/upload` → image upload page
- `/gallery` → image gallery

---

## Core Service

**ImageService**
- `upload(file)`
- `list()`
- `predict(id)`
- `imageUrl(id)`

All HTTP calls return **Observables**.
Requests are executed only on `subscribe()`.

---

## Main Components

**UploadComponent**
- File selection
- Image upload
- Display of returned image ID

**GalleryComponent**
- Load images on initialization
- Display gallery
- Trigger prediction based on status

---

## Run

```bash
npm install
ng serve
