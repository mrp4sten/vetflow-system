import { BaseApiService, PaginatedResponse } from './BaseApiService'
import type { Owner } from '@domain/models/Owner'
import { CreateOwnerDto, UpdateOwnerDto, OwnerFilterDto } from '@application/dtos/owner.dto'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'

export class OwnerService extends BaseApiService {
  async findById(id: number): Promise<Owner | null> {
    try {
      return await this.get<Owner>(API_ENDPOINTS.OWNERS.BY_ID(id))
    } catch (error: any) {
      if (error.status === 404) return null
      throw error
    }
  }

  async findAll(filter?: OwnerFilterDto): Promise<Owner[]> {
    const queryString = filter ? this.buildQueryString(filter) : ''
    const response = await this.get<PaginatedResponse<Owner>>(
      `${API_ENDPOINTS.OWNERS.BASE}${queryString}`
    )
    return response.content
  }

  async save(owner: CreateOwnerDto): Promise<Owner> {
    return await this.post<Owner, CreateOwnerDto>(
      API_ENDPOINTS.OWNERS.BASE,
      owner
    )
  }

  async update(id: number, owner: UpdateOwnerDto): Promise<Owner> {
    return await this.put<Owner, UpdateOwnerDto>(
      API_ENDPOINTS.OWNERS.BY_ID(id),
      owner
    )
  }

  async delete(id: number): Promise<void> {
    return await this.delete<void>(API_ENDPOINTS.OWNERS.BY_ID(id))
  }

  async search(searchTerm: string): Promise<Owner[]> {
    const filter: OwnerFilterDto = { searchTerm }
    return await this.findAll(filter)
  }
}

// Export singleton instance
export const ownerService = new OwnerService()