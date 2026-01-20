import { z } from 'zod'
import type { AppointmentType, AppointmentPriority } from '@domain/models/Appointment'

export const appointmentSchema = z.object({
  patientId: z.number().min(1, 'Patient is required'),
  veterinarianId: z.number().min(1, 'Veterinarian is required'),
  scheduledAt: z.string().min(1, 'Date and time are required'),
  duration: z.number().min(15, 'Minimum duration is 15 minutes').max(240, 'Maximum duration is 4 hours'),
  type: z.enum(['consultation', 'vaccination', 'surgery', 'grooming', 'emergency', 'checkup', 'other'] as const),
  priority: z.enum(['low', 'medium', 'high', 'emergency'] as const),
  reason: z.string().min(3, 'Please provide a reason for the appointment').max(500),
  notes: z.string().max(1000).optional(),
})

export type AppointmentFormData = z.infer<typeof appointmentSchema>