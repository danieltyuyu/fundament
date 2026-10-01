# Billing Bounded Context - Endpoints Documentation

Este documento detalla el uso de los endpoints REST expuestos por el Bounded Context de **Billing** (Facturación) en la plataforma Atelier. El contexto se divide principalmente en dos recursos: **Quotes** (Cotizaciones) y **Vouchers** (Comprobantes de Pago: Boletas/Facturas).

---

## 1. Quotes (Cotizaciones)
**Base URL:** `/api/v1/quotes`

Las cotizaciones representan el presupuesto inicial generado a partir de una orden de trabajo (Work Order). Pasan por un ciclo de vida: `DRAFT` -> `APPROVED` / `CANCELED`.

### 1.1. Crear una Cotización (Create Quote)
Genera una nueva cotización en estado `DRAFT` asociada a una orden de trabajo.

- **Método:** `POST`
- **Ruta:** `/api/v1/quotes`
- **Request Body:**
```json
{
  "workOrderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "branchId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "discountPercentage": 10.5
}
```
- **Respuestas:**
  - `201 Created`: Cotización creada exitosamente.
  - `400 Bad Request`: Errores de validación (ej. descuento inválido).
  - `409 Conflict`: Ya existe una cotización para esta orden de trabajo.

### 1.2. Actualizar Descuento (Update Quote Discount)
Permite modificar el descuento de una cotización, siempre y cuando esté en estado `DRAFT`.

- **Método:** `PUT`
- **Ruta:** `/api/v1/quotes/{id}`
- **Path Variable:** `id` (UUID de la cotización)
- **Request Body:**
```json
{
  "discountPercentage": 15.0
}
```
- **Respuestas:**
  - `200 OK`: Cotización actualizada.
  - `409 Conflict`: La cotización no está en estado `DRAFT`.

### 1.3. Aprobar Cotización (Approve Quote)
Transiciona el estado de la cotización de `DRAFT` a `APPROVED`. Solo las cotizaciones aprobadas pueden ser facturadas (convertidas a Vouchers).

- **Método:** `POST`
- **Ruta:** `/api/v1/quotes/{id}/approve`
- **Respuestas:**
  - `200 OK`: Cotización aprobada.

### 1.4. Cancelar Cotización (Cancel Quote)
Transiciona el estado de la cotización a `CANCELED`.

- **Método:** `POST`
- **Ruta:** `/api/v1/quotes/{id}/cancel`
- **Respuestas:**
  - `200 OK`: Cotización cancelada.

### 1.5. Obtener Cotizaciones
- **Por ID:** `GET /api/v1/quotes/{id}`
- **Por Sucursal:** `GET /api/v1/quotes/branch/{branchId}`

---

## 2. Vouchers (Comprobantes)
**Base URL:** `/api/v1/vouchers`

Un Voucher representa un comprobante de pago electrónico (Boleta o Factura). Se generan a partir de cotizaciones `APPROVED`.

### 2.1. Generar Comprobante (Generate Voucher)
Crea un comprobante a partir de una cotización aprobada y lo envía a SUNAT vía Facthub. Inicialmente se crea en estado `PENDING_PAYMENT`.

- **Método:** `POST`
- **Ruta:** `/api/v1/vouchers`
- **Request Body:**
```json
{
  "quoteId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "type": "INVOICE", 
  "customerDocumentType": "RUC",
  "customerDocumentNumber": "20123456789",
  "customerName": "Empresa Cliente S.A.C."
}
```
*Nota: `type` puede ser `INVOICE` (Factura) o `RECEIPT` (Boleta).*

- **Respuestas:**
  - `201 Created`: Comprobante emitido correctamente.
  - `409 Conflict`: La cotización no está en estado `APPROVED`.
  - `503 Service Unavailable`: Falla en la integración con Facthub.

### 2.2. Flujo Completo de Checkout (Process Checkout)
Genera el comprobante y registra el pago total en una sola transacción. Es útil para el caso de uso donde el cliente paga el total de inmediato.

- **Método:** `POST`
- **Ruta:** `/api/v1/vouchers/checkout`
- **Request Body:**
```json
{
  "quoteId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "type": "RECEIPT",
  "customerDocumentType": "DNI",
  "customerDocumentNumber": "70123456",
  "customerName": "Juan Perez",
  "method": "CREDIT_CARD"
}
```
*Nota: `method` soporta valores como `CASH`, `CREDIT_CARD`, `DEBIT_CARD`, `BANK_TRANSFER`.*

- **Respuestas:**
  - `201 Created`: Comprobante emitido y pagado en su totalidad (`PAID`).

### 2.3. Agregar un Pago Parcial (Add Payment)
Agrega un pago (parcial o total) a un comprobante que está en estado `PENDING_PAYMENT`. Si la suma de los pagos alcanza el monto total, el comprobante pasa a `PAID`.

- **Método:** `POST`
- **Ruta:** `/api/v1/vouchers/{voucherId}/payments`
- **Request Body:**
```json
{
  "amount": 50.00,
  "method": "CASH"
}
```
- **Respuestas:**
  - `200 OK`: Pago registrado.
  - `400 Bad Request`: El monto supera la deuda restante.
  - `409 Conflict`: El comprobante ya fue pagado en su totalidad o fue cancelado.

### 2.4. Eliminar un Pago (Remove Payment)
Elimina un pago registrado por error. Si el comprobante estaba pagado, regresará al estado de pendiente.

- **Método:** `DELETE`
- **Ruta:** `/api/v1/vouchers/{voucherId}/payments/{paymentId}`
- **Respuestas:**
  - `200 OK`: Pago eliminado.
  - `404 Not Found`: Pago no encontrado.

### 2.5. Obtener Comprobantes
- **Por ID:** `GET /api/v1/vouchers/{voucherId}`
- **Por Sucursal:** `GET /api/v1/vouchers?branchId={branchId}`

---

## 3. Consideraciones de Internacionalización (i18n)

Todos los endpoints que retornen errores de negocio o validación soportan **Internacionalización**. 

Para recibir los mensajes en el idioma deseado, debe enviar la cabecera HTTP:
`Accept-Language: es` (Para Español)
`Accept-Language: en` (Para Inglés)

*Ejemplo de respuesta de error:*
```json
{
  "code": "QUOTE_CONFLICT",
  "message": "Quote must be APPROVED to generate a voucher",
  "details": null
}
```
