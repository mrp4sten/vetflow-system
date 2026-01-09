export type UserRole = 'admin' | 'veterinarian' | 'assistant'

export interface SystemUser {
  id: number
  username: string
  email: string
  role: UserRole
  isActive: boolean
  firstName?: string
  lastName?: string
  createdAt: string
  updatedAt: string
}