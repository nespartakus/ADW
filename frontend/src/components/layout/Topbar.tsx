export function Topbar() {
  return (
    <header className="flex h-14 shrink-0 items-center justify-between border-b border-border bg-surface px-4">
      <span className="text-sm font-medium text-fg-muted">
        Automotive Diagnostic Workbench
      </span>
      <div className="flex items-center gap-3 text-xs text-fg-muted">
        <StatusDot label="LLM" status="unknown" />
        <StatusDot label="Almacenamiento" status="unknown" />
        <span className="rounded-full bg-elevated px-3 py-1">Usuario local</span>
      </div>
    </header>
  )
}

function StatusDot({ label, status }: { label: string; status: 'ok' | 'unknown' }) {
  const color = status === 'ok' ? 'bg-success' : 'bg-border'
  return (
    <span className="flex items-center gap-1.5">
      <span className={`inline-block h-2 w-2 rounded-full ${color}`} />
      {label}
    </span>
  )
}
