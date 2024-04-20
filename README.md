# Patient Management Microservices

A Spring Boot microservices-based patient management system for healthcare facilities. Supports patient registration, appointment scheduling, EHR integration, and real-time clinical alerts.

## Architecture

```
                    ┌──────────────┐
                    │  API Gateway │
                    │   (Zuul)     │
                    └──────┬───────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
┌────────▼───────┐ ┌──────▼───────┐ ┌───────▼──────┐
│ Patient Service│ │ Appointment  │ │ Notification │
│  (Spring Boot) │ │   Service    │ │   Service    │
└────────┬───────┘ └──────┬───────┘ └───────┬──────┘
         │                │                 │
         └────────┬───────┘                 │
                  │                         │
           ┌──────▼───────┐         ┌───────▼──────┐
           │  PostgreSQL   │         │ Apache Kafka │
           └──────────────┘         └──────────────┘
```

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.x, Spring Cloud, Spring Data JPA
- **Database:** PostgreSQL
- **Messaging:** Apache Kafka
- **Service Discovery:** Eureka
- **API Gateway:** Zuul
- **Containerization:** Docker, Docker Compose
- **Testing:** JUnit 5, Mockito
- **API Docs:** Swagger/OpenAPI

## Modules

| Service | Port | Description |
|---------|------|-------------|
| eureka-server | 8761 | Service discovery |
| api-gateway | 8080 | API routing and load balancing |
| patient-service | 8081 | Patient CRUD, medical history |
| appointment-service | 8082 | Scheduling, availability |
| notification-service | 8083 | Kafka consumer, email/SMS alerts |

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Quick Start
```bash
# Start infrastructure (PostgreSQL, Kafka, Zookeeper)
docker-compose up -d

# Build all services
mvn clean install

# Run each service
cd eureka-server && mvn spring-boot:run &
cd patient-service && mvn spring-boot:run &
cd appointment-service && mvn spring-boot:run &
cd notification-service && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
```

### Docker Compose (full stack)
```bash
docker-compose -f docker-compose.full.yml up --build
```

## API Endpoints

### Patient Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/patients | List all patients |
| GET | /api/patients/{id} | Get patient by ID |
| POST | /api/patients | Register new patient |
| PUT | /api/patients/{id} | Update patient |
| DELETE | /api/patients/{id} | Delete patient |

### Appointment Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/appointments | List appointments |
| POST | /api/appointments | Book appointment |
| PUT | /api/appointments/{id}/cancel | Cancel appointment |

## Testing
```bash
mvn test
```
