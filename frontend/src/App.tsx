import { Routes, Route, Navigate } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'
import { ProtectedRoute } from '@presentation/routes/ProtectedRoute'
import { MainLayout } from '@presentation/components/layout/MainLayout'
import { LoginPage } from '@presentation/pages/auth/LoginPage'
import { DashboardPage } from '@presentation/pages/dashboard/DashboardPage'

function App() {
  return (
    <Routes>
      {/* Public routes */}
      <Route path={ROUTES.AUTH.LOGIN} element={<LoginPage />} />
      
      {/* Protected routes */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
          
          {/* Appointment routes - TODO */}
          <Route path={ROUTES.APPOINTMENTS.LIST} element={<div>Appointments List</div>} />
          
          {/* Patient routes - TODO */}
          <Route path={ROUTES.PATIENTS.LIST} element={<div>Patients List</div>} />
          
          {/* Owner routes - TODO */}
          <Route path={ROUTES.OWNERS.LIST} element={<div>Owners List</div>} />
          
          {/* Medical Records routes - TODO */}
          <Route path={ROUTES.MEDICAL_RECORDS.LIST} element={<div>Medical Records</div>} />
          
          {/* Admin routes */}
          <Route 
            element={<ProtectedRoute requiredRoles={['admin']} />}
          >
            <Route path={ROUTES.ADMIN.USERS} element={<div>User Management</div>} />
            <Route path={ROUTES.ADMIN.AUDIT_LOGS} element={<div>Audit Logs</div>} />
          </Route>
          
          {/* User routes - TODO */}
          <Route path={ROUTES.SETTINGS} element={<div>Settings</div>} />
        </Route>
      </Route>
      
      {/* Fallback route */}
      <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />
    </Routes>
  )
}

export default App