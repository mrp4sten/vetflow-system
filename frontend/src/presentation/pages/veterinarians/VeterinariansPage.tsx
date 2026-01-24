import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Stethoscope, Eye, Mail, Calendar, CheckCircle, XCircle } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Button } from '@presentation/components/ui/button'
import { Input } from '@presentation/components/ui/input'
import { Badge } from '@presentation/components/ui/badge'
import { Switch } from '@presentation/components/ui/switch'
import { Label } from '@presentation/components/ui/label'
import { useVeterinarians } from '@presentation/hooks/useVeterinarians'
import { ROUTES } from '@shared/constants/routes'
import { formatDateTime } from '@shared/utils/dateFormatter'
import type { Veterinarian } from '@/domain/models/Veterinarian'

export const VeterinariansPage = () => {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')
  const [includeInactive, setIncludeInactive] = useState(false)
  
  const { data: veterinarians = [], isLoading, error } = useVeterinarians(includeInactive)

  // Filter veterinarians based on search
  const filteredVeterinarians = veterinarians.filter((vet) => {
    const searchLower = searchTerm.toLowerCase()
    return (
      vet.username.toLowerCase().includes(searchLower) ||
      vet.email.toLowerCase().includes(searchLower)
    )
  })

  const handleViewDetails = (id: number) => {
    navigate(ROUTES.VETERINARIANS.VIEW(id))
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading veterinarians...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-96">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-destructive">Error Loading Veterinarians</CardTitle>
            <CardDescription>
              {error instanceof Error ? error.message : 'An unexpected error occurred'}
            </CardDescription>
          </CardHeader>
        </Card>
      </div>
    )
  }

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight flex items-center gap-2">
            <Stethoscope className="h-8 w-8" />
            Veterinarians
          </h1>
          <p className="text-muted-foreground mt-1">
            Manage and view veterinarian information
          </p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardHeader>
          <CardTitle>Filters</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <Input
                placeholder="Search by name or email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full"
              />
            </div>
            <div className="flex items-center space-x-2">
              <Switch
                id="inactive-toggle"
                checked={includeInactive}
                onCheckedChange={setIncludeInactive}
              />
              <Label htmlFor="inactive-toggle">Show inactive</Label>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Veterinarians</CardTitle>
            <Stethoscope className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{veterinarians.length}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {veterinarians.filter(v => v.isActive).length}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Inactive</CardTitle>
            <XCircle className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-muted-foreground">
              {veterinarians.filter(v => !v.isActive).length}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Veterinarians List */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredVeterinarians.length === 0 ? (
          <Card className="col-span-full">
            <CardContent className="flex flex-col items-center justify-center py-12">
              <Stethoscope className="h-12 w-12 text-muted-foreground mb-4" />
              <p className="text-lg font-medium">No veterinarians found</p>
              <p className="text-sm text-muted-foreground mt-1">
                {searchTerm
                  ? 'Try adjusting your search criteria'
                  : 'No veterinarians available'}
              </p>
            </CardContent>
          </Card>
        ) : (
          filteredVeterinarians.map((vet) => (
            <VeterinarianCard
              key={vet.id}
              veterinarian={vet}
              onViewDetails={handleViewDetails}
            />
          ))
        )}
      </div>
    </div>
  )
}

interface VeterinarianCardProps {
  veterinarian: Veterinarian
  onViewDetails: (id: number) => void
}

const VeterinarianCard: React.FC<VeterinarianCardProps> = ({ veterinarian, onViewDetails }) => {
  return (
    <Card className={!veterinarian.isActive ? 'opacity-60' : ''}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center">
              <Stethoscope className="h-6 w-6 text-primary" />
            </div>
            <div>
              <CardTitle className="text-lg">{veterinarian.username}</CardTitle>
              <Badge variant={veterinarian.isActive ? 'default' : 'secondary'} className="mt-1">
                {veterinarian.isActive ? 'Active' : 'Inactive'}
              </Badge>
            </div>
          </div>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        <div className="space-y-2">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Mail className="h-4 w-4" />
            <span>{veterinarian.email}</span>
          </div>
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Calendar className="h-4 w-4" />
            <span>Joined {formatDateTime(veterinarian.createdAt)}</span>
          </div>
          {veterinarian.lastLogin && (
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <CheckCircle className="h-4 w-4" />
              <span>Last login: {formatDateTime(veterinarian.lastLogin)}</span>
            </div>
          )}
        </div>

        <Button
          onClick={() => onViewDetails(veterinarian.id)}
          variant="outline"
          className="w-full"
        >
          <Eye className="h-4 w-4 mr-2" />
          View Details
        </Button>
      </CardContent>
    </Card>
  )
}

export default VeterinariansPage
