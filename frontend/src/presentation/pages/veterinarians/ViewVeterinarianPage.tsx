import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Stethoscope, Mail, Calendar, CheckCircle, XCircle, User } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Button } from '@presentation/components/ui/button'
import { Badge } from '@presentation/components/ui/badge'
import { useVeterinarian } from '@presentation/hooks/useVeterinarians'
import { ROUTES } from '@shared/constants/routes'
import { formatDateTime } from '@shared/utils/dateFormatter'

export const ViewVeterinarianPage = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const veterinarianId = parseInt(id || '0', 10)

  const { data: veterinarian, isLoading, error } = useVeterinarian(veterinarianId)

  const handleBack = () => {
    navigate(ROUTES.VETERINARIANS.LIST)
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading veterinarian...</p>
        </div>
      </div>
    )
  }

  if (error || !veterinarian) {
    return (
      <div className="container mx-auto py-6">
        <Card>
          <CardHeader>
            <CardTitle className="text-destructive">Error Loading Veterinarian</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-muted-foreground mb-4">
              {error instanceof Error ? error.message : 'Veterinarian not found'}
            </p>
            <Button onClick={handleBack} variant="outline">
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to List
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button onClick={handleBack} variant="ghost" size="sm">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back
        </Button>
        <div className="flex-1">
          <h1 className="text-3xl font-bold tracking-tight flex items-center gap-2">
            <Stethoscope className="h-8 w-8" />
            {veterinarian.username}
          </h1>
          <p className="text-muted-foreground mt-1">Veterinarian Details</p>
        </div>
        <Badge variant={veterinarian.isActive ? 'default' : 'secondary'} className="text-lg px-4 py-1">
          {veterinarian.isActive ? (
            <>
              <CheckCircle className="h-4 w-4 mr-2" />
              Active
            </>
          ) : (
            <>
              <XCircle className="h-4 w-4 mr-2" />
              Inactive
            </>
          )}
        </Badge>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {/* Basic Information */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              Basic Information
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">Username</label>
              <p className="text-lg">{veterinarian.username}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">Email</label>
              <div className="flex items-center gap-2 text-lg">
                <Mail className="h-4 w-4 text-muted-foreground" />
                <a href={`mailto:${veterinarian.email}`} className="text-primary hover:underline">
                  {veterinarian.email}
                </a>
              </div>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">ID</label>
              <p className="text-lg font-mono">#{veterinarian.id}</p>
            </div>
          </CardContent>
        </Card>

        {/* Activity Information */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Activity Information
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">Account Created</label>
              <p className="text-lg">{formatDateTime(veterinarian.createdAt)}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">Last Login</label>
              <p className="text-lg">
                {veterinarian.lastLogin ? formatDateTime(veterinarian.lastLogin) : 'Never'}
              </p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground">Status</label>
              <div className="flex items-center gap-2">
                {veterinarian.isActive ? (
                  <>
                    <CheckCircle className="h-5 w-5 text-green-600" />
                    <span className="text-lg text-green-600">Active</span>
                  </>
                ) : (
                  <>
                    <XCircle className="h-5 w-5 text-muted-foreground" />
                    <span className="text-lg text-muted-foreground">Inactive</span>
                  </>
                )}
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Info Card */}
      <Card className="bg-muted/50">
        <CardContent className="pt-6">
          <div className="flex items-start gap-3">
            <Stethoscope className="h-5 w-5 text-primary mt-0.5" />
            <div>
              <h3 className="font-medium mb-1">About Veterinarian Management</h3>
              <p className="text-sm text-muted-foreground">
                Veterinarians are system users with the 'veterinarian' role. They can be assigned to appointments
                and create medical records for patients. Their account status determines whether they can be assigned
                to new appointments.
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export default ViewVeterinarianPage
