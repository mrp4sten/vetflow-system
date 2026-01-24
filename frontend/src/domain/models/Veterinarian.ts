/**
 * Veterinarian domain model
 * Represents a veterinarian (system user with role 'veterinarian')
 */
export interface Veterinarian {
  id: number
  username: string
  email: string
  isActive: boolean
  createdAt: string
  lastLogin?: string
}
