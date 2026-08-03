# Task Management API

API RESTful para gerenciamento de tarefas, desenvolvida com Spring Boot e Hibernate, com autenticação via JWT.

## Stack

- Java 21
- Spring Boot (Web, Security, Data JPA, Validation)
- Hibernate
- MySQL
- JWT (`com.auth0:java-jwt`)
- Lombok

## Como executar

1. Crie o banco de dados executando o script em [`sql/schema.sql`](sql/schema.sql), ou deixe o Hibernate criar/atualizar o schema automaticamente (`spring.jpa.hibernate.ddl-auto=update`, já configurado em `application.properties`).
2. Ajuste as credenciais do banco em `src/main/resources/application.properties` se necessário (`spring.datasource.username`/`password`).
3. Rode a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

A API sobe em `http://localhost:8080`.

## Autenticação

Todos os endpoints, exceto `/auth/register` e `/auth/login`, exigem um token JWT no header:

```
Authorization: Bearer <token>
```

### `POST /auth/register`

Cria um novo usuário e já retorna um token JWT.

**Request**
```json
{
  "username": "Fernanda",
  "email": "fernanda@example.com",
  "password": "123456"
}
```

**Response `201 Created`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Erros**
- `400` — campos obrigatórios ausentes/inválidos (username, email inválido, senha com menos de 6 caracteres).
- `409` — e-mail já cadastrado.

### `POST /auth/login`

**Request**
```json
{
  "email": "fernanda@example.com",
  "password": "123456"
}
```

**Response `200 OK`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Erros**
- `401` — credenciais inválidas.

### `GET /user`

Retorna os dados do usuário autenticado (a partir do token).

**Response `200 OK`**
```json
{
  "id": "b3f1...",
  "username": "Fernanda",
  "email": "fernanda@example.com"
}
```

## Tarefas

Todas as rotas abaixo exigem autenticação e operam apenas sobre as tarefas do usuário autenticado (`assignedTo`).

### `GET /tasks`

Lista as tarefas do usuário, com paginação e ordenação.

**Query params** (padrão do Spring Data)
- `page` (default `0`)
- `size` (default `20`)
- `sort` (default `deadline,asc`)

Exemplo: `GET /tasks?page=0&size=10&sort=deadline,desc`

**Response `200 OK`**
```json
{
  "content": [
    {
      "id": "a1c2...",
      "title": "Finalizar relatório",
      "description": "Revisar números do trimestre",
      "status": "PENDING",
      "createdOn": "2026-08-01",
      "deadline": "2026-08-10",
      "assignedTo": { "id": "b3f1...", "username": "Fernanda", "email": "fernanda@example.com" }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

### `GET /tasks/filter?status={status}`

Filtra as tarefas do usuário por status (`PENDING`, `IN_PROGRESS`, `COMPLETED`), com a mesma paginação/ordenação de `GET /tasks`.

Exemplo: `GET /tasks/filter?status=IN_PROGRESS&sort=deadline,asc`

### `GET /tasks/{id}`

**Response `200 OK`** — mesmo formato de item de `GET /tasks`.

**Erros**
- `404` — tarefa não existe ou não pertence ao usuário autenticado.

### `POST /tasks`

**Request**
```json
{
  "title": "Finalizar relatório",
  "description": "Revisar números do trimestre",
  "status": "PENDING",
  "deadline": "2026-08-10"
}
```

**Response `201 Created`** — tarefa criada, no mesmo formato de `GET /tasks/{id}`.

**Erros**
- `400` — título vazio, status inválido, ou `deadline` no passado.
- `409` — já existe uma tarefa com esse título para o usuário.

### `PUT /tasks/{id}`

Mesmo corpo de `POST /tasks`. Atualiza título, descrição, status e deadline da tarefa.

**Erros**
- `400` — validação de campos.
- `404` — tarefa não encontrada/não pertence ao usuário.
- `409` — título duplicado para outra tarefa do usuário.

### `DELETE /tasks/{id}`

**Response `204 No Content`**

**Erros**
- `404` — tarefa não encontrada/não pertence ao usuário.

## Formato de erros

Todas as respostas de erro seguem o mesmo formato, produzido pelo `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-08-03T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "fieldErrors": {
    "title": "must not be blank",
    "deadline": "must be a date in the present or in the future"
  }
}
```

Para erros sem validação de campo (404, 409, 401, 500), `fieldErrors` vem `null` e `message` traz uma descrição amigável do problema.

## Regras de negócio

- Título da tarefa não pode se repetir para o mesmo usuário (`TaskService`).
- `createdOn` é preenchido automaticamente na criação da tarefa (`Task#prePersist`).
- `deadline` não pode ser uma data no passado (`@FutureOrPresent`).
- Um usuário só pode visualizar, editar ou excluir as próprias tarefas (`TaskService`).
- Senhas são armazenadas com hash BCrypt (`SecurityConfig`).

## Estrutura do projeto

```
controller/  -> camada web (recebe request, delega ao service, monta response)
service/     -> regras de negócio (duplicidade, permissões, orquestração)
repository/  -> acesso a dados (Spring Data JPA)
entity/      -> entidades JPA (User, Task, TaskStatus)
dto/         -> objetos de request/response
exception/   -> exceções de domínio e tratamento global de erros
infra/       -> configuração de segurança (JWT) e CORS
```
