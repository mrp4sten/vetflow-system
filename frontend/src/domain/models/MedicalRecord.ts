import { Patient } from './Patient'
import { SystemUser } from './SystemUser'
import { Appointment } from './Appointment'

export type RecordType = 'examination' | 'diagnosis' | 'treatment' | 'surgery' | 'vaccination' | 'lab_result' | 'prescription' | 'other'

export interface Prescription {
  medication: string
  dosage: string
  frequency: string
  duration: string
  instructions?: string
}

export interface MedicalRecord {
  id: number
  patientId: number
  patient?: Patient
  appointmentId?: number
  appointment?: Appointment
  veterinarianId: number
  veterinarian?: SystemUser
  recordDate: string
  type: RecordType
  chiefComplaint?: string
  clinicalFindings?: string
  diagnosis?: string
  treatment?: string
  prescriptions?: Prescription[]
  labResults?: string
  followUpInstructions?: string
  attachments?: string[] // URLs to uploaded files
  createdAt: string
  updatedAt: string
  
  // Computed properties
  get isEditable(): boolean // Can only edit recent records
}