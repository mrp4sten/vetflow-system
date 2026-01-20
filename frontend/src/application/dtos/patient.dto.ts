import type { Species, Gender } from '@domain/models/Patient'

export interface CreatePatientDto {
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

export interface UpdatePatientDto {
  name?: string
  species?: Species
  breed?: string
  gender?: Gender
  birthDate?: string
  weight?: number
  color?: string
  microchipNumber?: string
  ownerId?: number
  isActive?: boolean
}

export interface PatientFilterDto {
  ownerId?: number
  species?: Species
  isActive?: boolean
  searchTerm?: string
  page?: number
  size?: number
  sort?: string
}