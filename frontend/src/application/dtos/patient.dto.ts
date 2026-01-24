import type { Species } from '@domain/models/Patient'

export interface CreatePatientDto {
  name: string
  species: Species
  breed?: string
  birthDate?: string
  weight?: number
  ownerId: number
}

export interface UpdatePatientDto {
  name?: string
  species?: Species
  breed?: string
  birthDate?: string
  weight?: number
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