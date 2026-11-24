# Fase 0 + 1 — Fundación e interfaz base

Objetivo: construir el monolito modular ADW (backend Spring Boot + frontend React SPA) con PostgreSQL/pgvector, migraciones Flyway, y una interfaz navegable con CRUD de vehículos y sesiones diagnósticas.
Branch: feat/fase-0-1-foundation
Estrategia de entrega: ask-on-risk
Test runner: backend Maven (JUnit + Testcontainers); frontend Vitest

## Specs

- S1: Backend Spring Boot 4.1 + Java 21 + Maven arranca localmente; health check operativo.
- S2: Frontend React/Vite/TypeScript SPA arranca localmente.
- S3: Docker Compose con PostgreSQL (pgvector) accesible y con volumen persistente.
- S4: Migraciones Flyway versionadas aplicadas al arranque.
- S5: Layout con sidebar (Dashboard, Diagnósticos, Vehículos, Biblioteca técnica, Dispositivos [Próximamente], Informes, Configuración) y topbar.
- S6: Dashboard con estados vacíos útiles, sin métricas ficticias.
- S7: CRUD de vehículos (alta, edición, listado, borrado suave).
- S8: Listado y creación de sesiones diagnósticas; reabrir una sesión.
- S9: Modelo de datos con validaciones (UUID, fechas UTC, NUMERIC para mediciones, unidades explícitas).
- S10: Datos demo claramente etiquetados.

## Tasks

- [x] T1 — Fundación raíz: .gitignore, README, .env.example, docker-compose, ADR, docs, AGENTS.md — S1,S3 — inline — 38e7d3e
- [x] T2 — Backend skeleton Spring Boot (pom, Application, config, health, actuator) — S1 — inline — 8b1f4b4
- [x] T3 — Frontend skeleton Vite/React/TS/Tailwind — S2 — inline — 8b1f4b4
- [x] T4 — Migración Flyway inicial (esquema vehicle + diagnostic_session) — S4,S9 — inline — d3e12c9
- [x] T5 — Layout (sidebar + topbar) + routing — S5 — inline — 8b1f4b4
- [x] T6 — Dashboard con estados vacíos — S6 — inline — 8b1f4b4
- [x] T7 — CRUD vehículos (backend REST + frontend) — S7 — inline
- [x] T8 — Sesiones diagnósticas (backend REST + frontend) — S8 — inline
- [x] T9 — Datos demo etiquetados + validación — S9,S10 — inline

## Log

- L1 (2026-11-24): "Mano, de lleno a la implementacion del spec, que necesitas para empesar?" — arranque de implementación del spec.
- L2 (2026-11-24): Usuario indicó repo destino https://github.com/nespartakus/ADW para seguimiento en git. Repo existe con README stub en main; gh autenticado como nespartakus. Git local inicializado y enlazado a origin; rama feat/fase-0-1-foundation.
- L3 (2026-11-24): Spring Boot 3.5 quedó EOL; se usó 4.1.3 (línea estable recomendada, la que sigue LangChain4j 1.24). Spring Boot 4 cambió: `spring-boot-starter-webmvc`, `spring-boot-flyway` (auto-config), Testcontainers 2.x (`org.testcontainers.postgresql.PostgreSQLContainer`, módulos `testcontainers-*`), Jackson 3 (`tools.jackson`). Frontend: Vite 8, React 19, Tailwind 4, TS 7 (sin `baseUrl`), oxlint.
- L4 (2026-11-24): Fase 0 + Fase 1 completas y verificadas. Backend compila y los 2 tests de integración (CRUD vehículo + sesión + soft delete) pasan con Testcontainers. Frontend compila (`npm run build`). Falta: smoke test en vivo con docker-compose y push de la rama.
