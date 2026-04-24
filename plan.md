# Plano de Integracao: Tela de Login/Registro com API Backend

## Visao Geral

O frontend (Symfony 7.4 + Twig) precisa consumir a API REST do backend (Spring Boot) para permitir o registro e login de **Cliente**, **Veterinario** e **Clinica**. Atualmente, a integracao esta completamente quebrada com multiplos bugs criticos.

---

## 1. Problemas Existentes (Bugs Criticos)

| # | Problema | Arquivo | Descricao |
|---|----------|---------|-----------|
| 1 | Formulario de registro envia para `#` | `cadastroUser/index.html.twig:29` | O form action e `#`, registro nunca chega ao servidor |
| 2 | Botao de login contem anchor tag | `login/index.html.twig:54` | `<button type="submit"><a href="...">Entrar</a></button>` - o anchor bypassa o form submit |
| 3 | Extensao dupla `.twig.twig` | `LoginController.php:60`, `CadastroUserController.php:29` | Templates `telaPosLogin.html.twig.twig` nao existem |
| 4 | DTO errado no login | `LoginController.php:37` | Usa `ClienteCadastroDto` em vez de `LoginDto` |
| 5 | `parseData()` envia so email+senha | `LoginController.php:65-71` | Ignora todos os campos de perfil |
| 6 | URL da API errada | `LoginController.php:44` | `'register'` e relativo, deveria ser `http://localhost:8080/api/auth/...` |
| 7 | `form_params` vs JSON conflito | `LoginController.php:49` | Envia form-encoded mas declara Content-Type: application/json |
| 8 | Logica de validacao invertida | `UserServices.php:11` | `isStrongPassword()` retorna true para senhas fortes, fazendo `validateForm()` rejeitar entradas validas |
| 9 | Nomes de campos nao correspondem | Forms vs Backend | Frontend: `user_name`/`password`/`password_confirmation`; Backend: `nomeUsuario`/`senha`/`confirmaSenha` |
| 10 | Nenhum token JWT e salvo | Todo frontend | Login retorna JWT mas nada salva ou envia |
| 11 | Sem estado de autenticacao | `security.yaml` | `users_in_memory: { memory: null }` sem autenticador |
| 12 | Arquivos de API vazios | `Api.js`, `PetService.js` | Nao existe camada de API client-side |
| 13 | Botao "Sair" nao funciona | `telaPosLogin.html.twig:37` | Sem rota de logout ou handler JS |

---

## 2. Endpoints da API Backend (Ja Implementados)

### 2.1 Login (todos os tipos de usuario)

```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "email": "string (obrigatorio, email valido)",
  "senha": "string (obrigatorio)"
}

Response 200:
{
  "user": {
    "id": "UUID",
    "nomeUsuario": "string",
    "email": "string",
    "nivelAcesso": "CLIENTE | CLINICA | VETERINARIO"
  },
  "perfilId": "UUID do perfil especifico",
  "tipoPerfil": "CLIENTE | CLINICA | VETERINARIO",
  "token": "JWT string"
}

Erros:
- 401 CREDENCIAIS_INVALIDAS: email/senha incorretos
- 401 EMAIL_NAO_VERIFICADO: email nao verificado ainda
- 404 RECURSO_NAO_ENCONTRADO: perfil nao encontrado
```

### 2.2 Registro Cliente

```
POST /api/auth/registro
Content-Type: application/json

Request:
{
  "nomeUsuario": "string (obrigatorio, max 10 chars)",
  "email": "string (obrigatorio, email valido)",
  "senha": "string (obrigatorio)",
  "confirmaSenha": "string (obrigatorio, deve ser igual a senha)",
  "nomeCompleto": "string (obrigatorio)",
  "cpf": "string (obrigatorio, 11-14 chars, CPF valido)",
  "dataNascimento": "date (obrigatorio, ISO format ex: 1990-01-15)",
  "pet": {
    "nome": "string (obrigatorio)",
    "especie": "string (obrigatorio)",
    "sexo": "string (obrigatorio)",
    "peso": "double (obrigatorio)",
    "dataNascimento": "date (obrigatorio, ISO format)",
    "castrado": "boolean (obrigatorio)"
  }
}

Response 201:
{
  "mensagem": "Registro realizado com sucesso. Verifique seu email para ativar sua conta.",
  "email": "user@example.com"
}
```

### 2.3 Registro Veterinario

```
POST /api/auth/registro-veterinario
Content-Type: application/json

Request:
{
  "nomeUsuario": "string (obrigatorio, max 10 chars)",
  "email": "string (obrigatorio, email valido)",
  "senha": "string (obrigatorio)",
  "confirmaSenha": "string (obrigatorio, deve ser igual a senha)",
  "nome": "string (obrigatorio)",
  "crmv": "string (obrigatorio, unico)",
  "especialidadeIds": ["UUID string (opcional)"]
}

Response 201:
{
  "mensagem": "Registro realizado com sucesso. Verifique seu email para ativar sua conta.",
  "email": "vet@example.com"
}
```

### 2.4 Registro Clinica

```
POST /api/auth/registro-clinica
Content-Type: application/json

Request:
{
  "nomeUsuario": "string (obrigatorio, max 10 chars)",
  "email": "string (obrigatorio, email valido)",
  "senha": "string (obrigatorio)",
  "confirmaSenha": "string (obrigatorio, deve ser igual a senha)",
  "nomeClinica": "string (obrigatorio)",
  "razaoSocial": "string (obrigatorio)",
  "cnpj": "string (obrigatorio, CNPJ valido)"
}

Response 201:
{
  "mensagem": "Registro realizado com sucesso. Verifique seu email para ativar sua conta.",
  "email": "clinic@example.com"
}
```

### 2.5 Verificar Email

```
GET /api/auth/verificar-email?token=<token>

Response 200:
{
  "mensagem": "Email verificado com sucesso! Voce ja pode fazer login."
}
```

### 2.6 Reenviar Verificacao

```
POST /api/auth/reenviar-verificacao
Content-Type: application/json

Request:
{ "email": "string" }

Response 200:
{
  "mensagem": "Email de verificacao reenviado com sucesso."
}
```

### 2.7 Formato Padrao de Erro

```json
{
  "timestamp": "2026-04-24T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "code": "SENHAS_NAO_CONFEREM",
  "message": "As senhas nao conferem",
  "path": "/api/auth/registro",
  "fieldErrors": [
    {
      "field": "email",
      "message": "O campo email e obrigatorio",
      "rejectedValue": null
    }
  ]
}
```

---

## 3. Mapeamento de Campos (Frontend -> Backend)

### 3.1 Login

| Campo Frontend (name) | Campo Backend | Observacao |
|-----------------------|---------------|------------|
| `email` | `email` | Ja coincide |
| `senha` | `senha` | Ja coincide |

### 3.2 Registro - Campos Base (todos os tipos)

| Campo Frontend (name) | Campo Backend | Observacao |
|-----------------------|---------------|------------|
| `user_name` | `nomeUsuario` | Renomear |
| `email` | `email` | Ja coincide |
| `password` | `senha` | Renomear |
| `password_confirmation` | `confirmaSenha` | Renomear |
| `nivel_acesso` | (determina o endpoint) | Nao e enviado no body, define rota |

### 3.3 Registro - Campos Especificos por Perfil

**CLIENTE** (`POST /api/auth/registro`):

| Campo Frontend (name) | Campo Backend | Observacao |
|-----------------------|---------------|------------|
| `nome_completo` | `nomeCompleto` | Renomear |
| `cpf` | `cpf` | Ja coincide |
| `data_nascimento` | `dataNascimento` | Renomear + converter para ISO date |
| (nao existe) | `pet` | **CAMPO AUSENTE** - precisa adicionar secao de pet no formulario |

**VETERINARIO** (`POST /api/auth/registro-veterinario`):

| Campo Frontend (name) | Campo Backend | Observacao |
|-----------------------|---------------|------------|
| `nome_vet` | `nome` | Renomear |
| `crmv` | `crmv` | Ja coincide |
| (nao existe) | `especialidadeIds` | Opcional - pode omitir |

**CLINICA** (`POST /api/auth/registro-clinica`):

| Campo Frontend (name) | Campo Backend | Observacao |
|-----------------------|---------------|------------|
| `nome_clinica` | `nomeClinica` | Renomear |
| `razao_social` | `razaoSocial` | Renomear |
| `cnpj` | `cnpj` | Ja coincide |

### 3.4 Campos do Frontend Nao Usados pelo Backend

Estes campos existem no formulario de registro mas a API do backend nao os recebe no registro:
- `ddd`, `telefone` - O backend tem endpoints separados para telefone (`POST /api/clientes/{id}/telefones`)
- `cep`, `cidade`, `uf` - O backend tem endpoints separados para endereco (`POST /api/clientes/{id}/enderecos`)

**Decisao**: Remover estes campos do formulario de registro OU manter como opcionais para serem enviados apos o registro. Recomendo **remover** do formulario de registro para simplificar o fluxo inicial.

---

## 4. Plano de Implementacao

### Fase 1: Configuracao e Servicos Base

#### 4.1.1 Configurar URL base da API Backend

**Arquivo**: `petCareFrontEnd/config/services.yaml` ou novo arquivo de config

Criar um parametro para a URL base do backend:
```yaml
parameters:
  app.backend_api_url: 'http://localhost:8080'
```

#### 4.1.2 Criar Servico de API (BackendApiService)

**Arquivo novo**: `petCareFrontEnd/src/Services/BackendApiService.php`

Responsabilidades:
- Injetar `Symfony\Contracts\HttpClient\HttpClientInterface`
- Receber a URL base do backend via config
- Fazer chamadas HTTP para o backend (login, registro cliente, registro vet, registro clinica)
- Retornar arrays decodificados do JSON
- Tratar erros HTTP do backend e converter em mensagens compreensiveis

Metodos necessarios:
```php
public function login(string $email, string $senha): array
public function registrarCliente(array $dados): array
public function registrarVeterinario(array $dados): array
public function registrarClinica(array $dados): array
public function verificarEmail(string $token): array
public function reenviarVerificacao(string $email): array
```

Todos os metodos devem:
- Enviar `Content-Type: application/json`
- Usar `json` (nao `form_params`) no corpo da requisicao
- Tratar respostas de erro (4xx, 5xx) e lancar excecoes apropriadas
- Usar URLs absolutas: `{baseUrl}/api/auth/login`, etc.

#### 4.1.3 Criar DTOs Corretos

**Arquivo novo**: `petCareFrontEnd/src/Dto/Login/LoginRequestDto.php`
- Campos: `email`, `senha`

**Arquivo novo**: `petCareFrontEnd/src/Dto/Cadastro/ClienteRegistroDto.php`
- Campos: `nomeUsuario`, `email`, `senha`, `confirmaSenha`, `nomeCompleto`, `cpf`, `dataNascimento`, `pet` (array)

**Arquivo novo**: `petCareFrontEnd/src/Dto/Cadastro/VeterinarioRegistroDto.php`
- Campos: `nomeUsuario`, `email`, `senha`, `confirmaSenha`, `nome`, `crmv`

**Arquivo novo**: `petCareFrontEnd/src/Dto/Cadastro/ClinicaRegistroDto.php`
- Campos: `nomeUsuario`, `email`, `senha`, `confirmaSenha`, `nomeClinica`, `razaoSocial`, `cnpj`

**Arquivo existente a corrigir**: `petCareFrontEnd/src/Dto/Login/LoginDto.php` - Manter como DTO de validacao do formulario

**Arquivo existente a corrigir**: `petCareFrontEnd/src/Dto/Cadastro/ClienteCadastroDto.php` - Atualizar campos para corresponder ao formulario

### Fase 2: Corrigir Controladores

#### 4.2.1 Refatorar LoginController

**Arquivo**: `petCareFrontEnd/src/Controller/Login/LoginController.php`

Alteracoes:
1. Injetar `BackendApiService` em vez de usar HttpClient diretamente
2. No metodo `login()`:
   - Receber dados do formulario via Request
   - Validar com `LoginDto` (campos: email, senha)
   - Chamar `BackendApiService::login($email, $senha)`
   - Se sucesso: salvar JWT token na sessao, salvar dados do usuario na sessao
   - Redirecionar para dashboard (`app_pos_login`)
   - Se erro: exibir mensagem de erro no formulario
3. Remover o metodo `handleUserRequest()` quebrado
4. Corrigir o nome do template (remover `.twig` duplicado)
5. Remover o DTO errado (`ClienteCadastroDto` no login)

#### 4.2.2 Refatorar CadastroUserController

**Arquivo**: `petCareFrontEnd/src/Controller/Cadastro/CadastroUserController.php`

Alteracoes:
1. Injetar `BackendApiService`
2. No metodo POST:
   - Receber dados do formulario via Request
   - Determinar o tipo de perfil pelo campo `nivel_acesso`
   - Validar campos obrigatorios conforme o tipo
   - Mapear nomes dos campos do formulario para nomes do backend
   - Chamar o metodo de registro apropriado do BackendApiService:
     - `CLIENTE` -> `registrarCliente()`
     - `VETERINARIO` -> `registrarVeterinario()`
     - `CLINICA` -> `registrarClinica()`
   - Se sucesso: redirecionar para pagina de "verifique seu email"
   - Se erro: exibir mensagens de erro no formulario
3. Corrigir nome do template (remover `.twig` duplicado)

### Fase 3: Corrigir Templates (Twig)

#### 4.3.1 Corrigir Template de Login

**Arquivo**: `petCareFrontEnd/templates/login/index.html.twig`

Alteracoes:
1. **Remover anchor tag dentro do botao submit** - O botao deve ser apenas:
   ```html
   <button type="submit" class="btn ...">Entrar</button>
   ```
2. Manter o form action apontando para `{{ path('app_login') }}`
3. Adicionar area para exibir mensagens de erro/flash:
   ```twig
   {% for message in app.flashes('error') %}
     <div class="alert alert-danger">{{ message }}</div>
   {% endfor %}
   ```
4. Os nomes dos campos `email` e `senha` ja coincidem com o backend - manter

#### 4.3.2 Corrigir Template de Registro

**Arquivo**: `petCareFrontEnd/templates/cadastroUser/index.html.twig`

Alteracoes:
1. **Alterar form action** de `#` para `{{ path('app_cadastro') }}`
2. Adicionar area para mensagens flash de erro/sucesso
3. **Renomear campos do formulario** para corresponder ao backend:

   | Campo Atual (name) | Novo name | type |
   |-------------------|-----------|------|
   | `user_name` | `nomeUsuario` | text |
   | `password` | `senha` | password |
   | `password_confirmation` | `confirmaSenha` | password |
   | `nome_completo` | `nomeCompleto` | text |
   | `data_nascimento` | `dataNascimento` | date |
   | `nome_vet` | `nome` (vet) | text |
   | `nome_clinica` | `nomeClinica` | text |
   | `razao_social` | `razaoSocial` | text |

4. **Adicionar secao de Pet no formulario** (quando `nivel_acesso` = CLIENTE):
   - Campos do pet: `pet[nome]`, `pet[especie]`, `pet[sexo]`, `pet[peso]`, `pet[dataNascimento]`, `pet[castrado]`
   - Exibir/ocultar junto com os campos de cliente via JavaScript
   - **NOTA**: O backend EXIGE o campo `pet` no registro de cliente. Se nao quisermos obrigar o pet no registro, precisamos alterar o backend para tornar `pet` opcional em `ClienteCreateRequest`. **Decisao recomendada**: Adicionar campos do pet no formulario como obrigatorios para cliente.

5. **Remover campos nao usados pelo backend** no registro:
   - `ddd`, `telefone`, `cep`, `cidade`, `uf`
   - Ou manter como secao opcional "Dados de Contato (preencha depois)"

6. Atualizar o JavaScript inline que mostra/esconde secoes para incluir a secao de pet

#### 4.3.3 Criar Template de Pos-Registro

**Arquivo novo**: `petCareFrontEnd/templates/cadastroUser/verificacao_email.html.twig`

- Exibir mensagem: "Registro realizado com sucesso! Verifique seu email para ativar sua conta."
- Link para reenviar verificacao
- Link para voltar ao login

#### 4.3.4 Corrigir Template Pos-Login (Dashboard)

**Arquivo**: `petCareFrontEnd/templates/home/telaPosLogin.html.twig`

- Exibir nome do usuario e tipo de perfil
- Adicionar funcionalidade ao botao "Sair" (chamar rota de logout)

### Fase 4: Gerenciamento de Sessao e JWT

#### 4.4.1 Armazenar JWT na Sessao

Apos login bem-sucedido no `LoginController`:
```php
$session->set('jwt_token', $response['token']);
$session->set('user', $response['user']);
$session->set('perfilId', $response['perfilId']);
$session->set('tipoPerfil', $response['tipoPerfil']);
```

#### 4.4.2 Criar Autenticador Symfony Customizado

**Arquivo novo**: `petCareFrontEnd/src/Security/BackendApiAuthenticator.php`

- Implementar `AuthenticatorInterface` ou usar approach mais simples
- Verificar se JWT existe na sessao
- Se JWT expirado ou ausente, redirecionar para login
- **Approach simplificada**: Usar middleware/event listener que verifica a sessao antes de cada request em rotas protegidas

#### 4.4.3 Rota de Logout

**Arquivo**: `petCareFrontEnd/src/Controller/Login/LoginController.php`

Adicionar rota `/logout`:
- Limpar sessao (`$session->invalidate()`)
- Redirecionar para home

#### 4.4.4 Proteger Rotas

- Rotas publicas: `/home`, `/login/form`, `/login`, `/cadastro/form`, `/cadastro`, `/verificacao-email`
- Rotas protegidas: `/app` (dashboard)
- Adicionar check no `HomeController::getApp()`: se nao ha token na sessao, redirecionar para login

### Fase 5: Corrigir Validacao

#### 4.5.1 Corrigir UserServices

**Arquivo**: `petCareFrontEnd/src/Services/UserServices.php`

Corrigir logica invertida de `validateForm()`:
```php
// ANTES (errado):
if (empty($dto->getEmail()) || empty($dto->getPassword()) || $this->isStrongPassword($dto->getPassword())) {
    return false;
}

// DEPOIS (correto):
if (empty($dto->getEmail()) || empty($dto->getPassword()) || !$this->isStrongPassword($dto->getPassword())) {
    return false;
}
```

#### 4.5.2 Adicionar Validacao Especifica por Tipo de Perfil

No `CadastroUserController`:
- Validar CPF para clientes (formato e digitos)
- Validar CNPJ para clinicas (formato e digitos)
- Validar CRMV para veterinarios (nao vazio)
- Validar formato de data de nascimento
- Validar que senha e confirmacao sao iguais

### Fase 6: Tratamento de Erros

#### 4.6.1 Mapear Codigos de Erro do Backend

Traduzir os `ErrorCode` do backend para mensagens amigaveis em portugues:

| ErrorCode do Backend | Mensagem para o Usuario |
|---------------------|------------------------|
| `CREDENCIAIS_INVALIDAS` | "Email ou senha incorretos" |
| `EMAIL_NAO_VERIFICADO` | "Email ainda nao verificado. Verifique sua caixa de entrada." |
| `SENHAS_NAO_CONFEREM` | "As senhas nao conferem" |
| `RECURSO_DUPLICADO` | "Este email ja esta cadastrado" |
| `VALIDACAO_INVALIDA` | "Dados invalidos. Verifique os campos." |
| `PARAMETRO_FALTANDO` | "Campo obrigatorio nao preenchido" |
| `RECURSO_NAO_ENCONTRADO` | "Recurso nao encontrado" |

#### 4.6.2 Exibir Erros de Validacao do Backend

Quando o backend retorna `fieldErrors`, exibir cada erro junto ao campo correspondente no formulario.

---

## 5. Ordem de Execucao

1. **Fase 1** - Config + Servicos Base (BackendApiService, DTOs, config URL)
2. **Fase 2** - Corrigir Controllers (LoginController, CadastroUserController)
3. **Fase 3** - Corrigir Templates (login, registro, verificacao, dashboard)
4. **Fase 4** - Sessao + JWT + Logout + Protecao de rotas
5. **Fase 5** - Corrigir validacao (UserServices + validacoes por tipo)
6. **Fase 6** - Tratamento de erros e mensagens amigaveis

---

## 6. Decisoes Pendentes

1. **Pet obrigatorio no registro de cliente?** O backend exige `pet` em `ClienteCreateRequest`. Opcoes:
   - **A)** Adicionar campos de pet no formulario de registro (recomendado)
   - **B)** Alterar o backend para tornar `pet` opcional

2. **Campos de endereco/telefone no registro?** O backend nao os aceita no endpoint de registro. Opcoes:
   - **A)** Remover do formulario de registro, deixar para preencher depois
   - **B)** Manter no formulario e enviar em chamadas separadas apos registro

3. **Como lidar com verificacao de email?** O backend exige verificacao antes do login. Opcoes:
   - **A)** Seguir o fluxo completo: registro -> email de verificacao -> verificacao -> login
   - **B)** Desabilitar verificacao no backend para desenvolvimento (alterar `UsuarioService`)

4. **URL do backend em producao?** Configurar via variavel de ambiente `.env`:
   ```
   BACKEND_API_URL=http://localhost:8080
   ```

---

## 7. Arquivos a Criar

| Arquivo | Descricao |
|---------|-----------|
| `petCareFrontEnd/src/Services/BackendApiService.php` | Servico de comunicacao com a API |
| `petCareFrontEnd/src/Dto/Login/LoginRequestDto.php` | DTO para request de login |
| `petCareFrontEnd/src/Dto/Cadastro/ClienteRegistroDto.php` | DTO para registro de cliente |
| `petCareFrontEnd/src/Dto/Cadastro/VeterinarioRegistroDto.php` | DTO para registro de veterinario |
| `petCareFrontEnd/src/Dto/Cadastro/ClinicaRegistroDto.php` | DTO para registro de clinica |
| `petCareFrontEnd/templates/cadastroUser/verificacao_email.html.twig` | Pagina pos-registro |

## 8. Arquivos a Modificar

| Arquivo | Alteracoes |
|---------|-----------|
| `petCareFrontEnd/src/Controller/Login/LoginController.php` | Refatorar login, adicionar logout, corrigir DTO |
| `petCareFrontEnd/src/Controller/Cadastro/CadastroUserController.php` | Refatorar registro, chamar API, corrigir template |
| `petCareFrontEnd/src/Controller/Home/HomeController.php` | Proteger dashboard, verificar sessao |
| `petCareFrontEnd/src/Services/UserServices.php` | Corrigir logica de validacao invertida |
| `petCareFrontEnd/templates/login/index.html.twig` | Remover anchor no botao, adicionar flash messages |
| `petCareFrontEnd/templates/cadastroUser/index.html.twig` | Corrigir action, renomear campos, adicionar pet |
| `petCareFrontEnd/templates/home/telaPosLogin.html.twig` | Adicionar dados do usuario, botao sair funcional |
| `petCareFrontEnd/config/services.yaml` | Adicionar parametro de URL do backend |
| `petCareFrontEnd/.env` | Adicionar BACKEND_API_URL |
