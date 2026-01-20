import type { RecordType, Prescription } from '@domain/models/MedicalRecord'

export interface CreateMedicalRecordDto {
  patientId: number
  appointmentId?: number
  recordDate: string
  type: RecordType
  chiefComplaint?: string
  clinicalFindings?: string
  diagnosis?: string
  treatment?: string
  prescriptions?: Prescription[]
  labResults?: string
  followUpInstructions?: string
  attachments?: string[]
}

export interface UpdateMedicalRecordDto {
  recordDate?: string
  type?: RecordType
  chiefComplaint?: string
  clinicalFindings?: string
  diagnosis?: string
  treatment?: string
  prescriptions?: Prescription[]
  labResults?: string
  followUpInstructions?: string
  attachments?: string[]
}

export interface MedicalRecordFilterDto {
  patientId?: number
  appointmentId?: number
  veterinarianId?: number
  type?: RecordType
  dateFrom?: string
  dateTo?: string
  page?: number
  size?: number
  sort?: string
}