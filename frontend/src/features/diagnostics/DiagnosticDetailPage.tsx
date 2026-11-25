import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, type DiagnosticMessage } from '@/lib/api'
import { AnalysisView } from './AnalysisView'

export function DiagnosticDetailPage() {
  const { id } = useParams<{ id: string }>()
  const queryClient = useQueryClient()
  const [input, setInput] = useState('')

  const { data: session, isLoading, isError } = useQuery({
    queryKey: ['diagnostic', id],
    queryFn: () => api.diagnostics.get(id!),
    enabled: !!id,
  })

  const { data: messages = [] } = useQuery({
    queryKey: ['messages', id],
    queryFn: () => api.diagnostics.listMessages(id!),
    enabled: !!id,
  })

  const sendMutation = useMutation({
    mutationFn: (content: string) => api.diagnostics.sendMessage(id!, content),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['messages', id] })
      setInput('')
    },
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
    <div className="flex h-full flex-col gap-4">
      <div>
        <Link to="/diagnostics" className="text-sm text-accent hover:underline">
          ← Diagnósticos
        </Link>
        <h1 className="mt-1 text-xl font-semibold">{session.title}</h1>
        <p className="text-sm text-fg-muted">
          {session.vehicle.make} {session.vehicle.model} {session.vehicle.year}
        </p>
      </div>

      <div className="min-h-0 flex-1 overflow-y-auto rounded-lg border border-border bg-surface p-4">
        {messages.length === 0 ? (
          <p className="text-sm text-fg-muted">
            Describe el síntoma para recibir un análisis estructurado con hipótesis, información
            faltante y un plan de pruebas.
          </p>
        ) : (
          <div className="flex flex-col gap-4">
            {messages.map((m) => (
              <MessageBubble key={m.id} message={m} />
            ))}
          </div>
        )}
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault()
          if (input.trim()) sendMutation.mutate(input)
        }}
        className="flex gap-2"
      >
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          rows={2}
          placeholder="Describe el síntoma, condiciones y datos disponibles…"
          className="flex-1 rounded-md border border-border bg-graphite px-3 py-2 text-sm text-fg placeholder:text-fg-muted focus:border-accent focus:outline-none"
        />
        <button
          type="submit"
          disabled={sendMutation.isPending || !input.trim()}
          className="self-end rounded-md bg-accent px-4 py-2 text-sm font-medium text-graphite hover:bg-accent-strong disabled:opacity-50"
        >
          {sendMutation.isPending ? 'Analizando…' : 'Enviar'}
        </button>
      </form>

      {sendMutation.isError && (
        <p className="text-sm text-danger">{(sendMutation.error as Error).message}</p>
      )}
    </div>
  )
}

function MessageBubble({ message }: { message: DiagnosticMessage }) {
  if (message.role === 'USER') {
    return (
      <div className="flex justify-end">
        <div className="max-w-[80%] rounded-lg bg-elevated px-4 py-2 text-sm">{message.content}</div>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-2">
      <div className="max-w-[90%] rounded-lg border border-border bg-graphite px-4 py-3 text-sm">
        {message.content && <p className="mb-3 font-medium">{message.content}</p>}
        {message.structuredPayload ? (
          <AnalysisView analysis={message.structuredPayload} />
        ) : (
          <p className="text-fg-muted">{message.content ?? 'Sin contenido'}</p>
        )}
      </div>
    </div>
  )
}
