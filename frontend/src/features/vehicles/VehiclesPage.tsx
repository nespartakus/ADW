import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { api, type Vehicle, type VehicleInput } from '@/lib/api'

const vehicleSchema = z.object({
  vin: z.string().max(17),
  make: z.string().min(1, 'Obligatorio').max(100),
  model: z.string().min(1, 'Obligatorio').max(100),
  year: z.number().int().min(1900).max(2100),
  market: z.string().max(50),
  platform: z.string().max(100),
  engineCode: z.string().max(100),
  engineDescription: z.string().max(255),
  displacement: z.number().min(0).max(9999.99).nullable(),
  fuelType: z.string().max(50),
  aspiration: z.string().max(50),
  ecu: z.string().max(100),
  transmission: z.string().max(100),
  modifications: z.string().max(4000),
  notes: z.string().max(4000),
})

type FormValues = z.infer<typeof vehicleSchema>

const inputCls =
  'w-full rounded-md border border-border bg-graphite px-3 py-2 text-sm text-fg placeholder:text-fg-muted focus:border-accent focus:outline-none'

const emptyForm: FormValues = {
  vin: '',
  make: '',
  model: '',
  year: new Date().getFullYear(),
  market: '',
  platform: '',
  engineCode: '',
  engineDescription: '',
  displacement: null,
  fuelType: '',
  aspiration: '',
  ecu: '',
  transmission: '',
  modifications: '',
  notes: '',
}

function toInput(v: FormValues): VehicleInput {
  return {
    vin: v.vin || null,
    make: v.make,
    model: v.model,
    year: v.year,
    market: v.market || null,
    platform: v.platform || null,
    engineCode: v.engineCode || null,
    engineDescription: v.engineDescription || null,
    displacement: v.displacement ?? null,
    fuelType: v.fuelType || null,
    aspiration: v.aspiration || null,
    ecu: v.ecu || null,
    transmission: v.transmission || null,
    modifications: v.modifications || null,
    notes: v.notes || null,
  }
}

function toFormValues(v: Vehicle): FormValues {
  return {
    vin: v.vin ?? '',
    make: v.make,
    model: v.model,
    year: v.year,
    market: v.market ?? '',
    platform: v.platform ?? '',
    engineCode: v.engineCode ?? '',
    engineDescription: v.engineDescription ?? '',
    displacement: v.displacement,
    fuelType: v.fuelType ?? '',
    aspiration: v.aspiration ?? '',
    ecu: v.ecu ?? '',
    transmission: v.transmission ?? '',
    modifications: v.modifications ?? '',
    notes: v.notes ?? '',
  }
}

export function VehiclesPage() {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState<Vehicle | null>(null)
  const [showForm, setShowForm] = useState(false)

  const { data: vehicles = [], isLoading } = useQuery({
    queryKey: ['vehicles'],
    queryFn: api.vehicles.list,
  })

  const createMutation = useMutation({
    mutationFn: api.vehicles.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vehicles'] })
      setShowForm(false)
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, input }: { id: string; input: VehicleInput }) =>
      api.vehicles.update(id, input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vehicles'] })
      setShowForm(false)
      setEditing(null)
    },
  })

  const deleteMutation = useMutation({
    mutationFn: api.vehicles.remove,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vehicles'] }),
  })

  if (isLoading) {
    return <p className="text-sm text-fg-muted">Cargando vehículos…</p>
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Vehículos</h1>
          <p className="mt-1 text-sm text-fg-muted">
            {vehicles.length} vehículo{vehicles.length === 1 ? '' : 's'} registrado
            {vehicles.length === 1 ? '' : 's'}.
          </p>
        </div>
        <button
          type="button"
          className="rounded-md bg-accent px-4 py-2 text-sm font-medium text-graphite hover:bg-accent-strong"
          onClick={() => {
            setEditing(null)
            setShowForm((v) => !v)
          }}
        >
          Registrar vehículo
        </button>
      </div>

      {showForm && (
        <VehicleForm
          initial={editing ? toFormValues(editing) : emptyForm}
          onSubmit={(input) => {
            if (editing) {
              updateMutation.mutate({ id: editing.id, input })
            } else {
              createMutation.mutate(input)
            }
          }}
          onCancel={() => {
            setShowForm(false)
            setEditing(null)
          }}
        />
      )}

      {vehicles.length === 0 ? (
        <EmptyState />
      ) : (
        <div className="overflow-hidden rounded-lg border border-border">
          <table className="w-full text-sm">
            <thead className="bg-surface text-left text-xs uppercase tracking-wide text-fg-muted">
              <tr>
                <th className="px-4 py-3 font-medium">Vehículo</th>
                <th className="px-4 py-3 font-medium">Año</th>
                <th className="px-4 py-3 font-medium">Motor</th>
                <th className="px-4 py-3 font-medium">Transmisión</th>
                <th className="px-4 py-3 font-medium">VIN</th>
                <th className="px-4 py-3 font-medium"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {vehicles.map((v) => (
                <tr key={v.id} className="bg-graphite hover:bg-surface">
                  <td className="px-4 py-3 font-medium">
                    {v.make} {v.model}
                  </td>
                  <td className="px-4 py-3 text-fg-muted">{v.year}</td>
                  <td className="px-4 py-3 text-fg-muted">
                    {v.engineDescription ?? v.engineCode ?? '—'}
                  </td>
                  <td className="px-4 py-3 text-fg-muted">{v.transmission ?? '—'}</td>
                  <td className="px-4 py-3 font-mono text-xs text-fg-muted">{v.vin ?? '—'}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      className="mr-3 text-sm text-accent hover:underline"
                      onClick={() => {
                        setEditing(v)
                        setShowForm(true)
                      }}
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      className="text-sm text-danger hover:underline"
                      onClick={() => {
                        if (confirm(`¿Eliminar ${v.make} ${v.model}?`)) {
                          deleteMutation.mutate(v.id)
                        }
                      }}
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

function VehicleForm({
  initial,
  onSubmit,
  onCancel,
}: {
  initial: FormValues
  onSubmit: (input: VehicleInput) => void
  onCancel: () => void
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(vehicleSchema),
    defaultValues: initial,
  })

  return (
    <form
      onSubmit={handleSubmit((values) => onSubmit(toInput(values)))}
      className="rounded-lg border border-border bg-surface p-5"
    >
      <h2 className="mb-4 text-sm font-medium text-fg">Datos del vehículo</h2>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <Field label="VIN (opcional)" error={errors.vin?.message}>
          <input className={inputCls} {...register('vin')} placeholder="1G1..." />
        </Field>
        <Field label="Marca" error={errors.make?.message}>
          <input className={inputCls} {...register('make')} />
        </Field>
        <Field label="Modelo" error={errors.model?.message}>
          <input className={inputCls} {...register('model')} />
        </Field>
        <Field label="Año" error={errors.year?.message}>
          <input className={inputCls} type="number" {...register('year', { valueAsNumber: true })} />
        </Field>
        <Field label="Mercado" error={errors.market?.message}>
          <input className={inputCls} {...register('market')} placeholder="MX, US…" />
        </Field>
        <Field label="Plataforma/generación" error={errors.platform?.message}>
          <input className={inputCls} {...register('platform')} />
        </Field>
        <Field label="Código de motor" error={errors.engineCode?.message}>
          <input className={inputCls} {...register('engineCode')} placeholder="L59" />
        </Field>
        <Field label="Descripción de motor" error={errors.engineDescription?.message}>
          <input className={inputCls} {...register('engineDescription')} placeholder="5.3L V8" />
        </Field>
        <Field label="Cilindrada (L)" error={errors.displacement?.message}>
          <input
            className={inputCls}
            type="number"
            step="0.1"
            {...register('displacement', { setValueAs: (v) => (v === '' ? null : Number(v)) })}
          />
        </Field>
        <Field label="Combustible" error={errors.fuelType?.message}>
          <input className={inputCls} {...register('fuelType')} placeholder="gasoline" />
        </Field>
        <Field label="Aspiración" error={errors.aspiration?.message}>
          <input className={inputCls} {...register('aspiration')} />
        </Field>
        <Field label="ECU/PCM" error={errors.ecu?.message}>
          <input className={inputCls} {...register('ecu')} />
        </Field>
        <Field label="Transmisión" error={errors.transmission?.message}>
          <input className={inputCls} {...register('transmission')} />
        </Field>
      </div>

      <div className="mt-4 grid gap-4">
        <Field label="Modificaciones" error={errors.modifications?.message}>
          <textarea className={inputCls} rows={2} {...register('modifications')} />
        </Field>
        <Field label="Notas" error={errors.notes?.message}>
          <textarea className={inputCls} rows={2} {...register('notes')} />
        </Field>
      </div>

      <div className="mt-5 flex gap-3">
        <button
          type="submit"
          className="rounded-md bg-accent px-4 py-2 text-sm font-medium text-graphite hover:bg-accent-strong"
        >
          Guardar
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

function Field({
  label,
  error,
  children,
}: {
  label: string
  error?: string
  children: React.ReactNode
}) {
  return (
    <label className="flex flex-col gap-1">
      <span className="text-xs text-fg-muted">{label}</span>
      {children}
      {error && <span className="text-xs text-danger">{error}</span>}
    </label>
  )
}

function EmptyState() {
  return (
    <div className="flex flex-col items-center gap-2 rounded-md border border-dashed border-border py-16 text-center">
      <p className="text-sm text-fg-muted">Aún no hay vehículos registrados.</p>
      <p className="text-sm text-fg-muted">Usa «Registrar vehículo» para añadir el primero.</p>
    </div>
  )
}
