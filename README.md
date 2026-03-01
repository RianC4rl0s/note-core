# Note Core API

Spring Boot REST API with JWT authentication, RBAC (Role-Based Access Control), and admin management.

## Quick Start

### Prerequisites

- Java 21
- Docker (for PostgreSQL)

### 1. Start PostgreSQL

```bash
docker run -e POSTGRES_DB=note_core_dev -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:17
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. Flyway runs the migrations automatically and the `DataSeeder` creates two default users:

| User | Email | Password | Role |
|------|-------|----------|------|
| Admin | `admin@rcsl.com` | `admin123` | SUPER_ADMIN |
| User | `user@rcsl.com` | `user1234` | USER |

---

## Authentication

### Login as Admin

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@rcsl.com",
    "password": "admin123"
  }'
```

Response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Login as User

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@rcsl.com",
    "password": "user1234"
  }'
```

### Register a new user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "phone": "+5511999999999",
    "birthDate": "1990-01-15"
  }'
```

### Refresh token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<your-refresh-token>"
  }'
```

---

## Using authenticated endpoints

All endpoints below require the `Authorization` header:

```
Authorization: Bearer <your-access-token>
```

### Get current user profile

```bash
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <token>"
```

### Update current user profile

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "phone": "+5511888888888"
  }'
```

### List users (requires USER_READ permission)

```bash
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin-token>"
```

---

## Admin endpoints

All admin endpoints require the `ADMIN` role.

### Roles

```bash
# List all roles
curl http://localhost:8080/api/admin/roles \
  -H "Authorization: Bearer <admin-token>"

# Create a role
curl -X POST http://localhost:8080/api/admin/roles \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MODERATOR",
    "description": "Moderator role"
  }'

# Update a role (built-in roles cannot be edited)
curl -X PUT http://localhost:8080/api/admin/roles/4 \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MOD",
    "description": "Updated description"
  }'

# Delete a role (built-in roles cannot be deleted)
curl -X DELETE http://localhost:8080/api/admin/roles/4 \
  -H "Authorization: Bearer <admin-token>"

# Set permissions for a role (replaces all)
curl -X PUT http://localhost:8080/api/admin/roles/4/permissions \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionIds": [1, 2]
  }'
```

### Permissions

```bash
# List all permissions
curl http://localhost:8080/api/admin/permissions \
  -H "Authorization: Bearer <admin-token>"

# Create a permission
curl -X POST http://localhost:8080/api/admin/permissions \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "POST_WRITE",
    "description": "Create and edit posts"
  }'

# Update a permission (built-in permissions cannot be edited)
curl -X PUT http://localhost:8080/api/admin/permissions/4 \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "POST_MANAGE",
    "description": "Updated description"
  }'

# Delete a permission (built-in permissions cannot be deleted)
curl -X DELETE http://localhost:8080/api/admin/permissions/4 \
  -H "Authorization: Bearer <admin-token>"
```

### Assign roles to a user (replaces all roles)

```bash
curl -X PUT http://localhost:8080/api/admin/users/<user-uuid>/roles \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "roleIds": [1, 2]
  }'
```

### Impersonate a user (requires ADMIN_IMPERSONATE permission)

```bash
curl -X POST http://localhost:8080/api/admin/impersonate \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUserId": "<user-uuid>"
  }'
```

---

## Build & Test

```bash
./mvnw compile              # Compile
./mvnw test                 # Run tests (uses H2 in-memory)
./mvnw clean verify         # Clean build + all tests
./mvnw package -DskipTests  # Build JAR
```
