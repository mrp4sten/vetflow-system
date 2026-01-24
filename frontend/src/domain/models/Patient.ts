import { Owner } from './Owner'

export type Species = 'dog' | 'cat'

export interface Patient {
  id: number
  name: string
  species: Species
  breed?: string
  birthDate?: string
  weight?: number // in kg
  ownerId: number
  owner?: Owner
  isActive: boolean
  createdAt: string
  updatedAt: string
  
  // Computed properties
  get age(): number | undefined
  get weightInLbs(): number | undefined
}