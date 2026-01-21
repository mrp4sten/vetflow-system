import type { Appointment } from '@domain/models/Appointment'
import { addDurationToDate, formatDate, isAfter } from '@infrastructure/utils/date-utils'
import { DATE_FORMATS } from '@infrastructure/utils/date-utils'
import { PatientMapper } from './patient.mapper'

export class AppointmentMapper {
  static toDomain(dto: any): Appointment {
    return {
      ...dto,
      scheduledAt: dto.appointmentDate || dto.scheduledAt, // Map backend field to frontend field
      duration: dto.duration || 60, // Default duration if not provided
      patient: dto.patient ? PatientMapper.toDomain(dto.patient) : undefined,
      get endTime() {
        const startDate = new Date(this.scheduledAt)
        const endDate = addDurationToDate(startDate, this.duration)
        return formatDate(endDate, DATE_FORMATS.DATETIME)
      },
      get isOverdue() {
        return this.status === 'scheduled' && isAfter(new Date(), new Date(this.scheduledAt))
      },
      get canReschedule() {
        return ['scheduled', 'confirmed'].includes(this.status)
      },
      get canCancel() {
        return ['scheduled', 'confirmed'].includes(this.status)
      }
    }
  }

  static toDomainList(dtos: any[]): Appointment[] {
    return dtos.map(dto => AppointmentMapper.toDomain(dto))
  }
}