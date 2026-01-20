import type { UserRole } from '@domain/models/SystemUser'

export const ROLE_DISPLAY_NAMES: Record<UserRole, string> = {
  admin: 'Administrator',
  veterinarian: 'Veterinarian',
  assistant: 'Assistant',
}

export const ROLE_PERMISSIONS = {
  admin: [
    'manage_users',
    'view_audit_logs',
    'manage_system_settings',
    'view_all_records',
    'delete_records',
  ],
  veterinarian: [
    'create_appointments',
    'edit_appointments',
    'create_medical_records',
    'edit_medical_records',
    'view_patient_records',
    'prescribe_medications',
  ],
  assistant: [
    'view_appointments',
    'create_patients',
    'edit_patients',
    'create_owners',
    'edit_owners',
    'check_in_patients',
  ],
} as const

export const hasPermission = (role: UserRole, permission: string): boolean => {
  return ROLE_PERMISSIONS[role]?.includes(permission as any) ?? false
}