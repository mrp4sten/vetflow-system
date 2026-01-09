import { Owner } from './Owner'

export type Species = 'dog' | 'cat' | 'bird' | 'rabbit' | 'other'
export type Gender = 'male' | 'female' | 'unknown'

export interface Patient {
  id: number
  name: string
  species: Species
  breed?: string
  gender: Gender
  birthDate?: string
  weight?: number // in kg
  color?: string
  microchipNumber?: string
  ownerId: number
  owner?: Owner
  isActive: boolean
  createdAt: string
  updatedAt: string
  
  // Computed properties
  get age(): number | undefined
  get weightInLbs(): number | undefined
}