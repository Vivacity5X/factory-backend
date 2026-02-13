#  Event Ingestion & Analytics System

##  Overview

This project implements a backend system for **ingesting and analyzing machine-generated events**.  
It is engineered to handle unreliable, duplicate, and concurrent submissions while producing deterministic analytics across configurable time windows.

The implementation emphasizes:

- ✅ Correctness  
- ✅ Thread safety  
- ✅ Clean architecture  
- ✅ Deterministic behavior  
- ✅ Honest performance evaluation  

---

##  Problem Statement

Machines — not humans — send events to the backend. This introduces several real-world challenges:

- Events may arrive multiple times (duplicates)
- Events may arrive out of order
- Multiple machines may send events concurrently
- Client-provided timestamps cannot be fully trusted

### ✅ What This System Guarantees

- Safe batch ingestion  
- Deterministic deduplication  
- Early validation & rejection of invalid data  
- Reliable analytics through a stats API  

---

##  Key Design Decisions

### 1️⃣ In-Memory, Thread-Safe Storage
- Events are stored using `ConcurrentHashMap`
- Atomic updates via `ConcurrentHashMap.compute()`
- **No global locks** → high concurrency

---

### 2️⃣ Deterministic Deduplication
- `eventId` acts as the identity key

| Scenario | Behavior |
|--------|------------|
| Same `eventId` + same payload | Deduplicated |
| Same `eventId` + different payload | Updated |
| Older update | Ignored |

Conflict resolution uses **backend-generated `receivedTime`**.

---

### 3️⃣ Time Handling Strategy

| Field | Purpose |
|--------|------------|
| `eventTime` | Used for analytics |
| `receivedTime` | Used for conflict resolution |

All timestamps use **UTC (`Instant`)** for consistency.

---

### 4️⃣ Validation Strategy

Invalid events are rejected immediately if:

- `durationMs < 0`
- `durationMs > 6 hours`
- `eventTime` is more than **15 minutes in the future**

Rejected events:

- ❌ Do NOT affect system state  
- ✅ Return structured rejection reasons  

---

##  Architecture

![System Architecture](https://github.com/user-attachments/assets/27355286-f9cd-47c6-a9f4-f59b7b4934dd)

### Architectural Style
Layered, **stateless Spring Boot architecture** designed for horizontal scalability.

**Flow:**

Clients → Controllers → Service Layer → Event Store → Analytics → Responses


### Core Components

**Client Layer**
- curl / CLI  
- Postman  
- Web / React  

⬇ **HTTP REST**

**API Layer — REST Controllers**
- Request parsing  
- HTTP status handling  
- Delegation  

⬇  

**Service Layer — Core Brain**
- Validation  
- Deduplication  
- Batch ingestion  
- Concurrency-safe processing  

👉 *Stateless Service – Horizontally Scalable*

⬇  

**Event Store**
  *ConcurrentHashMap<EventId, Event>*
  - Atomic writes  
- Lock-free design  

⬇  

**Analytics Engine**
- Machine filtering  
- Time-window queries  
- Event aggregation  
- Defect rate calculation  
- Health status derivation  

⬇  

**Response Layer**
- HTTP JSON responses  
- `BatchResult`  
- `MachineStats`

---

## 📂 Project Structure

```bash
.
└── src
    └── main
        └── java
            └── com
                └── example
                    └── factory
                        ├── controller        # REST API endpoints
                        ├── service           # Core business logic
                        ├── store             # Thread-safe in-memory storage
                        ├── model             # Domain entities
                        ├── dto               # Request/Response objects
                        ├── exception         # Custom exceptions & handlers
                        └── FactoryBackendApplication.java   # Spring Boot entry point
```

---

##  APIs

---

### ✅ Batch Ingestion API

### `POST /events/batch`

Accepts a batch of machine events.

#### Example Request
```json
[
  {
    "eventId": "E-1",
    "eventTime": "2026-01-13T05:00:00Z",
    "machineId": "M-001",
    "durationMs": 1000,
    "defectCount": 1
  }
]
```
#### Response
```json
{
  "accepted": 1,
  "deduped": 0,
  "rejected": 0,
  "rejections": []
}
```

### ✅ Stats API

### `GET /stats`

#### Query Parameters

| Parameter | Description |
|------------|--------------|
| `machineId` | Machine identifier |
| `start` | Inclusive start time |
| `end` | Exclusive end time |

#### Example
```bash
GET /stats?machineId=M-001&start=2026-01-13T00:00:00Z&end=2026-01-14T00:00:00Z
```

#### Example Response
```json
{
  "machineId": "M-001",
  "start": "2026-01-13T00:00:00Z",
  "end": "2026-01-14T00:00:00Z",
  "eventsCount": 1,
  "defectsCount": 1,
  "avgDefectRate": 0.0416,
  "status": "Healthy"
}
```
## 🧪 Testing Strategy

Tests are written with **JUnit 5** and focus on correctness and concurrency safety.

### Covered Scenarios
- ✅ Valid ingestion  
- ✅ Deduplication  
- ✅ Validation failures  
- ✅ Concurrent ingestion  
- ✅ Event-time filtering  
- ✅ Ignoring unknown defects (-1)  

### Run Tests
```bash
./mvnw test
```
✔ **BUILD SUCCESS**

---

## ⚡ Performance Characteristics

- In-memory storage  
- **O(1)** average access  
- No blocking global locks  
- Batch optimized for ~1000 events  

📊 Detailed benchmarks → `BENCHMARK.md`

---

##  Running Locally

### Requirements
- Java **17+**

### Start the Application
```bash
./mvnw spring-boot:run
```

## Server runs at:

[http://localhost:8080](http://localhost:8080)

## ✅ Quick Output Check
### Batch Ingestion
```bash
curl -X POST http://localhost:8080/events/batch \
  -H "Content-Type: application/json" \
  --data-binary "@events_1000.json"
```

### Fetch Stats
```bash
curl "http://localhost:8080/stats?machineId=M-001&start=2026-01-13T00:00:00Z&end=2026-01-14T00:00:00Z"
```

##  Future Enhancements
- Persistent database storage
- Distributed ingestion
- Kafka / messaging systems
- Authentication & authorization
- Horizontal scaling

## ✅ Final Notes
This system prioritizes:
- Clarity over cleverness
- Deterministic behavior
- Production-style design
- Explainable engineering decisions
- Built specifically for machine-generated traffic patterns.

## ✨ Author
Chaitanya — 2026
