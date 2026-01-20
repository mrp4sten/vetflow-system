import { BaseApiService, PaginatedResponse } from './BaseApiService'
import type { Appointment } from '@domain/models/Appointment'
import { CreateAppointmentDto, UpdateAppointmentDto, AppointmentFilterDto } from '@application/dtos/appointment.dto'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'
import { AppointmentRepository } from '@domain/ports/repositories/AppointmentRepository'
import { AppointmentMapper } from '@application/mappers/appointment.mapper'

export class AppointmentService extends BaseApiService implements AppointmentRepository {
  async findById(id: number): Promise<Appointment | null> {
    try {
      const dto = await this.get<any>(API_ENDPOINTS.APPOINTMENTS.BY_ID(id))
      return AppointmentMapper.toDomain(dto)
    } catch (error: any) {
      if (error.status === 404) return null
      throw error
    }
  }

  async findAll(filter?: AppointmentFilterDto): Promise<Appointment[]> {
    const queryString = filter ? this.buildQueryString(filter) : ''
    const response = await this.get<PaginatedResponse<any>>(
      `${API_ENDPOINTS.APPOINTMENTS.BASE}${queryString}`
    )
    return AppointmentMapper.toDomainList(response.content)
  }

  async findByDateRange(startDate: string, endDate: string): Promise<Appointment[]> {
    const queryString = this.buildQueryString({ startDate, endDate })
    return await this.get<Appointment[]>(
      `${API_ENDPOINTS.APPOINTMENTS.BY_DATE}${queryString}`
    )
  }

  async save(appointment: CreateAppointmentDto): Promise<Appointment> {
    return await this.post<Appointment, CreateAppointmentDto>(
      API_ENDPOINTS.APPOINTMENTS.BASE,
      appointment
    )
  }

  async update(id: number, appointment: UpdateAppointmentDto): Promise<Appointment> {
    return await this.put<Appointment, UpdateAppointmentDto>(
      API_ENDPOINTS.APPOINTMENTS.BY_ID(id),
      appointment
    )
  }

  async delete(id: number): Promise<void> {
    return await this.delete<void>(API_ENDPOINTS.APPOINTMENTS.BY_ID(id))
  }

  async updateStatus(id: number, status: string): Promise<Appointment> {
    return await this.patch<Appointment, { status: string }>(
      `${API_ENDPOINTS.APPOINTMENTS.BY_ID(id)}/status`,
      { status }
    )
  }

  async checkAvailability(
    veterinarianId: number,
    scheduledAt: string,
    duration: number
  ): Promise<boolean> {
    const queryString = this.buildQueryString({ veterinarianId, scheduledAt, duration })
    const response = await this.get<{ available: boolean }>(
      `${API_ENDPOINTS.APPOINTMENTS.AVAILABILITY}${queryString}`
    )
    return response.available
  }

  async getByPatient(patientId: number): Promise<Appointment[]> {
    return await this.get<Appointment[]>(
      API_ENDPOINTS.APPOINTMENTS.BY_PATIENT(patientId)
    )
  }
}

// Export singleton instance
export const appointmentService = new AppointmentService()