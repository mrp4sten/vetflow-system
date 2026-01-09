export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    LOGIN: '/auth/token',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    CURRENT_USER: '/auth/me',
  },
  
  // Owners
  OWNERS: {
    BASE: '/owners',
    BY_ID: (id: number) => `/owners/${id}`,
  },
  
  // Patients
  PATIENTS: {
    BASE: '/patients',
    BY_ID: (id: number) => `/patients/${id}`,
    BY_OWNER: (ownerId: number) => `/owners/${ownerId}/patients`,
  },
  
  // Appointments
  APPOINTMENTS: {
    BASE: '/appointments',
    BY_ID: (id: number) => `/appointments/${id}`,
    BY_DATE: '/appointments/by-date',
    BY_PATIENT: (patientId: number) => `/patients/${patientId}/appointments`,
    AVAILABILITY: '/appointments/availability',
  },
  
  // Medical Records
  MEDICAL_RECORDS: {
    BASE: '/medical-records',
    BY_ID: (id: number) => `/medical-records/${id}`,
    BY_PATIENT: (patientId: number) => `/patients/${patientId}/medical-records`,
    BY_APPOINTMENT: (appointmentId: number) => `/appointments/${appointmentId}/medical-records`,
  },
  
  // System Users
  USERS: {
    BASE: '/users',
    BY_ID: (id: number) => `/users/${id}`,
    VETERINARIANS: '/users/veterinarians',
  },
  
  // Audit Logs
  AUDIT_LOGS: {
    BASE: '/audit-logs',
    BY_ENTITY: (entityType: string, entityId: number) => `/audit-logs/${entityType}/${entityId}`,
  },
} as const