export const ROUTES = {
  // Public routes
  AUTH: {
    LOGIN: '/auth/login',
    FORGOT_PASSWORD: '/auth/forgot-password',
    RESET_PASSWORD: '/auth/reset-password',
  },
  
  // Protected routes
  DASHBOARD: '/',
  
  APPOINTMENTS: {
    LIST: '/appointments',
    CREATE: '/appointments/new',
    EDIT: (id: number) => `/appointments/${id}/edit`,
    VIEW: (id: number) => `/appointments/${id}`,
    CALENDAR: '/appointments/calendar',
  },
  
  PATIENTS: {
    LIST: '/patients',
    CREATE: '/patients/new',
    EDIT: (id: number) => `/patients/${id}/edit`,
    VIEW: (id: number) => `/patients/${id}`,
    MEDICAL_HISTORY: (id: number) => `/patients/${id}/history`,
  },
  
  OWNERS: {
    LIST: '/owners',
    CREATE: '/owners/new',
    EDIT: (id: number) => `/owners/${id}/edit`,
    VIEW: (id: number) => `/owners/${id}`,
  },
  
  MEDICAL_RECORDS: {
    LIST: '/medical-records',
    CREATE: '/medical-records/new',
    EDIT: (id: number) => `/medical-records/${id}/edit`,
    VIEW: (id: number) => `/medical-records/${id}`,
  },
  
  VETERINARIANS: {
    LIST: '/veterinarians',
    VIEW: (id: number) => `/veterinarians/${id}`,
  },
  
  // Admin routes
  ADMIN: {
    USERS: '/admin/users',
    USER_CREATE: '/admin/users/new',
    USER_EDIT: (id: number) => `/admin/users/${id}/edit`,
    AUDIT_LOGS: '/admin/audit-logs',
    SETTINGS: '/admin/settings',
  },
  
  // User routes
  PROFILE: '/profile',
  SETTINGS: '/settings',
} as const

export type Route = typeof ROUTES[keyof typeof ROUTES]