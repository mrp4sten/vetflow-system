import { AppointmentStatus, AppointmentType, AppointmentPriority } from '@domain/models/Appointment'

export interface CreateAppointmentDto {
  patientId: number
  veterinarianId: number
  scheduledAt: string
  duration: number
  type: AppointmentType
  priority: AppointmentPriority
  reason: string
  notes?: string
}

export interface UpdateAppointmentDto {
  veterinarianId?: number
  scheduledAt?: string
  duration?: number
  type?: AppointmentType
  priority?: AppointmentPriority
  reason?: string
  notes?: string
  status?: AppointmentStatus
}

export interface AppointmentFilterDto {
  patientId?: number
  veterinarianId?: number
  status?: AppointmentStatus
  dateFrom?: string
  dateTo?: string
  type?: AppointmentType
  priority?: AppointmentPriority
}