
# Performance Benchmark – Batch Ingestion

## 📌 Objective

This project implements a backend system for **ingesting and analyzing machine-generated events**.  
It is engineered to handle unreliable, duplicate, and concurrent submissions while producing deterministic analytics across configurable time windows.

> Batch ingestion of 1000 events should complete in under 1 second.

---

## 🖥️ Test Environment

- **Machine**: Developer Laptop
- **CPU**: AMD Ryzen 3250U (U-series)
- **RAM**: 12 GB (4 GB + 8 GB)
- **OS**: Windows
- **Java Version**: Java 17+ (tested with Java 22)
- **Spring Boot Version**: 3.5.9
- **Build Tool**: Maven Wrapper (`mvnw`)

No external services (database, cache, messaging) were used.



🧪 Test Setup

Dataset
- A single JSON file (`events_1000.json`)
- Contains **exactly 1000 events**
- Events differ only by `eventId` to ensure a controlled benchmark
- Payload structure is consistent across events

Application Startup
```bash
mvnw spring-boot:run
Server runs at:

http://localhost:8080
```
🚀 Benchmark Execution

The benchmark measures end-to-end latency for ingesting a single batch of 1000 events
in one HTTP request.

Command used:
```bash
curl.exe -X POST http://localhost:8080/events/batch `
  -H "Content-Type: application/json" `
--data-binary "@events_1000.json"
```
This measures:

HTTP request handling
JSON parsing
Validation
Deduplication
Storage
Response generation

| Run                       | Time Taken    |
| ------------------------- | ------------- |
| First run (cold JVM)      | ~0.33 seconds |
| Subsequent run (warm JVM) | ~0.05 seconds |

Both runs are well below the 1 second requirement.

🧠 Observations

First run includes JVM warm-up and JIT compilation
Subsequent runs benefit from optimized code paths
Performance is achieved without tuning or external dependencies

✅ Conclusion

The system comfortably meets the performance requirement defined in the assignment.

✔ One batch of 1000 events is ingested in well under 1 second on a mid-range developer laptop.