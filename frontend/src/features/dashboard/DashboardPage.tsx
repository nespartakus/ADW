import { Link } from 'react-router-dom'

export function DashboardPage() {
  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Dashboard</h1>
        <p className="mt-1 text-sm text-fg-muted">
          Resumen del taller y accesos rápidos.
        </p>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <StatCard label="Diagnósticos recientes" value="0" />
        <StatCard label="Vehículos registrados" value="0" />
        <StatCard label="Sesiones abiertas" value="0" />
        <StatCard label="Informes generados" value="0" />
      </div>

      <div className="flex flex-wrap gap-3">
        <QuickAction to="/diagnostics" label="Nuevo diagnóstico" />
        <QuickAction to="/vehicles" label="Registrar vehículo" />
        <QuickAction to="/knowledge" label="Añadir documentación" />
      </div>

      <section className="rounded-lg border border-border bg-surface p-5">
        <h2 className="text-sm font-medium text-fg-muted">Actividad reciente</h2>
        <EmptyState message="Aún no hay actividad. Registra un vehículo o abre un diagnóstico para empezar." />
      </section>
    </div>
  )
}

function StatCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg border border-border bg-surface p-4">
      <div className="text-2xl font-semibold tabular-nums text-fg">{value}</div>
      <div className="mt-1 text-xs text-fg-muted">{label}</div>
    </div>
  )
}

function QuickAction({ to, label }: { to: string; label: string }) {
  return (
    <Link
      to={to}
      className="rounded-md border border-border bg-elevated px-4 py-2 text-sm font-medium text-fg hover:border-accent hover:text-accent"
    >
      {label}
    </Link>
  )
}

function EmptyState({ message }: { message: string }) {
  return (
    <div className="mt-3 flex flex-col items-center gap-2 rounded-md border border-dashed border-border py-10 text-center">
      <p className="max-w-sm text-sm text-fg-muted">{message}</p>
    </div>
  )
}
