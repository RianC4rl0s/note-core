# API Integration Guide — Projects & Pages (Life Organizer)

Base URL: `http://localhost:8080` (dev)

Todas as rotas autenticadas requerem header: `Authorization: Bearer <access_token>`

---

## Planos (Admin only)

### Listar planos
```
GET /api/admin/plans
Auth: PLAN_READ permission
Response 200:
[
  {
    "id": 1,
    "name": "FREE",
    "description": "Free plan with basic limits",
    "maxProjects": 10,
    "maxPagesPerProject": 50,
    "builtIn": true,
    "createdAt": "2026-02-28T12:00:00Z",
    "updatedAt": "2026-02-28T12:00:00Z"
  }
]
```

### Buscar plano por ID
```
GET /api/admin/plans/{id}
Auth: PLAN_READ permission
Response 200:
{
  "id": 1,
  "name": "FREE",
  "description": "Free plan with basic limits",
  "maxProjects": 10,
  "maxPagesPerProject": 50,
  "builtIn": true,
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### Criar plano
```
POST /api/admin/plans
Auth: PLAN_WRITE permission
Content-Type: application/json
Body:
{
  "name": "PRO",                  // required, unique
  "description": "Pro plan",      // optional
  "maxProjects": 50,              // required, min 1
  "maxPagesPerProject": 200       // required, min 1
}
Response 201: (mesmo formato do GET)
```

### Atualizar plano
```
PUT /api/admin/plans/{id}
Auth: PLAN_WRITE permission
Content-Type: application/json
Body (todos campos opcionais, partial update):
{
  "name": "PRO_PLUS",
  "description": "Pro Plus plan",
  "maxProjects": 100,
  "maxPagesPerProject": 500
}
Response 200: (mesmo formato do GET)
Erro 400: "Built-in plans cannot be edited"
```

### Deletar plano
```
DELETE /api/admin/plans/{id}
Auth: PLAN_WRITE permission
Response 204: No Content
Erro 400: "Built-in plans cannot be deleted"
```

### Atribuir plano a usuário
```
PUT /api/admin/users/{userId}/plan
Auth: PLAN_ASSIGN permission
Content-Type: application/json
Body:
{
  "planId": 1    // required
}
Response 200: UserResponse (ver abaixo)
```

---

## UserResponse (atualizado)

O campo `planName` foi adicionado ao response do usuário. Afeta `/api/users/me`, `/api/users`, `/api/users/{id}`.

```json
{
  "id": "uuid",
  "name": "John",
  "email": "john@example.com",
  "phone": null,
  "birthDate": null,
  "avatarUrl": null,
  "enabled": true,
  "active": true,
  "roles": ["USER"],
  "planName": "FREE",       // novo campo — null se sem plano
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

---

## Projetos (Authenticated — qualquer usuário logado)

Cada usuário só vê/edita/deleta seus próprios projetos. Projetos de outros usuários retornam 404.

### Listar meus projetos (paginado)
```
GET /api/projects?page=0&size=20&sort=createdAt,desc
Auth: Bearer token (qualquer usuário autenticado)
Response 200:
{
  "content": [
    {
      "id": "uuid",
      "name": "Meu Projeto",
      "description": "Descrição",
      "createdAt": "2026-02-28T12:00:00Z",
      "updatedAt": "2026-02-28T12:00:00Z"
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

### Buscar projeto por ID
```
GET /api/projects/{id}
Auth: Bearer token
Response 200:
{
  "id": "uuid",
  "name": "Meu Projeto",
  "description": "Descrição",
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
Response 404: { "status": 404, "message": "Project not found" }
```

### Criar projeto
```
POST /api/projects
Auth: Bearer token
Content-Type: application/json
Body:
{
  "name": "Meu Projeto",       // required
  "description": "Descrição"   // optional
}
Response 201:
{
  "id": "uuid",
  "name": "Meu Projeto",
  "description": "Descrição",
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
Erro 400: { "status": 400, "message": "Project limit reached. Maximum allowed: 10" }
```

### Atualizar projeto
```
PUT /api/projects/{id}
Auth: Bearer token
Content-Type: application/json
Body (todos campos opcionais, partial update):
{
  "name": "Novo Nome",
  "description": "Nova descrição"
}
Response 200: (mesmo formato do GET)
```

### Deletar projeto
```
DELETE /api/projects/{id}
Auth: Bearer token
Response 204: No Content
Nota: deleta o projeto E todas as páginas dele (cascade)
```

---

## Páginas (Authenticated — qualquer usuário logado)

Páginas pertencem a um projeto. O usuário precisa ser dono do projeto para acessar suas páginas.

### Listar páginas de um projeto
```
GET /api/projects/{projectId}/pages
Auth: Bearer token
Response 200:
[
  {
    "id": "uuid",
    "projectId": "uuid",
    "title": "Minha Página",
    "pageData": "conteúdo em texto/JSON/markdown",
    "position": 0,
    "createdAt": "2026-02-28T12:00:00Z",
    "updatedAt": "2026-02-28T12:00:00Z"
  }
]
Nota: retorna ordenado por position ASC
```

### Buscar página por ID
```
GET /api/projects/{projectId}/pages/{id}
Auth: Bearer token
Response 200:
{
  "id": "uuid",
  "projectId": "uuid",
  "title": "Minha Página",
  "pageData": "conteúdo",
  "position": 0,
  "createdAt": "2026-02-28T12:00:00Z",
  "updatedAt": "2026-02-28T12:00:00Z"
}
```

### Criar página
```
POST /api/projects/{projectId}/pages
Auth: Bearer token
Content-Type: application/json
Body:
{
  "title": "Minha Página",                  // required
  "pageData": "conteúdo da página",          // optional, max 50KB (51200 chars)
  "position": 0                              // optional, default = próxima posição
}
Response 201: (mesmo formato do GET)
Erro 400: { "status": 400, "message": "Page limit reached. Maximum allowed per project: 50" }
```

### Atualizar página
```
PUT /api/projects/{projectId}/pages/{id}
Auth: Bearer token
Content-Type: application/json
Body (todos campos opcionais, partial update):
{
  "title": "Novo Título",
  "pageData": "novo conteúdo",
  "position": 1
}
Response 200: (mesmo formato do GET)
```

### Deletar página
```
DELETE /api/projects/{projectId}/pages/{id}
Auth: Bearer token
Response 204: No Content
```

---

## Erros Padrão

Todas as rotas seguem o mesmo formato de erro:

```json
{
  "status": 400,
  "message": "Mensagem descritiva do erro"
}
```

| Status | Situação |
|--------|----------|
| 400 | Validação falhou, limite atingido, regra de negócio violada |
| 401 | Token ausente ou inválido |
| 403 | Sem permissão para a ação |
| 404 | Recurso não encontrado (ou não pertence ao usuário) |

---

## Limites de Plano

- Sem plano atribuído: **10 projetos**, **50 páginas/projeto**
- Com plano: usa `maxProjects` e `maxPagesPerProject` do plano
- Limite verificado no momento da criação (POST)
- O campo `planName` no UserResponse indica o plano atual do usuário (null = sem plano)

---

## Novas Permissions

| Permission | Descrição |
|------------|-----------|
| `PLAN_READ` | Listar/ver planos |
| `PLAN_WRITE` | Criar/editar/deletar planos |
| `PLAN_ASSIGN` | Atribuir plano a usuário |

Essas permissions já vêm atribuídas aos roles ADMIN e SUPER_ADMIN.
