export interface CreateOwnerDto {
  name: string
  phone: string
  email: string
  address: string
}

export interface UpdateOwnerDto {
  name?: string
  phone?: string
  email?: string
  address?: string
}

export interface OwnerFilterDto {
  searchTerm?: string // search in name, email, phone
  city?: string
  state?: string
  page?: number
  size?: number
  sort?: string
}