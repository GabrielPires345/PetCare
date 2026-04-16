# PetCare Backend - API Documentation

## Overview

Spring Boot 4.0.5 application providing REST API for the PetCare platform. Java 21, PostgreSQL, Spring Data JPA, Spring Security with JWT authentication.

## Authentication

All endpoints except `/api/auth/**` and `/api/servicos/**` require a valid JWT token.

To authenticate, include the token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

---

## Endpoints

### 1. Authentication (`/api/auth`)

#### POST `/api/auth/registro`

Registers a new client (creates User + Client Profile + Pet). Returns a JWT token.

**Auth:** Not required

**Request Body:**

| Field | Type | Required | Validation |
|---|---|---|---|
| `username` | String | Yes | Max 10 characters |
| `email` | String | Yes | Must be a valid email format |
| `password` | String | Yes | - |
| `confirmPassword` | String | Yes | Must match `password` |
| `fullName` | String | Yes | - |
| `cpf` | String | Yes | 11-14 characters |
| `birthDate` | LocalDate | Yes | ISO format: `yyyy-MM-dd` |
| `pet` | PetRequest | Yes | Nested object (see below) |

**PetRequest (nested):**

| Field | Type | Required | Validation |
|---|---|---|---|
| `name` | String | Yes | - |
| `species` | String | Yes | - |
| `gender` | String | Yes | - |
| `weight` | Double | Yes | - |
| `birthDate` | LocalDate | Yes | ISO format: `yyyy-MM-dd` |
| `neutered` | Boolean | Yes | - |

**Response (201 Created):**
```json
{
  "user": {
    "id": "uuid",
    "username": "string",
    "email": "string",
    "accessLevel": "CLIENTE"
  },
  "token": "eyJhbGci..."
}
```

**Response (400 Bad Request):**
- `"Passwords do not match"` if `password` != `confirmPassword`

---

#### POST `/api/auth/login`

Authenticates a user and returns a JWT token.

**Auth:** Not required

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `email` | String | Yes |
| `password` | String | Yes |

**Response (200 OK):**
```json
{
  "user": {
    "id": "uuid",
    "username": "string",
    "email": "string",
    "accessLevel": "CLIENTE"
  },
  "clienteId": "uuid",
  "token": "eyJhbGci..."
}
```

**Response (401 Unauthorized):** `"Invalid credentials"`

---

### 2. Agendamento (`/api/agendamentos`)

#### POST `/api/agendamentos`

Schedules a new appointment. Validates that Pet, Clinic, Veterinarian, and Service exist. Sets initial status to `AGENDADO` and calculates `valorFinal` based on the service price.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `petId` | UUID | Yes |
| `clinicaId` | UUID | Yes |
| `veterinarioId` | UUID | Yes |
| `servicoId` | UUID | Yes |
| `dataHoraMarcada` | LocalDateTime | Yes | ISO format: `yyyy-MM-ddTHH:mm:ss` |

**Response (201 Created):**
```json
{
  "id": "uuid",
  "petName": "string",
  "clinicaName": "string",
  "veterinarioName": "string",
  "servicoName": "string",
  "dataHoraMarcada": "2026-04-15T10:00:00",
  "status": "AGENDADO",
  "valorFinal": 150.00
}
```

---

#### GET `/api/agendamentos/{id}`

Retrieves an appointment by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `AgendamentoResponse` (same structure as above)

---

#### GET `/api/agendamentos/cliente/{clienteId}`

Lists all appointments for a specific client.

**Auth:** Required (JWT)

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "petName": "string",
    "clinicaName": "string",
    "veterinarioName": "string",
    "servicoName": "string",
    "dataHoraMarcada": "2026-04-15T10:00:00",
    "status": "AGENDADO",
    "valorFinal": 150.00
  }
]
```

---

#### DELETE `/api/agendamentos/{id}`

Cancels an appointment. Status is updated to `CANCELADO`.

**Auth:** Required (JWT)

**Response (204 No Content)**

---

### 3. Cliente (`/api/clientes`)

#### GET `/api/clientes`

Lists all registered clients.

**Auth:** Required (JWT)

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "fullName": "string",
    "cpf": "string",
    "birthDate": "2000-01-01"
  }
]
```

---

#### GET `/api/clientes/{id}`

Retrieves a client by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `ClienteResponse` (same structure as above)

---

#### PUT `/api/clientes/{id}`

Updates a client's profile information.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `fullName` | String | Yes |
| `cpf` | String | Yes | 11-14 characters |
| `birthDate` | LocalDate | Yes |

**Response (200 OK):** Updated `ClienteResponse`

---

#### DELETE `/api/clientes/{id}`

Deletes a client (soft delete).

**Auth:** Required (JWT)

**Response (204 No Content)**

---

#### POST `/api/clientes/{id}/enderecos`

Adds an address to a client.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required | Validation |
|---|---|---|---|
| `street` | String | Yes | - |
| `number` | String | Yes | - |
| `neighborhood` | String | Yes | - |
| `city` | String | Yes | - |
| `state` | String | Yes | Max 2 characters (e.g., `SP`) |
| `zipCode` | String | Yes | - |

**Response (201 Created):**
```json
{
  "id": "uuid",
  "street": "string",
  "number": "string",
  "neighborhood": "string",
  "city": "string",
  "state": "SP",
  "zipCode": "string"
}
```

---

#### GET `/api/clientes/{id}/enderecos`

Lists all addresses for a client.

**Auth:** Required (JWT)

**Response (200 OK):** `List<EnderecoResponse>`

---

#### POST `/api/clientes/{id}/telefones`

Adds a phone number to a client.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required | Validation |
|---|---|---|---|
| `ddd` | String | Yes | Max 2 characters |
| `number` | String | Yes | - |
| `isWhatsApp` | Boolean | Yes | - |

**Response (201 Created):**
```json
{
  "id": "uuid",
  "ddd": "11",
  "number": "999999999",
  "isWhatsApp": true
}
```

---

#### GET `/api/clientes/{id}/telefones`

Lists all phone numbers for a client.

**Auth:** Required (JWT)

**Response (200 OK):** `List<TelefoneResponse>`

---

### 4. Clinica (`/api/clinicas`)

#### GET `/api/clinicas`

Lists all clinics.

**Auth:** Required (JWT)

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "clinicName": "string",
    "corporateName": "string",
    "cnpj": "string"
  }
]
```

---

#### GET `/api/clinicas/{id}`

Retrieves a clinic by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `ClinicaResponse` (same structure as above)

---

#### POST `/api/clinicas`

Creates a new clinic.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `clinicName` | String | Yes |
| `corporateName` | String | Yes |
| `cnpj` | String | Yes | Valid CNPJ format |

**Response (201 Created):** `ClinicaResponse`

---

#### PUT `/api/clinicas/{id}`

Updates a clinic.

**Auth:** Required (JWT)

**Request Body:** Same as POST `/api/clinicas`

**Response (200 OK):** Updated `ClinicaResponse`

---

#### DELETE `/api/clinicas/{id}`

Deletes a clinic.

**Auth:** Required (JWT)

**Response (204 No Content)**

---

#### POST `/api/clinicas/{id}/enderecos`

Adds an address to a clinic.

**Auth:** Required (JWT)

**Request Body:** Same fields as `/api/clientes/{id}/enderecos`

**Response (201 Created):** `EnderecoResponse`

---

#### GET `/api/clinicas/{id}/enderecos`

Lists all addresses for a clinic.

**Auth:** Required (JWT)

**Response (200 OK):** `List<EnderecoResponse>`

---

#### POST `/api/clinicas/{id}/telefones`

Adds a phone number to a clinic.

**Auth:** Required (JWT)

**Request Body:** Same fields as `/api/clientes/{id}/telefones`

**Response (201 Created):** `TelefoneResponse`

---

#### GET `/api/clinicas/{id}/telefones`

Lists all phone numbers for a clinic.

**Auth:** Required (JWT)

**Response (200 OK):** `List<TelefoneResponse>`

---

### 5. Servico (`/api/servicos`)

#### GET `/api/servicos`

Lists all available services.

**Auth:** Not required

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "nome": "string",
    "descricao": "string",
    "precoBase": 150.00,
    "duracaoMinutos": 60
  }
]
```

---

#### GET `/api/servicos/{id}`

Retrieves a service by ID.

**Auth:** Not required

**Response (200 OK):** `ServicoResponse` (same structure as above)

---

#### POST `/api/servicos`

Creates a new service.

**Auth:** Not required

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `nome` | String | Yes |
| `descricao` | String | No |
| `precoBase` | BigDecimal | Yes |
| `duracaoMinutos` | Integer | Yes |

**Response (201 Created):** `ServicoResponse`

---

#### PUT `/api/servicos/{id}`

Updates a service.

**Auth:** Not required

**Request Body:** Same as POST `/api/servicos`

**Response (200 OK):** Updated `ServicoResponse`

---

#### DELETE `/api/servicos/{id}`

Deletes a service.

**Auth:** Not required

**Response (204 No Content)**

---

### 6. Pet (`/api/pets`)

#### POST `/api/pets/cliente/{clienteId}`

Adds a new pet to a client.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `name` | String | Yes |
| `species` | String | Yes |
| `gender` | String | Yes |
| `weight` | Double | Yes |
| `birthDate` | LocalDate | Yes |
| `neutered` | Boolean | Yes |

**Response (201 Created):**
```json
{
  "id": "uuid",
  "name": "string",
  "species": "string",
  "gender": "string",
  "weight": 5.5,
  "birthDate": "2023-01-01",
  "neutered": false
}
```

---

#### GET `/api/pets/{id}`

Retrieves a pet by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `PetResponse` (same structure as above)

---

#### DELETE `/api/pets/{id}`

Deletes a pet (soft delete).

**Auth:** Required (JWT)

**Response (204 No Content)**

---

### 7. Veterinario (`/api/veterinarios`)

#### GET `/api/veterinarios`

Lists all veterinarians.

**Auth:** Required (JWT)

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "nome": "string",
    "crmv": "string",
    "especialidadeIds": ["uuid"]
  }
]
```

---

#### GET `/api/veterinarios/{id}`

Retrieves a veterinarian by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `VeterinarioResponse` (same structure as above)

---

#### POST `/api/veterinarios`

Creates a new veterinarian.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `name` | String | Yes |
| `crmv` | String | Yes |
| `specialtyIds` | Set<UUID> | No |

**Response (201 Created):** `VeterinarioResponse`

---

#### PUT `/api/veterinarios/{id}`

Updates a veterinarian.

**Auth:** Required (JWT)

**Request Body:** Same as POST `/api/veterinarios`

**Response (200 OK):** Updated `VeterinarioResponse`

---

#### DELETE `/api/veterinarios/{id}`

Deletes a veterinarian.

**Auth:** Required (JWT)

**Response (204 No Content)**

---

### 8. Especialidade (`/api/especialidades`)

#### GET `/api/especialidades`

Lists all specialties.

**Auth:** Required (JWT)

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "name": "string"
  }
]
```

---

#### GET `/api/especialidades/{id}`

Retrieves a specialty by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `EspecialidadeResponse` (same structure as above)

---

#### POST `/api/especialidades`

Creates a new specialty.

**Auth:** Required (JWT)

**Request Body:**

| Field | Type | Required |
|---|---|---|
| `name` | String | Yes |

**Response (201 Created):** `EspecialidadeResponse`

---

#### PUT `/api/especialidades/{id}`

Updates a specialty.

**Auth:** Required (JWT)

**Request Body:** Same as POST `/api/especialidades`

**Response (200 OK):** Updated `EspecialidadeResponse`

---

#### DELETE `/api/especialidades/{id}`

Deletes a specialty.

**Auth:** Required (JWT)

**Response (204 No Content)**

---

### 9. Endereco (`/api/enderecos`)

Global address management endpoints.

#### GET `/api/enderecos`

Lists all addresses in the system.

**Auth:** Required (JWT)

**Response (200 OK):** `List<EnderecoResponse>`

---

#### GET `/api/enderecos/{id}`

Retrieves an address by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `EnderecoResponse`

---

#### PUT `/api/enderecos/{id}`

Updates an address.

**Auth:** Required (JWT)

**Request Body:** Same fields as POST `/api/clientes/{id}/enderecos`

**Response (200 OK):** Updated `EnderecoResponse`

---

#### DELETE `/api/enderecos/{id}`

Deletes an address.

**Auth:** Required (JWT)

**Response (204 No Content)**

---

### 10. Telefone (`/api/telefones`)

Global phone number management endpoints.

#### GET `/api/telefones`

Lists all phone numbers in the system.

**Auth:** Required (JWT)

**Response (200 OK):** `List<TelefoneResponse>`

---

#### GET `/api/telefones/{id}`

Retrieves a phone number by ID.

**Auth:** Required (JWT)

**Response (200 OK):** `TelefoneResponse`

---

#### PUT `/api/telefones/{id}`

Updates a phone number.

**Auth:** Required (JWT)

**Request Body:** Same fields as POST `/api/clientes/{id}/telefones`

**Response (200 OK):** Updated `TelefoneResponse`

---

#### DELETE `/api/telefones/{id}`

Deletes a phone number.

**Auth:** Required (JWT)

**Response (204 No Content)**

---

## Summary Table

| Controller | Base Path | Auth | Endpoints |
|---|---|---|---|
| Auth | `/api/auth` | None | `POST /registro`, `POST /login` |
| Agendamento | `/api/agendamentos` | JWT | `POST /`, `GET /{id}`, `GET /cliente/{clienteId}`, `DELETE /{id}` |
| Cliente | `/api/clientes` | JWT | `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/enderecos`, `GET /{id}/enderecos`, `POST /{id}/telefones`, `GET /{id}/telefones` |
| Clinica | `/api/clinicas` | JWT | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/enderecos`, `GET /{id}/enderecos`, `POST /{id}/telefones`, `GET /{id}/telefones` |
| Servico | `/api/servicos` | None | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Pet | `/api/pets` | JWT | `POST /cliente/{clienteId}`, `GET /{id}`, `DELETE /{id}` |
| Veterinario | `/api/veterinarios` | JWT | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Especialidade | `/api/especialidades` | JWT | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Endereco | `/api/enderecos` | JWT | `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| Telefone | `/api/telefones` | JWT | `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |

**Total: 47 endpoints** across 10 controllers.

---

## Error Handling

The application currently returns generic `500 Internal Server Error` for unhandled exceptions. Error messages are returned as plain text strings.

---

## Tech Stack

- **Java 21** with Records for DTOs
- **Spring Boot 4.0.5**
- **Spring Data JPA** with PostgreSQL
- **Spring Security** with JWT (jjwt 0.12.6)
- **Lombok** (@Data, @Builder, @RequiredArgsConstructor, @UtilityClass)
- **Bean Validation** (Hibernate Validator)
