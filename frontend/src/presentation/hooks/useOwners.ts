import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ownerService } from '@application/services/api/OwnerService'
import type { Owner } from '@domain/models/Owner'
import { CreateOwnerDto, UpdateOwnerDto, OwnerFilterDto } from '@application/dtos/owner.dto'

const OWNER_QUERY_KEY = 'owners'

export const useOwners = (filter?: OwnerFilterDto) => {
  return useQuery({
    queryKey: [OWNER_QUERY_KEY, filter],
    queryFn: () => ownerService.findAll(filter),
  })
}

export const useOwner = (id: number) => {
  return useQuery({
    queryKey: [OWNER_QUERY_KEY, id],
    queryFn: () => ownerService.findById(id),
    enabled: !!id,
  })
}

export const useCreateOwner = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateOwnerDto) => ownerService.save(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [OWNER_QUERY_KEY] })
    },
  })
}

export const useUpdateOwner = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateOwnerDto }) =>
      ownerService.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: [OWNER_QUERY_KEY] })
      queryClient.invalidateQueries({ queryKey: [OWNER_QUERY_KEY, id] })
    },
  })
}

export const useDeleteOwner = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => ownerService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [OWNER_QUERY_KEY] })
    },
  })
}

export const useSearchOwners = (searchTerm: string) => {
  return useQuery({
    queryKey: [OWNER_QUERY_KEY, 'search', searchTerm],
    queryFn: () => ownerService.search(searchTerm),
    enabled: searchTerm.length > 2,
  })
}