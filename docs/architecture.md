# Arquitectura lógica

ADW es un **monolito modular**. No se usan microservicios en esta etapa.

```text
┌──────────────────────────────────────────────────────────────┐
│                         React SPA                            │
│ Dashboard | Vehicles | Diagnostic Workspace | Library | Admin│
└──────────────────────────────┬───────────────────────────────┘
                               │ HTTPS / REST / SSE
┌──────────────────────────────▼───────────────────────────────┐
│                     Spring Boot API                          │
│ Auth | Vehicle | Diagnostic | Evidence | Documents | Reports │
├──────────────────────────────────────────────────────────────┤
│                    Application Layer                         │
│ Use cases | DTOs | Validation | Authorization | Events       │
├──────────────────────────────────────────────────────────────┤
│                     Domain Layer                             │
│ Vehicle | DiagnosticSession | DTC | Measurement | TestPlan   │
│ Hypothesis | Evidence | TechnicalSource | DiagnosticReport   │
├──────────────────────────────────────────────────────────────┤
│                    AI / Intelligence Layer                   │
│ Agent Orchestrator | Specialist Services | RAG | Guardrails  │
│ Provider Adapter | Prompt Registry | Tool Registry           │
├──────────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                      │
│ PostgreSQL/pgvector | File Storage | LLM APIs | MCP Clients  │
│ OBD Adapter (future) | CAN Adapter (future) | Audit Log      │
└──────────────────────────────────────────────────────────────┘
```

## Límites arquitectónicos

- Los controladores REST no invocan directamente al LLM.
- Los agentes no acceden directamente a repositorios JPA.
- Las herramientas de IA llaman servicios de aplicación controlados.
- El dominio no depende de LangChain4j, Spring AI ni SDKs de proveedores.
- La selección de proveedor/modelo está detrás de una abstracción.
- Las respuestas del LLM se validan contra esquemas antes de persistirse.
- Las fuentes documentales conservan metadatos y referencias de página.

## Estructura del backend

```
backend/src/main/java/com/adw/
├── AdwApplication.java
├── shared/           # utilidades transversales
├── auth/             # autenticación y autorización
├── vehicle/          # dominio de vehículos
├── diagnostic/       # sesiones, mensajes, hipótesis, pruebas
├── evidence/         # DTC, mediciones, adjuntos
├── knowledge/        # biblioteca técnica y RAG
├── report/           # informes
├── ai/               # orquestación, agentes, herramientas, proveedores, RAG, prompts, guardrails
└── infrastructure/   # persistencia, almacenamiento, auditoría, configuración
```
