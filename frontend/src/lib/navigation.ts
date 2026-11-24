export interface NavItem {
  path: string
  label: string
  comingSoon?: boolean
}

export const NAV_ITEMS: NavItem[] = [
  { path: '/dashboard', label: 'Dashboard' },
  { path: '/diagnostics', label: 'Diagnósticos' },
  { path: '/vehicles', label: 'Vehículos' },
  { path: '/knowledge', label: 'Biblioteca técnica' },
  { path: '/devices', label: 'Dispositivos', comingSoon: true },
  { path: '/reports', label: 'Informes' },
  { path: '/settings', label: 'Configuración' },
]
