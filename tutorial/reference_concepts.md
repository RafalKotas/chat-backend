# 📚 Reference Concepts

This document provides concise explanations of key technologies and terms used in the project. Each section gives 
a short, practical description of what the concept is and what role it plays in our application.

## 🟦 Spring Boot
Spring Boot is a Java framework designed to make backend development faster and easier.

### What it does:
- Automatically configures your application ("auto-configuration")
- Provides embedded severs (Tomcat) so you can run apps without installation
- Organizes the application into layered architecture
- Manages dependencies via starters (e.g., `spring-boot-starter-web`)
- Simplifies creating REST APIs and microservices

### Why we use it:
- Reduces boilerplate
- Convenient for building modern backend applications
- Works perfectly with Spring Security, JPA, and WebSockets

### Spring Boot vs Spring Framework

- **Spring Framework** is the core foundation that provides features such as:
  - dependency injection,
  - transaction management,
  - MVC architecture,
  - security integration

  It gives tou a lot of flexibility, but requires **explicit configuration** (XML or Java-based).

- **Spring Boot** is built on top of Spring Framework and focuses on **convention over configuration**.

In practice:
- With Spring Framework, you manually configure many components.
- With Spring Boot, most configuration is done automatically based on:
  - dependencies on the classpath,
  - sensible defaults,
  - `application.yaml` properties.

Spring Boot does **not replace** Spring Framework - it **simplifies using it**

---

## 🟥 Apache Tomcat
Apache Tomcat is a web server and servlet container used by Spring Boot as the default embedded server.

### What it does:
- Listens for incoming HTTP requests
- Routes requests to Spring controllers
- Manages HTTP connections and threads
- Hosts WebSocket connections

### Why we use it:
- Embedded directly in Spring Boot (no separate installation)
- Production-proven and stable
- Supports both REST APIs and WebSockets

## 🟫 Hibernate 
Hibernate is an ORM (Object-Relational Mapping) library used by Spring Data JPA.

### What it does:
- Maps Java classes to database tables
- Automatically generates SQL queries
- Handles transactions, caching, lazy loading
- Creates/updates tables based on entity classes

### Why we use it:
- Removes the need to write SQL manually for basic operations
- Ensures consistency between Java models and the database
- Simplifies CRUD logic

### Why we rarely use it directly:
- Hibernate/JPA sit on top of JDBC and abstract away low-level calls
- JDBC is used internally by Hibernate to communicate with the database

---

## 🔵 WebSockets
WebSockets provide a persistent, bidirectional communication channel between client and server.

### What it does:
- Keeps a single connection open between frontend and backend
- Allows the server to push messages to clients in real time
- Eliminates the need for constant polling

### Why we use it:
- Required for real-time chat messaging
- Enables instant message delivery
- Scales well for chat and notification systems

---

## 🟩 JDBC
JDBC (Java Database Connectivity) is the low-level API for connecting to relational databases.

### What it does:
- Opens connections to a database (PostgreSQL in our case)
- Allows execution of SQL statements
- Transfers query results back to Java

## Why we rarely use it directly:
- Hibernate/JPA sit on top of JDBC and abstract away low-level calls
- JDBC is used internally by Hibernate to communicate with the database

---

## 🐘 PosgreSQL
PostgreSQL is the relational database used by our application.

### What it does:
- Stores users, contacts, messages, and chat structures
- Ensures transactional consistency
- Provides indexing, constraints, SQL queries

### Why we use it:
- Reliable, stable, open-source database
- Works extremely well with Hibernate
- Supports JSON, relations, and advanced SQL features

---

## 🟪 .env Files
A `.env` file stores environment variables (like credentials) outside of source code.

### What it does:
- Holds sensitive configuration values
- Prevents secrets form being committed to Git
- Supplies variables to Docker Compose or Spring Boot

### Typical examples:
```
DB_NAME=chatdb
DB_USER=chatuser
DB_PASSWORD=superSecretPassword
```

### Why we use it:
- Keeps credentials safe
- Allows different configurations for local, test, production
- Works well with Docker Compose and Spring profiles

---

More reference sections will be added as the project grows.