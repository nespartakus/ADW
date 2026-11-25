# Fase 2 — Asistente LLM funcional

Objetivo: integrar LangChain4j con configuración segura del proveedor, conversación contextual persistida y salida estructurada validada (contrato de la sección 7 del spec).
Branch: feat/fase-0-1-foundation (se continúa sobre la misma rama)
Estrategia de entrega: ask-on-risk
Test runner: backend Maven (JUnit + Testcontainers); frontend Vitest

## Specs

- S1: Configuración segura del proveedor LLM (proveedor, modelo, API key cifrada en reposo, baseUrl).
- S2: API de configuración: GET/PUT /settings/llm (sin secretos), POST /settings/llm/test, DELETE /settings/llm/key.
- S3: Abstracción de proveedor (ChatModelProvider); dominio sin dependencia de LangChain4j.
- S4: Persistencia de mensajes de diagnóstico (rol, contenido, payload estructurado).
- S5: Orquestador inicial que produce análisis estructurado validado (contrato sección 7).
- S6: Endpoint de conversación por sesión (POST /diagnostics/{id}/messages + analyze).
- S7: Errores comprensibles de cuota/timeout/API key inválida.

## Tasks

- T1 — Migración V3 (llm_configuration + diagnostic_message) + entidades — S1,S4
- T2 — SecretEncryptor AES-GCM + abstracción LlmClient + adaptador LangChain4j (OpenAI) — S1,S3
- T3 — Settings API (GET/PUT/test/DELETE key) — S2
- T4 — Contrato DiagnosticAnalysis (records sección 7) + validación — S5
- T5 — AutomotiveOrchestrator (prompt + parse + validación + retry limitado) — S5
- T6 — Endpoints de mensajes + analyze (persistencia) — S4,S6
- T7 — Manejo de errores LLM — S7
- T8 — Frontend: página Configuración + chat básico en el diagnóstico — S2,S6

## Log

- L1 (2026-11-25): "Manito, síguele, continúa con el desarrollo" — se continúa Fase 2 sin especificar proveedor; se integra OpenAI por defecto (candidato #1 del spec, y OpenAI-compatible cubre Ollama vía baseUrl). API LangChain4j 1.24 confirmado: chat no-streaming para salida estructurada.
