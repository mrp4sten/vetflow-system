import type { Patient, Species, Gender } from '@domain/models/Patient'

export interface RegisterPatientCommand {
  name: string
  species: Species
  breed?: string
  gender: Gender
  birthDate?: string
  weight?: number
  color?: string
  microchipNumber?: string
  ownerId: number
}

export interface RegisterPatientUseCase {
  execute(command: RegisterPatientCommand): Promise<Patient>
  validateMicrochipNumber(microchipNumber: string): Promise<boolean>
  checkDuplicateName(name: string, ownerId: number): Promise<boolean>
}