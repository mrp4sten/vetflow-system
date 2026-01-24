import { z } from 'zod'

export const patientSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters').max(100),
  species: z.enum(['dog', 'cat'] as const),
  breed: z.string().max(100).optional(),
  birthDate: z.string().optional(),
  weight: z.number().min(0.1).max(500).optional(),
  ownerId: z.number().min(1, 'Owner is required'),
})

export type PatientFormData = z.infer<typeof patientSchema>