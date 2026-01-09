export interface CreateOwnerDto {
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
  address?: string
  city?: string
  state?: string
  zipCode?: string
}

export interface UpdateOwnerDto {
  firstName?: string
  lastName?: string
  email?: string
  phoneNumber?: string
  address?: string
  city?: string
  state?: string
  zipCode?: string
}

export interface OwnerFilterDto {
  searchTerm?: string // search in name, email, phone
  city?: string
  state?: string
  page?: number
  size?: number
  sort?: string
}