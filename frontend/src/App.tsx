import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from '@/components/layout/AppLayout'
import { ComingSoonPage } from '@/components/ComingSoonPage'
import { DashboardPage } from '@/features/dashboard/DashboardPage'
import { DiagnosticDetailPage } from '@/features/diagnostics/DiagnosticDetailPage'
import { DiagnosticsPage } from '@/features/diagnostics/DiagnosticsPage'
import { VehiclesPage } from '@/features/vehicles/VehiclesPage'

export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="diagnostics" element={<DiagnosticsPage />} />
        <Route path="diagnostics/:id" element={<DiagnosticDetailPage />} />
        <Route path="vehicles" element={<VehiclesPage />} />
        <Route path="knowledge" element={<ComingSoonPage title="Biblioteca técnica" />} />
        <Route path="devices" element={<ComingSoonPage title="Dispositivos" />} />
        <Route path="reports" element={<ComingSoonPage title="Informes" />} />
        <Route path="settings" element={<ComingSoonPage title="Configuración" />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Route>
    </Routes>
  )
}
