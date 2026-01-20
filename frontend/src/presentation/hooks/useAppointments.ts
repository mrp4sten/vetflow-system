import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { appointmentService } from '@application/services/api/AppointmentService'
import type { Appointment, AppointmentStatus } from '@domain/models/Appointment'
import { CreateAppointmentDto, UpdateAppointmentDto, AppointmentFilterDto } from '@application/dtos/appointment.dto'

const APPOINTMENT_QUERY_KEY = 'appointments'

export const useAppointments = (filter?: AppointmentFilterDto) => {
  return useQuery({
    queryKey: [APPOINTMENT_QUERY_KEY, filter],
    queryFn: () => appointmentService.findAll(filter),
  })
}

export const useAppointment = (id: number) => {
  return useQuery({
    queryKey: [APPOINTMENT_QUERY_KEY, id],
    queryFn: () => appointmentService.findById(id),
    enabled: !!id,
  })
}

export const useAppointmentsByDateRange = (startDate: string, endDate: string) => {
  return useQuery({
    queryKey: [APPOINTMENT_QUERY_KEY, 'date-range', startDate, endDate],
    queryFn: () => appointmentService.findByDateRange(startDate, endDate),
    enabled: !!startDate && !!endDate,
  })
}

export const useCreateAppointment = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateAppointmentDto) => appointmentService.save(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [APPOINTMENT_QUERY_KEY] })
    },
  })
}

export const useUpdateAppointment = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateAppointmentDto }) =>
      appointmentService.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: [APPOINTMENT_QUERY_KEY] })
      queryClient.invalidateQueries({ queryKey: [APPOINTMENT_QUERY_KEY, id] })
    },
  })
}

export const useUpdateAppointmentStatus = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: AppointmentStatus }) =>
      appointmentService.updateStatus(id, status),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: [APPOINTMENT_QUERY_KEY] })
      queryClient.invalidateQueries({ queryKey: [APPOINTMENT_QUERY_KEY, id] })
    },
  })
}

export const useDeleteAppointment = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => appointmentService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [APPOINTMENT_QUERY_KEY] })
    },
  })
}

export const useCheckAppointmentAvailability = () => {
  return useMutation({
    mutationFn: ({
      veterinarianId,
      scheduledAt,
      duration,
    }: {
      veterinarianId: number
      scheduledAt: string
      duration: number
    }) => appointmentService.checkAvailability(veterinarianId, scheduledAt, duration),
  })
}