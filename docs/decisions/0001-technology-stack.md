# ADR-0001: Stack tecnológico

**Estado:** Aceptado
**Fecha:** 2026-11-24

## Contexto

El producto es un monolito modular para diagnóstico automotriz asistido por IA,
con un backend que posee la API y la autenticación, y una SPA desplegable como
archivos estáticos.

## Decisiones

- **Java 21 + Spring Boot 4.1 + Maven** — el spec mencionaba "3.x o línea estable
  compatible"; Spring Boot 3.5 quedó EOL (junio 2026), así que se usa la línea
  estable recomendada en producción (4.1.3), que es la que sigue LangChain4j
  1.24.0. Spring Framework 7 requiere JDK 17+; Java 21 es compatible.
- **LangChain4j** como framework de integración LLM (no Google ADK), por su
  integración directa con Spring Boot y soporte de proveedores, memoria, RAG,
  herramientas y MCP.
- **PostgreSQL + pgvector** para persistencia relacional y búsqueda vectorial.
- **Flyway** para migraciones versionadas.
- **React + TypeScript (strict) + Vite + Tailwind + shadcn/ui** en el frontend.
  Se descarta Next.js para que Spring Boot sea el dueño de la API.
- **Testcontainers** para pruebas de integración.
- **Arquitectura:** monolito modular con capas separadas (dominio, aplicación,
  inteligencia, infraestructura). El dominio no depende de LangChain4j ni de
  SDKs de proveedores.

## Consecuencias

- Evolución futura hacia microservicios es posible sin reescribir el producto.
- El desacoplamiento del dominio permite evaluar otro framework LLM sin rehacer
  el sistema.
