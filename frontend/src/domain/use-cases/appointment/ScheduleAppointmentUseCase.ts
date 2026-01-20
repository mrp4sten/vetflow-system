import type { Appointment, AppointmentType, AppointmentPriority } from '@domain/models/Appointment'

export interface ScheduleAppointmentCommand {
  patientId: number
  veterinarianId: number
  scheduledAt: string
  duration: number
  type: AppointmentType
  priority: AppointmentPriority
  reason: string
  notes?: string
}

export interface ScheduleAppointmentUseCase {
  execute(command: ScheduleAppointmentCommand): Promise<Appointment>
  checkAvailability(veterinarianId: number, scheduledAt: string, duration: number): Promise<boolean>
  getConflictingAppointments(veterinarianId: number, scheduledAt: string, duration: number): Promise<Appointment[]>
}