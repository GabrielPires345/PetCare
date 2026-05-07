# PetCare - Rotas e Portas

## Infraestrutura (Docker)

| Servico   | Porta Host | Porta Container | URL                          | Credenciais                  |
|-----------|------------|-----------------|------------------------------|------------------------------|
| PostgreSQL| 5433       | 5432            | `localhost:5433`             | user: `postgres` / pass: `postgres` / db: `petcare` |
| pgAdmin   | 5050       | 80              | `http://localhost:5050`      | email: `admin@petcare.com` / pass: `admin` |
| MailHog SMTP | 1025    | 1025            | `localhost:1025`             | -                            |
| MailHog Web  | 8025    | 8025            | `http://localhost:8025`      | -                            |

---

## Backend (Spring Boot) - `http://localhost:8080`

### Autenticacao - `/api/auth`

| Metodo | Rota                           | Descricao                        | Auth |
|--------|--------------------------------|----------------------------------|------|
| POST   | `/api/auth/login`              | Login (retorna JWT)              | Nao  |
| POST   | `/api/auth/registro`           | Registro de cliente              | Nao  |
| POST   | `/api/auth/registro-clinica`   | Registro de clinica              | Nao  |
| POST   | `/api/auth/registro-veterinario` | Registro de veterinario        | Nao  |
| GET    | `/api/auth/verificar-email`    | Verificar email (token no query) | Nao  |
| POST   | `/api/auth/reenviar-verificacao` | Reenviar email de verificacao  | Nao  |

### Clientes - `/api/clientes`

| Metodo | Rota                     | Descricao            | Auth |
|--------|--------------------------|----------------------|------|
| GET    | `/api/clientes`          | Listar todos         | Sim  |
| GET    | `/api/clientes/{id}`     | Buscar por ID        | Sim  |
| PUT    | `/api/clientes/{id}`     | Atualizar            | Sim  |
| DELETE | `/api/clientes/{id}`     | Remover              | Sim  |

### Pets - `/api/pets`

| Metodo | Rota                              | Descricao                  | Auth |
|--------|-----------------------------------|----------------------------|------|
| POST   | `/api/pets/cliente/{clienteId}`   | Criar pet para o cliente   | Sim  |
| GET    | `/api/pets/{id}`                  | Buscar pet por ID          | Sim  |
| GET    | `/api/pets/cliente/{clienteId}`   | Listar pets do cliente     | Sim  |
| DELETE | `/api/pets/{id}`                  | Remover pet                | Sim  |

### Agendamentos - `/api/agendamentos`

| Metodo | Rota                                  | Descricao                       | Auth |
|--------|---------------------------------------|---------------------------------|------|
| POST   | `/api/agendamentos`                   | Criar agendamento               | Sim  |
| GET    | `/api/agendamentos/{id}`              | Buscar por ID                   | Sim  |
| GET    | `/api/agendamentos/cliente/{clienteId}` | Listar do cliente             | Sim  |
| GET    | `/api/agendamentos`                   | Listar todos                    | Sim  |
| DELETE | `/api/agendamentos/{id}`              | Cancelar agendamento            | Sim  |

### Clinicas - `/api/clinicas`

| Metodo | Rota                  | Descricao            | Auth |
|--------|-----------------------|----------------------|------|
| GET    | `/api/clinicas`       | Listar todas         | Sim  |
| GET    | `/api/clinicas/{id}`  | Buscar por ID        | Sim  |
| POST   | `/api/clinicas`       | Criar                | Sim  |
| PUT    | `/api/clinicas/{id}`  | Atualizar            | Sim  |
| DELETE | `/api/clinicas/{id}`  | Remover              | Sim  |

### Veterinarios - `/api/veterinarios`

| Metodo | Rota                       | Descricao            | Auth |
|--------|----------------------------|----------------------|------|
| GET    | `/api/veterinarios`        | Listar todos         | Sim  |
| GET    | `/api/veterinarios/{id}`   | Buscar por ID        | Sim  |
| POST   | `/api/veterinarios`        | Criar                | Sim  |
| PUT    | `/api/veterinarios/{id}`   | Atualizar            | Sim  |
| DELETE | `/api/veterinarios/{id}`   | Remover              | Sim  |

### Servicos - `/api/servicos`

| Metodo | Rota                  | Descricao            | Auth |
|--------|-----------------------|----------------------|------|
| GET    | `/api/servicos`       | Listar todos         | Nao  |
| GET    | `/api/servicos/{id}`  | Buscar por ID        | Nao  |
| POST   | `/api/servicos`       | Criar                | Sim  |
| PUT    | `/api/servicos/{id}`  | Atualizar            | Sim  |
| DELETE | `/api/servicos/{id}`  | Remover              | Sim  |

### Especialidades - `/api/especialidades`

| Metodo | Rota                        | Descricao            | Auth |
|--------|-----------------------------|----------------------|------|
| GET    | `/api/especialidades`       | Listar todas         | Sim  |
| GET    | `/api/especialidades/{id}`  | Buscar por ID        | Sim  |
| POST   | `/api/especialidades`       | Criar                | Sim  |
| PUT    | `/api/especialidades/{id}`  | Atualizar            | Sim  |
| DELETE | `/api/especialidades/{id}`  | Remover              | Sim  |

---

## Frontend (Symfony) - `http://localhost:8000`

### Rotas Publicas

| Metodo | Rota               | Descricao                    | Arquivo                              |
|--------|--------------------|------------------------------|--------------------------------------|
| GET    | `/`                | Pagina inicial               | `HomeController::index`              |
| GET    | `/login/form`      | Formulario de login          | `LoginController::loginForm`         |
| POST   | `/login`           | Processar login              | `LoginController::login`             |
| GET    | `/logout`          | Logout                       | `LogoutController::logout`           |
| GET    | `/registro`        | Formulario de registro       | `RegistrationController::form`       |
| POST   | `/registro`        | Processar registro cliente   | `RegistrationController::register`   |
| GET    | `/registro/clinica`     | Form registro clinica   | `RegistrationController::formClinica` |
| POST   | `/registro/clinica`     | Processar registro clinica | `RegistrationController::registerClinica` |
| GET    | `/registro/veterinario` | Form registro veterinario | `RegistrationController::formVet` |
| POST   | `/registro/veterinario` | Processar registro vet   | `RegistrationController::registerVet` |
| GET    | `/verificar-email` | Verificacao de email         | `EmailVerificationController`        |

### Rotas Protegidas (`/app/*` - requer ROLE_USER)

| Metodo | Rota                        | Descricao                | Arquivo                              |
|--------|-----------------------------|--------------------------|--------------------------------------|
| GET    | `/app`                      | Dashboard pos-login      | `HomeController::posLogin`           |
| GET    | `/app/pets`                 | Listar pets              | `PetController::index`               |
| GET    | `/app/pets/novo`            | Form novo pet            | `PetController::novo`                |
| POST   | `/app/pets/novo`            | Criar pet                | `PetController::criar`               |
| POST   | `/app/pets/{petId}/remover` | Remover pet              | `PetController::remover`             |
| GET    | `/app/agendamentos`         | Listar agendamentos      | `AgendamentoController::index`       |
| GET    | `/app/agendamentos/novo`    | Form novo agendamento    | `AgendamentoController::novo`        |
| POST   | `/app/agendamentos/novo`    | Criar agendamento        | `AgendamentoController::criar`       |
| POST   | `/app/agendamentos/{id}/cancelar` | Cancelar agendamento | `AgendamentoController::cancelar`   |
| GET    | `/app/perfil`               | Ver perfil               | `PerfilController::index`            |
| GET    | `/app/perfil/editar`        | Form editar perfil       | `PerfilController::editar`           |
| POST   | `/app/perfil/editar`        | Salvar perfil            | `PerfilController::salvar`           |

---

## Arquitetura de Comunicacao

```
Browser
  │
  ├─ http://localhost:8000 ──► Frontend (Symfony 7.4 / PHP 8.2 / Apache)
  │     │
  │     └─ HttpClient ──► http://backend:8080 ──► Backend (Spring Boot / Java 21)
  │                                                    │
  │                                                    ├─► PostgreSQL (postgres:5432)
  │                                                    └─► MailHog SMTP (mailhog:1025)
  │
  ├─ http://localhost:8080/swagger-ui.html ──► Backend API (Swagger/Endpoints)
  ├─ http://localhost:5050 ──► pgAdmin
  └─ http://localhost:8025 ──► MailHog Web UI
```

### Rede Docker

Todos os servicos rodam na rede `petcare-net` (bridge). O frontend se comunica com o backend via nome do container (`backend:8080`), nao via localhost.
