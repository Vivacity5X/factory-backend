<img width="1536" height="1024" alt="ChatGPT Image May 24, 2026, 03_43_24 PM" src="https://github.com/user-attachments/assets/52260037-b71b-4f03-b8d2-13fc6da306d5" />
<img width="1536" height="1024" alt="ChatGPT Image May 24, 2026, 03_43_24 PM" src="https://github.com/user-attachments/assets/acfa79fd-b160-436b-be13-226adf91a63b" />
# Factory Backend Event Ingestion & Analytics System

##  Overview

This project implements a production-inspired backend system for ingesting, validating, storing, and analyzing machine-generated telemetry events.

The system is designed to simulate real-world industrial/IoT event pipelines where multiple machines continuously emit operational data.

It supports:

* ✅ Batch event ingestion
* ✅ Deterministic deduplication
* ✅ Concurrent processing
* ✅ MySQL persistence
* ✅ Time-window analytics
* ✅ Swagger/OpenAPI documentation
* ✅ Realistic telemetry simulation
* ✅ Unit & concurrency testing

---

#  Real-World Use Case

Imagine a factory floor containing multiple industrial machines.

Each machine continuously sends telemetry such as:

* Production duration
* Defect counts
* Operational timestamps
* Machine identifiers

This backend system ingests those machine events safely, prevents duplicates during concurrent submissions, persists telemetry into MySQL, and computes analytics to monitor machine health.

The architecture models a production-inspired backend system for handling large volumes of machine-generated factory events and analytics.

---

#  Key Engineering Goals

The system emphasizes:

* Thread safety
* Deterministic behavior
* Clean layered architecture
* Production-style API design
* Explainable engineering decisions
* Reliable analytics

---

# 🏗 Architecture

![System Architecture](https://github.com/user-attachments/assets/27355286-f9cd-47c6-a9f4-f59b7b4934dd)

## Architectural Style

Layered, stateless Spring Boot backend architecture.

### Flow

Clients → Controllers → Services → Storage → Analytics → JSON Responses

---

## Core Components

### 🔹 Client Layer

Clients interact through:

* curl
* Postman
* Swagger UI
* Future React frontend

---

### 🔹 API Layer — REST Controllers

Responsibilities:

* HTTP request parsing
* Response generation
* Delegation to services
* Status handling

---

### 🔹 Service Layer — Core Business Logic

Responsibilities:

* Event validation
* Deduplication
* Batch ingestion
* Concurrency-safe operations
* Analytics computation

The service layer is intentionally stateless and horizontally scalable.

---

### 🔹 Persistence Layer

Telemetry events are persisted using:

* MySQL
* Spring Data JPA
* Hibernate ORM

Repository-based querying supports efficient time-window analytics.

---

### 🔹 Event Store

Concurrency-safe ingestion uses:

```java
ConcurrentHashMap<EventId, Event>
```

Features:

* Atomic updates
* Lock-free reads
* Concurrent deduplication

---

### 🔹 Analytics Engine

Computes:

* Event counts
* Defect totals
* Average defect rate
* Machine health status
* Time-window filtered analytics

---

# ⚙️ Technical Stack

| Category    | Technology                  |
| ----------- | --------------------------- |
| Backend     | Spring Boot                 |
| Language    | Java 22                     |
| Database    | MySQL                       |
| ORM         | Spring Data JPA / Hibernate |
| API Docs    | Swagger / OpenAPI           |
| Testing     | JUnit 5 + Mockito           |
| Build Tool  | Maven                       |
| Concurrency | ConcurrentHashMap           |
| Data Format | JSON                        |

---

#  Project Structure

```bash
src
├── main
│   └── java/com/example/factory
│       ├── controller
│       ├── service
│       ├── repository
│       ├── store
│       ├── model
│       ├── dto
│       ├── exception
│       └── config
│
├── test
│   └── java/com/example/factory
│       ├── EventServiceTest
│       └── EventGenerator
```

---

# 🔄 Event Processing Workflow

## Batch Ingestion Flow

1. Client submits batch events
2. Controller receives request
3. Service validates events
4. Deduplication logic executes
5. Accepted events persist into MySQL
6. Analytics APIs query telemetry data
7. JSON response returned

---

# 🛡️ Validation Rules

Events are rejected if:

* `durationMs < 0`
* `durationMs > 6 hours`
* `eventTime` is more than 15 minutes in the future

Rejected events:

* ❌ Do not affect system state
* ✅ Return structured rejection reasons

---

# 🔁 Deduplication Strategy

`eventId` acts as the identity key.

| Scenario                     | Behavior     |
| ---------------------------- | ------------ |
| Same eventId + same payload  | Deduplicated |
| Same eventId + newer payload | Updated      |
| Older update                 | Ignored      |

Conflict resolution uses backend-generated `receivedTime`.

---

# 🌐 REST APIs

---

## ✅ Batch Ingestion API

### `POST /events/batch`

Accepts a batch of telemetry events.

### Example Request

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

### Example Response

```json
{
  "accepted": 1,
  "deduped": 0,
  "rejected": 0,
  "rejections": []
}
```

---

## ✅ Machine Analytics API

### `GET /stats`

### Query Parameters

| Parameter | Description          |
| --------- | -------------------- |
| machineId | Machine identifier   |
| start     | Inclusive start time |
| end       | Exclusive end time   |

### Example

```bash
GET /stats?machineId=M-001&start=2026-01-13T00:00:00Z&end=2026-01-14T00:00:00Z
```

### Example Response

```json
{
  "machineId": "M-001",
  "eventsCount": 200,
  "defectsCount": 20,
  "avgDefectRate": 0.83,
  "status": "Healthy"
}
```

---

# 📘 Swagger API Documentation

Swagger/OpenAPI documentation is integrated for interactive API testing.

## Swagger URL

```bash
http://localhost:8080/swagger-ui/index.html
```

Features:

* Interactive API testing
* Request/response schemas
* Endpoint documentation
* Faster integration debugging

---

# 🧪 Testing Strategy

The project includes unit and concurrency tests using:

* JUnit 5
* Mockito

## Covered Scenarios

* ✅ Valid ingestion
* ✅ Deduplication
* ✅ Validation failures
* ✅ Concurrent ingestion
* ✅ Event-time filtering
* ✅ Ignoring unknown defects
* ✅ Analytics verification

## Run Tests

```bash
./mvnw test
```

✔ BUILD SUCCESS

---

# ⚡ Benchmarking & Telemetry Simulation

A custom telemetry generator creates realistic industrial event traffic.

Generated telemetry includes:

* Multiple machines
* Different timestamps
* Variable durations
* Randomized defect patterns

## Generate Benchmark Events

```bash
java EventGenerator
```

This creates:

```bash
events_1000.json
```

used for batch ingestion benchmarking.

---

# 📈 Performance Characteristics

* O(1) average in-memory access
* Concurrent ingestion support
* Batch optimized for ~1000 events
* Lock-free event processing
* Repository-backed analytics queries

---

# ▶️ Running Locally

## Requirements

* Java 17+
* MySQL 8+
* Maven

---

## Configure Database

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/factorydb
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

## Start Application

```bash
./mvnw spring-boot:run
```

Server runs at:

```bash
http://localhost:8080
```

---

# 🚀 Quick Demo Commands

## Batch Ingestion

```bash
curl.exe -X POST http://localhost:8080/events/batch ^
-H "Content-Type: application/json" ^
--data-binary "@events_1000.json"
```

---

## Fetch Analytics

```bash
curl.exe "http://localhost:8080/stats?machineId=M-001&start=2026-01-13T00:00:00Z&end=2027-01-14T00:00:00Z"
```

---

# 🔮 Future Enhancements

* Kafka-based streaming ingestion
* Docker deployment
* Kubernetes scaling
* Alerting engine
* Authentication & authorization
* React monitoring dashboard
* Distributed event processing

---

# 💡 Key Learning Outcomes

This project helped explore:

* Backend architecture design
* Concurrent programming
* REST API engineering
* Batch event processing
* Database persistence
* Analytics computation
* Production-inspired backend patterns

---

# 👨‍💻 Author

**Chaitanya** — 2026
