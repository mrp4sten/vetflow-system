import { z } from 'zod'

export const patientSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters').max(100),
  species: z.enum(['dog', 'cat', 'bird', 'rabbit', 'other'] as const),
  breed: z.string().max(100).optional(),
  gender: z.enum(['male', 'female', 'unknown'] as const),
  birthDate: z.string().optional(),
  weight: z.number().min(0.1).max(500).optional(),
  color: z.string().max(50).optional(),
  microchipNumber: z.string().length(15, 'Microchip must be 15 digits').optional().or(z.literal('')),
  ownerId: z.number().min(1, 'Owner is required'),
})

export type PatientFormData = z.infer<typeof patientSchema>