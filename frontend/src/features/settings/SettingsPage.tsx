import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import { api, type TestConnectionResult } from '@/lib/api'

const inputCls =
  'w-full rounded-md border border-border bg-graphite px-3 py-2 text-sm text-fg placeholder:text-fg-muted focus:border-accent focus:outline-none'

export function SettingsPage() {
  const queryClient = useQueryClient()
  const { data, isLoading } = useQuery({ queryKey: ['llmSettings'], queryFn: api.settings.get })

  const [provider, setProvider] = useState('openai')
  const [model, setModel] = useState('')
  const [baseUrl, setBaseUrl] = useState('')
  const [enabled, setEnabled] = useState(false)
  const [apiKey, setApiKey] = useState('')
  const [testResult, setTestResult] = useState<TestConnectionResult | null>(null)
  const [saved, setSaved] = useState(false)

  useEffect(() => {
    if (data) {
      setProvider(data.provider ?? 'openai')
      setModel(data.model ?? '')
      setBaseUrl(data.baseUrl ?? '')
      setEnabled(data.enabled)
    }
  }, [data])

  const saveMutation = useMutation({
    mutationFn: () =>
      api.settings.update({
        provider,
        model,
        baseUrl: baseUrl || undefined,
        enabled,
        apiKey: apiKey || undefined,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['llmSettings'] })
      setApiKey('')
      setSaved(true)
      setTimeout(() => setSaved(false), 2500)
    },
  })

  const testMutation = useMutation({
    mutationFn: api.settings.test,
    onSuccess: (r) => setTestResult(r),
    onError: (e) => setTestResult({ ok: false, provider: null, message: (e as Error).message }),
  })

  const deleteKeyMutation = useMutation({
    mutationFn: api.settings.deleteKey,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['llmSettings'] }),
  })

  if (isLoading) {
    return <p className="text-sm text-fg-muted">Cargando configuración…</p>
  }

  return (
    <div className="flex max-w-2xl flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Configuración</h1>
        <p className="mt-1 text-sm text-fg-muted">
          Proveedor y modelo del asistente de diagnóstico.
        </p>
      </div>

      <div className="flex flex-col gap-4 rounded-lg border border-border bg-surface p-5">
        <Field label="Proveedor">
          <input className={inputCls} value={provider} onChange={(e) => setProvider(e.target.value)} />
        </Field>

        <Field label="Modelo">
          <input
            className={inputCls}
            value={model}
            onChange={(e) => setModel(e.target.value)}
            placeholder="gpt-4o-mini"
          />
        </Field>

        <Field label="Base URL (opcional, para OpenAI-compatible / Ollama)">
          <input
            className={inputCls}
            value={baseUrl}
            onChange={(e) => setBaseUrl(e.target.value)}
            placeholder="http://localhost:11434/v1"
          />
        </Field>

        <Field label={`API key ${data?.hasApiKey ? '(configurada)' : ''}`}>
          <input
            className={inputCls}
            type="password"
            value={apiKey}
            onChange={(e) => setApiKey(e.target.value)}
            placeholder={data?.hasApiKey ? 'Deja en blanco para conservar la actual' : 'sk-...'}
          />
        </Field>

        <label className="flex items-center gap-2 text-sm">
          <input
            type="checkbox"
            checked={enabled}
            onChange={(e) => setEnabled(e.target.checked)}
            className="h-4 w-4"
          />
          Habilitado
        </label>

        {data?.hasApiKey && (
          <button
            type="button"
            onClick={() => deleteKeyMutation.mutate()}
            className="w-fit text-sm text-danger hover:underline"
          >
            Eliminar API key guardada
          </button>
        )}

        <div className="flex flex-wrap items-center gap-3">
          <button
            type="button"
            onClick={() => saveMutation.mutate()}
            disabled={saveMutation.isPending}
            className="rounded-md bg-accent px-4 py-2 text-sm font-medium text-graphite hover:bg-accent-strong disabled:opacity-50"
          >
            {saveMutation.isPending ? 'Guardando…' : 'Guardar'}
          </button>
          <button
            type="button"
            onClick={() => testMutation.mutate()}
            disabled={testMutation.isPending}
            className="rounded-md border border-border px-4 py-2 text-sm text-fg-muted hover:text-fg disabled:opacity-50"
          >
            {testMutation.isPending ? 'Probando…' : 'Probar conexión'}
          </button>
          {saved && <span className="text-sm text-success">Guardado</span>}
        </div>

        {saveMutation.isError && (
          <p className="text-sm text-danger">{(saveMutation.error as Error).message}</p>
        )}

        {testResult && (
          <div
            className={`rounded-md border px-4 py-3 text-sm ${
              testResult.ok ? 'border-success/30 bg-success/10 text-success' : 'border-danger/30 bg-danger/10 text-danger'
            }`}
          >
            {testResult.ok ? `Conexión correcta (${testResult.provider}): ${testResult.message}` : testResult.message}
          </div>
        )}
      </div>
    </div>
  )
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="flex flex-col gap-1">
      <span className="text-xs text-fg-muted">{label}</span>
      {children}
    </label>
  )
}
