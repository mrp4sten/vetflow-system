export interface Owner {
  id: number
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
  address?: string
  city?: string
  state?: string
  zipCode?: string
  createdAt: string
  updatedAt: string
  
  // Computed property
  get fullName(): string
}