import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { medicalRecordService } from '@application/services/api/MedicalRecordService'
import type { MedicalRecord } from '@domain/models/MedicalRecord'
import type { CreateMedicalRecordDto, UpdateMedicalRecordDto, MedicalRecordFilterDto } from '@application/dtos/medical-record.dto'

const MEDICAL_RECORD_QUERY_KEY = 'medical-records'

export const useMedicalRecords = (filter?: MedicalRecordFilterDto) => {
  return useQuery({
    queryKey: [MEDICAL_RECORD_QUERY_KEY, filter],
    queryFn: () => medicalRecordService.findAll(filter),
  })
}

export const useMedicalRecord = (id: number) => {
  return useQuery({
    queryKey: [MEDICAL_RECORD_QUERY_KEY, id],
    queryFn: () => medicalRecordService.findById(id),
    enabled: !!id,
  })
}

export const useMedicalRecordsByPatient = (patientId: number) => {
  return useQuery({
    queryKey: [MEDICAL_RECORD_QUERY_KEY, 'patient', patientId],
    queryFn: () => medicalRecordService.findByPatientId(patientId),
    enabled: !!patientId,
  })
}

export const useMedicalRecordsByAppointment = (appointmentId: number) => {
  return useQuery({
    queryKey: [MEDICAL_RECORD_QUERY_KEY, 'appointment', appointmentId],
    queryFn: () => medicalRecordService.findByAppointmentId(appointmentId),
    enabled: !!appointmentId,
  })
}

export const useCreateMedicalRecord = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateMedicalRecordDto) => medicalRecordService.save(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [MEDICAL_RECORD_QUERY_KEY] })
    },
  })
}

export const useUpdateMedicalRecord = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateMedicalRecordDto }) =>
      medicalRecordService.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: [MEDICAL_RECORD_QUERY_KEY] })
      queryClient.invalidateQueries({ queryKey: [MEDICAL_RECORD_QUERY_KEY, id] })
    },
  })
}

export const useDeleteMedicalRecord = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => medicalRecordService.deleteRecord(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [MEDICAL_RECORD_QUERY_KEY] })
    },
  })
}
