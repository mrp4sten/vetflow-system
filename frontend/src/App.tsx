import { Routes, Route, Navigate } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'
import { ProtectedRoute } from '@presentation/routes/ProtectedRoute'
import { MainLayout } from '@presentation/components/layout/MainLayout'
import { ErrorBoundary } from '@presentation/components/shared/ErrorBoundary/ErrorBoundary'
import { LoginPage } from '@presentation/pages/auth/LoginPage'
import { DashboardPage } from '@presentation/pages/dashboard/DashboardPage'
import { AppointmentsPage } from '@presentation/pages/appointments/AppointmentsPage'
import { CreateAppointmentPage } from '@presentation/pages/appointments/CreateAppointmentPage'
import { ViewAppointmentPage } from '@presentation/pages/appointments/ViewAppointmentPage'
import { EditAppointmentPage } from '@presentation/pages/appointments/EditAppointmentPage'
import { AppointmentCalendarPage } from '@presentation/pages/appointments/AppointmentCalendarPage'
import { PatientsPage } from '@presentation/pages/patients/PatientsPage'
import { CreatePatientPage } from '@presentation/pages/patients/CreatePatientPage'
import { ViewPatientPage } from '@presentation/pages/patients/ViewPatientPage'
import { EditPatientPage } from '@presentation/pages/patients/EditPatientPage'
import { OwnersPage } from '@presentation/pages/owners/OwnersPage'
import { CreateOwnerPage } from '@presentation/pages/owners/CreateOwnerPage'
import { ViewOwnerPage } from '@presentation/pages/owners/ViewOwnerPage'
import { EditOwnerPage } from '@presentation/pages/owners/EditOwnerPage'
import { MedicalRecordsPage } from '@presentation/pages/medical-records/MedicalRecordsPage'
import { CreateMedicalRecordPage } from '@presentation/pages/medical-records/CreateMedicalRecordPage'
import { ViewMedicalRecordPage } from '@presentation/pages/medical-records/ViewMedicalRecordPage'
import { EditMedicalRecordPage } from '@presentation/pages/medical-records/EditMedicalRecordPage'
import { SettingsPage } from '@presentation/pages/settings/SettingsPage'

function App() {
  return (
    <ErrorBoundary>
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
          <Route path="/appointments/:id/edit" element={<EditAppointmentPage />} />
          <Route path={ROUTES.APPOINTMENTS.CALENDAR} element={<AppointmentCalendarPage />} />
          
          {/* Patient routes */}
          <Route path={ROUTES.PATIENTS.LIST} element={<PatientsPage />} />
          <Route path={ROUTES.PATIENTS.CREATE} element={<CreatePatientPage />} />
          <Route path="/patients/:id" element={<ViewPatientPage />} />
          <Route path="/patients/:id/edit" element={<EditPatientPage />} />
          
          {/* Owner routes */}
          <Route path={ROUTES.OWNERS.LIST} element={<OwnersPage />} />
          <Route path={ROUTES.OWNERS.CREATE} element={<CreateOwnerPage />} />
          <Route path="/owners/:id" element={<ViewOwnerPage />} />
          <Route path="/owners/:id/edit" element={<EditOwnerPage />} />
          
          {/* Medical Records routes */}
          <Route path={ROUTES.MEDICAL_RECORDS.LIST} element={<MedicalRecordsPage />} />
          <Route path={ROUTES.MEDICAL_RECORDS.CREATE} element={<CreateMedicalRecordPage />} />
          <Route path="/medical-records/:id" element={<ViewMedicalRecordPage />} />
          <Route path="/medical-records/:id/edit" element={<EditMedicalRecordPage />} />
          
          {/* Admin routes */}
          <Route 
            element={<ProtectedRoute requiredRoles={['admin']} />}
          >
            <Route path={ROUTES.ADMIN.USERS} element={<div>User Management</div>} />
            <Route path={ROUTES.ADMIN.AUDIT_LOGS} element={<div>Audit Logs</div>} />
          </Route>
          
          {/* User routes */}
          <Route path={ROUTES.SETTINGS} element={<SettingsPage />} />
        </Route>
      </Route>
      
      {/* Fallback route */}
      <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />
    </Routes>
    </ErrorBoundary>
  )
}

export default App