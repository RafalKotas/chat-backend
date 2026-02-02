# Real-time Messaging Architecture

This section describes how real-time communication is implemented in the application using **WebSockets, STOMP**, and 
**Spring Messaging.**<br>
It is intended for both backend and frontend developers, providing a complete overview of message flow, authentication, 
available destinations, and server-side responsibilities.

---

## 1. Technology Stack

The real-time layer relies on:

### Backend
- Spring WebSocket (`@EnableWebSocketMessageBroker`)
- STOMP protocol over WebSocket
- SimpleBroker for message broadcasting
- `ChatController` for message routing
- `MessageService` + JPA for persistence
- Optional `JwtChannelInterceptor` for WebSocket-level authentication

### Frontend

The WebSocket API is compatible with:
- `@stomp/stompjs` (recommended)
- `SockJS` fallback
- Any STOMP-capable client for mobile or desktop apps

---

## 2. WebSocket Endpoint

The backend exposes a single handshake endpoint:

```
/ws
```

Clients must establish a STOMP connection using this endpoint before interacting with chat rooms.

Example JavaScript:

```
const client = new Client({
  webSocketFactory: () => new WebSocket("ws://localhost:8080/ws"),
});
```

---

## 3. Message Routing Model

### 3.1 Application Destinations

Messages sent from clients to the server go through:

```
/app/**
```

These map to controller methods annotated with `@MessageMapping`.

Available application destinations:

| Destination             | Controller Method              | Purpose                             |
|:------------------------|:-------------------------------|-------------------------------------|
| `/app/chat.sendMessage` | `ChatController.sendMessage()` | Send text message to a chat         |
| `/app/chat.addUser`     | `ChatController.addUser()`     | Notify that a user joined the chat  |

---

### 3.2 Broadcast Destinations

The server broadcasts messages to all subscribers via:

```
/topic/chat.{chatId}
```

Clients must subscribe to this topic to receive updates:

```
client.subscribe("/topic/chat.room-1", callback);
```

---

## 4. Message Types & Structure

Messages exchanged through STOMP use a shared structure represented by:

`ChatMessage`

```
{
  "chatId": "room-1",
  "sender": "Alice",
  "content": "Hello world",
  "type": "CHAT"
}
```

The `type` field uses the `ChatMessageType` enum:
- `CHAT` - standard user message
- `JOIN` - user joins a room
- `LEAVE` - user leaves a room

---

## 5. Server Workflow

### 5.1 Sending Messages

When a client sends:

```
SEND /app/chat.sendMessage
```

The server:
1. Logs the incoming message.
2. Converts it into a `Message` JPA entity.
3. Persists it in the database.
4. Broadcasts the original `ChatMessage` to:
```
/topic/chat.{chatId}
```

All connected clients receive the message instantly.

---

### 5.2 User Joins Chat

When a client sends:

```
SEND /app/chat.addUser
```

The server:
- Enforces `type = JOIN`
- Broadcasts a join event to:

```
/topic/chat.{chatId}
```

This allows the UI to show notifications like:
> "Alice joined the room"

---

## 6. JWT Authentication (Optional but Recommended)

The HTTP REST API uses normal Spring Security filters,<br>
but WebSocket connections do **not** automatically inherit JWT validation.

To secure WebSocket connections, we can enable the `JwtChannelInterceptor`.

This interceptor:

1. Reads the `Authorization` header during `CONNECT`.
2. Extract the JWT token.
3. Validates it with `JwtUtils`.
4. If valid — sets `SecurityContext` user.
5. If invalid — rejects the connection (`return null`).

Clients must then connect with:
```
client.connect({
  Authorization: "Bearer " + token
});
```

If the interceptor is not enabled, WebSocket messages remain unauthenticated.

---

## 7. Chat History Retrieval

WebSockets deliver *real-time* messages only.<br>
Historical messages are obtained via REST:

```
GET /api/messages/{chatId}
```

Response structure: `MessageResponse`

Example:

```
[
  {
    "id": "uuid",
    "chatId": "room-1",
    "sender": "Alice",
    "content": "Hi",
    "createdAt": "2024-10-10T12:00:00Z"
  }
]
```

Typical UI flow:
1. Load history via HTTP.
2. Subscribe to WebSocket topic.
3. Append new messages live.

---

## 8. End-to-END Sequence Diagram

```
Client -------------------------> Server
   |                                 |
   |--- CONNECT (with JWT) --------->|
   |<---------- CONNECTED -----------|
   |                                 |
   |--- SUBSCRIBE /topic/chat.X ---->|
   |                                 |
   |--- SEND /app/chat.sendMessage ->|
   |                                 |  Server saves to DB
   |                                 |  Server broadcasts
   |<---------- MESSAGE -------------|
```

--- 

## 9. Frontend Integration Summary

The frontend must:

✔ Connect to `/ws`

✔ Send JWT header during `CONNECT` (if enabled)

✔ Subscribe to `/topic/chat.{chatId}`

✔ Use `/app/chat.sendMessage` to send chat messages

✔ Use `/app/chat.addUser` to notify joining

✔ Use REST `/api/messages/{chatId}` to load history

This architecture ensures:
- real-time communication
- persistent message storage
- clean separation of responsibilities
- extensibly for typing indicators, read receipts, and more

---

## 10. Future Improvements
The current WebSocket module is a solid MVP.<br>
Possible enhancements:
- Presence tracking (online/offline status)
- Typing indicators
- Private messaging channels
- Chat-level authorization rules
- Replacing SimpleBroker with RabbitMQ/Redis for scaling