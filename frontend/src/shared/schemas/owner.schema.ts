import { z } from 'zod'

export const ownerSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters').max(120, 'Name must be at most 120 characters'),
  phone: z.string().min(10, 'Phone must be at least 10 digits').max(30, 'Phone must be at most 30 characters'),
  email: z.string().email('Invalid email address').max(120, 'Email must be at most 120 characters'),
  address: z.string().min(1, 'Address is required').max(255, 'Address must be at most 255 characters'),
})

export type OwnerFormData = z.infer<typeof ownerSchema>