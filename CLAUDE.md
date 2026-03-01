# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.3 REST API monolith using Java 21. Maven build system with JWT authentication, RBAC, and Flyway migrations.

**Stack:** Spring MVC, Spring Security (OAuth2 Resource Server + JWT), Spring Data JPA, Bean Validation, Lombok, Flyway
**Database:** PostgreSQL (dev + prod), H2 (tests only)
**Package:** `com.note_core` (underscore due to hyphen restriction in Java packages)

## Build & Development Commands

All commands use the Maven Wrapper (`./mvnw`) — no Maven installation required.

```bash
./mvnw compile                  # Compile
./mvnw spring-boot:run          # Run the application (needs PostgreSQL + dev profile)
./mvnw clean verify             # Clean build + all tests
./mvnw package                  # Build JAR
./mvnw package -DskipTests      # Build JAR without tests
```

**Dev profile requires PostgreSQL:** `docker run -e POSTGRES_DB=note_core_dev -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:17`

**DataSeeder** creates two default users on startup:
- Admin: `admin@rcsl.com` / `admin123` (SUPER_ADMIN)
- User: `user@rcsl.com` / `user1234` (USER)

## Testing

JUnit 5 with Spring Boot test starters (`@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`).
Tests use H2 in-memory database (configured in `src/test/resources/application.yaml`).

```bash
./mvnw test                                                # All tests
./mvnw test -Dtest=ClassName                               # Single test class
./mvnw test -Dtest=ClassName#methodName                    # Single test method
```

## Architecture

Entry point: `src/main/java/com/note_core/NoteCoreApplication.java`
Configuration: `src/main/resources/application.yaml` (+ `-dev.yaml`, `-prod.yaml`)

### Package Structure

```
com.note_core/
  config/           → SecurityConfig, JwtConfig, JpaAuditingConfig, RouteConfig, DataSeeder
  security/         → JwtTokenProvider, UserPrincipal, CustomUserDetailsService, ImpersonationService, PreAuth
  common/entity/    → BaseEntity (UUID id, createdAt, updatedAt with JPA Auditing)
  common/exception/ → GlobalExceptionHandler, ResourceNotFoundException, BusinessException, ErrorResponse
  auth/             → AuthController, AuthService + DTOs (LoginRequest, RegisterRequest, RefreshTokenRequest, TokenResponse, ImpersonateRequest)
  user/             → User, Role, Permission entities + repos + UserService, UserController + DTOs
  admin/            → AdminController, AdminService + DTOs (role/permission/plan management, impersonation)
  plan/             → Plan entity + PlanRepository, PlanService, PlanController + DTOs
  project/          → Project, Page entities + repos + ProjectService, PageService, ProjectController, PageController + DTOs
```

### Key Patterns

- **DTOs:** Java Records
- **IDs:** UUID for domain entities (User, Project, Page); Long for configuration entities (Role, Permission, Plan)
- **Timestamps:** `Instant` with JPA Auditing (`@CreatedDate`, `@LastModifiedDate`)
- **Auth:** JWT via `spring-security-oauth2-resource-server` (HMAC-SHA256). Access + refresh tokens. Roles extracted from `"roles"` JWT claim.
- **RBAC:** Roles (USER, ADMIN, SUPER_ADMIN) with Permissions. Enforced via `@PreAuthorize` using constants from `PreAuth.java`.
- **Permissions:** USER_READ, USER_WRITE, ADMIN_IMPERSONATE, PLAN_READ, PLAN_WRITE, PLAN_ASSIGN
- **Impersonation:** JWT claim `impersonator_id` — stateless.
- **Public routes:** Managed via `RouteConfig.PUBLIC_ROUTES` array, consumed by SecurityConfig.
- **Built-in protection:** Built-in roles, permissions, and plans cannot be edited or deleted.
- **Ownership scoping:** Projects and Pages are user-scoped — users can only access their own resources (returns 404 for others).
- **Plan limits:** Users have plan-based limits on projects and pages. Without a plan: 10 projects, 50 pages/project. With a plan: uses `maxProjects` and `maxPagesPerProject`. Enforced at creation time.
- **Migrations:** Flyway under `db/migration/2026/02/V{yyyyMMddHHmm}__name.sql`

### API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/login` | Public | Login → access + refresh tokens |
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/refresh` | Public | Refresh access token |
| GET | `/api/users/me` | Authenticated | Current user profile |
| PUT | `/api/users/me` | Authenticated | Update current user |
| GET | `/api/users` | USER_READ | List users (paginated) |
| GET | `/api/users/{id}` | USER_READ | Get user by ID |
| POST | `/api/admin/impersonate` | ADMIN_IMPERSONATE | Impersonate a user |
| GET/POST | `/api/admin/roles` | IS_ADMIN | List / create roles |
| GET/PUT/DELETE | `/api/admin/roles/{id}` | IS_ADMIN | Get / update / delete role |
| PUT | `/api/admin/roles/{id}/permissions` | IS_ADMIN | Set role permissions (replaces all) |
| GET/POST | `/api/admin/permissions` | IS_ADMIN | List / create permissions |
| PUT/DELETE | `/api/admin/permissions/{id}` | IS_ADMIN | Update / delete permission |
| PUT | `/api/admin/users/{id}/roles` | IS_ADMIN | Set user roles (replaces all) |
| PUT | `/api/admin/users/{id}/plan` | PLAN_ASSIGN | Assign plan to user |
| GET/POST | `/api/admin/plans` | PLAN_READ/PLAN_WRITE | List / create plans |
| GET/PUT/DELETE | `/api/admin/plans/{id}` | PLAN_READ/PLAN_WRITE | Get / update / delete plan |
| GET/POST | `/api/projects` | Authenticated | List (paginated) / create projects |
| GET/PUT/DELETE | `/api/projects/{id}` | Authenticated | Get / update / delete project |
| GET/POST | `/api/projects/{projectId}/pages` | Authenticated | List / create pages |
| GET/PUT/DELETE | `/api/projects/{projectId}/pages/{id}` | Authenticated | Get / update / delete page |

Lombok is enabled with annotation processing (configured in `.idea/compiler.xml`). The `-parameters` javac flag is set for Spring's parameter name discovery.