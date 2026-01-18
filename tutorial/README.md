# 📘 Tutorial Overview

This `tutorial/` directory contains **step-by-step explanations** 
of the backend setup and core concepts used in this project.

The goal of this tutorial is to explain **what is happening**, **why it is needed**, 
and **how the pieces work together**, in a way that is understandable even for someone new to Spring Boot, Docker, 
or backend development.

---

## 📂 Tutorial Structure

Each important configuration or concept is described in a **separate file**, focused on a single topic.

### Currently planed files:

1. **`application-yaml.md`**
    Explains how Spring Boot is configured:
    - database connection
    - JPA / Hibernate settings
    - server configuration
   
2. **`docker-compose.md`**
   Explains how Docker Compose is used to run infrastructure services:
   - PostgreSQL database
   - pgAdmin
   - volumes, ports, and health checks
   
---

## 🎯 Why this approach?

Instead of relying only on commit messages or implicit knowledge, this tutorial:
- documents architectural decisions
- helps onboard new developers
- serves as a learning resource
- makes the project easier to understand and maintain

---

## 🚀 What comes next?

Future tutorial files will cover:
- User entity and database model
- Registration and login flow
- Password hashing
- JWT authentication (access & refresh tokens)
- WebSocket messaging

---

Each new step in the project will be accompanied by a corresponding tutorial file.

