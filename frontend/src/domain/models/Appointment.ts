import { Patient } from './Patient'
import { SystemUser } from './SystemUser'

export type AppointmentStatus = 'scheduled' | 'confirmed' | 'in_progress' | 'completed' | 'cancelled' | 'no_show'
export type AppointmentType = 'consultation' | 'vaccination' | 'surgery' | 'grooming' | 'emergency' | 'checkup' | 'other'
export type AppointmentPriority = 'low' | 'medium' | 'high' | 'emergency'

export interface Appointment {
  id: number
  patientId: number
  patient?: Patient
  veterinarianId: number
  veterinarian?: SystemUser
  scheduledAt: string
  duration: number // in minutes
  type: AppointmentType
  status: AppointmentStatus
  priority: AppointmentPriority
  reason: string
  notes?: string
  createdAt: string
  updatedAt: string
  createdBy?: SystemUser
  
  // Computed properties
  get endTime(): string
  get isOverdue(): boolean
  get canReschedule(): boolean
  get canCancel(): boolean
}