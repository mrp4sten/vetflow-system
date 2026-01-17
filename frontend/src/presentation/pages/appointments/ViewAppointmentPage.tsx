import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { format } from 'date-fns'
import { ArrowLeft, Calendar, Clock, User, Heart, Edit, Trash, FileText } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Badge } from '@presentation/components/ui/badge'
import { Separator } from '@presentation/components/ui/separator'
import { ConfirmDialog } from '@presentation/components/shared/ConfirmDialog/ConfirmDialog'
import { useAppointment, useUpdateAppointmentStatus, useDeleteAppointment } from '@presentation/hooks/useAppointments'
import { ROUTES } from '@shared/constants/routes'
import {
  APPOINTMENT_STATUS_COLORS,
  APPOINTMENT_STATUS_DISPLAY,
  APPOINTMENT_TYPE_DISPLAY,
  APPOINTMENT_PRIORITY_COLORS,
  APPOINTMENT_PRIORITY_DISPLAY,
} from '@shared/constants/appointment-status'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@presentation/components/ui/dropdown-menu'
import { AppointmentStatus } from '@domain/models/Appointment'

export const ViewAppointmentPage: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  
  const appointmentId = parseInt(id || '0')
  const { data: appointment, isLoading } = useAppointment(appointmentId)
  const updateStatus = useUpdateAppointmentStatus()
  const deleteAppointment = useDeleteAppointment()

  const canEdit = hasAnyRole(['admin', 'veterinarian'])
  const canDelete = hasAnyRole(['admin'])

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!appointment) {
    return (
      <div className="text-center py-12">
        <p className="text-lg text-muted-foreground">Appointment not found</p>
        <Button
          variant="outline"
          className="mt-4"
          onClick={() => navigate(ROUTES.APPOINTMENTS.LIST)}
        >
          Back to Appointments
        </Button>
      </div>
    )
  }

  const handleStatusChange = async (status: AppointmentStatus) => {
    try {
      await updateStatus.mutateAsync({ id: appointmentId, status })
      toast.success('Appointment status updated successfully')
    } catch (error) {
      toast.error('Failed to update appointment status')
    }
  }

  const handleDelete = async () => {
    try {
      await deleteAppointment.mutateAsync(appointmentId)
      toast.success('Appointment deleted successfully')
      navigate(ROUTES.APPOINTMENTS.LIST)
    } catch (error) {
      toast.error('Failed to delete appointment')
    }
  }

  const statusColor = APPOINTMENT_STATUS_COLORS[appointment.status]
  const priorityColor = APPOINTMENT_PRIORITY_COLORS[appointment.priority]

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate(ROUTES.APPOINTMENTS.LIST)}
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Appointment Details</h2>
            <p className="text-muted-foreground">
              View and manage appointment information
            </p>
          </div>
        </div>

        {canEdit && (
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => navigate(ROUTES.APPOINTMENTS.EDIT(appointmentId))}
            >
              <Edit className="mr-2 h-4 w-4" />
              Edit
            </Button>
            
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline">
                  Update Status
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>Change Status</DropdownMenuLabel>
                <DropdownMenuSeparator />
                {Object.entries(APPOINTMENT_STATUS_DISPLAY).map(([key, value]) => (
                  <DropdownMenuItem
                    key={key}
                    onClick={() => handleStatusChange(key as AppointmentStatus)}
                    disabled={appointment.status === key}
                  >
                    {value}
                  </DropdownMenuItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>

            {canDelete && (
              <Button variant="destructive" onClick={() => setDeleteDialogOpen(true)}>
                <Trash className="mr-2 h-4 w-4" />
                Delete
              </Button>
            )}
          </div>
        )}
      </div>

      {/* Main Content */}
      <div className="grid gap-6 md:grid-cols-3">
        {/* Appointment Info - 2 columns */}
        <div className="md:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Appointment Information</CardTitle>
              <CardDescription>
                Scheduled appointment details and status
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              {/* Status and Priority Badges */}
              <div className="flex gap-2">
                <Badge className={`bg-${statusColor}-100 text-${statusColor}-800 hover:bg-${statusColor}-200`}>
                  {APPOINTMENT_STATUS_DISPLAY[appointment.status]}
                </Badge>
                <Badge variant="outline" className={`border-${priorityColor}-300 text-${priorityColor}-700`}>
                  {APPOINTMENT_PRIORITY_DISPLAY[appointment.priority]}
                </Badge>
                <Badge variant="outline">
                  {APPOINTMENT_TYPE_DISPLAY[appointment.type]}
                </Badge>
              </div>

              {/* Date and Time */}
              <div className="grid gap-4 md:grid-cols-2">
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Date & Time</p>
                  <div className="flex items-center gap-2">
                    <Calendar className="h-4 w-4 text-muted-foreground" />
                    <p className="font-medium">
                      {format(new Date(appointment.scheduledAt), 'EEEE, MMMM d, yyyy')}
                    </p>
                  </div>
                  <div className="flex items-center gap-2">
                    <Clock className="h-4 w-4 text-muted-foreground" />
                    <p className="font-medium">
                      {format(new Date(appointment.scheduledAt), 'h:mm a')} - {format(new Date(appointment.endTime), 'h:mm a')}
                    </p>
                  </div>
                </div>

                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Duration</p>
                  <p className="font-medium">{appointment.duration} minutes</p>
                </div>
              </div>

              <Separator />

              {/* Reason and Notes */}
              <div className="space-y-4">
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Reason for Visit</p>
                  <p className="font-medium">{appointment.reason}</p>
                </div>

                {appointment.notes && (
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">Additional Notes</p>
                    <p className="text-sm">{appointment.notes}</p>
                  </div>
                )}
              </div>

              {/* Created Info */}
              <Separator />
              <div className="text-sm text-muted-foreground">
                <p>Created on {format(new Date(appointment.createdAt), 'MMM d, yyyy h:mm a')}</p>
                {appointment.createdBy && (
                  <p>Created by {appointment.createdBy.firstName} {appointment.createdBy.lastName}</p>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Quick Actions */}
          <Card>
            <CardHeader>
              <CardTitle>Quick Actions</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-2">
              <Button
                variant="outline"
                onClick={() => navigate(ROUTES.MEDICAL_RECORDS.CREATE, { 
                  state: { 
                    appointmentId: appointment.id,
                    patientId: appointment.patient?.id 
                  }
                })}
              >
                <FileText className="mr-2 h-4 w-4" />
                Create Medical Record
              </Button>
              {appointment.canReschedule && (
                <Button variant="outline" disabled>
                  <Calendar className="mr-2 h-4 w-4" />
                  Reschedule (Coming Soon)
                </Button>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Patient & Veterinarian Info - 1 column */}
        <div className="space-y-6">
          {/* Patient Card */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Heart className="h-5 w-5" />
                Patient Information
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {appointment.patient ? (
                <>
                  <div>
                    <p className="font-medium text-lg">{appointment.patient.name}</p>
                    <p className="text-sm text-muted-foreground">
                      {appointment.patient.species} - {appointment.patient.breed || 'Mixed'}
                    </p>
                  </div>
                  
                  {appointment.patient.owner && (
                    <div>
                      <p className="text-sm text-muted-foreground">Owner</p>
                      <p className="font-medium">{appointment.patient.owner.fullName}</p>
                      <p className="text-sm">{appointment.patient.owner.phoneNumber}</p>
                    </div>
                  )}

                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => navigate(ROUTES.PATIENTS.VIEW(appointment.patient!.id))}
                  >
                    View Patient Details
                  </Button>
                </>
              ) : (
                <p className="text-muted-foreground">No patient information available</p>
              )}
            </CardContent>
          </Card>

          {/* Veterinarian Card */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Veterinarian
              </CardTitle>
            </CardHeader>
            <CardContent>
              {appointment.veterinarian ? (
                <div className="space-y-2">
                  <p className="font-medium">
                    Dr. {appointment.veterinarian.firstName} {appointment.veterinarian.lastName}
                  </p>
                  <p className="text-sm text-muted-foreground">{appointment.veterinarian.email}</p>
                </div>
              ) : (
                <p className="text-muted-foreground">No veterinarian assigned</p>
              )}
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDelete}
        title="Delete Appointment"
        description={`Are you sure you want to delete this appointment for ${appointment.patient?.name}? This action cannot be undone.`}
        confirmText="Delete Appointment"
        variant="danger"
      />
    </div>
  )
}