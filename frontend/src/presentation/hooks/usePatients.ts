import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { patientService } from '@application/services/api/PatientService'
import { Patient } from '@domain/models/Patient'
import { CreatePatientDto, UpdatePatientDto, PatientFilterDto } from '@application/dtos/patient.dto'

const PATIENT_QUERY_KEY = 'patients'

export const usePatients = (filter?: PatientFilterDto) => {
  return useQuery({
    queryKey: [PATIENT_QUERY_KEY, filter],
    queryFn: () => patientService.findAll(filter),
  })
}

export const usePatient = (id: number) => {
  return useQuery({
    queryKey: [PATIENT_QUERY_KEY, id],
    queryFn: () => patientService.findById(id),
    enabled: !!id,
  })
}

export const usePatientsByOwner = (ownerId: number) => {
  return useQuery({
    queryKey: [PATIENT_QUERY_KEY, 'owner', ownerId],
    queryFn: () => patientService.findByOwnerId(ownerId),
    enabled: !!ownerId,
  })
}

export const useCreatePatient = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreatePatientDto) => patientService.save(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [PATIENT_QUERY_KEY] })
    },
  })
}

export const useUpdatePatient = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdatePatientDto }) =>
      patientService.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: [PATIENT_QUERY_KEY] })
      queryClient.invalidateQueries({ queryKey: [PATIENT_QUERY_KEY, id] })
    },
  })
}

export const useDeactivatePatient = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => patientService.deactivate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [PATIENT_QUERY_KEY] })
    },
  })
}

export const useSearchPatients = (searchTerm: string) => {
  return useQuery({
    queryKey: [PATIENT_QUERY_KEY, 'search', searchTerm],
    queryFn: () => patientService.searchByName(searchTerm),
    enabled: searchTerm.length > 2, // Only search with 3+ characters
  })
}