import { useQuery } from '@tanstack/react-query'
import { api } from '@infrastructure/api/axios-client'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'
import { SystemUser } from '@domain/models/SystemUser'

const VETERINARIAN_QUERY_KEY = 'veterinarians'

export const useVeterinarians = () => {
  return useQuery({
    queryKey: [VETERINARIAN_QUERY_KEY],
    queryFn: async () => {
      const response = await api.get<SystemUser[]>(API_ENDPOINTS.USERS.VETERINARIANS)
      return response.data
    },
  })
}