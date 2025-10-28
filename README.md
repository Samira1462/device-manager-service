# 🖥️ Device Manager Service

A Spring Boot–based REST API for managing devices.  
This service supports creating, updating, deleting, searching, and listing devices, with pagination, validation, and OpenAPI (Swagger) documentation.

---

## 🚀 Features

- CRUD operations for devices
- Search devices by brand or state
- Validation and exception handling
- Pagination and sorting
- DTO-based architecture
- JPA + PostgreSQL persistence
- Comprehensive unit & integration tests
- OpenAPI 3 / Swagger UI documentation

---

## 🧩 Tech Stack

| Layer | Technology                     |
|-------|--------------------------------|
| Framework | Spring Boot 3.x                |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Database | PostgreSQL                     |
| ORM | Spring Data JPA / Hibernate    |
| Build Tool | Maven                          |
| Tests | JUnit 5, Mockito, RestAssured  |
| Java Version | 21+                            |

---

## 🛠️ Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **PostgreSQL 16**
- **Docker**

---

## ⚙️ Environment Variables

You can configure the app via environment variables or directly in `application.yml`.

---

## 🧰 Setup Instructions

### 1️⃣ Clone the repository

```bash
git clone git@github.com:Samira1462/device-manager-service.git
cd device-manager-service
```

### 2️⃣ Configure database
This project uses **PostgreSQL** as its main database.  
To make setup easy, you can run the database inside a Docker container.
```bash
docker-compose up -d
```

### 3️⃣ Run the application

```bash
mvn spring-boot:run
```

---

## 🧭 API Documentation (Swagger UI)

Once the application is running, open:

👉 **http://localhost:8080/swagger-ui/index.html**

You’ll see detailed API documentation with:
- Descriptions for each endpoint
- Request/response schemas
- Example payloads

Swagger is powered by **Springdoc OpenAPI**, configured in:

```
com.codechallenge.devicemanagerservice.config.OpenApiConfig
```

---

## 🔍 Example Endpoints

| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/devices` | Create a new device |
| `GET` | `/api/devices/{id}` | Get device by ID |
| `PUT` | `/api/devices/{id}` | Update device |
| `DELETE` | `/api/devices/{id}` | Delete device |
| `GET` | `/api/devices` | Get all devices (paged) |
| `GET` | `/api/devices/search?brand=Apple&state=AVAILABLE` | Search devices by brand/state |

---

## 🧪 Testing

### Run all tests
```bash
mvn test
```

The project includes:
- **Unit tests** (with Mockito)
- **Integration tests** (with RestAssured)

---

## 🧾 Example JSONs

### Create Device
```json
{
  "name": "MacBook Pro 16",
  "brand": "Apple",
  "state": "AVAILABLE"
}
```

### Update Device
```json
{
  "name": "MacBook Pro 14",
  "brand": "Apple",
  "state": "IN_USE"
}
```

---

## 👩‍💻 Author

**Samira Radmaneshfar**  
💼 Code Challenge 2025 — Device Manager Service  
📧 [Samira.Rad1462@gmail.com]
