import type { AppointmentStatus, AppointmentType, AppointmentPriority } from '@domain/models/Appointment'

export const APPOINTMENT_STATUS_DISPLAY: Record<AppointmentStatus, string> = {
  scheduled: 'Scheduled',
  confirmed: 'Confirmed',
  in_progress: 'In Progress',
  completed: 'Completed',
  cancelled: 'Cancelled',
  no_show: 'No Show',
}

export const APPOINTMENT_STATUS_COLORS: Record<AppointmentStatus, string> = {
  scheduled: 'blue',
  confirmed: 'green',
  in_progress: 'yellow',
  completed: 'gray',
  cancelled: 'red',
  no_show: 'orange',
}

export const APPOINTMENT_TYPE_DISPLAY: Record<AppointmentType, string> = {
  consultation: 'Consultation',
  vaccination: 'Vaccination',
  surgery: 'Surgery',
  grooming: 'Grooming',
  emergency: 'Emergency',
  checkup: 'Check-up',
  other: 'Other',
}

export const APPOINTMENT_TYPE_ICONS: Record<AppointmentType, string> = {
  consultation: 'MessageSquare',
  vaccination: 'Syringe',
  surgery: 'Heart',
  grooming: 'Sparkles',
  emergency: 'AlertCircle',
  checkup: 'Stethoscope',
  other: 'Calendar',
}

export const APPOINTMENT_PRIORITY_DISPLAY: Record<AppointmentPriority, string> = {
  low: 'Low',
  medium: 'Medium',
  high: 'High',
  emergency: 'Emergency',
}

export const APPOINTMENT_PRIORITY_COLORS: Record<AppointmentPriority, string> = {
  low: 'gray',
  medium: 'blue',
  high: 'orange',
  emergency: 'red',
}

export const DEFAULT_APPOINTMENT_DURATION = 30 // minutes

export const APPOINTMENT_DURATIONS = [
  { value: 15, label: '15 minutes' },
  { value: 30, label: '30 minutes' },
  { value: 45, label: '45 minutes' },
  { value: 60, label: '1 hour' },
  { value: 90, label: '1.5 hours' },
  { value: 120, label: '2 hours' },
]