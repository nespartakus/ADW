# ADW — Agent guidance

Automotive Diagnostic Workbench: modular monolith (Java 21 / Spring Boot 3 backend,
React/Vite/TypeScript SPA frontend). Product brief: `automotive-diagnostic-workbench-spec.md`.

## Non-negotiables

- Read-only toward vehicles in MVP. No actuator/write operations.
- No fabricated technical values (torque, pressure, pinouts, TSBs). Every
  technical datum needs a source, applicable vehicle/variant, and unit.
- Never log or commit secrets, API keys, or credentials.
- Domain must not depend on LangChain4j or provider SDKs.
- LLM provider/model selection sits behind an abstraction.
- Validate LLM structured output against schemas before persisting.

## Language

- UI copy: Spanish (México), i18n-ready.
- Code, identifiers, comments: English.
- Commit messages: Conventional Commits (English subject).

## Layout

- Backend: `backend/` (package root `com.adw`).
- Frontend: `frontend/`.
- Docs: `docs/`; ADRs in `docs/decisions/`.
- Local runtime data: `local-data/` (gitignored except `.gitkeep`).
