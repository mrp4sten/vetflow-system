import type { Patient } from '@domain/models/Patient'

export interface PatientFilter {
  ownerId?: number
  species?: string
  isActive?: boolean
  searchTerm?: string // for name, microchip
}

export interface PatientRepository {
  findById(id: number): Promise<Patient | null>
  findAll(filter?: PatientFilter): Promise<Patient[]>
  findByOwnerId(ownerId: number): Promise<Patient[]>
  findByMicrochipNumber(microchipNumber: string): Promise<Patient | null>
  save(patient: Partial<Patient>): Promise<Patient>
  update(id: number, patient: Partial<Patient>): Promise<Patient>
  delete(id: number): Promise<void>
  deactivate(id: number): Promise<void>
}