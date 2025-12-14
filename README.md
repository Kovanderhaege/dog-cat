# Dog / Cat Image Classifier — Full-Stack & ML Project

This repository is a full-stack and machine learning project designed to demonstrate
the complete lifecycle of an image classification system, from data preparation and
model training to production-grade inference integrated into a backend service.

The application allows users to upload images, store them, and trigger an AI-based
prediction that classifies images as `dog`, `cat`, or `other`.

---

## Project Scope

This project intentionally avoids external AI APIs or black-box services.
The model is trained locally, exported, and deployed as part of the backend.

The focus is on:
- clean frontend/backend separation
- reproducible ML training
- deterministic inference in production
- explicit model lifecycle management

---

## Architecture Overview

High-level pipeline:

Image (upload)
→ Backend storage
→ ONNX Runtime inference (Java)
→ Softmax + confidence thresholding
→ Persisted prediction result
→ Frontend display


---

## Machine Learning Pipeline

- Task: image classification (`dog` / `cat` / `other`)
- Model: ResNet18 (fine-tuned)
- Training framework: PyTorch
- Inference format: ONNX
- Runtime: ONNX Runtime (Java)

### Dataset

- Dog / Cat images from public datasets
- `other` class built from heterogeneous image sources
- Dataset split: train / validation / test

### Training

- Transfer learning from ImageNet weights
- Cross-entropy loss
- Validation using macro F1-score
- Explicit overfitting control

### Inference Design

- Model exported to ONNX
- Numerical parity validated (PyTorch vs ONNX)
- Confidence-based rejection using a calibrated threshold (τ)
- Threshold applied outside the model as a business rule

---

## Backend Features

- Image upload and storage
- Prediction lifecycle management (`PENDING`, `DONE`, `ERROR`)
- ONNX-based image inference (CPU)
- Deterministic prediction with confidence score
- Persistent prediction results

---

## Frontend Features

- Image upload UI
- Image gallery
- Prediction trigger per image
- Status and confidence display

---

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL (Docker)
- ONNX Runtime (Java)

### Frontend

- Angular (standalone components)
- TypeScript
- RxJS
- Angular Router

---

## Model Artifacts

The following inference artifacts are versioned in the repository:

- `model_v0.onnx`
- `labels.json`
- `preprocess.json`
- `threshold.json`

Training checkpoints and datasets are intentionally excluded.

---

## What This Project Is / Is Not

This project is:

- a full-stack application with real ML inference
- a demonstration of end-to-end ML integration
- a training and portfolio project

This project is not:

- a production SaaS
- a generic AutoML pipeline
- a wrapper around third-party AI APIs
