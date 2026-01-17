import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { format } from 'date-fns'
import { ArrowLeft, Heart, User, Edit, Trash, Calendar, FileText } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Badge } from '@presentation/components/ui/badge'
import { Separator } from '@presentation/components/ui/separator'
import { ConfirmDialog } from '@presentation/components/shared/ConfirmDialog/ConfirmDialog'
import { usePatient, useDeactivatePatient } from '@presentation/hooks/usePatients'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'
import { formatAge } from '@infrastructure/utils/date-utils'

export const ViewPatientPage: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const [deactivateDialogOpen, setDeactivateDialogOpen] = useState(false)
  
  const patientId = parseInt(id || '0')
  const { data: patient, isLoading } = usePatient(patientId)
  const deactivatePatient = useDeactivatePatient()

  const canEdit = hasAnyRole(['admin', 'veterinarian', 'assistant'])
  const canDelete = hasAnyRole(['admin'])

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!patient) {
    return (
      <div className="text-center py-12">
        <p className="text-lg text-muted-foreground">Patient not found</p>
        <Button
          variant="outline"
          className="mt-4"
          onClick={() => navigate(ROUTES.PATIENTS.LIST)}
        >
          Back to Patients
        </Button>
      </div>
    )
  }

  const handleDeactivate = async () => {
    try {
      await deactivatePatient.mutateAsync(patientId)
      toast.success(`${patient.name} has been deactivated successfully`)
      navigate(ROUTES.PATIENTS.LIST)
    } catch (error) {
      toast.error('Failed to deactivate patient')
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate(ROUTES.PATIENTS.LIST)}
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h2 className="text-3xl font-bold tracking-tight flex items-center gap-2">
              <Heart className="h-8 w-8 text-pink-500" />
              {patient.name}
            </h2>
            <p className="text-muted-foreground">
              {patient.species} • {patient.breed || 'Mixed'}
            </p>
          </div>
        </div>

        {canEdit && patient.isActive && (
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => navigate(ROUTES.PATIENTS.EDIT(patientId))}
            >
              <Edit className="mr-2 h-4 w-4" />
              Edit
            </Button>
            
            {canDelete && (
              <Button variant="destructive" onClick={() => setDeactivateDialogOpen(true)}>
                <Trash className="mr-2 h-4 w-4" />
                Deactivate
              </Button>
            )}
          </div>
        )}
      </div>

      {/* Status Badge */}
      {!patient.isActive && (
        <Badge variant="secondary" className="text-lg px-4 py-2">
          Inactive Patient
        </Badge>
      )}

      {/* Main Content */}
      <div className="grid gap-6 md:grid-cols-3">
        {/* Patient Info - 2 columns */}
        <div className="md:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Patient Information</CardTitle>
              <CardDescription>
                Basic details and medical information
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid gap-4 md:grid-cols-2">
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Name</p>
                  <p className="font-medium">{patient.name}</p>
                </div>

                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Species</p>
                  <p className="font-medium capitalize">{patient.species}</p>
                </div>

                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Breed</p>
                  <p className="font-medium">{patient.breed || 'Mixed / Unknown'}</p>
                </div>

                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Gender</p>
                  <p className="font-medium capitalize">{patient.gender}</p>
                </div>

                {patient.birthDate && (
                  <>
                    <div className="space-y-1">
                      <p className="text-sm text-muted-foreground">Birth Date</p>
                      <p className="font-medium">
                        {format(new Date(patient.birthDate), 'MMMM d, yyyy')}
                      </p>
                    </div>

                    <div className="space-y-1">
                      <p className="text-sm text-muted-foreground">Age</p>
                      <p className="font-medium">{formatAge(patient.birthDate)}</p>
                    </div>
                  </>
                )}

                {patient.weight && (
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">Weight</p>
                    <p className="font-medium">
                      {patient.weight} kg ({patient.weightInLbs?.toFixed(1)} lbs)
                    </p>
                  </div>
                )}

                {patient.color && (
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">Color/Markings</p>
                    <p className="font-medium">{patient.color}</p>
                  </div>
                )}

                {patient.microchipNumber && (
                  <div className="space-y-1 md:col-span-2">
                    <p className="text-sm text-muted-foreground">Microchip Number</p>
                    <p className="font-mono font-medium">{patient.microchipNumber}</p>
                  </div>
                )}
              </div>

              <Separator />

              {/* Registration Info */}
              <div className="text-sm text-muted-foreground">
                <p>Registered on {format(new Date(patient.createdAt), 'MMMM d, yyyy')}</p>
                {patient.updatedAt !== patient.createdAt && (
                  <p>Last updated {format(new Date(patient.updatedAt), 'MMMM d, yyyy')}</p>
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
                onClick={() => navigate(ROUTES.APPOINTMENTS.CREATE, { 
                  state: { patientId: patient.id }
                })}
              >
                <Calendar className="mr-2 h-4 w-4" />
                Schedule Appointment
              </Button>
              <Button
                variant="outline"
                onClick={() => navigate(ROUTES.MEDICAL_RECORDS.CREATE, { 
                  state: { patientId: patient.id }
                })}
              >
                <FileText className="mr-2 h-4 w-4" />
                Add Medical Record
              </Button>
              <Button
                variant="outline"
                onClick={() => navigate(ROUTES.PATIENTS.MEDICAL_HISTORY(patient.id))}
              >
                <FileText className="mr-2 h-4 w-4" />
                View Medical History
              </Button>
            </CardContent>
          </Card>
        </div>

        {/* Owner Info - 1 column */}
        <div className="space-y-6">
          {/* Owner Card */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Owner Information
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {patient.owner ? (
                <>
                  <div>
                    <p className="font-medium text-lg">{patient.owner.fullName}</p>
                  </div>
                  
                  <Separator />

                  <div className="space-y-2 text-sm">
                    <div>
                      <p className="text-muted-foreground">Email</p>
                      <p>{patient.owner.email}</p>
                    </div>
                    
                    <div>
                      <p className="text-muted-foreground">Phone</p>
                      <p>{patient.owner.phoneNumber}</p>
                    </div>

                    {patient.owner.address && (
                      <div>
                        <p className="text-muted-foreground">Address</p>
                        <p>{patient.owner.address}</p>
                        {(patient.owner.city || patient.owner.state) && (
                          <p>
                            {patient.owner.city && patient.owner.state
                              ? `${patient.owner.city}, ${patient.owner.state}`
                              : patient.owner.city || patient.owner.state}
                            {patient.owner.zipCode && ` ${patient.owner.zipCode}`}
                          </p>
                        )}
                      </div>
                    )}
                  </div>

                  <Separator />

                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => navigate(ROUTES.OWNERS.VIEW(patient.owner!.id))}
                  >
                    View Owner Details
                  </Button>
                </>
              ) : (
                <p className="text-muted-foreground">No owner information available</p>
              )}
            </CardContent>
          </Card>

          {/* Recent Activity Placeholder */}
          <Card>
            <CardHeader>
              <CardTitle>Recent Activity</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground">
                Recent appointments and medical records will appear here.
              </p>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Deactivate Confirmation Dialog */}
      <ConfirmDialog
        open={deactivateDialogOpen}
        onOpenChange={setDeactivateDialogOpen}
        onConfirm={handleDeactivate}
        title="Deactivate Patient"
        description={`Are you sure you want to deactivate ${patient.name}? This will mark the patient as inactive but preserve all medical history.`}
        confirmText="Deactivate Patient"
        variant="warning"
      />
    </div>
  )
}