# Fase 0 + 1 — Fundación e interfaz base

Objetivo: construir el monolito modular ADW (backend Spring Boot + frontend React SPA) con PostgreSQL/pgvector, migraciones Flyway, y una interfaz navegable con CRUD de vehículos y sesiones diagnósticas.
Branch: feat/fase-0-1-foundation
Estrategia de entrega: ask-on-risk
Test runner: backend Maven (JUnit + Testcontainers); frontend Vitest

## Specs

- S1: Backend Spring Boot 3 + Java 21 + Maven arranca localmente; health check operativo.
- S2: Frontend React/Vite/TypeScript SPA arranca localmente.
- S3: Docker Compose con PostgreSQL (pgvector) accesible y con volumen persistente.
- S4: Migraciones Flyway versionadas aplicadas al arranque.
- S5: Layout con sidebar (Dashboard, Diagnósticos, Vehículos, Biblioteca técnica, Dispositivos [Próximamente], Informes, Configuración) y topbar.
- S6: Dashboard con estados vacíos útiles, sin métricas ficticias.
- S7: CRUD de vehículos (alta, edición, listado con búsqueda/filtros, borrado suave).
- S8: Listado y creación de sesiones diagnósticas; reabrir una sesión.
- S9: Modelo de datos con validaciones (UUID, fechas UTC, NUMERIC para mediciones, unidades explícitas).
- S10: Datos demo claramente etiquetados.

## Tasks

- T1 — Fundación raíz: .gitignore, README, .env.example, docker-compose, ADR, docs, AGENTS.md — S1,S3 — inline
- T2 — Backend skeleton Spring Boot (pom, Application, config, health, actuator) — S1 — inline
- T3 — Frontend skeleton Vite/React/TS/Tailwind — S2 — inline
- T4 — Migración Flyway inicial (esquema vehicle + diagnostic_session) — S4,S9 — inline
- T5 — Layout (sidebar + topbar) + routing — S5 — inline
- T6 — Dashboard con estados vacíos — S6 — inline
- T7 — CRUD vehículos (backend REST + frontend) — S7 — inline
- T8 — Sesiones diagnósticas (backend REST + frontend) — S8 — inline
- T9 — Datos demo etiquetados + validación — S9,S10 — inline

## Log

- L1 (2026-11-24): "Mano, de lleno a la implementacion del spec, que necesitas para empesar?" — arranque de implementación del spec.
- L2 (2026-11-24): Usuario indicó repo destino https://github.com/nespartakus/ADW para seguimiento en git. Repo existe con README stub en main; gh autenticado como nespartakus. Git local inicializado y enlazado a origin; rama feat/fase-0-1-foundation.
