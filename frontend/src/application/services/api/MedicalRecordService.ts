import { BaseApiService } from './BaseApiService'
import type { PaginatedResponse } from './BaseApiService'
import type { MedicalRecord } from '@domain/models/MedicalRecord'
import type { CreateMedicalRecordDto, UpdateMedicalRecordDto, MedicalRecordFilterDto } from '@application/dtos/medical-record.dto'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'

export class MedicalRecordService extends BaseApiService {
  async findById(id: number): Promise<MedicalRecord | null> {
    try {
      return await this.get<MedicalRecord>(API_ENDPOINTS.MEDICAL_RECORDS.BY_ID(id))
    } catch (error: any) {
      if (error.status === 404) return null
      throw error
    }
  }

  async findAll(filter?: MedicalRecordFilterDto): Promise<MedicalRecord[]> {
    const queryString = filter ? this.buildQueryString(filter) : ''
    const response = await this.get<MedicalRecord[]>(
      `${API_ENDPOINTS.MEDICAL_RECORDS.BASE}${queryString}`
    )
    return response
  }

  async findByPatientId(patientId: number): Promise<MedicalRecord[]> {
    return await this.get<MedicalRecord[]>(
      API_ENDPOINTS.MEDICAL_RECORDS.BY_PATIENT(patientId)
    )
  }

  async findByAppointmentId(appointmentId: number): Promise<MedicalRecord[]> {
    return await this.get<MedicalRecord[]>(
      API_ENDPOINTS.MEDICAL_RECORDS.BY_APPOINTMENT(appointmentId)
    )
  }

  async save(record: CreateMedicalRecordDto): Promise<MedicalRecord> {
    return await this.post<MedicalRecord, CreateMedicalRecordDto>(
      API_ENDPOINTS.MEDICAL_RECORDS.BASE,
      record
    )
  }

  async update(id: number, record: UpdateMedicalRecordDto): Promise<MedicalRecord> {
    return await this.put<MedicalRecord, UpdateMedicalRecordDto>(
      API_ENDPOINTS.MEDICAL_RECORDS.BY_ID(id),
      record
    )
  }

  async deleteRecord(id: number): Promise<void> {
    return await this.delete<void>(API_ENDPOINTS.MEDICAL_RECORDS.BY_ID(id))
  }
}

// Export singleton instance
export const medicalRecordService = new MedicalRecordService()
