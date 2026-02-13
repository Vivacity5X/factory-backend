# Event Ingestion & Analytics System

## 📌 Overview

This project implements a backend system for ingesting and analyzing machine-generated events.
The system is designed to handle unreliable, duplicate, and concurrent event submissions while
providing deterministic analytics over time windows.

The implementation strictly follows the assignment specification, with a focus on:
- correctness
- thread safety
- clear design
- honest performance evaluation

---

## 🎯 Problem Statement

Machines (not humans) send events to the backend:
- Events may arrive multiple times (duplicates)
- Events may arrive out of order
- Multiple machines may send events concurrently
- Client-provided timestamps cannot be fully trusted

What this system does:
- Ingest events safely in batches
- Deduplicate and reconcile updates
- Reject invalid data
- Provide analytics via a stats API

---

## 🧠 Key Design Decisions



### 1 In-Memory, Thread-Safe Storage
- Events are stored in memory using `ConcurrentHashMap`
- Atomic updates are implemented using `ConcurrentHashMap.compute()`
- No global locks are used

### 2 Deterministic Deduplication
- `eventId` is the identity key
- Same `eventId` + same payload → deduplicated
- Same `eventId` + different payload → updated
- Older updates are ignored using backend-generated `receivedTime`

### 3 Time Handling
- `eventTime` is used for analytics
- `receivedTime` is used only for conflict resolution
- All timestamps use UTC (`Instant`)

### 4 Validation Strategy
Invalid events are rejected early:
- `durationMs < 0` or `durationMs > 6 hours`
- `eventTime` more than 15 minutes in the future

Rejected events:
- Do not affect system state
- Return structured rejection reasons

---

## 🏗️ Architecture

<img width="1024" height="1536" alt=" Image Feb 13, 2026, 11_21_44 PM" src="https://github.com/user-attachments/assets/27355286-f9cd-47c6-a9f4-f59b7b4934dd" />


Controller
↓
Service (validation + business logic)
↓
Thread-safe Store (ConcurrentHashMap)
↓
Analytics (eventTime-based)


- Controllers are thin (HTTP only)
- Services own business logic
- Store guarantees atomicity

---

## 📂 Project Structure

src/main/java/com/example/factory
│
├── controller # REST APIs
├── service # Business logic
├── store # Thread-safe in-memory storage
├── model # Internal domain models
├── dto # API contracts
├── exception # Business exceptions
└── FactoryBackendApplication.java

---

## 🚀 APIs

### 1️⃣ Batch Ingestion API

**POST `/events/batch`**
Accepts a batch of machine events.

Example request:
[
  {
    "eventId": "E-1",
    "eventTime": "2026-01-13T05:00:00Z",
    "machineId": "M-001",
    "durationMs": 1000,
    "defectCount": 1
  }
]
Example response:
{
  "accepted": 1,
  "deduped": 0,
  "rejected": 0,
  "rejections": []
}
2️⃣ Stats API
GET /stats
Query parameters:
machineId
start (inclusive)
end (exclusive)

Example:

GET /stats?machineId=M-001&start=2026-01-13T00:00:00Z&end=2026-01-14T00:00:00Z
Example response:

{
  "machineId": "M-001",
  "start": "2026-01-13T00:00:00Z",
  "end": "2026-01-14T00:00:00Z",
  "eventsCount": 1,
  "defectsCount": 1,
  "avgDefectRate": 0.0416,
  "status": "Healthy"
}
Machine status is derived using an average defect-rate threshold of 2 defects/hour.

🧪 Testing Strategy
Tests are written using JUnit 5 and focus on correctness and safety.

Covered scenarios:
Valid ingestion
Deduplication
Validation failures
Concurrent ingestion
Event-time window filtering
Ignoring unknown defects (-1)

Run tests:
.\mvnw test

All tests pass:
BUILD SUCCESS

⚡ Performance
In-memory storage
O(1) average access
No blocking global locks
Batch processing optimized for up to 1000 events
Detailed benchmark results are documented in BENCHMARK.md.

🛠️ How to Run Locally
Requirements
Java 17+

Run application
mvnw spring-boot:run
Application runs at:
http://localhost:8080

## OUTPUT CHECK
1.open terminal in project folder
2.make sure app is running
3.enter this cmd :
curl.exe -X POST http://localhost:8080/events/batch `
  -H "Content-Type: application/json" `
--data-binary "@events_1000.json"

4.For stats:
curl.exe "http://localhost:8080/stats?machineId=M-001&start=2026-01-13T00:00:00Z&end=2026-01-14T00:00:00Z"



🔮 Future


Persistent storage (database)
Distributed ingestion
Messaging systems (Kafka, queues)
Authentication / authorization


✅ Final Notes
The system prioritizes correctness and clarity
Designed for machine-generated traffic
All design decisions are test-verified and explainable

✨ Author
Chaitanya
Backend Intern Assignment – 2026
