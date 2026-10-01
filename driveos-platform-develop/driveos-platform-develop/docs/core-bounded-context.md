# Guía de Integración Frontend: Core Bounded Context

Esta guía describe cómo interactuar correctamente con los endpoints del Bounded Context `core`, enfocada específicamente en los flujos reales de la aplicación desde la perspectiva del Frontend (Registro, Inicio de Sesión y Perfiles).

Tras la última refactorización para seguir estándares de **API REST Pura**, recuerda que las mutaciones (`PUT`/`DELETE`) en este contexto requieren el ID del recurso (ej. `customerId`), NO el `userId`.

## 🧠 Lógica Principal: ¿Cómo lidiar con el `userId`?
Cuando un usuario inicia sesión mediante IAM, el frontend obtiene su `userId`. Sin embargo, los endpoints puros de `core` requieren el ID específico de la entidad (`customerId`, `employeeId`, `ownerId`) para actualizarla o eliminarla.

**El flujo correcto para manipular un perfil existente desde el frontend es:**
1. **Consultar (GET):** Usar el `userId` como parámetro de búsqueda (`?userId={userId}`) para obtener el perfil completo.
2. **Extraer el ID Real:** Del JSON de respuesta, guardar la propiedad principal `id` de ese recurso (por ejemplo, `customerId`).
3. **Mutar (PUT/DELETE):** Utilizar ese `id` real en la ruta del endpoint para modificar el recurso en cuestión (ej. `PUT /api/v1/customers/{customerId}`).

---

## 🚀 Flujos Principales de la Aplicación

### 1. Flujo de Registro (Sign-Up)

Todo registro inicia en el módulo IAM (`POST /api/v1/authentication/sign-up`). Una vez creado el usuario, el frontend obtiene el `userId` y pregunta al usuario qué perfil desea crear. Dependiendo de si la cuenta es nueva, ofrecerá crear un perfil de **Customer**, **Employee** o **Owner** (si el usuario ya tiene rol de Customer, solo mostrará Employee u Owner).

A continuación, los flujos para cada perfil en `core`:

#### A. Flujo: Registrarse como Customer (Cliente)
Si el usuario selecciona "Customer", el frontend debe determinar si es persona natural o empresa:
*   **Persona Natural:** El frontend solicita `firstname`, `lastname`, tipo de documento (DNI, CE, PASSPORT), número de documento y teléfono. El campo `isCorporate` se envía como `false`.
*   **Empresa:** El frontend solicita `businessName`, tipo de documento fijo a `RUC`, número de RUC y teléfono. El campo `isCorporate` se envía como `true`.
*   **Petición API:** Enviar un `POST /api/v1/customers` pasando el `userId` obtenido del IAM y los datos capturados en el payload. ¡Listo! Ya es un Customer.

#### B. Flujo: Registrarse como Employee (Empleado)
Si el usuario selecciona "Employee", el frontend solicita sus datos laborales y personales básicos.
*   **Petición API:** Enviar un `POST /api/v1/employees` incluyendo el `userId` y los datos correspondientes. ¡Listo! Ya es un Employee.

#### C. Flujo: Registrarse como Owner (Propietario)
Este es el flujo más completo e involucra múltiples entidades. Si elige "Owner":
1.  **Datos Personales:** Se rellenan los datos del Owner como persona normal. Se envía un `POST /api/v1/owners` con el `userId`. De la respuesta, **el frontend debe guardar el `ownerId`**.
2.  **Datos del Taller (Workshop):** El workshop representa la empresa. Se solicitan sus datos y se envía un `POST /api/v1/workshops` enviando el `ownerId` en el body. De la respuesta, **el frontend debe guardar el `workshopId`**.
3.  **Sede Física (Branch):** Se deben agregar los datos del local físico. Se envía un `POST /api/v1/branches` incluyendo el `workshopId` en el body. De la respuesta, **el frontend debe guardar el `branchId`**.
4.  **Suscripción y Pago:** Finalmente, el owner debe elegir un plan de suscripción fijo:
    *   **Lite:** `eeeee333-3333-3333-3333-333333333333`
    *   **Pro:** `eeeee222-2222-2222-2222-222222222222`
    *   **Max:** `eeeee111-1111-1111-1111-111111111111`
    Luego rellena datos simulados de pago (`card_number`, `holder_name`, `expiration_day`, `cvv`).
    Se envía un `POST /api/v1/branches/{branchId}/subscriptions` pasando los detalles de pago y el ID del plan seleccionado en el body.
    *¡Listo! Tienes un Owner con su taller, sucursal y suscripción activa.*

---

### 2. Flujo de Inicio de Sesión (Sign-In)

Cuando un usuario que ya existe coloca su correo y contraseña en el login (llamada a `POST /api/v1/authentication/sign-in` en IAM), el flujo en `core` es el siguiente:

1.  **Identificar Roles:** El frontend dispara `GET /api/v1/profiles/roles?userId={userId}`.
2.  **Selección de Vista:**
    *   Si el endpoint devuelve que tiene **MÁS de un rol** (ej. es Customer y también Owner), el frontend muestra una pantalla intermedia preguntando: *"¿Cómo deseas ingresar hoy?"*.
    *   Si el endpoint devuelve **UN SOLO rol**, el frontend entra directamente al dashboard correspondiente (Dashboard Customer o Dashboard Owner/Empleado).
3.  **Obtener ID del Agregado (Para Mantenimiento):** 
    Si el usuario ingresó a su dashboard y, por ejemplo, desea actualizar su perfil de propietario o ver sus talleres, el frontend debe consultar en silencio `GET /api/v1/owners?userId={userId}` para obtener el `ownerId` real.
    Ese `ownerId` es el que utilizará para hacer `PUT /api/v1/owners/{ownerId}` o para consultar `GET /api/v1/workshops?ownerId={ownerId}`.

---

## 📖 Referencia Rápida de Endpoints

### Profiles
*   **Obtener Roles por User ID:** `GET /api/v1/profiles/roles?userId={userId}`
*   **Buscar Perfil por Número de Documento:** `GET /api/v1/profiles?documentNumber={documentNumber}`

### Customers
*   **Crear:** `POST /api/v1/customers` (enviar `userId`)
*   **Obtener por User ID:** `GET /api/v1/customers?userId={userId}` (Sirve para extraer el `customerId`)
*   **Obtener por Customer ID:** `GET /api/v1/customers/{customerId}`
*   **Actualizar:** `PUT /api/v1/customers/{customerId}`
*   **Eliminar:** `DELETE /api/v1/customers/{customerId}`

### Employees
*   **Crear:** `POST /api/v1/employees` (enviar `userId`)
*   **Obtener por User ID:** `GET /api/v1/employees?userId={userId}` (Sirve para extraer el `employeeId`)
*   **Obtener por Employee ID:** `GET /api/v1/employees/{employeeId}`
*   **Actualizar:** `PUT /api/v1/employees/{employeeId}`
*   **Eliminar:** `DELETE /api/v1/employees/{employeeId}`

### Owners
*   **Crear:** `POST /api/v1/owners` (enviar `userId`)
*   **Obtener por User ID:** `GET /api/v1/owners?userId={userId}` (Sirve para extraer el `ownerId`)
*   **Obtener por Owner ID:** `GET /api/v1/owners/{ownerId}`
*   **Actualizar:** `PUT /api/v1/owners/{ownerId}`
*   **Eliminar:** `DELETE /api/v1/owners/{ownerId}`

### Workshops & Branches
*   **Crear Taller:** `POST /api/v1/workshops` (enviar `ownerId`)
*   **Listar Talleres del Dueño:** `GET /api/v1/workshops?ownerId={ownerId}`
*   **Actualizar Taller:** `PUT /api/v1/workshops/{workshopId}`
*   **Crear Sucursal:** `POST /api/v1/branches` (enviar `workshopId`)
*   **Listar Sucursales del Taller:** `GET /api/v1/branches?workshopId={workshopId}`
*   **Actualizar Sucursal:** `PUT /api/v1/branches/{branchId}`
*   **Eliminar Sucursal:** `DELETE /api/v1/branches/{branchId}`
*   **Crear/Pagar Suscripción:** `POST /api/v1/branches/{branchId}/subscriptions`
*   **Cancelar Suscripción:** `DELETE /api/v1/branches/{branchId}/subscription`
