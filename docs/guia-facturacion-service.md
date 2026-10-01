# Facthub Service - API Reference

Esta documentación detalla los endpoints expuestos por el sistema de facturación **Facthub Service**, sus parámetros requeridos, formatos de petición y ejemplos de respuesta.

La URL base para producción es: `https://facthub-service.onrender.com`

---

## 1. Health Check
Verifica el estado de conexión y disponibilidad del servidor web.

**Endpoint:** `GET /api/invoices/health`

### Respuesta Exitosa (`200 OK`)
```json
{
  "service": "facthub-billing-service",
  "status": "UP",
  "timestamp": "2026-06-02T00:44:18.753220823"
}
```

---

## 2. Registrar Empresa (Taller)
Registra a un nuevo cliente (Taller Automotriz) en el sistema multi-tenant, almacenando sus credenciales SOL y su certificado digital en formato binario.

**Endpoint:** `POST /api/companies`  
**Content-Type:** `multipart/form-data`

### Parámetros de Petición (Body)

| Campo | Tipo | Requerido | Descripción | Ejemplo |
| :--- | :--- | :---: | :--- | :--- |
| `ruc` | Text | Sí | RUC de 11 dígitos de la empresa emisora. | `20556677889` |
| `businessName` | Text | Sí | Razón social de la empresa. | `Taller Atelier` |
| `sunatSolUsername` | Text | Sí | Usuario secundario clave SOL. | `20556677889MODDATOS` |
| `sunatSolPassword` | Text | Sí | Contraseña de la clave SOL. | `MODDATOS123` |
| `certificatePassword` | Text | Sí | Clave del certificado digital PFX/P12. | `miclave` |
| `certificateFile` | File | Sí | Archivo físico del certificado digital. | `taller-atelier-cert.pfx` |

### Respuesta Exitosa (`201 Created`)
```json
{
  "ruc": "20556677889",
  "success": true,
  "businessName": "Taller Atelier",
  "message": "Company registered successfully"
}
```

---

## 3. Emitir Comprobante (Factura o Boleta)
Genera un comprobante electrónico en estándar UBL 2.1, lo firma digitalmente con el certificado de la empresa correspondiente y simula el envío a SUNAT.

**Endpoint:** `POST /api/invoices/issue`  
**Content-Type:** `application/json`

### Payload de Petición (JSON)

| Campo | Tipo | Descripción | Ejemplos Válidos |
| :--- | :--- | :--- | :--- |
| `issuerRuc` | String | El RUC de la empresa emisora (previamente registrada). | `"20556677889"` |
| `documentType` | String | El tipo de comprobante a emitir. | `"INVOICE"` (Factura), `"RECEIPT"` (Boleta) |
| `customerDocumentType` | String | Tipo de documento del cliente. | `"RUC"`, `"DNI"`, `"CE"`, `"PASAPORTE"` |
| `customerDocumentNumber` | String | Número de documento del cliente. Debe existir en la BD de Searchpe. | `"20100078941"`, `"10427891234"` |
| `customerName` | String | (Opcional/Sugerido) Nombre del cliente. | `"ACME PERU S.A.C."` |
| `items` | Array | Lista de productos o servicios facturados. | Ver ejemplo abajo. |

#### Formato de objeto `Item` (dentro del arreglo `items`)
* `description` (String): Nombre del servicio/producto.
* `quantity` (Integer): Cantidad a facturar.
* `unitPrice` (Decimal): Precio unitario en PEN.

### Ejemplo de Petición (Factura)
```json
{
  "issuerRuc": "20556677889",
  "documentType": "INVOICE",
  "customerDocumentType": "RUC",
  "customerDocumentNumber": "20100078941",
  "customerName": "ACME PERU S.A.C.",
  "items": [
    {
      "description": "Servicio de diagnóstico por escáner",
      "quantity": 1,
      "unitPrice": 150.00
    }
  ]
}
```

### Respuesta Exitosa (`200 OK`)
Devuelve un resumen estructurado del documento generado y el contenido completo en XML con validez legal.

```json
{
  "success": true,
  "message": "Invoice issued successfully",
  "invoice": {
    "id": "6bc97679-31ca-4e48-a86b-a79de31bc482",
    "series": "F001",
    "number": 10,
    "customerDocumentNumber": "20100078941",
    "customerName": "ACME PERU S.A.C.",
    "totalAmount": 177.00,
    "issueDate": "2026-06-01T17:41:17.9529019"
  },
  "sunat": {
    "status": "ACCEPTED",
    "ticket": "TICKET-MOCK-123456",
    "xmlContent": "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?><Invoice xmlns=\"urn:oasis... [XML TRUNCADO] ...</Invoice>"
  }
}
```

---

## Consideraciones de Seguridad y Casos de Uso
- **Multitenencia:** El campo `issuerRuc` en la creación de comprobantes es estrictamente necesario ya que determina qué certificado en la base de datos se utilizará para generar la firma `<ds:Signature>`.
- **Boletas de Venta:** Para emitir boletas, cambia el campo `documentType` a `"RECEIPT"` y usa un documento de identidad válido (ej. `"customerDocumentType": "DNI"`). El sistema automáticamente ajustará la serie a `B001` y el código de tipo de documento a `03`.
