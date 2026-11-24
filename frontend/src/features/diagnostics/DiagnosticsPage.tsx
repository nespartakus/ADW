import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link } from 'react-router-dom'
import { z } from 'zod'
import { api, type DiagnosticSession, type DiagnosticSessionInput } from '@/lib/api'

const sessionSchema = z.object({
  vehicleId: z.string().min(1, 'Selecciona un vehículo'),
  title: z.string().min(1, 'Obligatorio').max(200),
  customerReference: z.string().max(200),
  complaint: z.string().max(8000),
  operatingConditions: z.string().max(8000),
})

type FormValues = z.infer<typeof sessionSchema>

const inputCls =
  'w-full rounded-md border border-border bg-graphite px-3 py-2 text-sm text-fg placeholder:text-fg-muted focus:border-accent focus:outline-none'

export function DiagnosticsPage() {
  const queryClient = useQueryClient()
  const [showForm, setShowForm] = useState(false)

  const { data: sessions = [], isLoading } = useQuery({
    queryKey: ['diagnostics'],
    queryFn: api.diagnostics.list,
  })

  const { data: vehicles = [] } = useQuery({
    queryKey: ['vehicles'],
    queryFn: api.vehicles.list,
  })

  const createMutation = useMutation({
    mutationFn: api.diagnostics.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['diagnostics'] })
      setShowForm(false)
    },
  })

  if (isLoading) {
    return <p className="text-sm text-fg-muted">Cargando diagnósticos…</p>
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Diagnósticos</h1>
          <p className="mt-1 text-sm text-fg-muted">
            {sessions.length} sesión{sessions.length === 1 ? '' : 'es'} de diagnóstico.
          </p>
        </div>
        <button
          type="button"
          className="rounded-md bg-accent px-4 py-2 text-sm font-medium text-graphite hover:bg-accent-strong disabled:opacity-50"
          disabled={vehicles.length === 0}
          title={vehicles.length === 0 ? 'Registra un vehículo primero' : undefined}
          onClick={() => setShowForm((v) => !v)}
        >
          Nuevo diagnóstico
        </button>
      </div>

      {vehicles.length === 0 && (
        <p className="rounded-md border border-warning/30 bg-warning/10 px-4 py-3 text-sm text-warning">
          Registra un vehículo antes de abrir un diagnóstico.
        </p>
      )}

      {showForm && (
        <SessionForm
          vehicles={vehicles}
          onCancel={() => setShowForm(false)}
          onSubmit={(input) => createMutation.mutate(input)}
        />
      )}

      {sessions.length === 0 ? (
        <div className="flex flex-col items-center gap-2 rounded-md border border-dashed border-border py-16 text-center">
          <p className="text-sm text-fg-muted">Aún no hay diagnósticos.</p>
        </div>
      ) : (
        <div className="flex flex-col gap-2">
          {sessions.map((s) => (
            <Link
              key={s.id}
              to={`/diagnostics/${s.id}`}
              className="flex items-center justify-between rounded-lg border border-border bg-surface px-4 py-3 hover:border-accent"
            >
              <div>
                <div className="font-medium">{s.title}</div>
                <div className="text-sm text-fg-muted">
                  {s.vehicle.make} {s.vehicle.model} {s.vehicle.year}
                  {s.customerReference ? ` · ${s.customerReference}` : ''}
                </div>
              </div>
              <StatusBadge status={s.status} />
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}

function SessionForm({
  vehicles,
  onSubmit,
  onCancel,
}: {
  vehicles: { id: string; make: string; model: string; year: number }[]
  onSubmit: (input: DiagnosticSessionInput) => void
  onCancel: () => void
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(sessionSchema),
    defaultValues: { vehicleId: '', title: '', customerReference: '', complaint: '', operatingConditions: '' },
  })

  return (
    <form
      onSubmit={handleSubmit((values) =>
        onSubmit({
          vehicleId: values.vehicleId,
          title: values.title,
          customerReference: values.customerReference || undefined,
          complaint: values.complaint || undefined,
          operatingConditions: values.operatingConditions || undefined,
        }),
      )}
      className="rounded-lg border border-border bg-surface p-5"
    >
      <h2 className="mb-4 text-sm font-medium text-fg">Nueva sesión de diagnóstico</h2>

      <div className="grid gap-4">
        <label className="flex flex-col gap-1">
          <span className="text-xs text-fg-muted">Vehículo</span>
          <select className={inputCls} {...register('vehicleId')}>
            <option value="">Selecciona…</option>
            {vehicles.map((v) => (
              <option key={v.id} value={v.id}>
                {v.make} {v.model} {v.year}
              </option>
            ))}
          </select>
          {errors.vehicleId && <span className="text-xs text-danger">{errors.vehicleId.message}</span>}
        </label>

        <label className="flex flex-col gap-1">
          <span className="text-xs text-fg-muted">Título</span>
          <input className={inputCls} {...register('title')} placeholder="Ralentí inestable" />
          {errors.title && <span className="text-xs text-danger">{errors.title.message}</span>}
        </label>

        <label className="flex flex-col gap-1">
          <span className="text-xs text-fg-muted">Referencia del cliente (opcional)</span>
          <input className={inputCls} {...register('customerReference')} />
        </label>

        <label className="flex flex-col gap-1">
          <span className="text-xs text-fg-muted">Síntoma / motivo de ingreso</span>
          <textarea className={inputCls} rows={3} {...register('complaint')} />
        </label>

        <label className="flex flex-col gap-1">
          <span className="text-xs text-fg-muted">Condiciones de aparición</span>
          <textarea
            className={inputCls}
            rows={2}
            {...register('operatingConditions')}
            placeholder="Frío, caliente, ralentí, carga…"
          />
        </label>
      </div>

      <div className="mt-5 flex gap-3">
        <button
          type="submit"
          className="rounded-md bg-accent px-4 py-2 text-sm font-medium text-graphite hover:bg-accent-strong"
        >
          Crear diagnóstico
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded-md border border-border px-4 py-2 text-sm text-fg-muted hover:text-fg"
        >
          Cancelar
        </button>
      </div>
    </form>
  )
}

function StatusBadge({ status }: { status: DiagnosticSession['status'] }) {
  const cls = status === 'OPEN' ? 'bg-success/15 text-success' : 'bg-border text-fg-muted'
  const label = status === 'OPEN' ? 'Abierta' : 'Cerrada'
  return <span className={`rounded-full px-2.5 py-1 text-xs font-medium ${cls}`}>{label}</span>
}
