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
  },
}
