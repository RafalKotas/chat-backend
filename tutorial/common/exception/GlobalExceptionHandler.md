# 3.9 GlobalExceptionHandler - Centralized Error Handling

This document explains the architectural decision using a **global exception handler** and describes how it works 
within the application.

---

## 🎯 Purpose of GlobalExceptionHandler
A `@ControllerAdvice` class allows us to intercept exceptions thrown anywhere in the application and convert them into 
**consistent, readable, and safe JSON responses.**

Instead of returning raw stack traces or exposing sensitive internals, we:
- Map **domain-specific exceptions** → to meaningful HTTP responses
- Keep controllers clean (no `try/catch` needed)
- Centralize all error formating in one place
- Ensure consistent structure for frontend consumption

---

## 📦 Location
```
com.chatapp.chat.common.exception.GlobalExceptionHandler
```

---

## 🛠 How It Works

Spring Boot detects `@ControllerAdvice` and registers handlers for the entire application.

Whenever a controller or service throws an exception, Spring checks:

1. Do we have a matching `@ExceptionHandler` method?
2. If yes → use it to build HTTP response.
3. If not → fallback to the general `Exception.class` handler.

---

## 🧩 Structure of Returned JSON

Each handled error response follows this format:

```json
{
"timestamp": "2026-01-19T22:15:32.102Z",
"status": 400,
"error": "Bad Request",
"message": "Email already in use: john@example.com"
}
```

### Fields explained:
- **timestamp** - when the error occurred
- **status** - HTTP status code
- **error** - human-readable status label
- **message** - specific reason why request failed

## 📚 Exceptions Currently Supported

| Exception Type                 |         HTTP Code          |                                              When Triggered |
|--------------------------------|:--------------------------:|------------------------------------------------------------:|
| `EmailAlreadyUsedException`    |      400 Bad Request       |             When trying to register using an existing email |
| `UsernameAlreadyUsedException` |      400 Bad Request       | When trying to register with a username that already exists |
| `Exception`(fallback)          | 500 Internal Server Error  |                      Any unexpected or unhandled exception  |

This table will grow as new domains are added.

---

## 🧠 Why This Design?

### ✔ Separation of concerns

Controllers should express **API flow**, not error formatting logic.

### ✔ Consistent client experience

Frontend receives the same JSON error structure everywhere.

### ✔ Domain-driven clarity

Each domain (User, Auth, Chat, Messages) gets its own set of exceptions, but **handling remains unified.**

### ✔ Easy to extend

Adding a new exception only requires:

1. Creating a custom exception class
2. Adding one handler method to `GlobalExceptionHandler`

---

## 🧱 Next Steps

Future modules will add new exceptions such as:
- `InvalidCredentialsException`
- `JwtExpiredException`
- `UserNotFoundException`
- `ConversationNotFoundException`
- `ForbiddenActionException`

These will be documented and mapped in the same handler.

---

## ✅ Summary
`GlobalExceptionHandler` provides a single, maintainable, and predictable way to manage errors across the entire 
backend. It keeps code clean, improves security, and makes API responses clearer for frontend.