# DDD en la Migración a Microservicios

Este documento responde a la pregunta clave de la migración: **¿seguimos trabajando bajo DDD en cada microservicio?** La respuesta es **sí**, y no solo eso: el DDD que ya aplica el proyecto es su principal activo para llevar a cabo la migración con éxito.

---

## 1. Respuesta directa

El paso a microservicios **no cambia el modelo de dominio**, cambia dos cosas:

1. **El límite de despliegue** — cada `bounded context` pasa de ser un paquete dentro de un monolito a ser un proceso (servicio) independiente.
2. **El mecanismo de integración** — las llamadas Java directas y los eventos en-memory (`ApplicationEventPublisher`) se reemplazan por HTTP/gRPC y por un message broker.

La estructura hexagonal interna que ya existe por dominio se **conserva tal cual** dentro de cada microservicio:

```text
dominio/
  application/        (command/query services, puertos)
  domain/             (aggregates, entidades, VOs, repositorios, eventos)
  infrastructure/     (JPA, hashing, tokens, outbound adapters)
  interfaces/         (rest, events)
```

---

## 2. Correspondencia: lo que cambia, sigue siendo DDD

Los patrones DDD tienen nombres concretos para cada cosa que va a cambiar. Aquí la traducción:

| Hoy (monolito modular) | Mañana (microservicios) | Patrón DDD |
| :--- | :--- | :--- |
| `ApplicationEventPublisher` en-memory | RabbitMQ / Kafka | **Domain Events** → **Integration Events** |
| `ProductReservedEvent`, `PaymentProcessedEvent` | Mensajes en un topic | **Integration Events** |
| Llamadas directas a `QueryService` de otro módulo | Consumir su API o sus eventos | **Anti-Corruption Layer (ACL)** / **Open Host Service** |
| `shared/` (value objects, Result, eventos) | `shared-kernel` como librería versionada | **Shared Kernel** |
| IDs (`branchId`, `vehicleId`) sin FK | IDs + proyección leída por eventos | **Associations by Identifier** |
| — | Un servicio que replica/dedupe datos ajenos | **Published Language / Conformist** |

---

## 3. Los patrones DDD que cobran MÁS protagonismo

### 3.1 Anti-Corruption Layer (ACL)

Hoy el proyecto rompe este principio: `operations` importa `GetProductByIdQuery` de `inventory`, y `iot` importa `UserDetailsImpl` de `iam`. Eso contamina los modelos entre contextos.

Con microservicios, cada contexto debe definir su **propio modelo local** de los datos ajenos y traducir en un adaptador dedicado (ACL). Así el modelo de `operations` jamás "se entera" de los detalles internos de `inventory` o `iam`.

- **Antes:** `import com...inventory.domain...GetProductByIdQuery`
- **Después:** un cliente HTTP `ProductClient` + un DTO propio de `operations` (`ProductInfo`), mapeado en la ACL.

### 3.2 Sagas

Los flujos transaccionales que hoy son transacciones locales pasan a **sagas orquestadas por eventos**, la aplicación DDD a la consistencia eventual.

Ejemplo actual del proyecto: `VoucherPaidEvent → PaymentProcessedEvent → WorkOrderPaymentListener` y `ProductReservedEvent → InventoryStockListener`. Esto hoy corre en la misma JVM. Tras la migración se convierte en una saga real distribuida:

```text
billing  --PaymentProcessed-->  operations  --WorkOrderPaid-->  inventory
                                   (mark-as-paid)               (descontar stock físico)
```

### 3.3 Context Mapping

La relación entre `core`, `operations`, `billing`, `inventory`, `iot` y `fleet` es exactamente un **context map**. Definir quién es *upstream* y quién *downstream* es el insumo para decidir el orden de extracción y el diseño de cada contrato.

---

## 4. Context Map de DriveOS

Tabla de relaciones entre bounded contexts y el patrón de integración recomendado.

| Upstream (dueño del dato) | Downstream (consumidor) | Dato/Servicio | Patrón DDC | Mecanismo |
| :--- | :--- | :--- | :--- | :--- |
| `iam` | todos | `userId`, roles, claims JWT | **Open Host Service / Published Language** | API Gateway + JWT claims |
| `core` | `iot`, `fleet`, `billing`, `operations` | `branchId`, `customerId`, `employeeId`, `workshopId` | **Published Language** | IDs + eventos de referencia |
| `operations` | `inventory` | ID de producto reservado | **Anti-Corruption Layer** | Eventos `ProductReserved/Canceled` |
| `operations` | `billing` | `workOrderId` a facturar | **Anti-Corruption Layer** | API / evento |
| `billing` | `operations` | pago confirmado | **Saga (evento)** | `PaymentProcessedEvent` |
| `inventory` | `operations`, `billing` | stock, producto | **Anti-Corruption Layer** | API o proyección leída |
| `core` | `core` (interno) | owner → workshop → branch → subscription | **Aggregate (mismo contexto)** | transacciones locales |

### Lectura clave

- `iam` es el **upstream de autenticación** para todos: publica el `userId` y los claims; nadie debe depender de sus clases internas (`UserDetailsImpl`).
- `core` es el **upstream de identidades de negocio** (`branchId`, `customerId`, etc.); los demás consumen esos IDs **por referencia**, nunca por FK.
- `operations` y `billing` comparten un **flujo de saga** para el pago de la orden de trabajo.
- `inventory` reacciona a eventos de `operations`; son contextos separados unidos por eventos, no por tablas.

---

## 5. La regla de oro que ya se respeta

> **Un microservicio bien diseñado = un bounded context bien definido.**

El proyecto ya tiene los bounded contexts identificados. Lo que falta es **hacer sus límites físicos** en lugar de solo lógicos. El mapeo correcto es **por bounded context, no por agregado**:

| Servicio | Agregados/Entidades que agrupa (juntos) |
| :--- | :--- |
| `operations-service` | `WorkOrder` + `WorkOrderTask` + `WorkOrderTaskProduct` |
| `billing-service` | `Quote` + `Voucher` + `Payment` |
| `inventory-service` | `Product` + `ProductBatch` |
| `iot-service` | `Vehicle` + `Obd2Device` + `TelemetrySnapshot` + `DtcAlert` |
| `core-service` | `Customer` + `Employee` + `Owner` + `Workshop` + `Branch` + `BranchSubscription` |
| `iam-service` | `User` + `PasswordRecoveryToken` |
| `fleet-service` | `Appointment` + `CustomerRegistration` + `EmployeeRegistration` |

---

## 6. Riesgo a evitar (microservicio anémico)

**NO** caer en "un microservicio por agregado". Dividir `WorkOrder`, `Task` y `TaskProduct` en servicios separados rompería el aggregate y multiplicaría acoplamiento. La unidad de división es el **bounded context**, no la entidad.

Señales de alerta a evitar:

- Un servicio por tabla.
- Un servicio que solo hace CRUD de una entidad (sin comportamiento de dominio).
- Comunicación síncrona entre casi todos los servicios (chatty microservices).

---

## 7. Resumen

| Aspecto | Conclusión |
| :--- | :--- |
| ¿Sigue DDD? | Sí, y es el activo principal de la migración |
| ¿Qué cambia? | Límite de despliegue + mecanismo de integración |
| ¿Qué impulsa? | ACL, sagas, context map, shared kernel, asociaciones por ID |
| ¿Qué NO hacer? | Microservicio por agregado / anémico |
| Unidad de división | El bounded context, no la entidad |

La migración es, en esencia, un ejercicio de **Context Mapping aplicado a la infraestructura**: convertir límites lógicos ya existentes en límites físicos, preservando la integridad de cada modelo de dominio.