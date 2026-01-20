import type { Appointment, AppointmentStatus } from '@domain/models/Appointment'

export interface AppointmentFilter {
  patientId?: number
  veterinarianId?: number
  status?: AppointmentStatus
  dateFrom?: string
  dateTo?: string
  type?: string
}

export interface AppointmentRepository {
  findById(id: number): Promise<Appointment | null>
  findAll(filter?: AppointmentFilter): Promise<Appointment[]>
  findByDateRange(startDate: string, endDate: string): Promise<Appointment[]>
  save(appointment: Partial<Appointment>): Promise<Appointment>
  update(id: number, appointment: Partial<Appointment>): Promise<Appointment>
  delete(id: number): Promise<void>
  updateStatus(id: number, status: AppointmentStatus): Promise<Appointment>
}