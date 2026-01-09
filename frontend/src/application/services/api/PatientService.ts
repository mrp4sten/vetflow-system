import { BaseApiService, PaginatedResponse } from './BaseApiService'
import { Patient } from '@domain/models/Patient'
import { CreatePatientDto, UpdatePatientDto, PatientFilterDto } from '@application/dtos/patient.dto'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'
import { PatientRepository } from '@domain/ports/repositories/PatientRepository'

export class PatientService extends BaseApiService implements PatientRepository {
  async findById(id: number): Promise<Patient | null> {
    try {
      return await this.get<Patient>(API_ENDPOINTS.PATIENTS.BY_ID(id))
    } catch (error: any) {
      if (error.status === 404) return null
      throw error
    }
  }

  async findAll(filter?: PatientFilterDto): Promise<Patient[]> {
    const queryString = filter ? this.buildQueryString(filter) : ''
    const response = await this.get<PaginatedResponse<Patient>>(
      `${API_ENDPOINTS.PATIENTS.BASE}${queryString}`
    )
    return response.content
  }

  async findByOwnerId(ownerId: number): Promise<Patient[]> {
    return await this.get<Patient[]>(API_ENDPOINTS.PATIENTS.BY_OWNER(ownerId))
  }

  async findByMicrochipNumber(microchipNumber: string): Promise<Patient | null> {
    try {
      const queryString = this.buildQueryString({ microchipNumber })
      const response = await this.get<Patient[]>(
        `${API_ENDPOINTS.PATIENTS.BASE}${queryString}`
      )
      return response.length > 0 ? response[0] : null
    } catch (error) {
      return null
    }
  }

  async save(patient: CreatePatientDto): Promise<Patient> {
    return await this.post<Patient, CreatePatientDto>(
      API_ENDPOINTS.PATIENTS.BASE,
      patient
    )
  }

  async update(id: number, patient: UpdatePatientDto): Promise<Patient> {
    return await this.put<Patient, UpdatePatientDto>(
      API_ENDPOINTS.PATIENTS.BY_ID(id),
      patient
    )
  }

  async delete(id: number): Promise<void> {
    return await this.delete<void>(API_ENDPOINTS.PATIENTS.BY_ID(id))
  }

  async deactivate(id: number): Promise<void> {
    return await this.patch<void, { isActive: boolean }>(
      API_ENDPOINTS.PATIENTS.BY_ID(id),
      { isActive: false }
    )
  }

  async searchByName(name: string): Promise<Patient[]> {
    const filter: PatientFilterDto = { searchTerm: name }
    return await this.findAll(filter)
  }
}

// Export singleton instance
export const patientService = new PatientService()