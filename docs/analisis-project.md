# Análisis del Proyecto

DriveOS Platform ("Atelier") es una plataforma SaaS de talleres automotrices. Es un monolito modular en Spring Boot (Java 26, Maven), PostgreSQL, JPA/Hibernate, con arquitectura hexagonal interna (DDD + CQRS) y 8 dominios ya separados:

| Dominio (Bounded Context) | Clases | Responsabilidad |
| :--- | :--- | :--- |
| **core** | 149 | Clientes, dueños, empleados, talleres, sucursales, suscripciones, perfiles |
| **iot** | 123 | Vehículos, dispositivos OBD2, telemetría, alertas DTC |
| **operations** | 89 | Órdenes de trabajo, tareas, servicios |
| **fleet** | 75 | Citas (appointments), registros |
| **iam** | 66 | Autenticación, JWT, recuperación de contraseña, Google OAuth |
| **billing** | 63 | Vouchers, pagos, cotizaciones, integración Facthub |
| **inventory** | 35 | Productos, lotes, stock |
| **shared** | 23 | Kernel compartido (Result, value objects, eventos, naming strategy) |

## Puntos clave detectados

- **Un solo deploy:** Una `@SpringBootApplication` (`DriveosPlatformApplication`), un `pom.xml` para todo.
- **Una sola BD:** PostgreSQL compartida con FKs cruzando todo el dominio (`docs/atelier-schema.sql`, ~30 tablas).
- **Acoplamiento real entre módulos:** Dependencias sincrónicas directas entre bounded contexts, no vía API:
  - `billing` → `core`, `inventory`, `operations` (llama a sus QueryService directamente)
  - `iot` → `core` (`CustomerQueryService`), `iam` (`UserDetailsImpl`)
  - `operations` → `inventory` (`ProductQueryService`)
  - `fleet` → `core` (`EmployeeId`)
  - `inventory` → `operations` (sus eventos de dominio)
- **Eventos en-memory:** Vía `ApplicationEventPublisher` (no un message broker): `VoucherPaidEvent` → `PaymentProcessedEvent` → `WorkOrderPaymentListener`, `ProductReservedEvent` → `InventoryStockListener`. Esto es código síncrono disfrazado de async.
- **Acoplamiento de seguridad:** La autorización vive en `iam` pero `iot` importa `UserDetailsImpl` directamente (no hay un auth-context compartido limpio).
- **Servicio externo ya integrado:** Facthub (facturación electrónica SUNAT) vía `RestTemplate`.

---

# Propuesta de migración a microservicios

## Fase 0 — Precondición: romper el acoplamiento en código (sin deploy split aún)

Antes de separar procesos, hay que eliminar las dependencias sincrónicas entre bounded contexts, reemplazándolas por contratos de integración:

- **Extraer `shared` a una librería** (`driveos-shared-kernel` o similar): `Result`, `ApplicationError`, value objects (`Money`, `BranchId`, `VehicleId`, `CustomerId`, `Mileage`), `AbstractDomainAggregateRoot`, naming strategy, entity base y los eventos de integración (`PaymentProcessedEvent`, `ProductReservedEvent`, etc.). Cada servicio dependerá de esta lib como artifact versionado.
- **Reemplazar llamadas directas a `QueryService` entre módulos por:**
  - API cliente (REST/HTTP o gRPC), o
  - Eventos/datos replicados (cada servicio posee su propia proyección de datos ajenos).
- **Mover la autorización a un auth-context compartido:** Exponer `/me` y propagar claims del JWT (`userId`, `roles`, `branchId`) sin importar `UserDetailsImpl` en otros servicios. Un API Gateway valida el JWT.

## Fase 1 — Extracción por estratos (Strangler Fig)

Extraer primero los servicios con menos acoplamiento y límites claros, dejando el resto en el monolito:

1. **`iam-service` (autenticación):** Es el más natural: dueño de users, JWT, OAuth. Se convierte en el identity provider.
2. **`billing-service` + Facthub:** Dueño de vouchers/pagos/facturación; consume datos de work order.
3. **`iot-service` (telemetría OBD2):** Alto volumen, ideal candidato a escalado independiente y colas.
4. **`inventory-service` (stock) y `operations-service` (work orders):** Fuertemente acoplados entre sí por eventos de reserva de stock; extraerlos juntos o con un broker.
5. **`core-service` (perfiles/talleres) y `fleet-service` (citas):** Los más "pegados", se extraen al final.

## Fase 2 — Infraestructura distribuida

- **API Gateway (Spring Cloud Gateway):** Enrutamiento, autenticación JWT, rate limiting.
- **Service Discovery (Eureka/Consul):** Si se necesitan múltiples instancias.
- **Message Broker (RabbitMQ/Kafka):** Para reemplazar `ApplicationEventPublisher` por eventos reales: `PaymentProcessed`, `ProductReserved/Canceled`, `WorkOrderPaid`.
- **Base de datos por servicio (Database-per-service):** Romper las FKs cruzadas → cada servicio posee sus tablas y se integra por IDs (no FKs) + eventos/sagas.
- **Sagas:** Para flujos transaccionales cross-service (ej. pago → marcar work order como pagado → descontar stock).

---

# Monorepo vs. Multirepo

**Recomendación:** Monorepo.

## Razones

- Es un único producto con dominios altamente interdependientes y un flujo transaccional común (work order → inventario → facturación → pago). Los cambios suelen tocar varios servicios a la vez (p. ej. un evento que cruza 3 servicios).
- Tienen un kernel compartido (`shared`) que es consumido por todos; versionarlo cruzando repos sería un dolor operativo constante (publicar artifact, bump versiones en N repos).
- Equipo actualmente unificado (forkdevs); no hay necesidad de permisos/ownership separados por repo.
- Facilita CI/CD unificado, testeo integrado y refactors transversales (muy útil justo durante la transición monolito→microservicios). Puedes compartir protos/contratos de eventos en un solo lugar.
- El multirepo convendría solo si: los servicios fueran mantenidos por equipos independientes con ciclos de release desacoplados, o si alguno debiera ser open-source/versionado por separado (nada de eso aplica aquí).

## Estructura monorepo sugerida

```text
driveos-platform/
├── apps/                     # servicios Spring Boot
│   ├── api-gateway/
│   ├── iam-service/
│   ├── core-service/
│   ├── iot-service/
│   ├── operations-service/
│   ├── inventory-service/
│   ├── billing-service/
│   └── fleet-service/
├── libs/                     # librerías compartidas
│   ├── shared-kernel/        # Result, VOs, eventos, naming
│   └── auth/                 # JWT claims, security filters
├── contracts/                # OpenAPI / Avro / Protobuf / event schemas
├── deploy/                   # docker-compose, k8s, Helm
└── pom.xml                   # Maven parent (multi-module)