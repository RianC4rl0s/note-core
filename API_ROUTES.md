# API Routes — Note Core

Base URL: `http://localhost:8080`

Todas as rotas autenticadas requerem: `Authorization: Bearer <access_token>`

---

## Auth (Public)

### Login
```
POST /api/auth/login
Content-Type: application/json
Body:
{
  "email": "string",       // required
  "password": "string"     // required
}
Response 200:
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

### Registrar
```
POST /api/auth/register
Content-Type: application/json
Body:
{
  "name": "string",        // required
  "email": "string",       // required, email válido
  "password": "string",    // required, min 8 chars
  "phone": "string",       // optional
  "birthDate": "2000-01-15" // optional, formato ISO
}
Response 200:
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

### Refresh token
```
POST /api/auth/refresh
Content-Type: application/json
Body:
{
  "refreshToken": "string"  // required
}
Response 200:
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

---

## Usuário logado (Authenticated)

### Perfil atual
```
GET /api/users/me
Response 200: UserResponse
```

### Atualizar perfil
```
PUT /api/users/me
Content-Type: application/json
Body (todos opcionais, partial update):
{
  "name": "string",
  "phone": "string",
  "birthDate": "2000-01-15",
  "avatarUrl": "string"
}
Response 200: UserResponse
```

---

## Usuários (Requires USER_READ)

### Listar usuários (paginado)
```
GET /api/users?page=0&size=20&sort=createdAt,desc
Response 200: Page<UserResponse>
```

### Buscar usuário por ID
```
GET /api/users/{id}
Response 200: UserResponse
```

---

## Projetos (Authenticated — user-scoped)

Cada usuário só vê/edita/deleta seus próprios projetos.

### Listar projetos (paginado)
```
GET /api/projects?page=0&size=20&sort=createdAt,desc
Response 200: Page<ProjectResponse>
```

### Buscar projeto
```
GET /api/projects/{id}
Response 200: ProjectResponse
Response 404: { "status": 404, "message": "Project not found" }
```

### Criar projeto
```
POST /api/projects
Content-Type: application/json
Body:
{
  "name": "string",         // required
  "description": "string"   // optional
}
Response 201: ProjectResponse
Response 400: { "status": 400, "message": "Project limit reached. Maximum allowed: N" }
```

### Atualizar projeto
```
PUT /api/projects/{id}
Content-Type: application/json
Body (todos opcionais):
{
  "name": "string",
  "description": "string"
}
Response 200: ProjectResponse
```

### Deletar projeto (cascade nas páginas)
```
DELETE /api/projects/{id}
Response 204: No Content
```

---

## Páginas (Authenticated — dentro de projetos)

### Listar páginas de um projeto
```
GET /api/projects/{projectId}/pages
Response 200: PageResponse[] (ordenado por position ASC)
```

### Buscar página
```
GET /api/projects/{projectId}/pages/{id}
Response 200: PageResponse
```

### Criar página
```
POST /api/projects/{projectId}/pages
Content-Type: application/json
Body:
{
  "title": "string",        // required
  "pageData": "string",     // optional, max 51200 chars
  "position": 0             // optional, default = próxima posição
}
Response 201: PageResponse
Response 400: { "status": 400, "message": "Page limit reached. Maximum allowed per project: N" }
```

### Atualizar página
```
PUT /api/projects/{projectId}/pages/{id}
Content-Type: application/json
Body (todos opcionais):
{
  "title": "string",
  "pageData": "string",
  "position": 0
}
Response 200: PageResponse
```

### Deletar página
```
DELETE /api/projects/{projectId}/pages/{id}
Response 204: No Content
```

---

## Admin — Gerenciamento de Usuários (Requires USER_WRITE)

### Criar usuário
```
POST /api/admin/users
Content-Type: application/json
Body:
{
  "name": "string",         // required
  "email": "string",        // required, email válido
  "password": "string",     // required, min 8 chars
  "phone": "string",        // optional
  "birthDate": "2000-01-15", // optional
  "roleIds": [1, 2]         // optional, default = [USER]
}
Response 201: UserResponse
Response 400: "Email already in use"
```

### Ativar/Desativar usuário (toggle)
```
PATCH /api/admin/users/{id}/active
Response 200: UserResponse (campo "active" alternado)
Response 400: "Cannot deactivate admin users"
```

### Deletar usuário
```
DELETE /api/admin/users/{id}
Response 204: No Content
Response 400: "Cannot delete admin users"
```

---

## Admin — Roles (Requires IS_ADMIN)

### Listar roles
```
GET /api/admin/roles
Response 200: RoleResponse[]
```

### Criar role
```
POST /api/admin/roles
Content-Type: application/json
Body:
{
  "name": "string",         // required, unique
  "description": "string"   // optional
}
Response 201: RoleResponse
```

### Buscar role
```
GET /api/admin/roles/{id}
Response 200: RoleResponse
```

### Atualizar role
```
PUT /api/admin/roles/{id}
Content-Type: application/json
Body (todos opcionais):
{
  "name": "string",
  "description": "string"
}
Response 200: RoleResponse
Response 400: "Built-in roles cannot be edited"
```

### Deletar role
```
DELETE /api/admin/roles/{id}
Response 204: No Content
Response 400: "Built-in roles cannot be deleted"
```

### Atribuir permissions a role (substitui todas)
```
PUT /api/admin/roles/{id}/permissions
Content-Type: application/json
Body:
{
  "permissionIds": [1, 2]   // required
}
Response 200: RoleResponse
```

---

## Admin — Permissions (Requires IS_ADMIN)

### Listar permissions
```
GET /api/admin/permissions
Response 200: PermissionResponse[]
```

### Criar permission
```
POST /api/admin/permissions
Content-Type: application/json
Body:
{
  "name": "string",         // required, unique
  "description": "string"   // optional
}
Response 201: PermissionResponse
```

### Atualizar permission
```
PUT /api/admin/permissions/{id}
Content-Type: application/json
Body:
{
  "name": "string",
  "description": "string"
}
Response 200: PermissionResponse
Response 400: "Built-in permissions cannot be edited"
```

### Deletar permission
```
DELETE /api/admin/permissions/{id}
Response 204: No Content
Response 400: "Built-in permissions cannot be deleted"
```

---

## Admin — Atribuições (Requires IS_ADMIN / PLAN_ASSIGN)

### Atribuir roles a usuário (substitui todas)
```
PUT /api/admin/users/{id}/roles
Content-Type: application/json
Body:
{
  "roleIds": [1, 2]         // required
}
Response 200: UserResponse
```

### Atribuir plano a usuário
```
PUT /api/admin/users/{id}/plan
Auth: PLAN_ASSIGN permission
Content-Type: application/json
Body:
{
  "planId": 1               // required
}
Response 200: UserResponse
```

---

## Admin — Planos (Requires PLAN_READ / PLAN_WRITE)

### Listar planos
```
GET /api/admin/plans
Response 200: PlanResponse[]
```

### Buscar plano
```
GET /api/admin/plans/{id}
Response 200: PlanResponse
```

### Criar plano
```
POST /api/admin/plans
Content-Type: application/json
Body:
{
  "name": "string",              // required, unique
  "description": "string",      // optional
  "maxProjects": 10,            // required, min 1
  "maxPagesPerProject": 50      // required, min 1
}
Response 201: PlanResponse
```

### Atualizar plano
```
PUT /api/admin/plans/{id}
Content-Type: application/json
Body (todos opcionais):
{
  "name": "string",
  "description": "string",
  "maxProjects": 100,
  "maxPagesPerProject": 500
}
Response 200: PlanResponse
Response 400: "Built-in plans cannot be edited"
```

### Deletar plano
```
DELETE /api/admin/plans/{id}
Response 204: No Content
Response 400: "Built-in plans cannot be deleted"
```

---

## Admin — Impersonação (Requires ADMIN_IMPERSONATE)

### Impersonar usuário
```
POST /api/admin/impersonate
Content-Type: application/json
Body:
{
  "targetUserId": "uuid"    // required
}
Response 200:
{
  "accessToken": "string",
  "refreshToken": "string"
}
Response 400: "Cannot impersonate admin users"
```

---

## Response Schemas

### UserResponse
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "phone": "string | null",
  "birthDate": "2000-01-15 | null",
  "avatarUrl": "string | null",
  "enabled": true,
  "active": true,
  "roles": ["USER"],
  "planName": "FREE | null",
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### ProjectResponse
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string | null",
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### PageResponse
```json
{
  "id": "uuid",
  "projectId": "uuid",
  "title": "string",
  "pageData": "string | null",
  "position": 0,
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### PlanResponse
```json
{
  "id": 1,
  "name": "FREE",
  "description": "string | null",
  "maxProjects": 3,
  "maxPagesPerProject": 5,
  "builtIn": true,
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### RoleResponse
```json
{
  "id": 1,
  "name": "USER",
  "description": "string | null",
  "builtIn": true,
  "permissions": ["USER_READ", "USER_WRITE"],
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### PermissionResponse
```json
{
  "id": 1,
  "name": "USER_READ",
  "description": "string | null",
  "builtIn": true,
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### TokenResponse
```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

### Erro padrão
```json
{
  "status": 400,
  "message": "Mensagem descritiva do erro"
}
```

| Status | Situação |
|--------|----------|
| 400 | Validação, limite atingido, regra de negócio |
| 401 | Token ausente ou inválido |
| 403 | Sem permissão |
| 404 | Recurso não encontrado (ou não pertence ao usuário) |

---

## Planos seeded

| Plano | Max Projects | Max Pages/Project | Built-in |
|-------|-------------|-------------------|----------|
| FREE | 3 | 5 | Sim |
| BASIC | 5 | 10 | Sim |
| PRO | 5 | 20 | Sim |

## Usuários seeded

| Email | Senha | Role |
|-------|-------|------|
| admin@note.com | admin123 | SUPER_ADMIN |
| user@note.com | user1234 | USER (plano FREE) |
