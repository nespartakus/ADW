import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { api } from '@/lib/api'

export function DiagnosticDetailPage() {
  const { id } = useParams<{ id: string }>()

  const { data: session, isLoading, isError } = useQuery({
    queryKey: ['diagnostic', id],
    queryFn: () => api.diagnostics.get(id!),
    enabled: !!id,
  })

  if (isLoading) {
    return <p className="text-sm text-fg-muted">Cargando diagnóstico…</p>
  }

  if (isError || !session) {
    return (
      <div className="flex flex-col items-start gap-3">
        <p className="text-sm text-danger">No se pudo cargar el diagnóstico.</p>
        <Link to="/diagnostics" className="text-sm text-accent hover:underline">
          Volver a diagnósticos
        </Link>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link to="/diagnostics" className="text-sm text-accent hover:underline">
          ← Diagnósticos
        </Link>
        <h1 className="mt-2 text-2xl font-semibold">{session.title}</h1>
        <p className="mt-1 text-sm text-fg-muted">
          {session.vehicle.make} {session.vehicle.model} {session.vehicle.year} ·{' '}
          {session.status === 'OPEN' ? 'Abierta' : 'Cerrada'}
        </p>
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <InfoBlock label="Vehículo">
          {session.vehicle.make} {session.vehicle.model} {session.vehicle.year}
        </InfoBlock>
        <InfoBlock label="Referencia del cliente">
          {session.customerReference ?? '—'}
        </InfoBlock>
        <InfoBlock label="Síntoma / motivo de ingreso">
          {session.complaint ?? '—'}
        </InfoBlock>
        <InfoBlock label="Condiciones de aparición">
          {session.operatingConditions ?? '—'}
        </InfoBlock>
        <InfoBlock label="Iniciada">
          {new Date(session.startedAt).toLocaleString()}
        </InfoBlock>
        <InfoBlock label="Cerrada">
          {session.closedAt ? new Date(session.closedAt).toLocaleString() : '—'}
        </InfoBlock>
      </div>

      <div className="rounded-lg border border-dashed border-border p-6 text-center text-sm text-fg-muted">
        El espacio de trabajo de diagnóstico (conversación, DTC, hipótesis y pruebas) llega en
        fases posteriores. No disponible todavía.
      </div>
    </div>
  )
}

function InfoBlock({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="rounded-lg border border-border bg-surface p-4">
      <div className="text-xs uppercase tracking-wide text-fg-muted">{label}</div>
      <div className="mt-1 text-sm">{children}</div>
    </div>
  )
}
