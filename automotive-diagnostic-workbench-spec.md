# Automotive Diagnostic Workbench

## Especificación funcional y técnica para un agente de codificación

**Tipo de documento:** instrucciones de implementación / product brief\
**Objetivo:** construir un producto web propio para diagnóstico
automotriz asistido por IA.\
**Nombre provisional:** Automotive Diagnostic Workbench (ADW).\
**Idioma inicial:** español (México), con arquitectura preparada para
internacionalización.\
**Estrategia:** MVP funcional, modular y extensible; no construir una
plataforma distribuida prematuramente.

------------------------------------------------------------------------

# 1. Instrucción principal para el agente de codificación

Actúa como arquitecto de software y desarrollador senior full-stack
especializado en Java, Spring Boot, React y sistemas basados en LLM.

Debes construir un producto web llamado **Automotive Diagnostic
Workbench**, no un plugin de OpenCode, no una extensión de un IDE y no
un chatbot genérico.

El producto debe ayudar a técnicos y profesionales automotrices a
investigar fallas, organizar evidencia, consultar documentación técnica
y elaborar procedimientos de diagnóstico verificables. La IA debe
comportarse como un asistente técnico que razona con evidencia, reconoce
incertidumbre y propone pruebas; nunca debe inventar especificaciones
OEM ni presentar una hipótesis como diagnóstico confirmado.

## Reglas de ejecución

1.  Antes de modificar archivos, inspecciona el repositorio, identifica
    lo que ya existe y conserva las convenciones útiles.
2.  Si el repositorio está vacío, crea el proyecto con la estructura
    propuesta en este documento.
3.  Implementa por fases. Al terminar cada fase, ejecuta compilación,
    pruebas y validaciones pertinentes.
4.  No dejes botones decorativos: toda acción visible debe funcionar o
    mostrarse explícitamente como "No disponible todavía".
5.  No simules integraciones reales. Si una conexión OBD, CAN o
    proveedor LLM no está configurada, informa el estado y ofrece un
    modo demo claramente identificado.
6.  No introduzcas credenciales, API keys, tokens ni datos sensibles en
    el código, logs, respuestas de API o almacenamiento del navegador.
7.  No habilites operaciones de escritura hacia vehículos en el MVP.
8.  No inventes valores de torque, presión, resistencia, voltaje,
    pinouts, boletines o procedimientos. Cada dato técnico específico
    debe tener fuente, vehículo/variante aplicable y unidad.
9.  Mantén separación entre dominio automotriz, orquestación de IA,
    infraestructura, API y presentación.
10. Prioriza claridad, trazabilidad, pruebas automatizadas,
    accesibilidad y mantenibilidad sobre abstracciones sofisticadas.

Si una decisión técnica no está especificada, elige la opción más simple
que permita evolucionar sin rehacer el sistema y documenta brevemente la
decisión.

------------------------------------------------------------------------

# 2. Visión del producto

ADW es un entorno de trabajo para diagnóstico automotriz. No es
únicamente una ventana de chat. Su interfaz debe permitir que el
usuario:

-   Registre vehículos y sus configuraciones.
-   Abra una sesión de diagnóstico asociada a un vehículo.
-   Describa síntomas, condiciones de aparición y reparaciones previas.
-   Capture DTC, Freeze Frame, PIDs y mediciones manuales.
-   Adjunte fotografías, documentos, diagramas, capturas y registros.
-   Solicite un análisis asistido por IA.
-   Consulte hipótesis ordenadas por probabilidad y evidencia.
-   Siga un plan de pruebas paso a paso.
-   Registre resultados de pruebas y descarte hipótesis.
-   Consulte fuentes y fragmentos documentales utilizados por la IA.
-   Genere y exporte un informe técnico.
-   Mantenga el historial de diagnósticos y reparaciones.

## Principio central

**La IA propone; el técnico verifica.**

Toda respuesta de diagnóstico debe distinguir claramente:

-   Hechos aportados por el usuario.
-   Datos leídos desde instrumentos.
-   Datos obtenidos de documentación.
-   Inferencias del modelo.
-   Hipótesis pendientes de comprobar.
-   Diagnóstico confirmado por una prueba o por el técnico.

------------------------------------------------------------------------

# 3. Decisión tecnológica

## Backend

-   Java 21 o versión LTS vigente compatible con las versiones elegidas.
-   Spring Boot 3.x o línea estable compatible con las dependencias de
    LangChain4j.
-   Maven.
-   LangChain4j como framework principal de integración con modelos,
    tool calling, memoria y RAG.
-   Spring Web para API REST.
-   Spring Security para autenticación y autorización.
-   Spring Data JPA / Hibernate.
-   PostgreSQL.
-   pgvector para búsqueda vectorial.
-   Flyway para migraciones.
-   Bean Validation.
-   WebSocket o Server-Sent Events (SSE) para respuestas progresivas del
    asistente.
-   Testcontainers para pruebas de integración.

**No combinar LangChain4j y Google ADK en el MVP.** LangChain4j es la
elección inicial porque encaja directamente con Spring Boot, abstrae
varios proveedores de modelos y ofrece herramientas, memoria, RAG e
integración MCP. Mantener el dominio desacoplado permitirá evaluar otro
framework en el futuro sin reescribir el producto.

Documentación: - https://docs.langchain4j.dev/intro/ -
https://docs.langchain4j.dev/tutorials/spring-boot-integration/ -
https://docs.langchain4j.dev/tutorials/mcp/

## Frontend

-   React.
-   TypeScript con strict mode.
-   Vite.
-   Tailwind CSS.
-   shadcn/ui o componentes accesibles equivalentes.
-   React Router.
-   TanStack Query para estado remoto.
-   React Hook Form + Zod para formularios.
-   Librería de gráficos ligera para tendencias de PIDs y mediciones.
-   Vitest + Testing Library.
-   Playwright para pruebas end-to-end.

No utilizar Next.js en el MVP: el backend Spring Boot será el dueño de
la API y la autenticación. El frontend será una SPA desplegable como
archivos estáticos.

## Infraestructura local

-   Docker Compose.
-   PostgreSQL con pgvector.
-   Backend Spring Boot.
-   Frontend React en desarrollo separado; en producción puede servirse
    detrás de un reverse proxy.
-   Volumen persistente para documentos.
-   Configuración por variables de entorno.

## Proveedores LLM

Diseñar una capa de configuración para permitir proveedores
intercambiables. Para la primera implementación, integrar al menos un
proveedor mediante LangChain4j y dejar una interfaz/adaptador para los
siguientes.

Proveedores candidatos: - OpenAI. - Anthropic. - Google Gemini. -
Modelos locales compatibles con una API OpenAI, por ejemplo Ollama.

La API key se configura del lado servidor. Nunca debe enviarse al
navegador ni incluirse en el bundle del frontend.

------------------------------------------------------------------------

# 4. Arquitectura lógica

Usar un **monolito modular**. No crear microservicios en esta etapa.

``` text
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
│                     Domain Layer                              │
│ Vehicle | DiagnosticSession | DTC | Measurement | TestPlan    │
│ Hypothesis | Evidence | TechnicalSource | DiagnosticReport    │
├──────────────────────────────────────────────────────────────┤
│                    AI / Intelligence Layer                    │
│ Agent Orchestrator | Specialist Services | RAG | Guardrails    │
│ Provider Adapter | Prompt Registry | Tool Registry            │
├──────────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                       │
│ PostgreSQL/pgvector | File Storage | LLM APIs | MCP Clients    │
│ OBD Adapter (future) | CAN Adapter (future) | Audit Log        │
└──────────────────────────────────────────────────────────────┘
```

## Límites arquitectónicos

-   Los controladores REST no deben invocar directamente al LLM.
-   Los agentes no deben acceder directamente a repositorios JPA.
-   Las herramientas de IA deben llamar servicios de aplicación
    controlados.
-   El dominio no debe depender de LangChain4j, Spring AI ni SDK de
    proveedores.
-   La selección de proveedor/modelo debe estar detrás de una
    abstracción.
-   Las respuestas del LLM deben validarse contra esquemas antes de
    persistirse o mostrarse como datos estructurados.
-   Las fuentes documentales deben conservar metadatos y referencias de
    página.

------------------------------------------------------------------------

# 5. Diseño de la interfaz

La interfaz debe sentirse como software profesional de
taller/ingeniería, no como una landing page de IA.

## Dirección visual

-   Tema oscuro por defecto, con opción clara.
-   Fondo grafito o gris muy oscuro.
-   Superficies ligeramente elevadas y bordes discretos.
-   Acento principal azul eléctrico o cian moderado.
-   Verde únicamente para estados verificados/normal.
-   Ámbar para advertencias y pruebas pendientes.
-   Rojo para fallas críticas o condiciones de riesgo.
-   Tipografía sans-serif legible; números y unidades con buena
    alineación.
-   Densidad de información media-alta, similar a una herramienta
    técnica.
-   Iconografía consistente y etiquetas textuales; no depender solo del
    color.
-   Diseño responsive: escritorio primero, tablet usable y móvil para
    consulta/captura rápida.

Evitar gradientes llamativos, exceso de tarjetas, animaciones
decorativas, emojis en controles y aspecto de plantilla genérica.

## Navegación principal

Barra lateral persistente:

1.  **Dashboard**
2.  **Diagnósticos**
3.  **Vehículos**
4.  **Biblioteca técnica**
5.  **Dispositivos** (marcado "Próximamente" hasta tener integración
    real)
6.  **Informes**
7.  **Configuración**

Barra superior: - Nombre del espacio/taller. - Estado del proveedor
LLM. - Estado de almacenamiento/documentación. - Usuario activo. -
Acceso a configuración.

## Pantalla A: Dashboard

Mostrar: - Diagnósticos recientes. - Vehículos registrados. - Sesiones
abiertas. - Informes generados. - Estado de conexión con el modelo. -
Accesos rápidos: "Nuevo diagnóstico", "Registrar vehículo", "Añadir
documentación". - Actividad reciente.

No mostrar métricas ficticias. Para una instalación nueva, usar estados
vacíos útiles con llamadas a la acción.

## Pantalla B: Vehículos

Listado con búsqueda y filtros.

Cada vehículo debe mostrar: - Marca, modelo, año. - Motor/código de
motor. - VIN, si el usuario decide registrarlo. - ECU/módulos
conocidos. - Transmisión. - Etiquetas (OEM, swap, proyecto, cliente,
flota). - Último diagnóstico.

Formulario de alta/edición: - VIN opcional. - País/mercado. - Marca,
modelo, año. - Generación/plataforma. - Motor, desplazamiento,
combustible, aspiración. - ECU/PCM y calibración, si se conocen. -
Transmisión. - Modificaciones relevantes. - Notas.

Permitir configuraciones modificadas o swaps. No asumir que la ECU
corresponde a la marca del chasis.

## Pantalla C: Diagnostic Workspace (pantalla principal)

Diseñar una vista de trabajo en tres zonas para escritorio:

**Columna izquierda: contexto** - Vehículo seleccionado. - Datos de
identificación y configuración. - Síntomas. - Condiciones:
frío/caliente, ralentí/carga, velocidad, temperatura ambiente. - DTC
activos, pendientes e históricos. - Freeze Frame y datos capturados. -
Mediciones y evidencias adjuntas.

**Zona central: investigación** - Conversación con el asistente. -
Streaming de respuesta. - Indicador de etapa: recopilando datos,
investigando fuentes, formulando hipótesis, generando pruebas. -
Mensajes diferenciados entre usuario, agente y resultado de
herramientas. - Referencias clicables a documentos y páginas. - Acciones
para aceptar, editar o descartar una hipótesis.

**Panel derecho: diagnóstico** - Hipótesis ordenadas. - Nivel de
confianza cualitativo (bajo/medio/alto), siempre acompañado de
explicación. - Evidencia a favor y en contra. - Prueba siguiente
recomendada. - Estado de cada prueba: pendiente, en curso, aprobada,
fallida, no concluyente. - Advertencias de seguridad.

En pantallas pequeñas, convertir las columnas laterales en paneles o
pestañas.

## Pantalla D: Biblioteca técnica

Funciones: - Cargar PDF, TXT, DOCX y CSV inicialmente. - Mostrar
progreso de ingestión. - Mostrar documento, fabricante, modelo, años,
motor, sistema y origen. - Buscar por texto y por similitud semántica. -
Consultar fragmentos recuperados con página/sección. - Eliminar o
reindexar documentos. - Distinguir documentos OEM, aftermarket, notas
propias y fuentes públicas.

No realizar scraping de sitios protegidos ni presentar contenido sin
procedencia. El usuario debe tener derecho a cargar y utilizar cada
documento.

## Pantalla E: Informes

Un informe debe contener: - Identificación del vehículo. - Motivo de
ingreso/síntoma. - Condiciones de reproducción. - DTC y datos
relevantes. - Pruebas ejecutadas y resultados. - Hipótesis
descartadas. - Causa confirmada o estado "sin confirmar". - Reparación
sugerida o realizada. - Verificación posterior. - Fuentes consultadas. -
Fecha, autor y notas.

Exportación inicial: PDF y JSON. El informe debe dejar claro qué fue
generado por IA y qué fue confirmado por el técnico.

## Pantalla F: Configuración

-   Proveedor LLM.
-   Modelo.
-   API key (entrada enmascarada; nunca devolver la clave existente).
-   Parámetros seguros: timeout y límite de salida.
-   Botón "Probar conexión".
-   Estado de conexión y error comprensible.
-   Preferencias de idioma y unidades.
-   Gestión de usuarios (si se activa multiusuario).
-   Configuración de almacenamiento.

Al guardar una API key, enviarla por HTTPS al backend, cifrarla en
reposo y mostrar solo un indicador de que existe. No volver a mostrar el
secreto.

------------------------------------------------------------------------

# 6. Modelo de agentes

No implementar una multitud de agentes autónomos que se llamen entre sí
sin control. Crear un orquestador explícito que use especialistas como
servicios/agentes acotados.

## 6.1 Automotive Orchestrator

Responsabilidades: - Entender la solicitud. - Verificar que existe un
vehículo/contexto suficiente. - Identificar el sistema probable. -
Decidir qué especialistas consultar. - Solicitar fuentes y herramientas
pertinentes. - Consolidar resultados. - Devolver hipótesis y plan de
pruebas estructurados. - Pedir aclaraciones cuando falten datos
críticos.

No debe inventar mediciones ni ejecutar acciones físicas.

## 6.2 Electrical Engineer

Dominio: - Circuitos eléctricos y electrónicos. - Alimentaciones,
tierras, referencias de 5 V. - Caídas de tensión y continuidad. -
Sensores y actuadores. - Relevadores, fusibles y controladores. -
Lectura de diagramas y pinouts. - Interpretación de oscilogramas.

Debe proponer pruebas seguras y especificar herramienta, puntos de
medición, condición de prueba y resultado esperado solo cuando exista
una fuente aplicable.

## 6.3 Powertrain Specialist

Dominio: - Combustión, mezcla y sincronización. - Inyección y presión de
combustible. - Encendido. - Admisión, vacío y escape. - Sensores
CKP/CMP, MAF, MAP, ECT, IAT, O2/AFR. - Fuel trims y datos de
operación. - Transmisiones automáticas en una fase posterior.

Debe diferenciar síntomas, causas posibles y pruebas discriminantes.

## 6.4 ECU & Network Engineer

Dominio: - OBD-II / SAE J1979. - CAN y CAN FD. - ISO-TP. - UDS / ISO
14229. - J1939. - DBC, señales y mensajes. - Topología de módulos y
redes.

En MVP, solo lectura y análisis de archivos. No ejecutar servicios UDS
de escritura, rutinas, desbloqueos, programación ni codificación.

## 6.5 Technical Researcher

Responsabilidades: - Recuperar manuales, diagramas, TSB y
documentación. - Verificar aplicabilidad por marca, modelo, año, motor,
mercado y módulo. - Entregar citas con documento, página/sección y
fragmento. - Señalar conflictos entre fuentes. - No completar
información faltante mediante suposiciones.

## 6.6 Diagnostic Reasoning

Puede implementarse primero como servicio interno, no necesariamente
como agente con LLM independiente.

Responsabilidades: - Mantener hipótesis alternativas. - Comparar
evidencia a favor/en contra. - Priorizar pruebas por capacidad
discriminante, costo, riesgo y dificultad. - Evitar el reemplazo de
piezas por conjetura. - Actualizar el estado de hipótesis según
resultados.

------------------------------------------------------------------------

# 7. Contrato de salida del diagnóstico

Toda respuesta estructurada del agente debe cumplir un DTO/JSON
equivalente a:

``` json
{
  "summary": "Resumen breve del problema",
  "vehicleContext": {
    "vehicleId": "uuid",
    "configurationWarnings": []
  },
  "knownFacts": [
    {
      "statement": "Dato aportado o medido",
      "origin": "USER|DEVICE|DOCUMENT",
      "sourceId": null
    }
  ],
  "missingInformation": [
    {
      "question": "¿El síntoma aparece con el motor frío o caliente?",
      "reason": "Permite diferenciar causas"
    }
  ],
  "dtcs": [
    {
      "code": "P0171",
      "status": "ACTIVE",
      "description": "Descripción validada o marcada como genérica",
      "sourceIds": []
    }
  ],
  "hypotheses": [
    {
      "id": "H1",
      "title": "Posible fuga de aire no medido",
      "confidence": "MEDIUM",
      "supportingEvidence": [],
      "contradictingEvidence": [],
      "unknowns": [],
      "status": "UNTESTED"
    }
  ],
  "testPlan": [
    {
      "id": "T1",
      "title": "Inspección de fugas de admisión",
      "purpose": "Comprobar entrada de aire no medido",
      "tools": ["Smoke tester"],
      "preconditions": [],
      "procedure": [],
      "expectedResults": [],
      "safetyNotes": [],
      "sourceIds": [],
      "status": "PENDING"
    }
  ],
  "safetyWarnings": [],
  "sources": [
    {
      "id": "SRC1",
      "documentTitle": "Manual de servicio",
      "publisher": "OEM",
      "page": "sección/página",
      "url": null,
      "excerpt": "Fragmento breve de respaldo"
    }
  ],
  "conclusionStatus": "PRELIMINARY"
}
```

Validar la salida con DTOs y Bean Validation. Si el modelo devuelve JSON
inválido, intentar una reparación limitada o pedir una nueva salida;
nunca persistir una respuesta estructurada inválida como si fuera
correcta.

Los niveles de confianza son estimaciones del modelo, no probabilidades
estadísticas calibradas. Mostrar esta aclaración en la interfaz.

------------------------------------------------------------------------

# 8. Flujo de diagnóstico

Implementar el flujo como estados explícitos:

1.  `INTAKE`: recepción del problema.
2.  `CONTEXT_REVIEW`: revisión del vehículo y datos disponibles.
3.  `INFORMATION_GATHERING`: preguntas faltantes.
4.  `EVIDENCE_COLLECTION`: DTC, PIDs, mediciones, documentos y archivos.
5.  `HYPOTHESIS_GENERATION`: hipótesis alternativas.
6.  `TEST_PLANNING`: pruebas discriminantes y seguras.
7.  `TEST_EXECUTION`: el técnico registra resultados.
8.  `REASSESSMENT`: actualización de hipótesis.
9.  `CONCLUSION`: causa confirmada o diagnóstico no concluyente.
10. `REPORTING`: generación del informe.

No forzar todos los estados si el caso es sencillo. Registrar
transiciones para auditoría.

## Ejemplo de interacción

Entrada: "Chevrolet Silverado 2008, 5.3 L. Ralentí inestable, consumo
elevado y P0171."

Comportamiento esperado: - Confirmar variante de motor, mercado y
calibración cuando sea relevante. - Preguntar si el código es actual,
pendiente o histórico. - Solicitar Freeze Frame y fuel trims si están
disponibles. - Preguntar si ocurre en frío, caliente, ralentí o carga. -
Generar varias hipótesis, no una conclusión inmediata. - Recomendar
pruebas que discriminen las hipótesis. - Citar la fuente cuando indique
un valor o procedimiento específico. - Actualizar el razonamiento cuando
el usuario introduzca resultados.

No afirmar que el MAF está defectuoso solo por P0171.

------------------------------------------------------------------------

# 9. Integración LLM y API keys

## Requisitos

-   Crear `ChatModelProvider` o una abstracción equivalente en
    infraestructura.
-   La aplicación debe seleccionar proveedor y modelo mediante
    configuración.
-   No acoplar el dominio a clases específicas de
    OpenAI/Gemini/Anthropic.
-   Separar configuración de chat y embeddings.
-   Permitir configurar una API key por instalación en el MVP.
-   Preparar el modelo de datos para credenciales por usuario/tenant en
    una fase posterior.
-   No usar una API key del frontend.
-   No registrar prompts completos que puedan contener datos sensibles,
    salvo modo debug explícito y redactado.

## Gestión de secretos

Para desarrollo: - Variables de entorno o archivo `.env` excluido de
Git. - Proporcionar `.env.example` sin secretos.

Para producción: - Cifrado autenticado (por ejemplo AES-GCM) usando una
clave maestra externa al repositorio. - La clave maestra se inyecta por
secret manager o variable segura. - Rotación documentada. - La API nunca
devuelve el valor descifrado. - Nunca escribir secretos en logs ni
excepciones.

## Costos y límites

-   Definir timeout.
-   Definir máximo de tokens de salida.
-   Registrar proveedor, modelo, latencia y uso de tokens cuando el
    proveedor lo reporte.
-   Aplicar límites por sesión y usuario.
-   Mostrar errores de cuota/autenticación de forma comprensible.
-   No reintentar indefinidamente errores 4xx.
-   Reintentar errores transitorios con backoff limitado.

------------------------------------------------------------------------

# 10. RAG y documentación técnica

## Pipeline

1.  Subida y validación del archivo.
2.  Cálculo de hash para evitar duplicados.
3.  Extracción de texto y metadatos.
4.  Detección de páginas/secciones.
5.  División en chunks conservando encabezados y contexto.
6.  Generación de embeddings.
7.  Persistencia en PostgreSQL/pgvector.
8.  Recuperación híbrida: texto + similitud vectorial cuando sea viable.
9.  Filtrado por aplicabilidad del vehículo.
10. Re-ranking opcional.
11. Entrega de fragmentos con referencia documental al agente.

## Metadatos mínimos por documento

-   `documentId`
-   nombre y hash.
-   fabricante/editor.
-   tipo de documento.
-   marca/modelo/años aplicables.
-   motor/plataforma/módulo.
-   mercado.
-   idioma.
-   fecha de publicación, si se conoce.
-   número de página o sección.
-   origen y titularidad/licencia declarada.

## Reglas RAG

-   No citar un documento que no se haya recuperado.
-   No inventar páginas.
-   Indicar cuando la fuente es genérica o no corresponde exactamente a
    la variante.
-   Si dos fuentes discrepan, mostrar el conflicto.
-   Distinguir documentación OEM de foros, blogs y notas del usuario.
-   No indexar contenido al que el usuario no tenga derecho de uso.
-   Conservar el archivo original y permitir borrado completo de sus
    chunks y embeddings.

------------------------------------------------------------------------

# 11. Modelo de datos inicial

Entidades principales:

## User

-   id
-   email
-   passwordHash
-   role
-   createdAt

## Vehicle

-   id
-   ownerId
-   vin (nullable)
-   make
-   model
-   year
-   market
-   platform
-   engineCode
-   engineDescription
-   fuelType
-   aspiration
-   ecu
-   transmission
-   modifications
-   notes
-   createdAt
-   updatedAt

## DiagnosticSession

-   id
-   vehicleId
-   createdBy
-   title
-   customerReference (nullable)
-   complaint
-   operatingConditions
-   status
-   startedAt
-   closedAt

## DiagnosticMessage

-   id
-   sessionId
-   role
-   content
-   structuredPayload (nullable)
-   createdAt

## DiagnosticEvidence

-   id
-   sessionId
-   type
-   description
-   value (nullable)
-   unit (nullable)
-   origin
-   attachmentId (nullable)
-   metadata
-   createdAt

## DtcRecord

-   id
-   sessionId
-   code
-   status
-   description
-   sourceId (nullable)
-   freezeFrame (JSONB)

## Hypothesis

-   id
-   sessionId
-   title
-   explanation
-   confidence
-   status
-   supportingEvidence (JSONB)
-   contradictingEvidence (JSONB)

## DiagnosticTest

-   id
-   sessionId
-   hypothesisId (nullable)
-   title
-   procedure (JSONB)
-   expectedResults (JSONB)
-   safetyNotes (JSONB)
-   result
-   status
-   performedAt

## TechnicalDocument

-   id
-   ownerId
-   filename
-   sha256
-   mediaType
-   storageKey
-   metadata (JSONB)
-   ingestionStatus
-   createdAt

## DocumentChunk

-   id
-   documentId
-   chunkIndex
-   pageNumber
-   section
-   content
-   embedding (vector)
-   metadata (JSONB)

## DiagnosticReport

-   id
-   sessionId
-   version
-   content (JSONB)
-   generatedAt
-   generatedBy

## LlmConfiguration

-   id
-   ownerId
-   provider
-   model
-   encryptedApiKey
-   baseUrl (nullable)
-   enabled
-   updatedAt

Usar UUID como identificador. Fechas en UTC en backend y conversión a
zona local en frontend. Usar `NUMERIC` o tipos decimales adecuados para
mediciones; no usar `float` para valores que requieran precisión.
Guardar unidades explícitas.

------------------------------------------------------------------------

# 12. API REST inicial

Prefijo: `/api/v1`

## Vehículos

-   `GET /vehicles`
-   `POST /vehicles`
-   `GET /vehicles/{id}`
-   `PUT /vehicles/{id}`
-   `DELETE /vehicles/{id}` (soft delete preferible)

## Diagnósticos

-   `GET /diagnostics`
-   `POST /diagnostics`
-   `GET /diagnostics/{id}`
-   `PATCH /diagnostics/{id}`
-   `POST /diagnostics/{id}/messages`
-   `POST /diagnostics/{id}/analyze`
-   `GET /diagnostics/{id}/events` (SSE)
-   `POST /diagnostics/{id}/evidence`
-   `POST /diagnostics/{id}/dtcs`
-   `POST /diagnostics/{id}/tests`
-   `PATCH /diagnostics/{id}/tests/{testId}`
-   `POST /diagnostics/{id}/reassess`
-   `POST /diagnostics/{id}/close`

## Documentación

-   `POST /documents`
-   `GET /documents`
-   `GET /documents/{id}`
-   `DELETE /documents/{id}`
-   `POST /documents/{id}/reindex`
-   `GET /knowledge/search?q=...`

## Informes

-   `POST /diagnostics/{id}/reports`
-   `GET /reports/{id}`
-   `GET /reports/{id}/export?format=pdf`
-   `GET /reports/{id}/export?format=json`

## Configuración

-   `GET /settings/llm` (sin secretos)
-   `PUT /settings/llm`
-   `POST /settings/llm/test`
-   `DELETE /settings/llm/key`

Aplicar autorización por propietario a todos los recursos. No confiar en
un `ownerId` enviado por el cliente; obtenerlo del principal
autenticado.

------------------------------------------------------------------------

# 13. Herramientas de diagnóstico e integraciones futuras

Diseñar interfaces/adaptadores, pero no bloquear el MVP esperando
hardware.

## Fase futura OBD-II

Candidato: - `python-OBD`: https://github.com/brendan-w/python-OBD -
Adaptador ELM327 compatible. - Servicio local de adquisición que exponga
API autenticada al backend.

Funciones iniciales: - Leer VIN si el vehículo/protocolo lo permite. -
Leer DTC. - Leer PIDs soportados. - Capturar Freeze Frame cuando esté
disponible. - Registrar muestras con timestamp y unidades. - No borrar
códigos automáticamente.

## Fase futura CAN

Candidatos: - python-can: https://python-can.readthedocs.io/ - cantools:
https://github.com/cantools/cantools - SavvyCAN:
https://www.savvycan.com/

Funciones: - Importar logs CAN. - Filtrar por ID. - Analizar
periodicidad. - Decodificar DBC. - Graficar señales. - Comparar
capturas.

## Fase futura UDS/J2534

Candidatos: - udsoncan: https://github.com/pylessard/python-udsoncan -
python-can-isotp: https://github.com/pylessard/python-can-isotp -
Investigar interfaces J2534 compatibles y sus licencias.

Mantener lectura como comportamiento predeterminado. Las operaciones de
escritura, programación, codificación y rutinas de actuadores requieren
un diseño de seguridad independiente, permisos explícitos,
precondiciones verificadas y confirmación humana.

## MCP

LangChain4j puede actuar como cliente MCP. Integrar servidores MCP solo
cuando se haya auditado: - Identidad y procedencia del servidor. -
Herramientas y argumentos expuestos. - Permisos. - Operaciones de
escritura. - Validación de resultados. - Límites de tiempo y errores.

No permitir que un servidor MCP arbitrario ejecute comandos de sistema
ni acceda libremente a archivos.

------------------------------------------------------------------------

# 14. Seguridad funcional y automotriz

Esta sección es obligatoria.

1.  El MVP es de análisis y consulta, no de control del vehículo.
2.  Ninguna herramienta puede accionar actuadores o escribir en módulos.
3.  No ejecutar pruebas con motor en marcha sin advertencias y
    precondiciones.
4.  No sugerir puentes de terminales, cortocircuitos ni bypass de
    sistemas de seguridad.
5.  Advertir sobre ventiladores eléctricos, correas, superficies
    calientes, combustible, alta tensión híbrida/EV y movimiento
    inesperado.
6.  No recomendar manipular airbags, pretensores o circuitos
    pirotécnicos.
7.  No recomendar medir resistencia en circuitos energizados.
8.  En sistemas HV, indicar que se requiere personal cualificado y
    procedimiento OEM de desenergización.
9.  Las especificaciones deben incluir unidades, condiciones de medición
    y fuente.
10. El usuario debe poder marcar un resultado como "no realizado" o "no
    concluyente".
11. Guardar auditoría de herramientas invocadas, parámetros no
    sensibles, usuario y resultado.
12. El agente no debe afirmar que una reparación es segura o definitiva
    sin validación apropiada.

------------------------------------------------------------------------

# 15. Observabilidad y evaluación de calidad

Registrar: - ID de sesión. - ID de ejecución del agente. - Proveedor y
modelo. - Latencia. - Tokens reportados. - Herramientas invocadas. -
Documentos recuperados. - Errores de validación. - Resultado
estructurado.

No registrar API keys, contraseñas ni información sensible innecesaria.

## Evaluación

Crear un conjunto de casos de prueba versionado: - DTC comunes con
información suficiente. - DTC con contexto insuficiente. - Síntomas
ambiguos. - Datos contradictorios. - Vehículos modificados/swaps. -
Manual que no corresponde al motor. - Fuente sin página. - Medición
fuera de rango con unidades distintas. - Petición peligrosa de escritura
en ECU. - Respuesta del LLM con JSON inválido.

Métricas: - Exactitud de extracción de DTC. - Fidelidad de citas. - Tasa
de afirmaciones sin respaldo. - Calidad y seguridad de los planes de
prueba. - Capacidad para pedir información faltante. - Validez del JSON
estructurado. - Latencia y costo por diagnóstico.

No optimizar solo por "respuesta convincente". La métrica principal es
la utilidad técnica verificable y la ausencia de afirmaciones
inventadas.

------------------------------------------------------------------------

# 16. Estructura sugerida del repositorio

``` text
automotive-diagnostic-workbench/
├── README.md
├── AGENTS.md
├── docker-compose.yml
├── .env.example
├── docs/
│   ├── architecture.md
│   ├── decisions/
│   ├── api/
│   └── safety-policy.md
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/adw/
│       │   ├── AdwApplication.java
│       │   ├── shared/
│       │   ├── auth/
│       │   ├── vehicle/
│       │   ├── diagnostic/
│       │   ├── evidence/
│       │   ├── knowledge/
│       │   ├── report/
│       │   ├── ai/
│       │   │   ├── orchestration/
│       │   │   ├── agents/
│       │   │   ├── tools/
│       │   │   ├── providers/
│       │   │   ├── rag/
│       │   │   ├── prompts/
│       │   │   └── guardrails/
│       │   └── infrastructure/
│       └── test/
├── frontend/
│   ├── package.json
│   └── src/
│       ├── app/
│       ├── components/
│       ├── features/
│       │   ├── dashboard/
│       │   ├── vehicles/
│       │   ├── diagnostics/
│       │   ├── knowledge/
│       │   ├── reports/
│       │   └── settings/
│       ├── lib/
│       ├── hooks/
│       └── types/
├── local-data/
│   ├── documents/
│   └── exports/
└── test-fixtures/
    ├── diagnostic-cases/
    ├── obd/
    └── can/
```

La estructura puede ajustarse al generar el proyecto, pero conservar los
límites modulares.

------------------------------------------------------------------------

# 17. Plan de implementación por fases

## Fase 0 --- Inspección y fundación

-   Inspeccionar el repositorio.
-   Crear ADR breve con las decisiones tecnológicas.
-   Crear backend Spring Boot y frontend React.
-   Configurar Docker Compose y PostgreSQL.
-   Configurar migraciones Flyway.
-   Configurar lint, formatter y pruebas.
-   Crear README y `.env.example`.

**Aceptación:** backend y frontend arrancan localmente; health check
operativo; base de datos accesible.

## Fase 1 --- Interfaz navegable y dominio base

-   Construir layout, sidebar y topbar.
-   Implementar Dashboard con estados vacíos.
-   Implementar CRUD de vehículos.
-   Implementar listado y creación de sesiones diagnósticas.
-   Crear modelo de datos y validaciones.
-   Agregar datos demo claramente etiquetados.

**Aceptación:** un usuario puede registrar un vehículo, crear una sesión
y volver a abrirla.

## Fase 2 --- Asistente LLM funcional

-   Integrar LangChain4j.
-   Implementar configuración segura del proveedor.
-   Agregar prueba de conexión.
-   Implementar conversación con streaming SSE.
-   Persistir mensajes.
-   Crear orquestador inicial.
-   Implementar salida estructurada validada.
-   Manejar errores de cuota, timeout y API key inválida.

**Aceptación:** el usuario describe un síntoma y recibe análisis
estructurado, con preguntas faltantes e hipótesis preliminares.

## Fase 3 --- Flujo de diagnóstico

-   Crear panel de evidencia.
-   Registrar DTC y mediciones.
-   Mostrar hipótesis y evidencias a favor/en contra.
-   Crear plan de pruebas.
-   Registrar resultados.
-   Implementar reevaluación.
-   Mantener historial de cambios.

**Aceptación:** un diagnóstico puede pasar de intake a conclusión
preliminar/confirmada sin perder trazabilidad.

## Fase 4 --- Biblioteca técnica y RAG

-   Carga de PDF y TXT primero.
-   Extracción y chunking.
-   Embeddings.
-   Persistencia pgvector.
-   Búsqueda semántica.
-   Filtros de aplicabilidad.
-   Citas a documento/página.
-   Vista de documentos y fragmentos.

**Aceptación:** el agente responde usando documentos cargados y muestra
referencias verificables. Si no encuentra evidencia, lo declara.

## Fase 5 --- Especialistas y herramientas

-   Separar Electrical Engineer, Powertrain Specialist, ECU & Network
    Engineer y Technical Researcher.
-   Implementar despacho explícito desde el orquestador.
-   Crear herramientas de dominio con entradas/salidas tipadas.
-   Incorporar análisis de archivos DTC/OBD/CSV.
-   Añadir evaluación automatizada de casos.

**Aceptación:** el orquestador delega tareas delimitadas y consolida
resultados sin duplicar ni contradecir silenciosamente la evidencia.

## Fase 6 --- Informes y endurecimiento

-   Exportar PDF y JSON.
-   Añadir auditoría.
-   Añadir autenticación y autorización completas.
-   Revisar secretos, permisos, carga de archivos y límites.
-   Pruebas end-to-end.
-   Documentar instalación y backup.

**Aceptación:** producto instalable localmente, con datos persistentes y
flujo completo reproducible.

## Fase 7 --- Hardware e integraciones

-   Servicio local de adquisición OBD-II.
-   Importación de logs CAN.
-   Adaptadores para python-can/cantools.
-   MCP auditado para herramientas seleccionadas.
-   Pruebas con simuladores antes de conectar vehículos reales.

No iniciar esta fase hasta que el flujo de diagnóstico sin hardware sea
estable.

------------------------------------------------------------------------

# 18. Criterios de aceptación globales

El MVP se considera funcional cuando:

-   Se puede instalar con instrucciones reproducibles.
-   Un usuario puede configurar su proveedor y API key de forma segura.
-   Se pueden registrar varios vehículos y sesiones.
-   Se puede mantener una conversación contextual por diagnóstico.
-   La respuesta incluye hechos, incertidumbres, hipótesis y pruebas.
-   Las respuestas estructuradas pasan validación.
-   Se pueden adjuntar evidencias.
-   Se puede cargar documentación y recuperarla con referencias.
-   Se puede registrar el resultado de las pruebas.
-   Se puede generar un informe.
-   No se ejecuta ninguna operación de escritura sobre el vehículo.
-   Hay pruebas automatizadas para los flujos críticos.
-   La interfaz funciona en escritorio y tablet.
-   No existen botones principales sin acción implementada.

------------------------------------------------------------------------

# 19. Instrucciones para comenzar a programar

Comienza por la **Fase 0 y Fase 1**, no intentes implementar todo el
sistema en una sola generación.

Orden de trabajo: 1. Inspecciona el entorno y confirma versiones
instaladas. 2. Presenta un plan de archivos y dependencias. 3. Crea el
esqueleto del monolito modular. 4. Implementa Docker Compose y
persistencia. 5. Implementa el layout visual de la SPA. 6. Implementa
CRUD de vehículos y sesiones. 7. Ejecuta pruebas y corrige errores. 8.
Resume qué quedó implementado, cómo ejecutarlo y qué falta.

No avances a RAG, agentes múltiples ni hardware antes de tener una
aplicación navegable y persistente.

Al finalizar cada fase entrega: - Archivos creados/modificados. -
Decisiones técnicas relevantes. - Comandos para ejecutar. - Pruebas
ejecutadas y resultado. - Limitaciones conocidas. - Próxima fase
propuesta.

**Resultado esperado:** un producto propio, con identidad visual
profesional, arquitectura Java mantenible y una base sólida para
evolucionar hacia un asistente de diagnóstico automotriz multimarca con
capacidades reales de adquisición y análisis de datos.
