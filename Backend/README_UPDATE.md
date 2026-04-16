# PetCare Backend - Documentação da API

## Visão Geral

Aplicação Spring Boot 4.0.5 que fornece uma API REST para a plataforma PetCare. Java 21, PostgreSQL, Spring Data JPA, Spring Security com autenticação JWT.

## Pré-requisitos

Para executar o projeto, você precisa ter instalado:
- Java 21
- Maven 3.8+
- PostgreSQL (para ambiente de produção)

## Acesso à Documentação

### Swagger UI
A documentação da API está disponível através do Swagger UI:
- **Interface:** http://localhost:8080/swagger-ui.html
- **JSON da API:** http://localhost:8080/api-docs

### H2 Console
Para acesso ao banco de dados em ambiente de desenvolvimento:
- **Console:** http://localhost:8080/h2-console
- **JDBC URL:** jdbc:h2:mem:petcare
- **Usuário:** sa
- **Senha:** (vazia)

## Guia de Autenticação

Para acessar os endpoints protegidos da API, é necessário obter um token JWT:

1. **Obter token via endpoint de login:**
   - **POST** `/api/auth/login`
   - Envie email e senha no corpo da requisição
   - O token será retornado na resposta

2. **Usar token no Swagger UI:**
   - Clique no botão "Authorize" (ícone de cadeado no canto superior direito)
   - Insira: `Bearer <seu-token-jwt>`
   - Clique em "Authorize"

3. **Usar token em requisições:**
   - Adicione o header: `Authorization: Bearer <seu-token-jwt>`

## Autenticação

Todos os endpoints, exceto `/api/auth/**` e `/api/servicos/**`, exigem um token JWT válido.

Para autenticar, inclua o token no header `Authorization`:

```
Authorization: Bearer <seu-token-jwt>
```

---

## Endpoints

### 1. Autenticação (`/api/auth`)

#### POST `/api/auth/registro`

Registra um novo cliente (cria Usuário + Perfil de Cliente + Pet). Retorna um token JWT.

**Autenticação:** Não requerida

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório | Validação |
|---|---|---|---|
| `nomeUsuario` | String | Sim | Máximo 10 caracteres |
| `email` | String | Sim | Deve ser um formato de e-mail válido |
| `senha` | String | Sim | - |
| `confirmaSenha` | String | Sim | Deve ser igual a `senha` |
| `nomeCompleto` | String | Sim | - |
| `cpf` | String | Sim | 11-14 caracteres |
| `dataNascimento` | LocalDate | Sim | Formato ISO: `yyyy-MM-dd` |
| `pet` | PetRequest | Sim | Objeto aninhado (veja abaixo) |

**PetRequest (aninhado):**

| Campo | Tipo | Obrigatório | Validação |
|---|---|---|---|
| `nome` | String | Sim | - |
| `especie` | String | Sim | - |
| `sexo` | String | Sim | - |
| `peso` | Double | Sim | - |
| `dataNascimento` | LocalDate | Sim | Formato ISO: `yyyy-MM-dd` |
| `castrado` | Boolean | Sim | - |

**Resposta (201 Created):**
```json
{
  "user": {
    "id": "uuid",
    "nomeUsuario": "string",
    "email": "string",
    "nivelAcesso": "CLIENTE"
  },
  "token": "eyJhbGci..."
}
```

**Resposta (400 Bad Request):**
- `"As senhas não conferem"` se `senha` != `confirmaSenha`

---

#### POST `/api/auth/login`

Autentica um usuário e retorna um token JWT.

**Autenticação:** Não requerida

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `email` | String | Sim |
| `senha` | String | Sim |

**Resposta (200 OK):**
```json
{
  "user": {
    "id": "uuid",
    "nomeUsuario": "string",
    "email": "string",
    "nivelAcesso": "CLIENTE"
  },
  "clienteId": "uuid",
  "token": "eyJhbGci..."
}
```

**Resposta (401 Unauthorized):** `"Invalid credentials"`

---

### 2. Agendamento (`/api/agendamentos`)

#### GET `/api/agendamentos`

Lista todos os agendamentos.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):**
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

Agenda um novo atendimento. Valida se Pet, Clínica, Veterinário e Serviço existem. Define o status inicial como `AGENDADO` e calcula o `valorFinal` com base no preço do serviço.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `petId` | UUID | Sim |
| `clinicaId` | UUID | Sim |
| `veterinarioId` | UUID | Sim |
| `servicoId` | UUID | Sim |
| `dataHoraMarcada` | LocalDateTime | Sim | Formato ISO: `yyyy-MM-ddTHH:mm:ss` |

**Resposta (201 Created):**
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

Busca um agendamento pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `AgendamentoResponse` (mesma estrutura acima)

---

#### GET `/api/agendamentos/cliente/{clienteId}`

Lista todos os agendamentos de um cliente específico.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):**
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

Cancela um agendamento. O status é atualizado para `CANCELADO`.

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

### 3. Cliente (`/api/clientes`)

#### GET `/api/clientes`

Lista todos os clientes registrados.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):**
```json
[
  {
    "id": "uuid",
    "nomeCompleto": "string",
    "cpf": "string",
    "dataNascimento": "2000-01-01"
  }
]
```

---

#### GET `/api/clientes/{id}`

Busca um cliente pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `ClienteResponse` (mesma estrutura acima)

---

#### PUT `/api/clientes/{id}`

Atualiza as informações do perfil de um cliente.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nomeCompleto` | String | Sim |
| `cpf` | String | Sim | 11-14 caracteres |
| `dataNascimento` | LocalDate | Sim |

**Resposta (200 OK):** `ClienteResponse` atualizado

---

#### DELETE `/api/clientes/{id}`

Remove um cliente (soft delete).

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

#### POST `/api/clientes/{id}/enderecos`

Adiciona um endereço a um cliente.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório | Validação |
|---|---|---|---|
| `logradouro` | String | Sim | - |
| `numero` | String | Sim | - |
| `bairro` | String | Sim | - |
| `cidade` | String | Sim | - |
| `uf` | String | Sim | Máximo 2 caracteres (ex: `SP`) |
| `cep` | String | Sim | - |

**Resposta (201 Created):**
```json
{
  "id": "uuid",
  "logradouro": "string",
  "numero": "string",
  "bairro": "string",
  "cidade": "string",
  "uf": "SP",
  "cep": "string"
}
```

---

#### GET `/api/clientes/{id}/enderecos`

Lista todos os endereços de um cliente.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `List<EnderecoResponse>`

---

#### POST `/api/clientes/{id}/telefones`

Adiciona um telefone a um cliente.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório | Validação |
|---|---|---|---|
| `ddd` | String | Sim | Máximo 2 caracteres |
| `numero` | String | Sim | - |
| `whatsapp` | Boolean | Sim | - |

**Resposta (201 Created):**
```json
{
  "id": "uuid",
  "ddd": "11",
  "numero": "999999999",
  "whatsapp": true
}
```

---

#### GET `/api/clientes/{id}/telefones`

Lista todos os telefones de um cliente.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `List<TelefoneResponse>`

---

### 4. Clínica (`/api/clinicas`)

#### GET `/api/clinicas`

Lista todas as clínicas.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):**
```json
[
  {
    "id": "uuid",
    "nomeClinica": "string",
    "razaoSocial": "string",
    "cnpj": "string"
  }
]
```

---

#### GET `/api/clinicas/{id}`

Busca uma clínica pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `ClinicaResponse` (mesma estrutura acima)

---

#### POST `/api/clinicas`

Cria uma nova clínica.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nomeClinica` | String | Sim |
| `razaoSocial` | String | Sim |
| `cnpj` | String | Sim | Formato de CNPJ válido |

**Resposta (201 Created):** `ClinicaResponse`

---

#### PUT `/api/clinicas/{id}`

Atualiza uma clínica.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmo do POST `/api/clinicas`

**Resposta (200 OK):** `ClinicaResponse` atualizado

---

#### DELETE `/api/clinicas/{id}`

Remove uma clínica.

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

#### POST `/api/clinicas/{id}/enderecos`

Adiciona um endereço a uma clínica.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmos campos de `/api/clientes/{id}/enderecos`

**Resposta (201 Created):** `EnderecoResponse`

---

#### GET `/api/clinicas/{id}/enderecos`

Lista todos os endereços de uma clínica.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `List<EnderecoResponse>`

---

#### POST `/api/clinicas/{id}/telefones`

Adiciona um telefone a uma clínica.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmos campos de `/api/clientes/{id}/telefones`

**Resposta (201 Created):** `TelefoneResponse`

---

#### GET `/api/clinicas/{id}/telefones`

Lista todos os telefones de uma clínica.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `List<TelefoneResponse>`

---

### 5. Serviço (`/api/servicos`)

#### GET `/api/servicos`

Lista todos os serviços disponíveis.

**Autenticação:** Não requerida

**Resposta (200 OK):**
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

Busca um serviço pelo ID.

**Autenticação:** Não requerida

**Resposta (200 OK):** `ServicoResponse` (mesma estrutura acima)

---

#### POST `/api/servicos`

Cria um novo serviço.

**Autenticação:** Não requerida

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nome` | String | Sim |
| `descricao` | String | Não |
| `precoBase` | BigDecimal | Sim |
| `duracaoMinutos` | Integer | Sim |

**Resposta (201 Created):** `ServicoResponse`

---

#### PUT `/api/servicos/{id}`

Atualiza um serviço.

**Autenticação:** Não requerida

**Corpo da Requisição:** Mesmo do POST `/api/servicos`

**Resposta (200 OK):** `ServicoResponse` atualizado

---

#### DELETE `/api/servicos/{id}`

Remove um serviço.

**Autenticação:** Não requerida

**Resposta (204 No Content)**

---

### 6. Pet (`/api/pets`)

#### POST `/api/pets/cliente/{clienteId}`

Adiciona um novo pet a um cliente.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nome` | String | Sim |
| `especie` | String | Sim |
| `sexo` | String | Sim |
| `peso` | Double | Sim |
| `dataNascimento` | LocalDate | Sim |
| `castrado` | Boolean | Sim |

**Resposta (201 Created):**
```json
{
  "id": "uuid",
  "nome": "string",
  "especie": "string",
  "sexo": "string",
  "peso": 5.5,
  "dataNascimento": "2023-01-01",
  "castrado": false
}
```

---

#### GET `/api/pets/{id}`

Busca um pet pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `PetResponse` (mesma estrutura acima)

---

#### DELETE `/api/pets/{id}`

Remove um pet (soft delete).

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

### 7. Veterinário (`/api/veterinarios`)

#### GET `/api/veterinarios`

Lista todos os veterinários.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):**
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

Busca um veterinário pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `VeterinarioResponse` (mesma estrutura acima)

---

#### POST `/api/veterinarios`

Cria um novo veterinário.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nome` | String | Sim |
| `crmv` | String | Sim |
| `especialidadeIds` | Set<UUID> | Não |

**Resposta (201 Created):** `VeterinarioResponse`

---

#### PUT `/api/veterinarios/{id}`

Atualiza um veterinário.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmo do POST `/api/veterinarios`

**Resposta (200 OK):** `VeterinarioResponse` atualizado

---

#### DELETE `/api/veterinarios/{id}`

Remove um veterinário.

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

### 8. Especialidade (`/api/especialidades`)

#### GET `/api/especialidades`

Lista todas as especialidades.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):**
```json
[
  {
    "id": "uuid",
    "nome": "string"
  }
]
```

---

#### GET `/api/especialidades/{id}`

Busca uma especialidade pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `EspecialidadeResponse` (mesma estrutura acima)

---

#### POST `/api/especialidades`

Cria uma nova especialidade.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:**

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nome` | String | Sim |

**Resposta (201 Created):** `EspecialidadeResponse`

---

#### PUT `/api/especialidades/{id}`

Atualiza uma especialidade.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmo do POST `/api/especialidades`

**Resposta (200 OK):** `EspecialidadeResponse` atualizado

---

#### DELETE `/api/especialidades/{id}`

Remove uma especialidade.

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

### 9. Endereço (`/api/enderecos`)

Endpoints de gerenciamento global de endereços.

#### GET `/api/enderecos`

Lista todos os endereços do sistema.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `List<EnderecoResponse>`

---

#### GET `/api/enderecos/{id}`

Busca um endereço pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `EnderecoResponse`

---

#### PUT `/api/enderecos/{id}`

Atualiza um endereço.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmos campos do POST `/api/clientes/{id}/enderecos`

**Resposta (200 OK):** `EnderecoResponse` atualizado

---

#### DELETE `/api/enderecos/{id}`

Remove um endereço.

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

### 10. Telefone (`/api/telefones`)

Endpoints de gerenciamento global de telefones.

#### GET `/api/telefones`

Lista todos os telefones do sistema.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `List<TelefoneResponse>`

---

#### GET `/api/telefones/{id}`

Busca um telefone pelo ID.

**Autenticação:** Requerida (JWT)

**Resposta (200 OK):** `TelefoneResponse`

---

#### PUT `/api/telefones/{id}`

Atualiza um telefone.

**Autenticação:** Requerida (JWT)

**Corpo da Requisição:** Mesmos campos do POST `/api/clientes/{id}/telefones`

**Resposta (200 OK):** `TelefoneResponse` atualizado

---

#### DELETE `/api/telefones/{id}`

Remove um telefone.

**Autenticação:** Requerida (JWT)

**Resposta (204 No Content)**

---

## Tabela Resumo

| Controller | Caminho Base | Autenticação | Endpoints |
|---|---|---|---|
| Auth | `/api/auth` | Nenhuma | `POST /registro`, `POST /login` |
| Agendamento | `/api/agendamentos` | JWT | `POST /`, `GET /`, `GET /{id}`, `GET /cliente/{clienteId}`, `DELETE /{id}` |
| Cliente | `/api/clientes` | JWT | `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/enderecos`, `GET /{id}/enderecos`, `POST /{id}/telefones`, `GET /{id}/telefones` |
| Clínica | `/api/clinicas` | JWT | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/enderecos`, `GET /{id}/enderecos`, `POST /{id}/telefones`, `GET /{id}/telefones` |
| Serviço | `/api/servicos` | Nenhuma | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Pet | `/api/pets` | JWT | `POST /cliente/{clienteId}`, `GET /{id}`, `DELETE /{id}` |
| Veterinário | `/api/veterinarios` | JWT | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Especialidade | `/api/especialidades` | JWT | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Endereço | `/api/enderecos` | JWT | `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| Telefone | `/api/telefones` | JWT | `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |

**Total: 48 endpoints** em 10 controllers.

---

## Tratamento de Erros

A aplicação atualmente retorna `500 Internal Server Error` genérico para exceções não tratadas. As mensagens de erro são retornadas como texto puro.

---

## Stack Tecnológica

- **Java 21** com Records para DTOs
- **Spring Boot 4.0.5**
- **Spring Data JPA** com PostgreSQL
- **Spring Security** com JWT (jjwt 0.12.6)
- **Lombok** (@Data, @Builder, @RequiredArgsConstructor, @UtilityClass)
- **Bean Validation** (Hibernate Validator)
