# Guía de Integración Backend (API REST) - Atelier Platform

Esta guía está diseñada para el equipo de **Frontend**. Su propósito es explicar la arquitectura orientada a dominios (DDD) del backend, detallar cómo se interconectan los microservicios internos (Bounded Contexts) y mostrar ejemplos prácticos de los flujos de trabajo más importantes.

---

## 1. Arquitectura General y Autenticación (IAM)
**URL Base (Producción):** `https://atelier-platform.onrender.com`

El sistema Atelier está protegido mediante **JSON Web Tokens (JWT)**. Todas las peticiones al backend desplegado en Render (a excepción del login/registro) deben enviar un token válido en la cabecera HTTP:
`Authorization: Bearer <tu_token_aqui>`

### 🔑 Autenticación (Login)
* **Endpoint:** `POST /api/v1/authentication/sign-in`
* **Descripción:** Intercambia credenciales por un JWT y el ID del usuario.
* **Request:**
  ```json
  {
    "username": "taller@atelier.com",
    "password": "password123"
  }
  ```
* **Response (200 OK):**
  ```json
  {
    "id": 1,
    "username": "taller@atelier.com",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

---

## 2. Core Context (Perfiles, Talleres y Sucursales)
Este contexto maneja la estructura empresarial. Un Taller (Workshop) es dueño de múltiples Sucursales (Branches). Los mecánicos y clientes se registran aquí.

### 🏢 Obtener Sucursales (Branches)
* **Endpoint:** `GET /api/v1/branches/workshop/{workshopId}`
* **Descripción:** El frontend debe llamar a este endpoint al iniciar sesión para saber en qué sucursal está operando el usuario actual. Retornará el `branchId` necesario para casi todas las demás operaciones.

---

## 3. Operations Context (El Corazón del Negocio)
Aquí ocurre el trabajo del taller. El flujo típico es: Recepción de vehículo -> Diagnóstico -> Asignación de Tareas y Repuestos.

### 📋 Crear una Orden de Trabajo (Work Order)
Cuando un vehículo llega al taller, se abre una orden de trabajo.
* **Endpoint:** `POST /api/v1/work-orders`
* **Request:**
  ```json
  {
    "appointmentId": "76afc307-019b-4915-940e-1688b439efbd",
    "branchId": "827d14c8-2543-41c5-b33c-ef9b529dcd3e",
    "vehicleId": "11111111-1111-1111-1111-111111111111",
    "customerId": "85f93177-95e5-456d-9597-3cc3cadd91d5",
    "diagnosticSummary": "Fallo en los frenos y cambio de aceite",
    "mileageIn": 45000
  }
  ```

### 🔧 Añadir Tareas (Servicios) a la Orden
Una orden recién creada cuesta `0.00`. El mecánico debe agregarle tareas para que tenga precio.
* **Endpoint:** `POST /api/v1/work-orders/{workOrderId}/tasks`
* **Request:**
  ```json
  {
    "serviceId": "33333111-1111-1111-1111-111111111111",
    "assignedMechanicId": "44444444-4444-4444-4444-444444444444",
    "description": "Cambio de pastillas de freno"
  }
  ```

---

## 4. Billing Context (Cotización y Facturación)
Una vez que Operaciones termina de agregar tareas y repuestos, el flujo pasa a Facturación. El frontend debe guiar al usuario por un proceso de 3 pasos:

### Paso 1: Generar la Cotización (DRAFT)
Toma el total acumulado de la Orden de Trabajo y genera una proforma.
* **Endpoint:** `POST /api/v1/quotes`
* **Request:**
  ```json
  {
    "workOrderId": "ID_DE_LA_ORDEN",
    "branchId": "ID_DE_LA_SUCURSAL",
    "discountPercentage": 0.0
  }
  ```

### Paso 2: Aprobar la Cotización (APPROVED)
El cliente acepta el presupuesto.
* **Endpoint:** `POST /api/v1/quotes/{quoteId}/approvals`
* *(No requiere body)*

### Paso 3: Emitir el Comprobante (Voucher) a SUNAT
El backend se comunica con la pasarela Facthub para emitir el XML oficial. **Atención Frontend:** Si el `totalAmount` es 0, este endpoint fallará con un HTTP 400.
* **Endpoint:** `POST /api/v1/vouchers`
* **Request:**
  ```json
  {
    "quoteId": "ID_DE_LA_COTIZACION",
    "type": "RECEIPT", 
    "customerDocumentType": "DNI",
    "customerDocumentNumber": "77777777",
    "customerName": "Juan Carlos"
  }
  ```
*(Usa `type: "INVOICE"` y `customerDocumentType: "RUC"` para Facturas).*

### Paso 4: Registrar el Pago
* **Endpoint:** `POST /api/v1/vouchers/{voucherId}/payments`
* **Request:**
  ```json
  {
    "amount": 150.00,
    "method": "CREDIT_CARD"
  }
  ```

---

## 5. IoT & Fleet Contexts (Módulos Adicionales)
* **IoT (Telemetría):** Procesa datos en tiempo real de los dispositivos OBD2 conectados a los vehículos. 
* **Fleet (Citas):** Maneja la programación de citas de clientes mediante el `AppointmentsController`.

---

## 💡 Nota Importante para el Frontend Developer
El backend de Atelier expone más de **30 endpoints diferentes**. Para ver la documentación exhaustiva, en vivo, con **schemas (modelos exactos)** y **tipos de datos precisos**, dirígete a nuestra interfaz oficial de Swagger:

🔗 **`https://atelier-platform.onrender.com/swagger-ui.html`**

En Swagger podrás:
1. Autenticarte usando el botón **Authorize** (pegando allí tu token JWT).
2. Ver todos los campos opcionales y obligatorios de cada JSON.
3. Probar llamadas HTTP reales contra la base de datos de staging.

---

## 🗺️ Anexo: Mapa Completo de Endpoints
Para que tengas una visión global, aquí tienes el listado de todas las rutas (APIs) disponibles agrupadas por módulo. Revisa Swagger para ver el detalle (GET, POST, PUT, DELETE) de cada una.

**Autenticación & Usuarios (IAM)**
- `/api/v1/authentication/sign-in`
- `/api/v1/authentication/sign-up`
- `/api/v1/users/{userId}*`
- `/api/v1/users?email={email}`

**Perfiles, Talleres y Sucursales (Core Context)**
- `/api/v1/profiles`
- `/api/v1/workshops`
- `/api/v1/branches`
- `/api/v1/employees`
- `/api/v1/customers`
- `/api/v1/owners`

**Gestión de Órdenes y Servicios (Operations Context)**
- `/api/v1/work-orders`
- `/api/v1/work-orders/{id}/tasks`
- `/api/v1/work-orders/{id}/tasks/{taskId}/products`
- `/api/v1/services`

**Cotizaciones y Facturación (Billing Context)**
- `/api/v1/checkouts`
- `/api/v1/quotes`
- `/api/v1/vouchers`
- `/api/v1/vouchers/{voucherId}/payments`

**Inventario de Repuestos (Inventory Context)**
- `/api/v1/products`
- `/api/v1/products/{productId}/batches`

**Citas y Flotas (Fleet Context)**
- `/api/v1/appointments`

**Telemetría y Dispositivos OBD2 (IoT Context)**
- `/api/v1/vehicles`
- `/api/v1/obd2-devices`
- `/api/v1/obd2-device-registrations`
- `/api/v1/vh-telemetry-batches`
- `/api/v1/customer-vehicles`


---

## 📚 Referencia Completa de Endpoints

A continuación, se detalla **CADA ENDPOINT** disponible en el sistema con sus parámetros, body a enviar y lo que vas a recibir.

### `GET /api/v1/users`
**Propósito:** Get user by email

*Retrieves the details of a specific user using their email address. Useful for checking if a user account exists before creating a customer profile.*

**📍 Parámetros (URL / Query):**
- `email` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "email": "string"
  }
  ```
- **Código HTTP 404**: Not Found

---

### `GET /api/v1/profiles`
**Propósito:** Get profile by document number

*Searches for a user profile using their DNI/RUC. Useful for finding a user before registering them as an employee.*

**📍 Parámetros (URL / Query):**
- `documentNumber` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "profileId": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "profileType": "CUSTOMER"
  }
  ```
- **Código HTTP 404**: Not Found

---

### `GET /api/v1/workshops/{workshopId}`
**Propósito:** Get a workshop by ID

*Retrieves the details of a specific workshop*

**📍 Parámetros (URL / Query):**
- `workshopId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "ownerId": "string",
    "businessName": "string",
    "brandName": "string",
    "taxId": "string",
    "mileageIntervalConfig": 0
  }
  ```

---

### `PUT /api/v1/workshops/{workshopId}`
**Propósito:** Update an existing workshop

*Updates the details of a workshop by its ID*

**📍 Parámetros (URL / Query):**
- `workshopId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "businessName": "string_value",
  "brandName": "string_value",
  "taxId": "string_value",
  "mileageIntervalConfig": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "ownerId": "string",
    "businessName": "string",
    "brandName": "string",
    "taxId": "string",
    "mileageIntervalConfig": 0
  }
  ```

---

### `GET /api/v1/work-orders/{id}`
**Propósito:** Get a Work Order by ID

*Retrieves the details of a specific Work Order*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/work-orders/{id}`
**Propósito:** Update Work Order details

*Updates the diagnostic summary and mileage of a Work Order*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "diagnosticSummary": "string_value",
  "mileageIn": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/work-orders/{id}`
**Propósito:** Soft delete a Work Order

*Soft deletes a Work Order, releasing all active stock reservations*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/work-orders/{id}/tasks/{taskId}`
**Propósito:** Update mechanic task details

*Updates the details of a specific mechanic task*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "serviceId": "string_value",
  "assignedMechanicId": "string_value",
  "description": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/work-orders/{id}/tasks/{taskId}`
**Propósito:** Remove a task from the Work Order

*Removes a task from the Work Order, releasing all its stock reservations*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/work-orders/{id}/tasks/{taskId}/products/{productId}`
**Propósito:** Update a product's quantity in a task

*Updates the quantity of a product used in a specific mechanic task*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)
- `productId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "quantity": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/work-orders/{id}/tasks/{taskId}/products/{productId}`
**Propósito:** Remove a product/part from a task

*Removes a product from a task, releasing its stock reservation*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)
- `productId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/vehicles/{id}`
**Propósito:** Update client vehicle

*Updates client vehicle details by its unique identifier*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "plateNumber": "string_value",
  "brand": "string_value",
  "model": "string_value",
  "year": 0,
  "vin": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/vehicles/{id}`
**Propósito:** Delete client vehicle

*Performs a soft delete of a vehicle and deactivates active driver/OBD2 links*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/users/{userId}/password`
**Propósito:** Update user password

*Updates the password of a specific user*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "currentPassword": "string_value",
  "newPassword": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/users/{userId}/email`
**Propósito:** Update user email

*Updates the email address of a specific user and returns a new authentication token*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "email": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/services/service/{serviceId}`
**Propósito:** Update a service

*Updates an existing service using the service ID*

**📍 Parámetros (URL / Query):**
- `serviceId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "name": "string_value",
  "price": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "name": "string",
    "price": 0
  }
  ```

---

### `DELETE /api/v1/services/service/{serviceId}`
**Propósito:** Delete a service

*Deletes an existing service using the service ID*

**📍 Parámetros (URL / Query):**
- `serviceId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/quotes/{id}`
**Propósito:** Get quote by ID

*Retrieves a Quote by its unique identifier*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "workOrderId": "string",
    "branchId": "string",
    "subtotalAmount": 0,
    "discountPercentage": 0,
    "totalAmount": 0,
    "status": "string"
  }
  ```

---

### `PUT /api/v1/quotes/{id}`
**Propósito:** Update quote discount

*Updates the discount percentage of an existing DRAFT quote*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "discountPercentage": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/owners/user/{userId}`
**Propósito:** Update an owner profile

*Updates an existing owner profile using the user ID*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "firstName": "string_value",
  "lastName": "string_value",
  "documentType": "string_value",
  "documentNumber": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `DELETE /api/v1/owners/user/{userId}`
**Propósito:** Delete an owner profile

*Deletes an existing owner profile using the user ID*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/obd2-devices/{id}`
**Propósito:** Get OBD2 device by ID

*Retrieves the details of a registered OBD2 device by its unique ID*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "macAddress": "string",
    "status": "string"
  }
  ```

---

### `PUT /api/v1/obd2-devices/{id}`
**Propósito:** Update an OBD2 device

*Updates an existing registered OBD2 device's details (such as MAC address)*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "macAddress": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/obd2-devices/{id}`
**Propósito:** Delete an OBD2 device

*Performs a soft delete of an OBD2 device by its unique ID*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/inventory/products/{productId}`
**Propósito:** Get product details by ID

*Retrieves all details for a product including its associated batches*

**📍 Parámetros (URL / Query):**
- `productId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "category": "string",
    "name": "string",
    "sku": "string",
    "description": "string",
    "salePrice": 0,
    "minimumStock": 0,
    "currentStock": 0,
    "batches": array
  }
  ```

---

### `PUT /api/v1/inventory/products/{productId}`
**Propósito:** Update product details

*Updates the basic details of a product (Name, Category, SKU)*

**📍 Parámetros (URL / Query):**
- `productId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "name": "string_value",
  "category": "string_value",
  "sku": "string_value",
  "description": "string_value",
  "salePrice": 0,
  "minimumStock": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "category": "string",
    "name": "string",
    "sku": "string",
    "description": "string",
    "salePrice": 0,
    "minimumStock": 0,
    "currentStock": 0
  }
  ```

---

### `DELETE /api/v1/inventory/products/{productId}`
**Propósito:** Delete a product

*Deletes a product and all its associated batches*

**📍 Parámetros (URL / Query):**
- `productId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/employees/user/{userId}`
**Propósito:** Update an employee profile

*Updates an existing employee profile using the user ID*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "firstName": "string_value",
  "lastName": "string_value",
  "documentType": "string_value",
  "documentNumber": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `DELETE /api/v1/employees/user/{userId}`
**Propósito:** Delete an employee profile

*Deletes an existing employee profile using the user ID*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/customers/user/{userId}`
**Propósito:** Update a customer profile

*Updates an existing customer profile using the user ID*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "firstName": "string_value",
  "lastName": "string_value",
  "businessName": "string_value",
  "documentType": "string_value",
  "documentNumber": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "isCorporate": false,
    "firstName": "string",
    "lastName": "string",
    "businessName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `DELETE /api/v1/customers/user/{userId}`
**Propósito:** Delete a customer profile

*Deletes an existing customer profile using the user ID*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/branches/{branchId}`
**Propósito:** Get a branch by ID

*Retrieves the details of a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "workshopId": "string",
    "code": "string",
    "name": "string",
    "address": "string",
    "phone": "string"
  }
  ```

---

### `PUT /api/v1/branches/{branchId}`
**Propósito:** Update an existing branch

*Updates the details of a branch by its ID*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "code": "string_value",
  "name": "string_value",
  "address": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "workshopId": "string",
    "code": "string",
    "name": "string",
    "address": "string",
    "phone": "string"
  }
  ```

---

### `GET /api/v1/appointments/{appointmentId}`
**Propósito:** Get appointment by ID

*Returns the detail of a single appointment by its ID*

**📍 Parámetros (URL / Query):**
- `appointmentId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `PUT /api/v1/appointments/{appointmentId}`
**Propósito:** Update an appointment

*Updates an existing appointment by ID*

**📍 Parámetros (URL / Query):**
- `appointmentId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "branchId": "string_value",
  "customerId": "string_value",
  "vehicleId": "string_value",
  "scheduledStart": "string_value",
  "notes": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/appointments/{appointmentId}`
**Propósito:** Delete an appointment

*Soft deletes an appointment by ID*

**📍 Parámetros (URL / Query):**
- `appointmentId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/workshops`
**Propósito:** Create a new workshop

*Creates a new workshop*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "ownerId": "string_value",
  "businessName": "string_value",
  "brandName": "string_value",
  "taxId": "string_value",
  "mileageIntervalConfig": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "ownerId": "string",
    "businessName": "string",
    "brandName": "string",
    "taxId": "string",
    "mileageIntervalConfig": 0
  }
  ```

---

### `POST /api/v1/work-orders`
**Propósito:** Create a new Work Order

*Creates a new Work Order for a specific branch and vehicle*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "appointmentId": "string_value",
  "branchId": "string_value",
  "vehicleId": "string_value",
  "customerId": "string_value",
  "diagnosticSummary": "string_value",
  "mileageIn": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/work-orders/{id}/tasks`
**Propósito:** Add a mechanic task to a Work Order

*Adds a new mechanic task to an existing Work Order*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "serviceId": "string_value",
  "assignedMechanicId": "string_value",
  "description": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/work-orders/{id}/tasks/{taskId}/start`
**Propósito:** Start executing a task

*Sets the task status to DOING and captures the startedAt timestamp*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)

**📤 Qué tienes que enviar:** (Ningún Body JSON requerido)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/work-orders/{id}/tasks/{taskId}/reopen`
**Propósito:** Reopen a completed task

*Returns the task to DOING, clears completedAt, and keeps stock reserved*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)

**📤 Qué tienes que enviar:** (Ningún Body JSON requerido)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/work-orders/{id}/tasks/{taskId}/products`
**Propósito:** Add an inventory product/part to a task

*Adds a product from inventory to a specific mechanic task*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "productId": "string_value",
  "quantity": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/work-orders/{id}/tasks/{taskId}/complete`
**Propósito:** Complete a task

*Sets the task status to COMPLETED and captures the completedAt timestamp*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)
- `taskId` (path):  (Requerido: true)

**📤 Qué tienes que enviar:** (Ningún Body JSON requerido)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/vouchers`
**Propósito:** Get vouchers by branch

*Retrieves all vouchers emitted in a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/vouchers`
**Propósito:** Generate a new voucher

*Generates a new Voucher (Invoice/Receipt) based on an Approved Quote and sends it to SUNAT via Facthub*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "quoteId": "string_value",
  "type": "string_value",
  "customerDocumentType": "string_value",
  "customerDocumentNumber": "string_value",
  "customerName": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/vouchers/{voucherId}/payments`
**Propósito:** Add a payment to a voucher

*Records a partial or full payment for a given voucher*

**📍 Parámetros (URL / Query):**
- `voucherId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "amount": 50,
  "method": CASH
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/checkouts`
**Propósito:** Process checkout

*Generates a voucher and records a full payment in a single transaction*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "quoteId": "string_value",
  "type": "string_value",
  "customerDocumentType": "string_value",
  "customerDocumentNumber": "string_value",
  "customerName": "string_value",
  "method": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/vh_telemetry_batches`
**Propósito:** Ingest a batch of telemetry snapshots

*Ingests a new batch of telemetry snapshots from an OBD2 device*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "obd2DeviceId": "string_value",
  "snapshots": array
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/vehicles`
**Propósito:** Register a client vehicle

*Registers a client vehicle and links it to the authenticated user*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "plateNumber": "string_value",
  "brand": "string_value",
  "model": "string_value",
  "year": 0,
  "vin": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/services`
**Propósito:** Create a new service

*Creates a new service with the provided details*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "branchId": "string_value",
  "name": "string_value",
  "price": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "name": "string",
    "price": 0
  }
  ```

---

### `POST /api/v1/quotes`
**Propósito:** Create a new quote

*Creates a new Quote based on a Work Order*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "workOrderId": "string_value",
  "branchId": "string_value",
  "discountPercentage": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/quotes/{id}/cancellations`
**Propósito:** Cancel a quote

*Cancels a Quote, transitioning its state to CANCELED*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar:** (Ningún Body JSON requerido)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/quotes/{id}/approvals`
**Propósito:** Approve a quote

*Approves a Quote, transitioning its state from DRAFT to APPROVED*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar:** (Ningún Body JSON requerido)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/owners`
**Propósito:** Create a new owner profile

*Creates a new owner profile associated with a user ID*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "userId": "string_value",
  "firstName": "string_value",
  "lastName": "string_value",
  "documentType": "string_value",
  "documentNumber": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `GET /api/v1/obd2-devices`
**Propósito:** Get all OBD2 devices by branch

*Retrieves all registered OBD2 devices under a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/obd2-devices`
**Propósito:** Register a new OBD2 device

*Registers a new physical OBD2 device in the specified branch*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "branchId": "string_value",
  "macAddress": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/obd2-device-registrations`
**Propósito:** Get OBD2 device registrations by branch and status

*Retrieves all registered OBD2-vehicle couplings under a specific branch, filtered by status*

**📍 Parámetros (URL / Query):**
- `branchId` (query):  (Requerido: true)
- `status` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/obd2-device-registrations`
**Propósito:** Link OBD2 device to vehicle

*Links a registered OBD2 device to a specific vehicle inside a branch*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "obd2DeviceId": "string_value",
  "branchId": "string_value",
  "vehicleId": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/obd2-device-registrations/{id}/deactivate`
**Propósito:** Deactivate OBD2 device registration

*Deactivates/unlinks an active OBD2-vehicle coupling*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar:** (Ningún Body JSON requerido)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/inventory/products`
**Propósito:** Create a new Product

*Creates a new product in the inventory for a specific branch*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "branchId": "string_value",
  "category": "string_value",
  "name": "string_value",
  "sku": "string_value",
  "description": "string_value",
  "salePrice": 0,
  "minimumStock": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "category": "string",
    "name": "string",
    "sku": "string",
    "description": "string",
    "salePrice": 0,
    "minimumStock": 0,
    "currentStock": 0
  }
  ```

---

### `POST /api/v1/inventory/products/{productId}/batches`
**Propósito:** Add a batch to a product

*Adds a physical batch to an existing product, increasing its current stock*

**📍 Parámetros (URL / Query):**
- `productId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "quantity": 0,
  "acquisitionCost": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "batchId": "string",
    "initialQuantity": 0,
    "availableQuantity": 0,
    "acquisitionCost": 0
  }
  ```

---

### `POST /api/v1/employees`
**Propósito:** Create a new employee profile

*Creates a new employee profile associated with a user ID*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "userId": "string_value",
  "firstName": "string_value",
  "lastName": "string_value",
  "documentType": "string_value",
  "documentNumber": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `POST /api/v1/customers`
**Propósito:** Create a new customer profile

*Creates a new customer profile associated with a user ID*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "userId": "string_value",
  "isCorporate": false,
  "firstName": "string_value",
  "lastName": "string_value",
  "businessName": "string_value",
  "documentType": "string_value",
  "documentNumber": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "isCorporate": false,
    "firstName": "string",
    "lastName": "string",
    "businessName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `POST /api/v1/branches`
**Propósito:** Create a new branch

*Creates a new branch associated with a specific workshop*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "workshopId": "string_value",
  "code": "string_value",
  "name": "string_value",
  "address": "string_value",
  "phone": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "workshopId": "string",
    "code": "string",
    "name": "string",
    "address": "string",
    "phone": "string"
  }
  ```

---

### `POST /api/v1/branches/{branchId}/subscriptions/pay`
**Propósito:** Simulate payment and assign subscription

*Simulates a payment (Mock Stripe) using a dummy credit card and assigns the subscription plan*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "planId": "string_value",
  "billingCycle": "string_value",
  "cardNumber": "string_value",
  "cardHolderName": "string_value",
  "expirationDate": "string_value",
  "cvv": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "branchId": "string",
    "planId": "string",
    "billingCycle": "string",
    "status": "string",
    "startDate": "string",
    "endDate": "string"
  }
  ```

---

### `POST /api/v1/authentication/sign-up`
**Propósito:** Sign up

*Register a new user*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "email": "string_value",
  "password": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "email": "string"
  }
  ```

---

### `POST /api/v1/authentication/sign-in`
**Propósito:** Sign in

*Authenticate a user and return a token*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "email": "string_value",
  "password": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "email": "string",
    "token": "string"
  }
  ```

---

### `POST /api/v1/authentication/reset-password`
**Propósito:** Reset password

*Reset user password using recovery token*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "token": "string_value",
  "newPassword": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/authentication/google-sign-in`
**Propósito:** Google sign in

*Authenticate a user using Google and return a token*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "idToken": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "email": "string",
    "token": "string"
  }
  ```

---

### `POST /api/v1/authentication/forgot-password`
**Propósito:** Forgot password

*Send a password recovery email*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "email": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `POST /api/v1/appointments`
**Propósito:** Create a new appointment

*Creates a new appointment*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "branchId": "string_value",
  "customerId": "string_value",
  "vehicleId": "string_value",
  "scheduledStart": "string_value",
  "notes": "string_value"
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/workshops/owner/{ownerId}`
**Propósito:** Get workshops by owner ID

*Retrieves all workshops belonging to a specific owner*

**📍 Parámetros (URL / Query):**
- `ownerId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/work-orders/vehicle/{vehicleId}`
**Propósito:** Get all Work Orders for a specific vehicle

*Retrieves the service history (Work Orders) for a specific vehicle*

**📍 Parámetros (URL / Query):**
- `vehicleId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/work-orders/branch/{branchId}`
**Propósito:** Get all Work Orders for a specific branch

*Retrieves a list of all Work Orders associated with a specific branch (Multi-tenant query)*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/vouchers/{voucherId}`
**Propósito:** Get voucher by ID

*Retrieves a voucher using its unique identifier*

**📍 Parámetros (URL / Query):**
- `voucherId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "quoteId": "string",
    "type": "string",
    "customerDocumentType": "string",
    "customerDocumentNumber": "string",
    "customerName": "string",
    "totalAmount": 0,
    "status": "string",
    "externalInvoiceId": "string",
    "payments": array,
    "totalPaid": 0
  }
  ```

---

### `GET /api/v1/vh_telemetry_batches/latest/{deviceId}`
**Propósito:** Get the latest telemetry snapshot for a device

*Retrieves the most recent telemetry capture from a specific OBD2 device*

**📍 Parámetros (URL / Query):**
- `deviceId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/vh_telemetry_batches/history/{deviceId}`
**Propósito:** Get the historical telemetry snapshots for a device

*Retrieves a history of all telemetry snapshots recorded for a specific OBD2 device ordered descending by date*

**📍 Parámetros (URL / Query):**
- `deviceId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/vehicles/{vehicleId}/telemetry-snapshots`
**Propósito:** Get historical telemetry snapshots for vehicle

*Retrieves all telemetry snapshots captured for the vehicle since its active registration start date*

**📍 Parámetros (URL / Query):**
- `vehicleId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/vehicles/{vehicleId}/dtc-alerts`
**Propósito:** Get historical DTC alerts for vehicle

*Retrieves all DTC/motor alerts captured for the vehicle since its active registration start date*

**📍 Parámetros (URL / Query):**
- `vehicleId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/vehicles/available-for-linking`
**Propósito:** Get vehicles available for linking

*Retrieves all vehicles available for linking (unlinked) under a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/users/{userId}`
**Propósito:** Get user by ID

*Retrieves the details of a specific user*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/services/service/{branchId}`
**Propósito:** Get services by branch ID

*Retrieves all services belonging to a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/quotes/branch/{branchId}`
**Propósito:** Get quotes by branch ID

*Retrieves all Quotes belonging to a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/profiles/users/{userId}/roles`
**Propósito:** Get all profile roles for a specific user ID

*Returns a list of roles (e.g. OWNER, CUSTOMER, EMPLOYEE) that the user currently has.*

**📍 Parámetros (URL / Query):**
- `userId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/owners/{ownerId}`
**Propósito:** Get an owner profile by ID

*Retrieves the details of a specific owner profile*

**📍 Parámetros (URL / Query):**
- `ownerId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `GET /api/v1/obd2-devices/available`
**Propósito:** Get available OBD2 devices by branch

*Retrieves all available (unlinked) OBD2 devices registered under a specific branch*

**📍 Parámetros (URL / Query):**
- `branchId` (query):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/obd2-device-registrations/{id}/telemetry-snapshots`
**Propósito:** Get telemetry snapshots for registration

*Retrieves all telemetry snapshots captured under a specific OBD2-vehicle registration*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/obd2-device-registrations/{id}/dtc-alerts`
**Propósito:** Get DTC alerts for registration

*Retrieves all DTC alerts captured under a specific OBD2-vehicle registration*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/inventory/products/branch/{branchId}`
**Propósito:** Get all products for a branch

*Retrieves all products in the inventory belonging to the specified branch (multi-tenant query)*

**📍 Parámetros (URL / Query):**
- `branchId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/employees`
**Propósito:** Buscar un perfil de empleado

*Endpoint unificado para buscar a un empleado utilizando su User ID o su DNI/RUC.*

**🎯 Escenarios de Búsqueda Soportados:**

1. **Por User ID (Desde IAM):** 
   - **Request:** `GET /api/v1/employees?userId={userId}`

2. **Por Documento de Identidad (DNI/RUC):** 
   - **Request:** `GET /api/v1/employees?documentNumber={documentNumber}`

**📍 Parámetros (URL / Query):**
- `userId` (query): UUID del usuario (Opcional)
- `documentNumber` (query): Número de DNI/RUC (Opcional)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `GET /api/v1/employees/{employeeId}`
**Propósito:** Get an employee profile by ID

*Retrieves the details of a specific employee profile*

**📍 Parámetros (URL / Query):**
- `employeeId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "firstName": "string",
    "lastName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `GET /api/v1/customers/{customerId}`
**Propósito:** Get a customer profile by ID

*Retrieves the details of a specific customer profile*

**📍 Parámetros (URL / Query):**
- `customerId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "userId": "string",
    "isCorporate": false,
    "firstName": "string",
    "lastName": "string",
    "businessName": "string",
    "documentType": "string",
    "documentNumber": "string",
    "phone": "string"
  }
  ```

---

### `GET /api/v1/customers/{customerId}/vehicles`
**Propósito:** Get active vehicles for customer

*Retrieves all vehicles currently associated with an active registration for the customer*

**📍 Parámetros (URL / Query):**
- `customerId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/branches/workshop/{workshopId}`
**Propósito:** Get branches by workshop ID

*Retrieves all branches belonging to a specific workshop*

**📍 Parámetros (URL / Query):**
- `workshopId` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/appointments`
**Propósito:** Buscar y filtrar Citas (Appointments)

*Este es un endpoint unificado que permite buscar citas utilizando diferentes combinaciones de Query Parameters (filtros en la URL). Solo debes usar una de las siguientes combinaciones válidas a la vez. Si no envías ningún parámetro, el servidor retornará un error 400 (INVALID_QUERY_PARAMS).*

**🎯 Escenarios de Búsqueda Soportados:**

1. **Por Sucursal (Branch):** 
   - **Request:** `GET /api/v1/appointments?branchId={branchId}`
   - **Retorna:** Un arreglo con TODAS las citas registradas en esa sucursal, sin importar su estado.

2. **Por Sucursal y Estado:** 
   - **Request:** `GET /api/v1/appointments?branchId={branchId}&status={status}`
   - **Retorna:** Un arreglo con las citas de esa sucursal filtradas por un estado específico. (Valores válidos para status: `PENDING`, `COMPLETED`, `CANCELED`).

3. **Por Cliente (Customer):** 
   - **Request:** `GET /api/v1/appointments?customerId={customerId}`
   - **Retorna:** Un arreglo con el historial completo de citas asociadas a ese cliente en particular.

4. **Por Vehículo (Vehicle):** 
   - **Request:** `GET /api/v1/appointments?vehicleId={vehicleId}`
   - **Retorna:** Un arreglo con el historial completo de citas programadas para un vehículo específico.

**📍 Parámetros (URL / Query):**
- `branchId` (query): UUID de la sucursal (Opcional)
- `status` (query): Estado de la cita (Opcional)
- `customerId` (query): UUID del cliente (Opcional)
- `vehicleId` (query): UUID del vehículo (Opcional)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  [
    {
      "id": "string",
      "branchId": "string",
      "customerId": "string",
      "vehicleId": "string",
      "scheduledStart": "string",
      "scheduledEnd": "string",
      "status": "string",
      "notes": "string"
    }
  ]
  ```

---

### `PUT /api/v1/employee-registrations/{id}`
**Propósito:** Update an employee registration

*Updates an existing employee registration by ID*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "speciality": "string_value",
  "specialityName": "string_value",
  "salary": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `DELETE /api/v1/employee-registrations/{id}`
**Propósito:** Delete an employee registration

*Soft deletes an employee registration by ID, setting status to INACTIVE*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK

---

### `GET /api/v1/employee-registrations`
**Propósito:** Buscar y filtrar Registros de Empleados

*Endpoint unificado para listar los empleados registrados en una sucursal, con la opción de filtrar por su estado o buscar por ID de empleado.*

**🎯 Escenarios de Búsqueda Soportados:**

1. **Por Sucursal (Branch):** 
   - **Request:** `GET /api/v1/employee-registrations?branchId={branchId}`
   - **Retorna:** Todas las asignaciones de empleados en esa sucursal.

2. **Por Sucursal y Estado:** 
   - **Request:** `GET /api/v1/employee-registrations?branchId={branchId}&status={status}`
   - **Retorna:** Asignaciones en esa sucursal filtradas por estado (`ACTIVE`, `INACTIVE`).

3. **Por Empleado (Employee):**
   - **Request:** `GET /api/v1/employee-registrations?employeeId={employeeId}`
   - **Retorna:** El historial de asignaciones de un empleado específico.

**📍 Parámetros (URL / Query):**
- `branchId` (query): UUID de la sucursal (Opcional)
- `status` (query): Estado del registro (Opcional)
- `employeeId` (query): UUID del empleado (Opcional)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  [
    {
      "id": "string",
      "employeeId": "string",
      "branchId": "string",
      "speciality": "string",
      "specialityName": "string",
      "salary": 0.0,
      "status": "string",
      "createdAt": "string",
      "updatedAt": "string",
      "deletedAt": "string"
    }
  ]
  ```

---

### `GET /api/v1/employee-registrations/{id}`
**Propósito:** Get employee registration by ID

*Returns the detail of a single employee registration by its ID*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "employeeId": "string",
    "branchId": "string",
    "speciality": "string",
    "specialityName": "string",
    "salary": 0.0,
    "status": "string",
    "createdAt": "string",
    "updatedAt": "string",
    "deletedAt": "string"
  }
  ```

---

### `POST /api/v1/employee-registrations`
**Propósito:** Create a new employee registration

*Creates a new employee registration for a specific branch and employee*

**📍 Parámetros:** Ninguno.

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "employeeId": "string_value",
  "branchId": "string_value",
  "speciality": "string_value",
  "specialityName": "string_value",
  "salary": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "employeeId": "string",
    "branchId": "string",
    "speciality": "string",
    "specialityName": "string",
    "salary": 0.0,
    "status": "string",
    "createdAt": "string",
    "updatedAt": "string",
    "deletedAt": "string"
  }
  ```

---

### `PUT /api/v1/employee-registrations/{id}`
**Propósito:** Update an employee registration

*Updates an existing employee registration by ID*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📤 Qué tienes que enviar (Request Body JSON):**
```json
{
  "speciality": "string_value",
  "specialityName": "string_value",
  "salary": 0
}
```

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "employeeId": "string",
    "branchId": "string",
    "speciality": "string",
    "specialityName": "string",
    "salary": 0.0,
    "status": "string",
    "createdAt": "string",
    "updatedAt": "string",
    "deletedAt": "string"
  }
  ```

---

### `DELETE /api/v1/employee-registrations/{id}`
**Propósito:** Delete an employee registration

*Soft deletes an employee registration by ID, setting status to INACTIVE*

**📍 Parámetros (URL / Query):**
- `id` (path):  (Requerido: true)

**📥 Qué vas a recibir (Responses):**
- **Código HTTP 200**: OK
  ```json
  {
    "id": "string",
    "employeeId": "string",
    "branchId": "string",
    "speciality": "string",
    "specialityName": "string",
    "salary": 0.0,
    "status": "string",
    "createdAt": "string",
    "updatedAt": "string",
    "deletedAt": "string"
  }
  ```

---



