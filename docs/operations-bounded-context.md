# Guía de Integración Frontend: Operations Bounded Context

Este documento describe el flujo y uso de los endpoints del Bounded Context de **Operations** desde la perspectiva del Frontend. Este módulo es el corazón del taller: se encarga de gestionar el flujo completo de las órdenes de trabajo (Work Orders), las tareas asignadas a los mecánicos y el uso de productos/repuestos.

Tras la refactorización para seguir estándares de **API REST Pura**, la API tiene una restricción estricta de anidamiento (máximo 2 niveles de URL) y los endpoints para creación de recursos ahora retornan un código `201 Created`.

---

## 🧠 Lógica Principal: ¿Cómo lidiar con los anidamientos y los IDs?

En versiones anteriores, una URL podía anidar hasta 4 niveles (ej. `/work-orders/{id}/tasks/{taskId}/products`). Para cumplir con API REST Pura, el backend ahora expone controladores separados:

1. **Creación:** Todas las entidades secundarias (Tareas) se crean *a través* del Aggregate Root (`WorkOrder`). Esto significa que para crear una tarea haces `POST /api/v1/work-orders/{workOrderId}/tasks`.
2. **Acciones Directas:** Una vez que la tarea existe y el frontend tiene su `taskId`, todas las modificaciones de estado o adiciones (como agregar productos, cambiar el estado a iniciado/completado) se hacen **directamente sobre el endpoint de tareas** (`/api/v1/tasks/{taskId}...`). **No** es necesario enviar ni conocer el ID de la Work Order para estas acciones.
3. **Obtención del Contexto:** El backend resuelve internamente a qué Work Order pertenece una Tarea mediante consultas a base de datos usando el `taskId`.

---

## 🚀 Flujos Principales de la Aplicación

### 1. Flujo de la Orden de Trabajo (Work Order)

La Orden de Trabajo (Work Order) es el documento central sobre el que se registra el estado del vehículo y todas las reparaciones a realizar.

*   **A. Crear una Nueva Orden:**
    El frontend recopila los datos de la cita, la sucursal, el vehículo y el diagnóstico inicial. Se envía una petición `POST /api/v1/work-orders`. La respuesta será un `201 Created` y devolverá la orden creada con su `workOrderId`. La orden inicia en estado `PENDING`.
*   **B. Actualizar la Orden:**
    Si el mecánico o el asesor necesitan actualizar el diagnóstico o el kilometraje, se envía un `PUT /api/v1/work-orders/{workOrderId}`.
*   **C. Marcar como Pagada:**
    Una vez que el cliente paga (gestionado en Billing), el frontend (o el backend internamente) puede notificar este estado con `POST /api/v1/work-orders/{workOrderId}/mark-as-paid`. Esto cambia el estado a `PAID` y emite eventos de dominio para retirar permanentemente del inventario los productos usados.

### 2. Flujo de las Tareas (Tasks)

Las tareas representan el trabajo específico asignado a un mecánico.

*   **A. Agregar Tarea:**
    Dentro de la vista de una orden, el frontend solicita agregar una tarea. Se envía `POST /api/v1/work-orders/{workOrderId}/tasks` enviando el servicio, el mecánico (`employeeId`), descripción y precio de mano de obra. La tarea se crea con código `201 Created` en estado `PENDING` y se obtiene un `taskId`.
*   **B. Modificar Tarea:**
    Si hay un error en los datos iniciales o se debe reasignar de mecánico, el frontend hace un `PUT /api/v1/work-orders/{workOrderId}/tasks/{taskId}`.
*   **C. Cambios de Estado:**
    El mecánico, desde su vista, interactúa directamente con la tarea usando su `taskId`.
    *   Para iniciar: `POST /api/v1/tasks/{taskId}/start`
    *   Para finalizar: `POST /api/v1/tasks/{taskId}/complete`
    *   Para reabrir: `POST /api/v1/tasks/{taskId}/reopen`
    *Estos endpoints desencadenan automáticamente cambios de estado en la Work Order padre (ej. pasar de PENDING a IN_PROGRESS si se inicia la primera tarea).*

### 3. Flujo de Productos en Tareas (Repuestos)

Los mecánicos pueden asociar repuestos físicos a sus tareas. Esto afecta el inventario y el costo total de la orden. Al estar bajo un controlador propio de tareas (`WorkOrderTasksController`), se evita romper la regla de máximo 2 niveles.

*   **A. Agregar Producto a Tarea:**
    Dentro del detalle de una tarea, el mecánico elige un repuesto. El frontend envía `POST /api/v1/tasks/{taskId}/products` indicando el producto y su precio/cantidad. (Responde `201 Created`). Esto efectúa una "reserva" lógica en el inventario.
*   **B. Modificar Cantidad:**
    Si el mecánico usa más o menos cantidad de un producto ya agregado, el frontend envía `PUT /api/v1/tasks/{taskId}/products/{productId}`. El backend calculará la diferencia (delta) para ajustar las reservas de inventario a favor o en contra.
*   **C. Eliminar Producto:**
    Si se retira el producto de la tarea por error, el frontend envía `DELETE /api/v1/tasks/{taskId}/products/{productId}`. El backend liberará la reserva de inventario al 100%.

---

## 📖 Referencia Rápida de Endpoints

### Work Orders
*   **Crear:** `POST /api/v1/work-orders` *(201 Created)*
*   **Listar (Por Sucursal):** `GET /api/v1/work-orders?branchId={branchId}`
*   **Listar (Por Vehículo):** `GET /api/v1/work-orders?vehicleId={vehicleId}`
*   **Obtener por ID:** `GET /api/v1/work-orders/{workOrderId}`
*   **Actualizar Detalles:** `PUT /api/v1/work-orders/{workOrderId}`
*   **Marcar como Pagada:** `POST /api/v1/work-orders/{workOrderId}/mark-as-paid`
*   **Eliminar (Soft Delete):** `DELETE /api/v1/work-orders/{workOrderId}`

### Tareas desde Work Orders (Gestión Inicial)
*   **Crear Tarea:** `POST /api/v1/work-orders/{workOrderId}/tasks` *(201 Created)*
*   **Actualizar Detalles de Tarea:** `PUT /api/v1/work-orders/{workOrderId}/tasks/{taskId}`
*   **Eliminar Tarea:** `DELETE /api/v1/work-orders/{workOrderId}/tasks/{taskId}`

### Acciones Directas en Tareas (Límite 2 Niveles)
*   **Iniciar:** `POST /api/v1/tasks/{taskId}/start`
*   **Completar:** `POST /api/v1/tasks/{taskId}/complete`
*   **Reabrir:** `POST /api/v1/tasks/{taskId}/reopen`
*   **Agregar Producto:** `POST /api/v1/tasks/{taskId}/products` *(201 Created)*
*   **Obtener Productos:** `GET /api/v1/tasks/{taskId}/products`
*   **Actualizar Producto:** `PUT /api/v1/tasks/{taskId}/products/{productId}`
*   **Eliminar Producto:** `DELETE /api/v1/tasks/{taskId}/products/{productId}`
