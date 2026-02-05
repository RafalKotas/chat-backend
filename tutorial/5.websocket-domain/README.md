# Real-time Messaging Architecture

This section describes how real-time communication is implemented in the application using **WebSockets, STOMP**, and 
**Spring Messaging.**<br>
It explains message flow, authentication, routing, DTO structures, and how backend and frontend components interact.

Document applies to:

- `WsChatController`
- `WsInboundMessage / WsOutboundMessage`
- `WsMessageType`
- `WebSocketConfig`
- `JwtChannelInterceptor (optional)`
- `REST history (MessageController)`

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

### 3.1 Client → Server (`/app/**`)

All client-sent messages must use the `/app` prefix.

Available application destinations:

| Destination             | Controller Method              | Purpose                             |
|:------------------------|:-------------------------------|-------------------------------------|
| `/app/chat.sendMessage` | `ChatController.sendMessage()` | Send text message to a chat         |
| `/app/chat.addUser`     | `ChatController.addUser()`     | Notify that a user joined the chat  |

These map to `@MessageMapping`.

---

### 3.2 Server → Clients (`/topic/**`)

The server broadcasts messages to all subscribers via:

```
/topic/chat.{chatId}
```

Clients must subscribe to this topic to receive updates:

```
client.subscribe("/topic/chat.room-1", callback);
```

Every broadcast uses **WsOutboundMessage**.

---

## 4. WebSocket Message Types & DTOs

WebSocket communication uses two DTOs:

### Inbound (from client):

#### `WsInboundMessage`

```
{
  "chatId": "room-1",
  "sender": "Alice",
  "content": "Hello world",
  "type": "CHAT"
}
```

### Outbound (to clients):

#### `WsOutboundMessage` (typically includes server-added metadata)

```json
{
"id": "uuid",
"chatId": "room-1",
"sender": "Alice",
"content": "Hello!",
"type": "CHAT",
"timestamp": "2026-02-04T19:22:00Z"
}
```

Message Type Enum: WsMessageType

- `CHAT` — standard user message
- `JOIN` — a user joined
- `LEAVE` — a user left (future)

---

## 5. Server Workflow

### 5.1 Sending Messages

When a client sends:

```
SEND /app/chat.sendMessage
```

Backend (in `WsChatController.sendMessage()`):
1. Receives `WsInboundMessage`
2. Logs the event
3. Saves it as a `Message` JPA entity (database)
4. Builds `WsOutboundMessage` with metadata
5. Broadcasts to:
```
/topic/chat.{chatId}
```

All connected clients receive it instantly.

---

### 5.2 User Join Event

Client triggers:

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

JOIN events are **not persisted**.

---

## 6. JWT Authentication (Optional but Recommended)

By default, Spring Security’s HTTP filters **do not protect WebSocket connections**.

To secure WebSocket connections, we can enable the `JwtChannelInterceptor`.

This interceptor:

1. Reads the `Authorization` header during `CONNECT`.
2. Extract the JWT token (`Authorization: Bearer <token>`).
3. Validates it with `JwtUtils`.
4. If valid — sets `SecurityContext` user, attach authenticated `Principal` to session.
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

WebSockets deliver **real-time messages only**.<br>
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
1. Load history via REST
2. Connect WebSocket
3. Subscribe to `/topic/chat.{chatId}`
4. Receive new messages live

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
   | Server saves to DB              |  
   | WsOutboundMessage               |  
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
- Typing indicators (`TYPING` WsMessageType)
- Read receipts
- Private 1:1 channels
- Presence/status tracking
- Switching SimpleBroker to RabbitMQ / Redis for scalability
- Message editing / deletion
- Chat-level permissions