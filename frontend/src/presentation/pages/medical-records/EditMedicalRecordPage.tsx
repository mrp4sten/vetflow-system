import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { format } from 'date-fns'
import { ArrowLeft } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Label } from '@presentation/components/ui/label'
import { Textarea } from '@presentation/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@presentation/components/ui/select'
import { medicalRecordSchema, type MedicalRecordFormData } from '@shared/schemas/medical-record.schema'
import { useMedicalRecord, useUpdateMedicalRecord } from '@presentation/hooks/useMedicalRecords'
import { usePatients } from '@presentation/hooks/usePatients'
import { useAppointments } from '@presentation/hooks/useAppointments'
import { ROUTES } from '@shared/constants/routes'
import { RECORD_TYPE_DISPLAY } from '@shared/constants/medical-record-types'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'

export const EditMedicalRecordPage: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const recordId = parseInt(id || '0')

  const { data: record, isLoading: loadingRecord } = useMedicalRecord(recordId)
  const { data: patients = [], isLoading: loadingPatients } = usePatients({ isActive: true })
  const { data: appointments = [], isLoading: loadingAppointments } = useAppointments()
  const updateRecord = useUpdateMedicalRecord()

  const methods = useForm<MedicalRecordFormData>({
    resolver: zodResolver(medicalRecordSchema),
    defaultValues: {
      recordDate: format(new Date(), "yyyy-MM-dd'T'HH:mm"),
      type: 'examination',
    },
  })

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    reset,
    formState: { errors, isSubmitting },
  } = methods

  // Pre-populate form when record data is loaded
  useEffect(() => {
    if (record) {
      reset({
        patientId: record.patientId,
        appointmentId: record.appointmentId,
        recordDate: record.recordDate,
        type: record.type,
        chiefComplaint: record.chiefComplaint || '',
        clinicalFindings: record.clinicalFindings || '',
        diagnosis: record.diagnosis || '',
        treatment: record.treatment || '',
        labResults: record.labResults || '',
        followUpInstructions: record.followUpInstructions || '',
      })
    }
  }, [record, reset])

  const onSubmit = async (data: MedicalRecordFormData) => {
    try {
      await updateRecord.mutateAsync({ id: recordId, data })
      navigate(ROUTES.MEDICAL_RECORDS.VIEW(recordId))
    } catch (error) {
      // Error is handled by mutation
    }
  }

  if (loadingRecord || loadingPatients || loadingAppointments) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!record) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4">
        <p className="text-lg text-muted-foreground">Medical record not found</p>
        <Button onClick={() => navigate(ROUTES.MEDICAL_RECORDS.LIST)}>
          Back to Medical Records
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate(ROUTES.MEDICAL_RECORDS.VIEW(recordId))}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Edit Medical Record #{recordId}</h2>
          <p className="text-muted-foreground">
            Update medical record information
          </p>
        </div>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="grid gap-6 max-w-4xl">
            {/* Basic Information */}
            <Card>
              <CardHeader>
                <CardTitle>Basic Information</CardTitle>
                <CardDescription>
                  Update record details
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6 md:grid-cols-2">
                {/* Patient Selection */}
                <div className="space-y-2">
                  <Label htmlFor="patientId" className="required">
                    Patient
                  </Label>
                  <Select
                    value={watch('patientId')?.toString()}
                    onValueChange={(value) => setValue('patientId', parseInt(value))}
                  >
                    <SelectTrigger className={errors.patientId ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Select a patient" />
                    </SelectTrigger>
                    <SelectContent>
                      {patients.map((patient) => (
                        <SelectItem key={patient.id} value={patient.id.toString()}>
                          {patient.name} - {patient.owner?.fullName}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.patientId && (
                    <p className="text-sm text-red-600">{errors.patientId.message}</p>
                  )}
                </div>

                {/* Appointment Selection (Optional) */}
                <div className="space-y-2">
                  <Label htmlFor="appointmentId">Related Appointment (Optional)</Label>
                  <Select
                    value={watch('appointmentId')?.toString() || ''}
                    onValueChange={(value) => setValue('appointmentId', value ? parseInt(value) : undefined)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select appointment" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="">None</SelectItem>
                      {appointments.map((apt) => (
                        <SelectItem key={apt.id} value={apt.id.toString()}>
                          {format(new Date(apt.scheduledAt), 'MMM dd, yyyy')} - {apt.patient?.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Record Date */}
                <div className="space-y-2">
                  <Label htmlFor="recordDate" className="required">
                    Record Date & Time
                  </Label>
                  <input
                    type="datetime-local"
                    id="recordDate"
                    {...register('recordDate')}
                    className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                      errors.recordDate ? 'border-red-500' : ''
                    }`}
                  />
                  {errors.recordDate && (
                    <p className="text-sm text-red-600">{errors.recordDate.message}</p>
                  )}
                </div>

                {/* Record Type */}
                <div className="space-y-2">
                  <Label htmlFor="type" className="required">
                    Record Type
                  </Label>
                  <Select
                    value={watch('type')}
                    onValueChange={(value: any) => setValue('type', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(RECORD_TYPE_DISPLAY).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </CardContent>
            </Card>

            {/* Clinical Information */}
            <Card>
              <CardHeader>
                <CardTitle>Clinical Information</CardTitle>
                <CardDescription>
                  Update clinical findings and diagnosis
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Chief Complaint */}
                <div className="space-y-2">
                  <Label htmlFor="chiefComplaint">Chief Complaint</Label>
                  <Textarea
                    id="chiefComplaint"
                    {...register('chiefComplaint')}
                    rows={2}
                    placeholder="Main reason for visit..."
                  />
                </div>

                {/* Clinical Findings */}
                <div className="space-y-2">
                  <Label htmlFor="clinicalFindings">Clinical Findings</Label>
                  <Textarea
                    id="clinicalFindings"
                    {...register('clinicalFindings')}
                    rows={4}
                    placeholder="Physical examination findings, vital signs, etc..."
                  />
                </div>

                {/* Diagnosis */}
                <div className="space-y-2">
                  <Label htmlFor="diagnosis">Diagnosis</Label>
                  <Textarea
                    id="diagnosis"
                    {...register('diagnosis')}
                    rows={3}
                    placeholder="Primary and differential diagnoses..."
                  />
                </div>
              </CardContent>
            </Card>

            {/* Treatment & Follow-up */}
            <Card>
              <CardHeader>
                <CardTitle>Treatment & Follow-up</CardTitle>
                <CardDescription>
                  Update treatment plan and follow-up instructions
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Treatment */}
                <div className="space-y-2">
                  <Label htmlFor="treatment">Treatment</Label>
                  <Textarea
                    id="treatment"
                    {...register('treatment')}
                    rows={4}
                    placeholder="Treatment plan, procedures performed..."
                  />
                </div>

                {/* Lab Results */}
                <div className="space-y-2">
                  <Label htmlFor="labResults">Lab Results</Label>
                  <Textarea
                    id="labResults"
                    {...register('labResults')}
                    rows={3}
                    placeholder="Laboratory test results..."
                  />
                </div>

                {/* Follow-up Instructions */}
                <div className="space-y-2">
                  <Label htmlFor="followUpInstructions">Follow-up Instructions</Label>
                  <Textarea
                    id="followUpInstructions"
                    {...register('followUpInstructions')}
                    rows={3}
                    placeholder="Follow-up care instructions, next visit details..."
                  />
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="flex justify-end gap-4 mt-6 max-w-4xl">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate(ROUTES.MEDICAL_RECORDS.VIEW(recordId))}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Updating...
                </>
              ) : (
                'Update Medical Record'
              )}
            </Button>
          </div>
        </form>
      </FormProvider>
    </div>
  )
}
