export interface Vehicle {
  id: string
  vin: string | null
  make: string
  model: string
  year: number
  market: string | null
  platform: string | null
  engineCode: string | null
  engineDescription: string | null
  displacement: number | null
  fuelType: string | null
  aspiration: string | null
  ecu: string | null
  transmission: string | null
  modifications: string | null
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface VehicleInput {
  vin?: string | null
  make: string
  model: string
  year: number
  market?: string | null
  platform?: string | null
  engineCode?: string | null
  engineDescription?: string | null
  displacement?: number | null
  fuelType?: string | null
  aspiration?: string | null
  ecu?: string | null
  transmission?: string | null
  modifications?: string | null
  notes?: string | null
}

export interface VehicleSummary {
  id: string
  make: string
  model: string
  year: number
}

export interface DiagnosticSession {
  id: string
  vehicle: VehicleSummary
  title: string
  customerReference: string | null
  complaint: string | null
  operatingConditions: string | null
  status: 'OPEN' | 'CLOSED'
  startedAt: string
  closedAt: string | null
}

export interface DiagnosticSessionInput {
  vehicleId: string
  title: string
  customerReference?: string
  complaint?: string
  operatingConditions?: string
}

export interface LlmSettings {
  provider: string | null
  model: string | null
  baseUrl: string | null
  enabled: boolean
  hasApiKey: boolean
}

export interface LlmSettingsInput {
  provider: string
  model: string
  baseUrl?: string
  enabled: boolean
  apiKey?: string
}

export interface TestConnectionResult {
  ok: boolean
  provider: string | null
  message: string
}

export interface DiagnosticMessage {
  id: string
  role: 'USER' | 'ASSISTANT' | 'TOOL' | 'SYSTEM'
  content: string | null
  structuredPayload: DiagnosticAnalysis | null
  createdAt: string
}

export interface DiagnosticAnalysis {
  summary: string
  vehicleContext: { vehicleId: string | null; configurationWarnings: string[] } | null
  knownFacts: { statement: string; origin: string; sourceId: string | null }[]
  missingInformation: { question: string; reason: string | null }[]
  dtcs: { code: string; status: string; description: string | null; sourceIds: string[] }[]
  hypotheses: Hypothesis[]
  testPlan: TestPlanItem[]
  safetyWarnings: string[]
  sources: Source[]
  conclusionStatus: string
}

export interface Hypothesis {
  id: string
  title: string
  confidence: 'LOW' | 'MEDIUM' | 'HIGH'
  supportingEvidence: string[]
  contradictingEvidence: string[]
  unknowns: string[]
  status: string
}

export interface TestPlanItem {
  id: string
  title: string
  purpose: string | null
  tools: string[]
  preconditions: string[]
  procedure: string[]
  expectedResults: string[]
  safetyNotes: string[]
  sourceIds: string[]
  status: string
}

export interface Source {
  id: string
  documentTitle: string | null
  publisher: string | null
  page: string | null
  url: string | null
  excerpt: string | null
}

interface ApiErrorBody {
  code?: string
  message?: string
  fieldErrors?: Record<string, string>
}

export class ApiError extends Error {
  status: number
  fieldErrors: Record<string, string>

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

const BASE_URL = '/api/v1'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  })

  if (!res.ok) {
    const body = (await res.json().catch(() => null)) as ApiErrorBody | null
    throw new ApiError(res.status, body?.message ?? res.statusText, body?.fieldErrors ?? {})
  }

  if (res.status === 204) {
    return undefined as T
  }

  return (await res.json()) as T
}

export const api = {
  vehicles: {
    list: () => request<Vehicle[]>('/vehicles'),
    get: (id: string) => request<Vehicle>(`/vehicles/${id}`),
    create: (input: VehicleInput) =>
      request<Vehicle>('/vehicles', { method: 'POST', body: JSON.stringify(input) }),
    update: (id: string, input: VehicleInput) =>
      request<Vehicle>(`/vehicles/${id}`, { method: 'PUT', body: JSON.stringify(input) }),
    remove: (id: string) => request<void>(`/vehicles/${id}`, { method: 'DELETE' }),
  },
  diagnostics: {
    list: () => request<DiagnosticSession[]>('/diagnostics'),
    get: (id: string) => request<DiagnosticSession>(`/diagnostics/${id}`),
    create: (input: DiagnosticSessionInput) =>
      request<DiagnosticSession>('/diagnostics', { method: 'POST', body: JSON.stringify(input) }),
    listMessages: (id: string) => request<DiagnosticMessage[]>(`/diagnostics/${id}/messages`),
    sendMessage: (id: string, content: string) =>
      request<DiagnosticMessage[]>(`/diagnostics/${id}/messages`, {
        method: 'POST',
        body: JSON.stringify({ content }),
      }),
  },
  settings: {
    get: () => request<LlmSettings>('/settings/llm'),
    update: (input: LlmSettingsInput) =>
      request<LlmSettings>('/settings/llm', { method: 'PUT', body: JSON.stringify(input) }),
    test: () => request<TestConnectionResult>('/settings/llm/test', { method: 'POST' }),
    deleteKey: () => request<LlmSettings>('/settings/llm/key', { method: 'DELETE' }),
  },
}
