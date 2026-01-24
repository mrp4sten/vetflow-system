import { useQuery } from '@tanstack/react-query'
import { VeterinarianService } from '@/application/services/api/VeterinarianService'
import type { Veterinarian } from '@/domain/models/Veterinarian'

/**
 * Hook to fetch all veterinarians
 */
export const useVeterinarians = (includeInactive = false) => {
  return useQuery<Veterinarian[], Error>({
    queryKey: ['veterinarians', includeInactive],
    queryFn: () => VeterinarianService.getAll(includeInactive),
    staleTime: 1000 * 60 * 5, // 5 minutes
  })
}

/**
 * Hook to fetch a single veterinarian by ID
 */
export const useVeterinarian = (id: number) => {
  return useQuery<Veterinarian, Error>({
    queryKey: ['veterinarian', id],
    queryFn: () => VeterinarianService.getById(id),
    enabled: !!id,
    staleTime: 1000 * 60 * 5, // 5 minutes
  })
}

/**
 * Hook to fetch only active veterinarians
 */
export const useActiveVeterinarians = () => {
  return useQuery<Veterinarian[], Error>({
    queryKey: ['veterinarians', 'active'],
    queryFn: () => VeterinarianService.getActive(),
    staleTime: 1000 * 60 * 5, // 5 minutes
  })
}