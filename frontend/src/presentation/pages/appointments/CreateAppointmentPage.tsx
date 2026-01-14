import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { format } from 'date-fns'
import { ArrowLeft, Calendar, Clock } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { FormField } from '@presentation/components/shared/FormFields/FormField'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@presentation/components/ui/select'
import { Label } from '@presentation/components/ui/label'
import { Textarea } from '@presentation/components/ui/textarea'
import { appointmentSchema, AppointmentFormData } from '@shared/schemas/appointment.schema'
import { useCreateAppointment, useCheckAppointmentAvailability } from '@presentation/hooks/useAppointments'
import { usePatients } from '@presentation/hooks/usePatients'
import { useVeterinarians } from '@presentation/hooks/useVeterinarians'
import { ROUTES } from '@shared/constants/routes'
import {
  APPOINTMENT_TYPE_DISPLAY,
  APPOINTMENT_PRIORITY_DISPLAY,
  DEFAULT_APPOINTMENT_DURATION,
  APPOINTMENT_DURATIONS,
} from '@shared/constants/appointment-status'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'

export const CreateAppointmentPage: React.FC = () => {
  const navigate = useNavigate()
  const [checkingAvailability, setCheckingAvailability] = useState(false)

  const { data: patients = [], isLoading: loadingPatients } = usePatients({ isActive: true })
  const { data: veterinarians = [], isLoading: loadingVeterinarians } = useVeterinarians()
  const createAppointment = useCreateAppointment()
  const checkAvailability = useCheckAppointmentAvailability()

  const methods = useForm<AppointmentFormData>({
    resolver: zodResolver(appointmentSchema),
    defaultValues: {
      duration: DEFAULT_APPOINTMENT_DURATION,
      type: 'consultation',
      priority: 'medium',
      notes: '',
    },
  })

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = methods

  const watchVeterinarian = watch('veterinarianId')
  const watchScheduledAt = watch('scheduledAt')
  const watchDuration = watch('duration')

  const handleCheckAvailability = async () => {
    if (watchVeterinarian && watchScheduledAt && watchDuration) {
      setCheckingAvailability(true)
      try {
        const isAvailable = await checkAvailability.mutateAsync({
          veterinarianId: watchVeterinarian,
          scheduledAt: watchScheduledAt,
          duration: watchDuration,
        })
        if (!isAvailable) {
          methods.setError('scheduledAt', {
            message: 'This time slot is not available',
          })
        }
      } finally {
        setCheckingAvailability(false)
      }
    }
  }

  const onSubmit = async (data: AppointmentFormData) => {
    try {
      await createAppointment.mutateAsync(data)
      navigate(ROUTES.APPOINTMENTS.LIST)
    } catch (error) {
      // Error is handled by mutation
    }
  }

  if (loadingPatients || loadingVeterinarians) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate(ROUTES.APPOINTMENTS.LIST)}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Schedule Appointment</h2>
          <p className="text-muted-foreground">
            Create a new appointment for a patient
          </p>
        </div>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Card>
            <CardHeader>
              <CardTitle>Appointment Details</CardTitle>
              <CardDescription>
                Fill in the information to schedule an appointment
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid gap-6 md:grid-cols-2">
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

                {/* Veterinarian Selection */}
                <div className="space-y-2">
                  <Label htmlFor="veterinarianId" className="required">
                    Veterinarian
                  </Label>
                  <Select
                    value={watch('veterinarianId')?.toString()}
                    onValueChange={(value) => setValue('veterinarianId', parseInt(value))}
                  >
                    <SelectTrigger className={errors.veterinarianId ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Select a veterinarian" />
                    </SelectTrigger>
                    <SelectContent>
                      {veterinarians.map((vet) => (
                        <SelectItem key={vet.id} value={vet.id.toString()}>
                          Dr. {vet.firstName} {vet.lastName}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.veterinarianId && (
                    <p className="text-sm text-red-600">{errors.veterinarianId.message}</p>
                  )}
                </div>

                {/* Date and Time */}
                <div className="space-y-2">
                  <Label htmlFor="scheduledAt" className="required">
                    Date and Time
                  </Label>
                  <div className="flex gap-2">
                    <input
                      type="datetime-local"
                      id="scheduledAt"
                      {...register('scheduledAt')}
                      onBlur={handleCheckAvailability}
                      className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                        errors.scheduledAt ? 'border-red-500' : ''
                      }`}
                      min={format(new Date(), "yyyy-MM-dd'T'HH:mm")}
                    />
                    {checkingAvailability && (
                      <LoadingSpinner size="sm" className="mt-2" />
                    )}
                  </div>
                  {errors.scheduledAt && (
                    <p className="text-sm text-red-600">{errors.scheduledAt.message}</p>
                  )}
                </div>

                {/* Duration */}
                <div className="space-y-2">
                  <Label htmlFor="duration" className="required">
                    Duration
                  </Label>
                  <Select
                    value={watch('duration')?.toString()}
                    onValueChange={(value) => setValue('duration', parseInt(value))}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {APPOINTMENT_DURATIONS.map((duration) => (
                        <SelectItem key={duration.value} value={duration.value.toString()}>
                          {duration.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Type */}
                <div className="space-y-2">
                  <Label htmlFor="type" className="required">
                    Appointment Type
                  </Label>
                  <Select
                    value={watch('type')}
                    onValueChange={(value: any) => setValue('type', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(APPOINTMENT_TYPE_DISPLAY).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Priority */}
                <div className="space-y-2">
                  <Label htmlFor="priority" className="required">
                    Priority
                  </Label>
                  <Select
                    value={watch('priority')}
                    onValueChange={(value: any) => setValue('priority', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(APPOINTMENT_PRIORITY_DISPLAY).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              {/* Reason */}
              <div className="space-y-2">
                <Label htmlFor="reason" className="required">
                  Reason for Visit
                </Label>
                <Textarea
                  id="reason"
                  {...register('reason')}
                  rows={3}
                  placeholder="Describe the reason for this appointment..."
                  className={errors.reason ? 'border-red-500' : ''}
                />
                {errors.reason && (
                  <p className="text-sm text-red-600">{errors.reason.message}</p>
                )}
              </div>

              {/* Notes */}
              <div className="space-y-2">
                <Label htmlFor="notes">Additional Notes</Label>
                <Textarea
                  id="notes"
                  {...register('notes')}
                  rows={3}
                  placeholder="Any additional notes or special instructions..."
                />
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-end gap-4 mt-6">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate(ROUTES.APPOINTMENTS.LIST)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Creating...
                </>
              ) : (
                'Create Appointment'
              )}
            </Button>
          </div>
        </form>
      </FormProvider>
    </div>
  )
}