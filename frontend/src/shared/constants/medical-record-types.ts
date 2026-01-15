import type { RecordType } from '@domain/models/MedicalRecord'

export const RECORD_TYPE_DISPLAY: Record<RecordType, string> = {
  examination: 'Examination',
  diagnosis: 'Diagnosis',
  treatment: 'Treatment',
  surgery: 'Surgery',
  vaccination: 'Vaccination',
  lab_result: 'Lab Result',
  prescription: 'Prescription',
  other: 'Other',
}

export const RECORD_TYPE_COLORS: Record<RecordType, string> = {
  examination: 'bg-blue-100 text-blue-800',
  diagnosis: 'bg-red-100 text-red-800',
  treatment: 'bg-green-100 text-green-800',
  surgery: 'bg-purple-100 text-purple-800',
  vaccination: 'bg-cyan-100 text-cyan-800',
  lab_result: 'bg-yellow-100 text-yellow-800',
  prescription: 'bg-pink-100 text-pink-800',
  other: 'bg-gray-100 text-gray-800',
}
