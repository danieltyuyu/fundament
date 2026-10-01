# DriveOS Platform (Monorepo Microservices)

DriveOS Platform is an enterprise SaaS application for automotive workshop management ("Atelier"), built with Java 26, Spring Boot 4, Maven Multi-Module Monorepo architecture, and Domain-Driven Design (DDD).

## 🚀 Architecture Overview

The platform is decomposed into 7 autonomous microservices plus a central API Gateway and 2 shared kernel libraries:

```text
driveos-platform/
├── libs/
│   ├── shared-kernel/     # Pure domain primitives (Money, Result, ValueObjects, GlobalExceptionHandler)
│   └── auth/              # Shared JWT Security filter & Token parser
└── apps/
    ├── api-gateway/       # Port 8080 - Reverse Proxy & Entrypoint
    ├── iam-service/       # Port 8081 - Auth & IAM
    ├── billing-service/   # Port 8082 - Quotes, Vouchers & Facthub Integration
    ├── iot-service/       # Port 8083 - OBD2 Telemetry & DTC Alerts
    ├── operations-service/# Port 8084 - Work Orders & Tasks
    ├── inventory-service/ # Port 8085 - Products, Batches & Stock
    ├── core-service/      # Port 8086 - Workshops, Branches, Customers & Employees
    └── fleet-service/     # Port 8087 - Vehicle Fleets & Appointments
```

## 🛠️ Requirements & Building

- **Java**: JDK 26
- **Maven**: 3.9+ (or `./mvnw` wrapper included)

### Build & Run Tests

```bash
./mvnw clean test
```

### Documentation

- [Microservices Architecture & Deployment Specification](docs/microservices-architecture-and-deployment.md)
- [DDD Microservices Strategy](docs/ddd-microservicios.md)
- [API REST Integration Guide](docs/api-docs.md)
