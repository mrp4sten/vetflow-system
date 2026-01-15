import { z } from 'zod'

export const prescriptionSchema = z.object({
  medication: z.string().min(1, 'Medication name is required').max(200),
  dosage: z.string().min(1, 'Dosage is required').max(100),
  frequency: z.string().min(1, 'Frequency is required').max(100),
  duration: z.string().min(1, 'Duration is required').max(100),
  instructions: z.string().max(500).optional(),
})

export const medicalRecordSchema = z.object({
  patientId: z.number().min(1, 'Patient is required'),
  appointmentId: z.number().optional(),
  recordDate: z.string().min(1, 'Record date is required'),
  type: z.enum(['examination', 'diagnosis', 'treatment', 'surgery', 'vaccination', 'lab_result', 'prescription', 'other'] as const),
  chiefComplaint: z.string().max(1000).optional(),
  clinicalFindings: z.string().max(2000).optional(),
  diagnosis: z.string().max(1000).optional(),
  treatment: z.string().max(2000).optional(),
  labResults: z.string().max(2000).optional(),
  followUpInstructions: z.string().max(1000).optional(),
})

export type MedicalRecordFormData = z.infer<typeof medicalRecordSchema>
export type PrescriptionFormData = z.infer<typeof prescriptionSchema>
