import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Calendar, FileText, User, Stethoscope, Pencil, Trash, Printer } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Badge } from '@presentation/components/ui/badge'
import { Separator } from '@presentation/components/ui/separator'
import { ConfirmDialog } from '@presentation/components/shared/ConfirmDialog/ConfirmDialog'
import { useMedicalRecord, useDeleteMedicalRecord } from '@presentation/hooks/useMedicalRecords'
import { ROUTES } from '@shared/constants/routes'
import { RECORD_TYPE_DISPLAY, RECORD_TYPE_COLORS } from '@shared/constants/medical-record-types'
import { formatDate } from '@infrastructure/utils/date-utils'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'

export const ViewMedicalRecordPage: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const { hasAnyRole } = useAuth()
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  
  const recordId = parseInt(id || '0')
  const { data: record, isLoading } = useMedicalRecord(recordId)
  const deleteMedicalRecord = useDeleteMedicalRecord()

  const canEdit = hasAnyRole(['admin', 'veterinarian'])
  const canDelete = hasAnyRole(['admin'])

  const handleDelete = async () => {
    try {
      await deleteMedicalRecord.mutateAsync(recordId)
      toast.success('Medical record deleted successfully')
      navigate(ROUTES.MEDICAL_RECORDS.LIST)
    } catch (error) {
      toast.error('Failed to delete medical record')
    }
  }

  const handlePrint = () => {
    window.print()
    toast.success('Opening print dialog...')
  }

  if (isLoading) {
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
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate(ROUTES.MEDICAL_RECORDS.LIST)}
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Medical Record #{record.id}</h2>
            <p className="text-muted-foreground">
              {formatDate(record.recordDate, 'MMM dd, yyyy h:mm a')}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <Badge className={RECORD_TYPE_COLORS[record.type]}>
            {RECORD_TYPE_DISPLAY[record.type]}
          </Badge>
          <Button
            variant="outline"
            onClick={handlePrint}
            className="print:hidden"
          >
            <Printer className="mr-2 h-4 w-4" />
            Print
          </Button>
          {canEdit && (
            <Button
              onClick={() => navigate(ROUTES.MEDICAL_RECORDS.EDIT(recordId))}
              className="print:hidden"
            >
              <Pencil className="mr-2 h-4 w-4" />
              Edit
            </Button>
          )}
          {canDelete && (
            <Button
              variant="destructive"
              onClick={() => setDeleteDialogOpen(true)}
              className="print:hidden"
            >
              <Trash className="mr-2 h-4 w-4" />
              Delete
            </Button>
          )}
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {/* Patient Information */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              Patient Information
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {record.patient && (
              <>
                <div>
                  <p className="text-sm text-muted-foreground">Name</p>
                  <p className="font-medium">{record.patient.name}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Species / Breed</p>
                  <p className="font-medium capitalize">
                    {record.patient.species}
                    {record.patient.breed && ` - ${record.patient.breed}`}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Owner</p>
                  <p className="font-medium">{record.patient.owner?.fullName || 'N/A'}</p>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => navigate(ROUTES.PATIENTS.VIEW(record.patientId))}
                  className="w-full"
                >
                  View Patient Details
                </Button>
              </>
            )}
          </CardContent>
        </Card>

        {/* Veterinarian Information */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Stethoscope className="h-5 w-5" />
              Veterinarian
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {record.veterinarian && (
              <>
                <div>
                  <p className="text-sm text-muted-foreground">Name</p>
                  <p className="font-medium">
                    Dr. {record.veterinarian.firstName} {record.veterinarian.lastName}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Email</p>
                  <p className="font-medium">{record.veterinarian.email}</p>
                </div>
              </>
            )}
            {record.appointmentId && (
              <>
                <Separator />
                <div>
                  <p className="text-sm text-muted-foreground">Related Appointment</p>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => navigate(ROUTES.APPOINTMENTS.VIEW(record.appointmentId!))}
                    className="w-full mt-2"
                  >
                    View Appointment #{record.appointmentId}
                  </Button>
                </div>
              </>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Chief Complaint */}
      {record.chiefComplaint && (
        <Card>
          <CardHeader>
            <CardTitle>Chief Complaint</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap">{record.chiefComplaint}</p>
          </CardContent>
        </Card>
      )}

      {/* Clinical Findings */}
      {record.clinicalFindings && (
        <Card>
          <CardHeader>
            <CardTitle>Clinical Findings</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap">{record.clinicalFindings}</p>
          </CardContent>
        </Card>
      )}

      {/* Diagnosis */}
      {record.diagnosis && (
        <Card>
          <CardHeader>
            <CardTitle>Diagnosis</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap">{record.diagnosis}</p>
          </CardContent>
        </Card>
      )}

      {/* Treatment */}
      {record.treatment && (
        <Card>
          <CardHeader>
            <CardTitle>Treatment</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap">{record.treatment}</p>
          </CardContent>
        </Card>
      )}

      {/* Prescriptions */}
      {record.prescriptions && record.prescriptions.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Prescriptions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {record.prescriptions.map((prescription, index) => (
                <div key={index} className="border rounded-lg p-4">
                  <h4 className="font-semibold text-lg mb-2">{prescription.medication}</h4>
                  <div className="grid gap-2 text-sm">
                    <div>
                      <span className="text-muted-foreground">Dosage:</span> {prescription.dosage}
                    </div>
                    <div>
                      <span className="text-muted-foreground">Frequency:</span> {prescription.frequency}
                    </div>
                    <div>
                      <span className="text-muted-foreground">Duration:</span> {prescription.duration}
                    </div>
                    {prescription.instructions && (
                      <div>
                        <span className="text-muted-foreground">Instructions:</span>{' '}
                        {prescription.instructions}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Lab Results */}
      {record.labResults && (
        <Card>
          <CardHeader>
            <CardTitle>Lab Results</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap">{record.labResults}</p>
          </CardContent>
        </Card>
      )}

      {/* Follow-up Instructions */}
      {record.followUpInstructions && (
        <Card>
          <CardHeader>
            <CardTitle>Follow-up Instructions</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap">{record.followUpInstructions}</p>
          </CardContent>
        </Card>
      )}

      {/* Metadata */}
      <Card>
        <CardHeader>
          <CardTitle>Record Information</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 md:grid-cols-2">
          <div>
            <p className="text-sm text-muted-foreground">Created</p>
            <p className="font-medium">{formatDate(record.createdAt, 'MMM dd, yyyy h:mm a')}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Last Updated</p>
            <p className="font-medium">{formatDate(record.updatedAt, 'MMM dd, yyyy h:mm a')}</p>
          </div>
        </CardContent>
      </Card>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDelete}
        title="Delete Medical Record"
        description={`Are you sure you want to delete this medical record for ${record.patient?.name}? This action cannot be undone and all associated data will be permanently removed.`}
        confirmText="Delete Record"
        variant="danger"
      />
    </div>
  )
}
