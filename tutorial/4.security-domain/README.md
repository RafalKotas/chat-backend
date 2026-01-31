# Authentication Domain

## Purpose of the Authentication Module

The *Authentication* module is responsible for:
- User registration
- User login
- Generating JWT Tokens
- Validating JWT Tokens
- Injecting authenticated users into `SecurityContext`
- Handling authentication-related errors
It forms the foundation for all user-dependent modules, such as contact and chat.

---

## Package Structure

```
com.chatapp.chat.auth
 ├── dto
 │     ├── LoginRequest
 │     ├── LoginResponse
 │     ├── RegisterRequest
 │     └── RegisterResponse
 ├── AuthController
 └── AuthService
 
 com.chatapp.chat.security
 ├── JwtUtils
 ├── JwtAuthenticationFilter
 ├── CustomerDetailsService
 ├── AuthenticationEntryPointImpl
 └── SecurityConfig
 
 com.chatapp.chat.config
 └── PasswordConfig
 
 com.chatapp.chat.common.exception
 └── GlobalExceptionHandler
```

---

## 1. DTOs

### LoginRequest

Represents login input data:

```java
String email;
String password;
```

### LoginResponse

Returned on successful login:

```java
String token;
String email;
String displayName;
```

---

### RegisterRequest

Represents registration input data:

```java
String email;
String password;
String firstName;
String lastName;
String username;
```

### RegisterResponse

Returned after successful registration:

```java
UUID id;
String email;
String displayName;
```

---

## 2. AuthService
Responsible for:
- registering new users
- Authenticating credentials
- validating email/username uniqueness
- generating JWT tokens

It uses:
- `UserService`
- `PasswordEncoder`
- `JwtUtils`

---

## 3. AuthController

### Endpoints

`POST /api/auth/register`
-

- Accepts `RegisterRequest`
- Creates a new user
- Returns `RegisterResponse`

`POST /api/auth/login`
-

- Validates credentials
- Issues a JWT token
- Returns `LoginResponse`

---

## 4. Security Layer

### SecurityConfig

Configures Spring Security:
- disables HTTP sessions
- registers the JWT filter
- configures unauthorized response handling
- permits the `/auth/**` endpoints without authentication

---

### JWT Utility (JwtUtils)

`JwtUtils` is the low-level component responsible for **creating, validating** and **parsing** JSON Web Tokens (JWT).

It is used by:
- the **authentication module** (during login) to generate new tokens
- the **security filter** (`JwtAuthenticationFilter`) to validate tokens on every request
- Spring Security, indirectly, for authentication



#### Responsibilities:
- generating JWT tokens
- validating tokens
- extracting the subject (email)
- using a secure HMAC-SHA key via JJWT

Uses the library

```java
io.jsonwebtoken:jjwt-api
io.jsonwebtoken:jjwt-impl
io.jsonwebtoken:jjwt-jackson
```

---

## 🔐 How `generateToken()` Works — Step by Step

```java
public String generateToken(String subject) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
}
```

### Breakdown
1. **Create a timestamp**
    ```java
        Date now = new Date();
    ```
This marks the issuance time (`iat` claim).
2. **Compute expiration time**
    ```
    new Date(now.getTime() + expirationMs);
    ```
   - `expirationMs` is injected from `application.yaml`
   - sets the `exp` claim in the token
3. ** Build the token body**
   ```java
    .setSubject(subject)
    ```
    - `subject` is the user identifier (usually email or username)
    - becomes: `"sub": "email@example.com"`
4. **Add iat and exp claims**
    - .setIssuedAt(now)
    - .setExpiration(expiry)
5. **Sign the token**
   ```java
    .signWith(signingKey, SignatureAlgoritm.HS256)
   ```
This ensures integrity.

If the token is modified → signature verification fails → token invalid.
6. **Compact the token into a string**
    ```
      .compact();
   ```
   The output is a standard JWT:
   ```
    header.payload.signature
   ```

### Result

The result is a **self-contained token** that includes:
- who the user is
- when the token was issued
- when it expires
- a cryptographic signature to prevent tampering

This is what the frontend receives after login.

--- 

## 🔍 How parseToken() Works — Step by Step

```java
private Jws<Claims> parseToken(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token);
}
```

### Breakdown
1. **Create a JWT parser**
    ```java
        Jwts.parserBuilder()
    ```
   Prepares a new parsing pipeline (thread-safe).
2. **Attach the signing key**
   ```java
    .setSigningKey(signingKey)
    ```
   This tells the parser:
    >"Use this exact key to verify the signature."
3. **Build the parser**
    ```java
    .build()
    ```
4. **Validate and decode the token**
    ```
   .parseClaimsJws(token)
   ```
   This performs **all** checks at once:
   - verifies the signature
   - checks token structure
   - verifies token expiry (`exp`)
   - extracts the claims payload
   
   If anything is wrong, JJWT throws a `JwtException`.

### What parseToken() returns
A fully validated JWT:
```java
Jws<Claims> jws = parseToken(token);
Claims claims = jws.getBody();
```
Examples of extracted fields:
```json
{
  "sub": "john@example.com",
  "iat": 1738000000,
  "exp": 1738086400
}
```
This method is **never used directly by controllers.**
It is used by:
- `extractUserName()`
- `isValid()`
- `JwtAuthenticationFilter`

---

## ✏️ `extractUserName()`
```java
public String extractUserName(String token) {
    return parseToken(token).getBody().getSubject();
}
```

### Meaning

Once the token is validated, the method extracts `"sub"` — the authenticated identity.

Used by the JWT filter to load `UserDetails`.

```java
String username = jwtUtils.extractUserName(token);
```

---

## ✔️ isValid()

```java
public boolean isValid(String token) {
    try {
        parseToken(token);
        return true;
    } catch (JwtException e) {
        return false;
    }
}
```

### Meaning

Performs silent validation:
- signature valid?
- not expired?
- not malformed?
- issued by us?

If *any* check fails → returns `false`.

This is used in:

```java
if (jwtUtils.isValid(token)) {
    // continue processing request
}
```

---

## 🎯 Summary Diagram

```
Client Request → JwtAuthenticationFilter → JwtUtils.isValid()
                                               ↓
                                       JwtUtils.parseToken()
                                               ↓
                                      JwtUtils.extractUserName()
                                               ↓
                               Load user → set SecurityContext
                                               ↓
                                       Controller executes

```

`JwtUtils` is therefore the `core engine` of the entire authentication flow.

---

### JwtAuthenticationFilter
A `OncePerRequestFilter` that runs for every request.

Flow:

1. Extracts token from header `Authorization: Bearer <token>`
2. Validates token in `JwtUtils`
3. Loads the user with `CustomerDetailsService`
4. Populates the `SecurityContextHolder`
5. Continues the filter chain

If the token is invalid - the request passes through without authenticaion.

---

### CustomerDetailsService

Loads a user from the database for Spring Security by email

---

### AuthenticationEntryPointImpl

Defines JSON response for **401 Unauthorized**.
Used when a protected endpoint is accessed without a valid token.

Example response:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or missing token"
}
```

---

### Password Config

Provides a `BCryptPasswordEncoder` instance.

---

## 5. GlobalExceptionHandler

Handles:

| Exception                      | Status |        Description |
|--------------------------------|-------:|-------------------:|
| `EmailAlreadyUsedException`    |    400 |    Duplicate email |
| `UsernameAlreadyUsedException` |    400 | Duplicate username |
| `Exception`                    |    500 |   Unexpected error |

Always returns a JSON structure with timestamp, status and message.

---

## 6. Login Flow - Diagram

```
HTTP Request
      ↓
JwtAuthenticationFilter
      ↓ extract Bearer token
JwtUtils.isValid()
      ↓
CustomerDetailsService.loadUserByUsername()
      ↓
SecurityContext is populated
      ↓
Controller executes
```

---

## 7. Authorization Flow - Diagram

```
HTTP Request
      ↓
JwtAuthenticationFilter
      ↓ extract Bearer token
JwtUtils.isValid()
      ↓
CustomerDetailsService.loadUserByUsername()
      ↓
SecurityContext is populated
      ↓
Controller executes
```

---

## 8. JWT Configuration (Why It Exists and Why It Matters)

The authentication module requires several critical configuration values that define how JWT tokens are 
generated, signed, and validated. These values live under the `app.jwt` namespace inside `application.yaml`.

### Why a Configuration Section is Needed

JWT-based authentication is stateless. That means the backend does not store any login sessions - instead:
- The server generates a JWT token **signed with a private key**
- The client sends this token in every request (`Authorization: Bearer <token>)
- The backend validates the token using the `same secret key`

Because of this, the server must know:

**1. What secret key is used to sign tokens**

**2. How long tokens should be valid**

These two settings must be stored in the application configuration rather than hardcoded - because:
- They differ between environments (dev / test / production)
- Secrets should not be committed to the codebase
- Expiration times may be tuned without changing source code

--- 

## 8.1 Configuration Structure

```yaml
app:
  jwt:
    secret: "your-256-bit-secret-key-should-be-at-least-32-chars"
    expiration-ms: 86400000
```

### Properties Explained

| Property                |   Type |                                                                                         Meaning |
|-------------------------|-------:|------------------------------------------------------------------------------------------------:|
| `app.jwt.secret`        | String | Secret key used to **sign and verify JWT tokens**.<br> Must be at least **32 bytes** for HS256. |
| `app.jwt.expiration-ms` |   Long |        Duration (miliseconds) for how long an issued token stays valid.<br> Here: **24 hours.** |

---

## 8.2 Why the Secret Key Is Important

The JWT secret key is used by `JWTUtils` to:
- generate signed JWT tokens,
- verify incoming tokens,
- protect against tampering.

### Why it must be long (≥32 characters)?

Because HS256 (HMAC-SHA256) requires a minimum 256-bit key for secure signing.

If the key is shorter, JJWT will throw:

```
WeakKeyException: The signing key's size is 168 bits which is not secure enough for the HS256 algorithm.
```

--- 

## Why the Expiration Matters

Authentication systems should never issue timeless tokens.
A reasonable expiration ensures:
- stolen tokens become useless after a short time
- users are authenticated again after long inactivity
- backed can enforce security policies over time

The `expiration-ms` value determines:
- how long the user stays logged in,
- how often the front-end must refresh tokens.

**Example:**
`86400000 ms = 24 hours`
Meaning: tokens expire one day after being issued.

---

## How These Values Are Used in the Code

### In `JwtUtils` constructor:

```java
this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
this.expirationMs = expirationMs;
```

### During token generation:

```java
Date expiry = new Date(now.getTime() + expirationMs);
```

### During token validation:

```java
Jwts.parserBuilder()
    .setSigningKey(signingKey)
    .build()
    .parseClaimsJws(token);
```

If the token is expired or incorrectly signed, JJWT throws an exception and authentication fails.

---

## 8.5 Summary

This configuration is required because:
- authentication relies on JWT token generation and validation,
- token signing must use a secure key,
- token lifetime must be externally configurable,
- values differ between local/dev/prod environments.

Without it:
- the application cannot issue valid JWT tokens,
- authentication with always fail,
- security will be compromised.

## 📘 9. AuthController — Responsibilities and HTTP Behavior

`AuthController` exposes two primary authentication endpoints:

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
```

The controller is responsible only for **request routing** and **returning DTOs.**

All business logic is delegated to `AuthService`.

--- 

### 9.1 Register Endpoint (`POST /api/auth`)

#### Purpose

Creates a new user account and returns a lightweight representation of created user.

#### Example request body

```json
{
  "email": "user@example.com",
  "password": "secret123",
  "username": "johnny"
}
```

#### Response
- **HTTP status code:** `201 Created`
- **Body:**

  `RegisterResponse` record:
```java
public record RegisterResponse(
        UUID id,
        String email,
        String username
) {}
```

---

### 9.2 Why the HTTP Status Code Is 201 (Created)

The controller method uses:

```java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CREATED)
```

This annotation tells Spring *explicitly* what HTTP status code should be sent back **regardless of the 
method's return type**

#### ✔ Important clarification:

`RegisterResponse` is **only the HTTP response body.**

It does **not** and **should not** contain any information about HTTP status.

HTTP status is controlled entirely by Spring's Web framework.

#### How Spring handles this internally:
1. The controller method returns a Java object → Spring serializes it to JSON.
2. Before the response is sent, Spring checks for:
    - `@ResponseStatus`
    - exceptions mapped through `@ExceptionHandler`
    - default status cods (e.g., 200 OK)
3. Spring attaches the appropriate HTTP status code to the response.
4. The final HTTP response looks like:

```
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": "2abfca95-4a50-4e70-b2b2-0ccceddd884a",
  "email": "user@example.com",
  "username": "johnny"
}
```

#### ✔ Why this is good design
- Keeps DTOs clean (they represent **data**, not HTTP transport details).
- Controller retains control over HTTP semantics.
- Allows switching between `201`, `200`, `204`, etc. without touching domain models.

---

### 9.3 Login Endpoint (`POST /api/auth/login`)
Returns an authentication token and basic metadata:
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "expiresIn": 86400000
}
```

This endpoint does **not** use `@ResponseStatus`, so Spring returns:
- Http **200 OK** on success,
- or an error code forwarded by the `GlobalExceptionHandler`.

--- 

### 9.4 Summary

| Endpoint               |      Status |            Description |
|------------------------|------------:|-----------------------:|
| `POST /api/auth`       | 201 Created |      User registration |
| `POST /api/auth/login` |      200 OK | User login + JWT token |

HTTP status is controlled by annotations (e.g. `@ResponseStatus`) or exception handlers, **not** by the DTOs returned by 
controller methods.

---

### 📘 9.5 What Happens When Invalid Data is Sent to AuthController?
Spring Boot automatically validates and parses incoming requests sent to controller methods. 
When the client sends **invalid JSON, wrong field types, missing fields** or **malformed body**, several things can 
happen depending on the error type.

Below are all typical failure scenarios and how Spring reacts by default.

--- 

#### 1. 🚫 Malformed JSON (invalid JSON format)

**Example Request**:
```json
{ "email": "example.com",  "password": "123"   // missing closing brace
```

**What happens:**
- Spring cannot parse the request body
- `HttpMessageNotReadableException` is thrown before the controller is even called
- Response returned automatically:

```
HTTP 400 Bad Request
Content-Type: application/json
```

Body (Spring default):

```json
{
  "error": "Bad Request",
  "message": "JSON parse error: ..."
}
```

➡️ No custom exception handler yet covers this, so Spring handles it internally.

---

#### 2. 🚫 Wrong field types (e.g., number instead of string)

**Example invalid request**:
```json
{
  "email": 12345,
  "password": true
}
```

**Result**:
- Spring fails to map JSON → Java object (`RegisterRequest`)
- Again, `HttpMessageNotReadableException` is thrown

**Response:** `400 Bad Request`

This error also never reaches your controller method

--- 

#### 3. 🚫 Missing required fields (with no validation annotations)

If your DTO has `@NotNull`, `@Email`, etc., Spring will still construct the object:

```java
public record RegisterRequest(String email, String password, String username) {}
```

and call the controller.

Missing fields will simply be set to `null`.

➡️ Validation must be added manually — otherwise missing fields won’t throw errors.

--- 

### 4. 🚫 Validation errors (with @Valid and constraints)

If you later use:

```java
public RegisterResponse register(@RequestBody @Valid RegisterRequest request)
```

and annotate fields:

```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@NotBlank @Email String email
@NotBlank String password
```

Then Spring will:
- run Bean Validation (Jakarta Validation)
- detect violations before calling the service
- throw `MethodArgumentNotValidException`

Response looks like:

```
HTTP 400 Bad Request
```

Spring default body:

```
{
  "error": "Bad Request",
  "message": "Validation failed for object='registerRequest'. Error count: 1"
}
```

You can override this in `GlobalExceptionHandler`.

--- 

#### 5. 🚫 Unexpected server errors

If something inside your `AuthService` throws a runtime exception:
- It is caught by your `GlobalExceptionHanlder`
- Returned status: `500 INTERNAL_SERVER_ERROR`

Your handler returns:

```json
{
  "timestamp": "...",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unexpected error occurred"
}
```

---

## 9.6 Summary of Error Handling Behavior
| Situation                                            |                         Exception | Reaches Controller? | Status | Who Handles It?               |
|------------------------------------------------------|----------------------------------:|--------------------:|-------:|:------------------------------|
| Malformed JSON                                       | `HttpMessageNotReadableException` |                ❌ No |    400 | Spring                        |
| Wrong JSON field types                               | `HttpMessageNotReadableException` |                ❌ No |    400 | Spring                        |
| Missing fields (no validation)                       |                                 — |               ✔ Yes |   200+ | Controller                    |
| Bean validation failure (`@Valid`)                   | `MethodArgumentNotValidException` |                ❌ No |    400 | Spring (unless overridden)    |
| `AuthService` throws custom exception                |       e.g., IllegalStateException |               ✔ Yes |    500 | Your `GlobalExceptionHandler` |
| Custom business errors (`EmailAlreadyUsedException`) |                            Custom |               ✔ Yes |    400 | Your `GlobalExceptionHandler` |


---

## 10. Tests (to be created later)

Planned test classes:
- JwtUtilsTest
- JwtAuthenticationFilterTest
- AuthServiceTest
- AuthControllerTest

Testing will be done after completing the entire auth module.