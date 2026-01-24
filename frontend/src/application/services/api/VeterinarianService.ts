import { apiClient } from '@/infrastructure/http/apiClient'
import type { Veterinarian } from '@/domain/models/Veterinarian'

/**
 * Veterinarian Service
 * Handles all veterinarian-related API calls
 */
export class VeterinarianService {
  private static readonly BASE_URL = '/veterinarians'

  /**
   * Get all veterinarians
   * @param includeInactive - Whether to include inactive veterinarians
   */
  static async getAll(includeInactive = false): Promise<Veterinarian[]> {
    const params = includeInactive ? { includeInactive: 'true' } : {}
    const response = await apiClient.get<Veterinarian[]>(this.BASE_URL, { params })
    return response.data
  }

  /**
   * Get a veterinarian by ID
   */
  static async getById(id: number): Promise<Veterinarian> {
    const response = await apiClient.get<Veterinarian>(`${this.BASE_URL}/${id}`)
    return response.data
  }

  /**
   * Get all active veterinarians (convenience method)
   */
  static async getActive(): Promise<Veterinarian[]> {
    return this.getAll(false)
  }
}
