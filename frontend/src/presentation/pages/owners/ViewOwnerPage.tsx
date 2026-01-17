import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { format } from 'date-fns'
import { ArrowLeft, User, Mail, Phone, MapPin, Edit, Trash, Heart, Plus } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Separator } from '@presentation/components/ui/separator'
import { ConfirmDialog } from '@presentation/components/shared/ConfirmDialog/ConfirmDialog'
import { useOwner, useDeleteOwner } from '@presentation/hooks/useOwners'
import { usePatientsByOwner } from '@presentation/hooks/usePatients'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'
import { Badge } from '@presentation/components/ui/badge'

export const ViewOwnerPage: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  
  const ownerId = parseInt(id || '0')
  const { data: owner, isLoading: loadingOwner } = useOwner(ownerId)
  const { data: patients = [], isLoading: loadingPatients } = usePatientsByOwner(ownerId)
  const deleteOwner = useDeleteOwner()

  const canEdit = hasAnyRole(['admin', 'veterinarian', 'assistant'])
  const canDelete = hasAnyRole(['admin'])
  const hasPatients = patients.length > 0

  if (loadingOwner) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!owner) {
    return (
      <div className="text-center py-12">
        <p className="text-lg text-muted-foreground">Owner not found</p>
        <Button
          variant="outline"
          className="mt-4"
          onClick={() => navigate(ROUTES.OWNERS.LIST)}
        >
          Back to Owners
        </Button>
      </div>
    )
  }

  const handleDelete = async () => {
    try {
      await deleteOwner.mutateAsync(ownerId)
      toast.success(`${owner.fullName} has been deleted successfully`)
      navigate(ROUTES.OWNERS.LIST)
    } catch (error) {
      toast.error('Failed to delete owner')
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
            onClick={() => navigate(ROUTES.OWNERS.LIST)}
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h2 className="text-3xl font-bold tracking-tight flex items-center gap-2">
              <User className="h-8 w-8 text-blue-500" />
              {owner.fullName}
            </h2>
            <p className="text-muted-foreground">
              Pet Owner • {patients.length} {patients.length === 1 ? 'Pet' : 'Pets'}
            </p>
          </div>
        </div>

        {canEdit && (
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => navigate(ROUTES.OWNERS.EDIT(ownerId))}
            >
              <Edit className="mr-2 h-4 w-4" />
              Edit
            </Button>
            
            {canDelete && (
              <Button 
                variant="destructive" 
                onClick={() => setDeleteDialogOpen(true)}
              >
                <Trash className="mr-2 h-4 w-4" />
                Delete
              </Button>
            )}
          </div>
        )}
      </div>

      {/* Main Content */}
      <div className="grid gap-6 md:grid-cols-3">
        {/* Owner Info - 2 columns */}
        <div className="md:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Contact Information</CardTitle>
              <CardDescription>
                Primary contact details for this owner
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid gap-4 md:grid-cols-2">
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Full Name</p>
                  <p className="font-medium">{owner.fullName}</p>
                </div>

                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <Mail className="h-4 w-4 text-muted-foreground" />
                    <p className="text-sm text-muted-foreground">Email</p>
                  </div>
                  <a 
                    href={`mailto:${owner.email}`}
                    className="font-medium text-blue-600 hover:underline"
                  >
                    {owner.email}
                  </a>
                </div>

                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <Phone className="h-4 w-4 text-muted-foreground" />
                    <p className="text-sm text-muted-foreground">Phone Number</p>
                  </div>
                  <a 
                    href={`tel:${owner.phoneNumber}`}
                    className="font-medium text-blue-600 hover:underline"
                  >
                    {owner.phoneNumber}
                  </a>
                </div>
              </div>

              {(owner.address || owner.city || owner.state || owner.zipCode) && (
                <>
                  <Separator />
                  
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <MapPin className="h-4 w-4 text-muted-foreground" />
                      <p className="text-sm text-muted-foreground">Address</p>
                    </div>
                    <div className="font-medium">
                      {owner.address && <p>{owner.address}</p>}
                      {(owner.city || owner.state || owner.zipCode) && (
                        <p>
                          {owner.city && owner.state
                            ? `${owner.city}, ${owner.state}`
                            : owner.city || owner.state}
                          {owner.zipCode && ` ${owner.zipCode}`}
                        </p>
                      )}
                    </div>
                  </div>
                </>
              )}

              <Separator />

              {/* Registration Info */}
              <div className="text-sm text-muted-foreground">
                <p>Registered on {format(new Date(owner.createdAt), 'MMMM d, yyyy')}</p>
                {owner.updatedAt !== owner.createdAt && (
                  <p>Last updated {format(new Date(owner.updatedAt), 'MMMM d, yyyy')}</p>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Pets Section */}
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="flex items-center gap-2">
                    <Heart className="h-5 w-5 text-pink-500" />
                    Registered Pets ({patients.length})
                  </CardTitle>
                  <CardDescription>
                    All pets registered under this owner
                  </CardDescription>
                </div>
                {canEdit && (
                  <Button
                    size="sm"
                    onClick={() => navigate(ROUTES.PATIENTS.CREATE, { 
                      state: { ownerId: owner.id }
                    })}
                  >
                    <Plus className="mr-2 h-4 w-4" />
                    Add Pet
                  </Button>
                )}
              </div>
            </CardHeader>
            <CardContent>
              {loadingPatients ? (
                <div className="flex justify-center py-4">
                  <LoadingSpinner size="md" />
                </div>
              ) : patients.length > 0 ? (
                <div className="space-y-3">
                  {patients.map((patient) => (
                    <div
                      key={patient.id}
                      className="flex items-center justify-between p-3 border rounded-lg hover:bg-accent cursor-pointer transition-colors"
                      onClick={() => navigate(ROUTES.PATIENTS.VIEW(patient.id))}
                    >
                      <div className="flex items-center gap-3">
                        <Heart className="h-5 w-5 text-pink-500" />
                        <div>
                          <p className="font-medium">{patient.name}</p>
                          <p className="text-sm text-muted-foreground">
                            {patient.species} • {patient.breed || 'Mixed'}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {!patient.isActive && (
                          <Badge variant="secondary">Inactive</Badge>
                        )}
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation()
                            navigate(ROUTES.PATIENTS.VIEW(patient.id))
                          }}
                        >
                          View
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8">
                  <Heart className="h-12 w-12 text-muted-foreground mx-auto mb-4 opacity-50" />
                  <p className="text-muted-foreground mb-4">No pets registered yet</p>
                  {canEdit && (
                    <Button
                      variant="outline"
                      onClick={() => navigate(ROUTES.PATIENTS.CREATE, { 
                        state: { ownerId: owner.id }
                      })}
                    >
                      <Plus className="mr-2 h-4 w-4" />
                      Register First Pet
                    </Button>
                  )}
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Quick Actions - 1 column */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Quick Actions</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <Button
                variant="outline"
                className="w-full justify-start"
                onClick={() => navigate(ROUTES.PATIENTS.CREATE, { 
                  state: { ownerId: owner.id }
                })}
              >
                <Plus className="mr-2 h-4 w-4" />
                Register New Pet
              </Button>
              
              <Button
                variant="outline"
                className="w-full justify-start"
                onClick={() => window.open(`mailto:${owner.email}`)}
              >
                <Mail className="mr-2 h-4 w-4" />
                Send Email
              </Button>

              <Button
                variant="outline"
                className="w-full justify-start"
                onClick={() => window.open(`tel:${owner.phoneNumber}`)}
              >
                <Phone className="mr-2 h-4 w-4" />
                Call Owner
              </Button>
            </CardContent>
          </Card>

          {/* Statistics Card */}
          <Card>
            <CardHeader>
              <CardTitle>Statistics</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Total Pets</span>
                <span className="font-bold text-lg">{patients.length}</span>
              </div>
              <Separator />
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Active Pets</span>
                <span className="font-bold text-lg">
                  {patients.filter(p => p.isActive).length}
                </span>
              </div>
              <Separator />
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Inactive Pets</span>
                <span className="font-bold text-lg">
                  {patients.filter(p => !p.isActive).length}
                </span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDelete}
        title="Delete Owner"
        description={
          hasPatients
            ? `⚠️ Warning: ${owner.fullName} has ${patients.length} registered pet${patients.length > 1 ? 's' : ''}. Deleting this owner may affect their patient records. Are you sure you want to continue?`
            : `Are you sure you want to delete ${owner.fullName}? This action cannot be undone.`
        }
        confirmText="Delete Owner"
        variant={hasPatients ? "warning" : "danger"}
      />
    </div>
  )
}