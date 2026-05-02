# Smart Campus Sensor & Room Management API

> **5COSC022W Client-Server Architectures - University of Westminster (2025/26)**  
> A JAX-RS RESTful API built with Jersey and an embedded Grizzly HTTP server for managing campus rooms and IoT sensors.

---

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [How to Build and Run](#how-to-build-and-run)
- [API Endpoints](#api-endpoints)
- [Seeded Data](#seeded-data)
- [Sample curl Commands](#sample-curl-commands)
- [Error Responses](#error-responses)
- [Conceptual Report](#conceptual-report)

---

## Overview

The Smart Campus API provides a robust, scalable RESTful interface for campus facilities managers and automated building systems to manage rooms and IoT sensors across the university. It supports full room and sensor lifecycle management, nested sensor reading history, referential integrity enforcement, and a comprehensive error-handling strategy.

Key design decisions:
- **No database** - all state is held in thread-safe `ConcurrentHashMap` structures in a singleton `DataStore`
- **Embedded server** - runs as a standalone fat JAR with no servlet container needed
- **Versioned API** - all endpoints are under `/api/v1/`
- **HATEOAS** - a discovery endpoint provides navigation links to all resources

---

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 11 | Core language |
| Jersey (JAX-RS) | 2.39.1 | REST framework |
| Grizzly HTTP Server | embedded | Lightweight server |
| Jackson | 2.15.2 | JSON serialisation |
| Maven | 3.6+ | Build tool |

---

## Project Structure

```
smart-campus-jaxrs-api/
├── src/main/java/com/smartcampus/
│   ├── SmartCampusApplication.java          # Entry point, Grizzly server bootstrap
│   ├── exception/
│   │   ├── ErrorResponse.java               # Uniform error envelope
│   │   ├── GlobalExceptionMapper.java        # Catch-all 500 safety net
│   │   ├── LinkedResourceNotFoundException.java
│   │   ├── LinkedResourceNotFoundExceptionMapper.java  # 422
│   │   ├── RoomNotEmptyException.java
│   │   ├── RoomNotEmptyExceptionMapper.java  # 409
│   │   ├── RoomNotFoundException.java
│   │   ├── RoomNotFoundExceptionMapper.java  # 404
│   │   ├── SensorNotFoundException.java
│   │   ├── SensorNotFoundExceptionMapper.java # 404
│   │   ├── SensorUnavailableException.java
│   │   └── SensorUnavailableExceptionMapper.java # 403
│   ├── filter/
│   │   └── LoggingFilter.java               # Request/response logging
│   ├── model/
│   │   ├── Room.java
│   │   ├── Sensor.java
│   │   └── SensorReading.java
│   ├── resource/
│   │   ├── DiscoveryResource.java            # GET /api/v1/
│   │   ├── RoomResource.java                 # /api/v1/rooms
│   │   ├── SensorResource.java               # /api/v1/sensors
│   │   └── SensorReadingResource.java        # /api/v1/sensors/{id}/readings
│   └── service/
│       └── DataStore.java                    # Thread-safe in-memory store
└── pom.xml
```

---

## How to Build and Run

### Prerequisites

- Java 11 or higher installed
- Maven 3.6 or higher installed

Verify with:
```bash
java -version
mvn -version
```

### Step 1 - Clone the repository

```bash
git clone https://github.com/yourusername/smart-campus-jaxrs-api.git
cd smart-campus-jaxrs-api
```

### Step 2 - Build the fat JAR

```bash
mvn clean package
```

This compiles the project and bundles all dependencies into:
```
target/smart-campus-jaxrs-api-1.0.0.jar
```

### Step 3 - Run the server

```bash
java -jar target/smart-campus-jaxrs-api-1.0.0.jar
```

Expected output:
```
INFO: Smart Campus API started at: http://localhost:8080/api/v1/
INFO: Press CTRL+C to stop the server.
```

### Step 4 - Test the API

Use Postman or curl. Base URL: `http://localhost:8080/api/v1`

To stop the server: press `Ctrl + C`

---

## API Endpoints

### Part 1 - Discovery

| Method | Endpoint | Description | Success Status |
|--------|----------|-------------|---------------|
| GET | `/api/v1/` | Returns API metadata, version, contact info, and HATEOAS navigation links | 200 |

### Part 2 - Room Management

| Method | Endpoint | Description | Success Status |
|--------|----------|-------------|---------------|
| GET | `/api/v1/rooms` | Get all rooms (full objects) | 200 |
| POST | `/api/v1/rooms` | Create a new room | 201 + Location header |
| GET | `/api/v1/rooms/{roomId}` | Get a single room by ID | 200 |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room (blocked if sensors assigned) | 204 |

### Part 3 - Sensor Operations

| Method | Endpoint | Description | Success Status |
|--------|----------|-------------|---------------|
| GET | `/api/v1/sensors` | Get all sensors | 200 |
| GET | `/api/v1/sensors?type=CO2` | Filter sensors by type (case-insensitive) | 200 |
| GET | `/api/v1/sensors/{sensorId}` | Get a single sensor by ID | 200 |
| POST | `/api/v1/sensors` | Register a new sensor (validates roomId) | 201 + Location header |

### Part 4 - Sensor Readings (Sub-Resource)

| Method | Endpoint | Description | Success Status |
|--------|----------|-------------|---------------|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get full reading history for a sensor | 200 |
| POST | `/api/v1/sensors/{sensorId}/readings` | Append a new reading (updates parent currentValue) | 201 |

---

## Seeded Data

The API starts with the following data pre-loaded so you can test immediately without setup:

### Rooms

| ID | Name | Capacity |
|----|------|----------|
| LIB-301 | Library Quiet Study | 50 |
| LAB-101 | Computer Science Lab | 30 |

### Sensors

| ID | Type | Status | Room |
|----|------|--------|------|
| TEMP-001 | Temperature | ACTIVE | LIB-301 |
| CO2-001 | CO2 | ACTIVE | LIB-301 |
| OCC-001 | Occupancy | MAINTENANCE | LAB-101 |

---

## Sample curl Commands

### 1. Get API discovery info (Part 1)
```bash
curl -X GET http://localhost:8080/api/v1/
```

### 2. Get all rooms (Part 2)
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 3. Create a new room (Part 2)
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "HALL-202", "name": "Main Hall", "capacity": 100}'
```

### 4. Delete a room with sensors - triggers 409 (Part 2 / Part 5)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

### 5. Get all sensors filtered by type (Part 3)
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 6. Register a new sensor with valid roomId (Part 3)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"type": "Temperature", "roomId": "LAB-101"}'
```

### 7. Register a sensor with invalid roomId - triggers 422 (Part 3 / Part 5)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"type": "CO2", "roomId": "FAKE-999"}'
```

### 8. Get reading history for a sensor (Part 4)
```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

### 9. Post a new reading to an active sensor (Part 4)
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 24.7}'
```

### 10. Post a reading to a MAINTENANCE sensor - triggers 403 (Part 5)
```bash
curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 15.0}'
```

---

## Error Responses

Every error in the API returns a consistent JSON envelope - raw stack traces are never exposed:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Room not found with ID: FAKE-999",
  "timestamp": "2026-05-02T08:00:00Z"
}
```

| HTTP Status | Scenario | Exception Class |
|-------------|----------|----------------|
| 400 | Missing required fields in request body | — |
| 403 | Posting a reading to a MAINTENANCE or OFFLINE sensor | `SensorUnavailableException` |
| 404 | Room or sensor ID does not exist | `RoomNotFoundException` / `SensorNotFoundException` |
| 409 | Creating duplicate room or deleting room with sensors | `RoomNotEmptyException` |
| 422 | Sensor created with a roomId that does not exist | `LinkedResourceNotFoundException` |
| 500 | Any unexpected runtime error (stack trace never exposed) | `GlobalExceptionMapper` |

---

## Conceptual Report

### Part 1.1 - JAX-RS Resource Lifecycle & Thread Safety

**Q: What is the default lifecycle of a JAX-RS Resource class and how does it impact in-memory data management?**

By default, JAX-RS creates a **new instance** of every resource class for each incoming HTTP request (request-scoped lifecycle). This means resource classes are stateless - they cannot hold mutable instance fields for shared data because each request gets its own object, and any data stored on it disappears when the request ends.

This has a direct and critical impact on how shared state must be managed. In this project, all shared data - rooms, sensors, and readings - lives in the `DataStore` singleton, which is instantiated exactly once at class-load time by the JVM using eager initialisation. This is inherently thread-safe without requiring synchronisation on `getInstance()`.

Since JAX-RS creates a new resource instance per request, multiple requests can execute concurrently and all reach the same `DataStore`. To prevent race conditions, all collections use `ConcurrentHashMap`, which allows safe concurrent reads without locking. For write operations that must span multiple collections atomically - such as adding a sensor and simultaneously updating its parent room's sensor list — `synchronized` blocks are used to ensure no two threads interleave those writes. Without this, two concurrent POST requests could each pass the room-exists check but only one would correctly update the room's sensor list, causing a silent data inconsistency.

---

### Part 1.2 - HATEOAS and Self-Describing APIs

**Q: Why is HATEOAS considered a hallmark of advanced RESTful design and how does it benefit client developers?**

HATEOAS - Hypermedia as the Engine of Application State - is the principle that API responses include navigation links telling clients where to go next, rather than clients hardcoding every URL. This is considered the highest level of REST maturity (Richardson Maturity Level 3) because it makes the API genuinely self-describing.

The benefits over static documentation are significant. First, if a URL structure changes server-side, clients following the embedded links adapt automatically instead of breaking - the server owns the URL and the client simply follows what it receives. Second, a developer new to the API can start at the single discovery endpoint and navigate the entire system without reading any external documentation. Third, it reduces coupling between client and server — adding new resources requires no client changes as long as they appear in the discovery response. Static documentation becomes outdated the moment it is published; a HATEOAS endpoint is always current because it is the live API describing itself.

---

### Part 2.1 - Full Objects vs ID-Only Lists

**Q: What are the implications of returning only IDs versus returning full room objects when listing rooms?**

Returning only IDs forces the client to make a separate GET request for every room just to display basic information. With 100 rooms, that means 101 total HTTP requests — this is the N+1 problem and is seriously damaging to both performance and bandwidth. Each request carries its own TCP overhead, latency, and HTTP headers.

Returning full objects in a single call eliminates this entirely at the cost of a larger initial payload. For a campus management system where room objects are small and the number of rooms is manageable, full objects are the correct design choice. The bandwidth cost of the larger payload is far smaller than the cumulative cost of dozens of round-trip requests, and the client receives everything it needs to render a room list in one call.

---

### Part 2.2 - Idempotency of DELETE

**Q: Is the DELETE operation idempotent in your implementation?**

DELETE is considered idempotent in REST because the intended outcome - the resource no longer exists - is the same regardless of how many times the request is sent. In this implementation, the first DELETE on an existing empty room returns **204 No Content**. A second identical DELETE returns **404 Not Found** because the resource is gone. The response code differs, but the server state is identical after both calls — the room does not exist either way. This is correct REST behaviour and consistent with the HTTP specification, which defines idempotency in terms of server state, not response codes.

The one non-idempotent scenario is attempting to delete a room that still has sensors. This always returns 409 Conflict until the sensors are removed, which is an intentional business constraint to prevent orphaned sensor records.

---

### Part 3.1 - @Consumes and Content-Type Enforcement

**Q: What are the technical consequences if a client sends data in the wrong format such as text/plain?**

The `@Consumes(MediaType.APPLICATION_JSON)` annotation declares the media type this endpoint accepts. If a client sends a request with `Content-Type: text/plain` or `application/xml`, JAX-RS intercepts the request **before the method body is ever reached** and automatically returns **415 Unsupported Media Type**. No custom code is required — the framework handles this entirely through content negotiation. This protects the endpoint from receiving data it cannot safely parse and provides the client with a precise, actionable error code explaining exactly why the request was rejected.

---

### Part 3.2 - @QueryParam vs Path Segment for Filtering

**Q: Why is the query parameter approach superior to a path segment for filtering collections?**

Path segments like `/sensors/type/CO2` imply a fixed resource hierarchy - they suggest that CO2 is a distinct sub-resource of sensors, which is semantically incorrect. Query parameters like `/sensors?type=CO2` signal that you are **narrowing an existing collection**, which is exactly what filtering is.

Query parameters are also optional by nature, so the same single endpoint handles both the unfiltered case (`GET /sensors`) and the filtered case (`GET /sensors?type=CO2`) cleanly with no duplication. Using a path segment would require a separate endpoint, mislead clients about the resource structure, and make the API harder to evolve. The filtering in this implementation is also case-insensitive - `?type=co2`, `?type=CO2`, and `?type=Co2` all return identical results — which further reduces friction for API consumers.

---

### Part 4.1 - Sub-Resource Locator Pattern

**Q: What are the architectural benefits of the Sub-Resource Locator pattern?**

Without the sub-resource locator pattern, every nested path would be defined in a single class. `SensorResource` would handle sensors, readings, individual readings, and any future nesting added to the API. As the system grows this becomes an unmaintainable monolith - a single class with hundreds of methods and no clear responsibility boundary.

The locator pattern solves this by making `SensorResource` a **gateway** that validates the parent sensor exists, then delegates all reading logic to a dedicated `SensorReadingResource` class. Each class is focused on a single concern — `SensorResource` manages sensors, `SensorReadingResource` manages readings. This follows the Single Responsibility Principle directly. It also means the child resource can safely assume its sensor is always valid because the parent validated it before construction, eliminating defensive null checks throughout the child. Testing, debugging, and extending each layer independently becomes much simpler as a result.

---

### Part 5.2 - HTTP 422 vs 404 for Missing References

**Q: Why is HTTP 422 often more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?**

404 Not Found means the **requested endpoint** was not found. When a client POSTs a new sensor with an invalid `roomId`, the endpoint `/api/v1/sensors` was found successfully, the JSON was syntactically valid, and the request was fully understood. The problem is that a field **inside the payload** contains a broken reference - semantically equivalent to a foreign key violation in a relational database.

422 Unprocessable Entity communicates that the server understood the request and the content type is correct, but the **semantic content** of the payload cannot be processed. This is a far more accurate and informative signal. Returning 404 would mislead the client into thinking they called the wrong URL, sending them in the wrong direction when debugging. 422 tells them the URL was correct but their payload contained an invalid reference, pointing them immediately to the real problem.

---

### Part 5.4 - Cybersecurity Risks of Exposing Stack Traces

**Q: What are the cybersecurity risks of exposing internal Java stack traces to external API consumers?**

A raw Java stack trace exposes several categories of sensitive internal information. It reveals **internal class names and package structure**, allowing an attacker to reconstruct the architecture of the system. It exposes **framework and library versions** (e.g., Jersey 2.39.1, Grizzly), enabling the attacker to look up known CVEs for those exact versions. It shows **server-side file paths** revealing the directory structure of the deployment environment. It exposes **exact line numbers** where errors occur, allowing a targeted attacker to craft inputs designed to trigger specific failure points.

Combined, this information can be used to identify exploitable vulnerabilities, plan injection attacks against specific methods, and reverse-engineer the internal structure of the application. The `GlobalExceptionMapper` prevents all of this by catching every unhandled `Throwable`, logging the full detail server-side for developers, and returning only a safe generic message to the client — ensuring internal implementation details never cross the API boundary.

---

### Part 5.5 - JAX-RS Filters vs Manual Logging

**Q: Why is it better to use JAX-RS filters for logging rather than inserting Logger statements inside every resource method?**

Inserting `Logger.info()` calls inside every resource method violates the **Single Responsibility Principle** - resource methods exist to handle business logic, not observability. It also creates a maintenance risk: every new endpoint added to the API requires the developer to remember to add logging, and a single forgotten method means a gap in coverage with no warning.

A `ContainerRequestFilter` and `ContainerResponseFilter` registered as a `@Provider` apply **automatically to every matched route** with zero extra work per endpoint. This guarantees 100% logging coverage regardless of how many endpoints exist or are added in the future. It also centralises all logging logic in a single class — changing the log format, adding correlation IDs, or switching logging frameworks requires editing one file rather than dozens. This is a textbook application of the cross-cutting concerns pattern, and filters are exactly the mechanism JAX-RS provides for it.

---

## Author

**Aryan Paudel**  
University of Westminster  
5COSC022W — Client-Server Architectures  
2025/26
