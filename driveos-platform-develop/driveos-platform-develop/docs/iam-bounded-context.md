# Guía de Integración Frontend: IAM Bounded Context

Este documento describe el flujo y uso de los endpoints del Bounded Context de **IAM (Identity & Access Management)** desde la perspectiva del Frontend. En este sprint, nos centraremos exclusivamente en los flujos principales de autenticación (Registro, Inicio de Sesión y Recuperación de Contraseña), ignorando por el momento los endpoints de configuración.

El objetivo principal de IAM es manejar la seguridad, generar credenciales y proveer al frontend del identificador base: el `userId`.

---

## 🔐 1. Flujo de Registro (Sign-Up)

**Endpoint:** `POST /api/v1/authentication/sign-up`

### ¿Cómo lo usa el Frontend?
1. El usuario navega a la página de registro.
2. Ingresa sus credenciales base (correo electrónico y contraseña).
3. El frontend envía estos datos al endpoint de `sign-up`.
4. Si la respuesta es exitosa, se crea la cuenta de autenticación y la plataforma devuelve o permite acceder al `userId` del nuevo usuario.
5. **Paso Siguiente:** A partir de aquí, el flujo pasa al dominio `core`. Como la cuenta es totalmente nueva, el frontend le pregunta al usuario qué tipo de perfil quiere (Customer, Employee u Owner) y desencadena los flujos explicados en la guía de `core-bounded-context`. Si el usuario ya era "Customer", el frontend solo le ofrecería crear un perfil secundario como "Employee" u "Owner".

---

## 🔑 2. Flujo de Inicio de Sesión (Sign-In)

**Endpoint:** `POST /api/v1/authentication/sign-in`

### ¿Cómo lo usa el Frontend?
1. El usuario (que ya tiene una cuenta) navega a la página de login.
2. Ingresa su correo electrónico y su contraseña.
3. El frontend envía la petición al endpoint de `sign-in`.
4. **Respuesta Exitosamente:** El API devuelve el JWT Token y los datos de la sesión, incluyendo el **`userId`**. El frontend guarda el Token en `localStorage/cookies` para autorizar futuras peticiones.
5. **Paso Siguiente (Redirección Inteligente):**
   *   El frontend invoca el endpoint `GET /api/v1/profiles/roles?userId={userId}` (del módulo `core`).
   *   Si el usuario tiene múltiples roles (ej. es Customer y también Owner), se le muestra una pantalla intermedia consultando: *"¿Cómo deseas ingresar hoy?"*.
   *   Si tiene solo un rol, el frontend lo redirige automáticamente a su Dashboard respectivo (Dashboard Customer, Employee o Owner).

---

## 🆘 3. Flujo de Olvidé mi Contraseña (Forgot & Reset Password)

Cuando el usuario olvida su contraseña, el frontend debe manejar un flujo de dos pasos: enviar el enlace de recuperación y procesar el cambio.

### A. Solicitar Recuperación (Forgot Password)
**Endpoint:** `POST /api/v1/authentication/forgot-password`

1. El usuario hace clic en *"Olvidé mi contraseña"*.
2. El frontend muestra una vista pidiendo el correo electrónico de la cuenta comprometida.
3. El frontend hace un POST al endpoint enviando ese correo.
4. El backend genera un token de recuperación y simula (o realiza) el envío de un email al usuario.
5. El correo contiene un enlace con esta estructura (gestionada por el frontend):
   `https://atelier-webapp-11848.vercel.app/reset-password?token=55f75b85-4eb3-4d9d-9a9b-71e35ad450a9`

### B. Restablecer la Contraseña (Reset Password)
**Endpoint:** `POST /api/v1/authentication/reset-password`

1. El usuario abre su correo, hace clic en el enlace y aterriza en la vista `/reset-password` del Frontend.
2. **¡Importante!** El usuario **NUNCA** introduce el token manualmente. El frontend debe leer el parámetro `?token=...` directamente desde la URL.
3. El frontend muestra una pantalla para que el usuario ingrese y confirme su **nueva contraseña**.
4. Al darle a guardar, el frontend hace un POST al endpoint `reset-password` enviando la `nueva contraseña` y el `token` extraído de la URL.
5. Si es exitoso, el frontend redirige automáticamente a la página de **Sign-in**, donde el usuario ahora podrá iniciar sesión de forma normal con su nueva contraseña.

---

*Nota: Cualquier otro endpoint de IAM relacionado con configuraciones, edición interna de cuentas, o desactivación no se evaluará en este sprint.*
