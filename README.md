# Automotive Diagnostic Workbench (ADW)

Entorno de trabajo para diagnóstico automotriz asistido por IA. ADW ayuda a
técnicos y profesionales automotrices a investigar fallas, organizar evidencia,
consultar documentación técnica y elaborar procedimientos de diagnóstico
verificables.

**Principio central:** la IA propone; el técnico verifica.

> Estado actual: Fase 0 + Fase 1 (fundación e interfaz base) en desarrollo.

## Stack

- **Backend:** Java 21, Spring Boot 3, Maven, LangChain4j, Spring Data JPA, PostgreSQL + pgvector, Flyway
- **Frontend:** React, TypeScript (strict), Vite, Tailwind CSS, shadcn/ui, React Router, TanStack Query
- **Infraestructura local:** Docker Compose

## Requisitos

- JDK 21
- Maven 3.9+
- Node.js 20+ y npm
- Docker + Docker Compose

## Arranque rápido

```bash
# 1. Levantar PostgreSQL
docker compose up -d

# 2. Backend
cd backend
mvn spring-boot:run

# 3. Frontend
cd frontend
npm install
npm run dev
```

Backend: http://localhost:8080 · Frontend: http://localhost:5173 · Health check: http://localhost:8080/actuator/health

## Documentación

- [`docs/architecture.md`](docs/architecture.md) — arquitectura lógica del monolito modular
- [`docs/decisions/`](docs/decisions/) — ADRs (decisiones técnicas)
- [`docs/safety-policy.md`](docs/safety-policy.md) — política de seguridad funcional
- [`automotive-diagnostic-workbench-spec.md`](automotive-diagnostic-workbench-spec.md) — especificación funcional y técnica completa

## Seguridad

El MVP es de **análisis y consulta**, no de control del vehículo. Ninguna
operación de escritura hacia vehículos está habilitada. Las API keys se
configuran del lado servidor y nunca se envían al navegador.
