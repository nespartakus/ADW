import { NavLink } from 'react-router-dom'
import { NAV_ITEMS } from '@/lib/navigation'

export function Sidebar() {
  return (
    <aside className="flex w-60 shrink-0 flex-col border-r border-border bg-surface">
      <div className="flex h-14 items-center gap-2 border-b border-border px-4">
        <span className="text-lg font-bold tracking-tight text-fg">ADW</span>
      </div>
      <nav className="flex flex-1 flex-col gap-1 p-2">
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              [
                'flex items-center justify-between rounded-md px-3 py-2 text-sm',
                isActive
                  ? 'bg-elevated font-medium text-accent'
                  : 'text-fg-muted hover:bg-elevated hover:text-fg',
              ].join(' ')
            }
          >
            <span>{item.label}</span>
            {item.comingSoon && (
              <span className="rounded bg-border px-1.5 py-0.5 text-[10px] uppercase tracking-wide text-fg-muted">
                Próximamente
              </span>
            )}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}
