import { Routes, Route, Navigate } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'
import { ProtectedRoute } from '@presentation/routes/ProtectedRoute'
import { MainLayout } from '@presentation/components/layout/MainLayout'
import { LoginPage } from '@presentation/pages/auth/LoginPage'
import { DashboardPage } from '@presentation/pages/dashboard/DashboardPage'
import { AppointmentsPage } from '@presentation/pages/appointments/AppointmentsPage'
import { CreateAppointmentPage } from '@presentation/pages/appointments/CreateAppointmentPage'
import { ViewAppointmentPage } from '@presentation/pages/appointments/ViewAppointmentPage'

function App() {
  return (
    <Routes>
      {/* Public routes */}
      <Route path={ROUTES.AUTH.LOGIN} element={<LoginPage />} />
      
      {/* Protected routes */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
          
          {/* Appointment routes */}
          <Route path={ROUTES.APPOINTMENTS.LIST} element={<AppointmentsPage />} />
          <Route path={ROUTES.APPOINTMENTS.CREATE} element={<CreateAppointmentPage />} />
          <Route path="/appointments/:id" element={<ViewAppointmentPage />} />
          <Route path="/appointments/:id/edit" element={<div>Edit Appointment</div>} />
          <Route path={ROUTES.APPOINTMENTS.CALENDAR} element={<div>Calendar View</div>} />
          
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