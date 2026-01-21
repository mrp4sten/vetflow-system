import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ColumnDef } from '@tanstack/react-table'
import { Heart, MoreHorizontal, Plus, Eye, Edit, Trash, User, Download, FileText } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { DataTable } from '@presentation/components/shared/DataTable/DataTable'
import { Badge } from '@presentation/components/ui/badge'
import { Checkbox } from '@presentation/components/ui/checkbox'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@presentation/components/ui/dropdown-menu'
import { usePatients, useDeactivatePatient } from '@presentation/hooks/usePatients'
import type { Patient } from '@domain/models/Patient'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'
import { formatAge } from '@infrastructure/utils/date-utils'
import { exportToCSV } from '@infrastructure/utils/export-utils'
import { exportTableToPDF } from '@infrastructure/utils/pdf-utils'
import { BulkActionsToolbar } from '@presentation/components/shared/BulkActions/BulkActionsToolbar'
import { ConfirmDialog } from '@presentation/components/shared/ConfirmDialog/ConfirmDialog'

export const PatientsPage: React.FC = () => {
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const [speciesFilter, setSpeciesFilter] = useState<string | undefined>()
  const [selectedPatients, setSelectedPatients] = useState<Patient[]>([])
  const [bulkDeleteDialogOpen, setBulkDeleteDialogOpen] = useState(false)

  const { data: patients = [], isLoading } = usePatients({ species: speciesFilter })
  const deactivatePatient = useDeactivatePatient()

  const canEdit = hasAnyRole(['admin', 'veterinarian', 'assistant'])
  const canDelete = hasAnyRole(['admin'])

  const handleExportCSV = () => {
    const exportData = patients.map(patient => ({
      id: patient.id,
      name: patient.name,
      species: patient.species,
      breed: patient.breed || '',
      gender: patient.gender,
      birthDate: patient.birthDate,
      age: formatAge(patient.birthDate),
      weight: `${patient.weight} kg`,
      color: patient.color || '',
      microchipId: patient.microchipId || '',
      owner: patient.owner?.name || '',
      ownerEmail: patient.owner?.email || '',
      ownerPhone: patient.owner?.phone || '',
      isActive: patient.isActive ? 'Active' : 'Inactive',
      medicalHistory: patient.medicalHistory || '',
      allergies: patient.allergies || '',
      createdAt: new Date(patient.createdAt).toLocaleDateString(),
    }))

    exportToCSV(
      exportData,
      `patients-export-${new Date().toISOString().split('T')[0]}.csv`
    )
    toast.success('Patients data exported successfully')
  }

  const handleExportPDF = () => {
    const pdfData = patients.map(patient => ({
      id: patient.id,
      name: patient.name,
      species: patient.species,
      breed: patient.breed || 'Mixed',
      gender: patient.gender,
      age: formatAge(patient.birthDate),
      weight: `${patient.weight} kg`,
      owner: patient.owner?.fullName || '',
      ownerPhone: patient.owner?.phoneNumber || '',
      isActive: patient.isActive ? 'Active' : 'Inactive'
    }))

    const columns = [
      { header: 'ID', dataKey: 'id' },
      { header: 'Name', dataKey: 'name' },
      { header: 'Species', dataKey: 'species' },
      { header: 'Breed', dataKey: 'breed' },
      { header: 'Gender', dataKey: 'gender' },
      { header: 'Age', dataKey: 'age' },
      { header: 'Weight', dataKey: 'weight' },
      { header: 'Owner', dataKey: 'owner' },
      { header: 'Phone', dataKey: 'ownerPhone' },
      { header: 'Status', dataKey: 'isActive' }
    ]

    exportTableToPDF(
      pdfData,
      columns,
      `patients-${new Date().toISOString().split('T')[0]}.pdf`,
      'VetFlow - Patient Records'
    )
    toast.success('PDF exported successfully')
  }

  const handleBulkExportCSV = () => {
    const exportData = selectedPatients.map(patient => ({
      id: patient.id,
      name: patient.name,
      species: patient.species,
      breed: patient.breed || '',
      gender: patient.gender,
      birthDate: patient.birthDate,
      age: formatAge(patient.birthDate),
      weight: `${patient.weight} kg`,
      owner: patient.owner?.fullName || '',
      ownerPhone: patient.owner?.phoneNumber || '',
      isActive: patient.isActive ? 'Active' : 'Inactive',
    }))

    exportToCSV(
      exportData,
      `patients-bulk-export-${new Date().toISOString().split('T')[0]}.csv`
    )
    toast.success(`${selectedPatients.length} patients exported to CSV`)
  }

  const handleBulkExportPDF = () => {
    const pdfData = selectedPatients.map(patient => ({
      id: patient.id,
      name: patient.name,
      species: patient.species,
      breed: patient.breed || 'Mixed',
      gender: patient.gender,
      age: formatAge(patient.birthDate),
      weight: `${patient.weight} kg`,
      owner: patient.owner?.fullName || '',
      ownerPhone: patient.owner?.phoneNumber || '',
      isActive: patient.isActive ? 'Active' : 'Inactive'
    }))

    const columns = [
      { header: 'ID', dataKey: 'id' },
      { header: 'Name', dataKey: 'name' },
      { header: 'Species', dataKey: 'species' },
      { header: 'Breed', dataKey: 'breed' },
      { header: 'Gender', dataKey: 'gender' },
      { header: 'Age', dataKey: 'age' },
      { header: 'Weight', dataKey: 'weight' },
      { header: 'Owner', dataKey: 'owner' },
      { header: 'Phone', dataKey: 'ownerPhone' },
      { header: 'Status', dataKey: 'isActive' }
    ]

    exportTableToPDF(
      pdfData,
      columns,
      `patients-bulk-${new Date().toISOString().split('T')[0]}.pdf`,
      'VetFlow - Selected Patient Records'
    )
    toast.success(`${selectedPatients.length} patients exported to PDF`)
  }

  const handleBulkDelete = async () => {
    try {
      // Note: This would need a backend endpoint for bulk delete
      // For now, we'll delete them one by one
      for (const patient of selectedPatients) {
        await deactivatePatient.mutateAsync(patient.id)
      }
      toast.success(`${selectedPatients.length} patients deactivated successfully`)
      setSelectedPatients([])
      setBulkDeleteDialogOpen(false)
    } catch (error) {
      toast.error('Failed to deactivate patients')
    }
  }

  const columns: ColumnDef<Patient>[] = [
    {
      id: 'select',
      header: ({ table }) => (
        <div data-no-row-click onClick={(e) => e.stopPropagation()}>
          <Checkbox
            checked={table.getIsAllPageRowsSelected()}
            onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
            aria-label="Select all"
          />
        </div>
      ),
      cell: ({ row }) => (
        <div data-no-row-click onClick={(e) => e.stopPropagation()}>
          <Checkbox
            checked={row.getIsSelected()}
            onCheckedChange={(value) => row.toggleSelected(!!value)}
            aria-label="Select row"
          />
        </div>
      ),
      enableSorting: false,
      enableHiding: false,
    },
    {
      accessorKey: 'name',
      header: 'Patient',
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <Heart className="h-4 w-4 text-pink-500" />
          <div>
            <div className="font-medium">{row.original.name}</div>
            <div className="text-sm text-muted-foreground">
              {row.original.species} • {row.original.breed || 'Mixed'}
            </div>
          </div>
        </div>
      ),
    },
    {
      accessorKey: 'owner',
      header: 'Owner',
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <User className="h-4 w-4 text-muted-foreground" />
          <div>
            <div className="font-medium">{row.original.owner?.name}</div>
            <div className="text-sm text-muted-foreground">
              {row.original.owner?.phone}
            </div>
          </div>
        </div>
      ),
    },
    {
      accessorKey: 'gender',
      header: 'Gender',
      cell: ({ row }) => (
        <Badge variant="outline" className="capitalize">
          {row.original.gender}
        </Badge>
      ),
    },
    {
      accessorKey: 'birthDate',
      header: 'Age',
      cell: ({ row }) => {
        if (!row.original.birthDate) return <span className="text-muted-foreground">Unknown</span>
        return <span>{formatAge(row.original.birthDate)}</span>
      },
    },
    {
      accessorKey: 'weight',
      header: 'Weight',
      cell: ({ row }) => {
        if (!row.original.weight) return <span className="text-muted-foreground">-</span>
        return (
          <div className="text-sm">
            <div>{row.original.weight} kg</div>
            <div className="text-muted-foreground">
              {row.original.weightInLbs?.toFixed(1)} lbs
            </div>
          </div>
        )
      },
    },
    {
      accessorKey: 'isActive',
      header: 'Status',
      cell: ({ row }) => (
        <Badge variant={row.original.isActive ? 'default' : 'secondary'}>
          {row.original.isActive ? 'Active' : 'Inactive'}
        </Badge>
      ),
    },
    {
      id: 'actions',
      cell: ({ row }) => {
        const patient = row.original

        // Note: Deactivate functionality commented out - backend doesn't support isActive field yet
        // const handleDeactivate = async () => {
        //   if (confirm(`Are you sure you want to deactivate ${patient.name}? This will mark the patient as inactive but preserve all medical history.`)) {
        //     await deactivatePatient.mutateAsync(patient.id)
        //   }
        // }

        return (
          <div data-no-row-click>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="h-8 w-8 p-0">
                  <span className="sr-only">Open menu</span>
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>Actions</DropdownMenuLabel>
                <DropdownMenuItem
                  onClick={() => navigate(ROUTES.PATIENTS.VIEW(patient.id))}
                >
                  <Eye className="mr-2 h-4 w-4" />
                  View Details
                </DropdownMenuItem>
                {canEdit && (
                  <>
                    <DropdownMenuItem
                      onClick={() => navigate(ROUTES.PATIENTS.EDIT(patient.id))}
                    >
                      <Edit className="mr-2 h-4 w-4" />
                      Edit
                    </DropdownMenuItem>
                    {/* Deactivate option removed - backend doesn't support isActive field */}
                  </>
                )}
                {canDelete && (
                  <>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem className="text-red-600">
                      <Trash className="mr-2 h-4 w-4" />
                      Delete
                    </DropdownMenuItem>
                  </>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        )
      },
    },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Patients</h2>
          <p className="text-muted-foreground">
            Manage patient records and medical history
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={handleExportCSV}>
            <Download className="mr-2 h-4 w-4" />
            Export CSV
          </Button>
          <Button variant="outline" onClick={handleExportPDF}>
            <FileText className="mr-2 h-4 w-4" />
            Export PDF
          </Button>
          {canEdit && (
            <Button onClick={() => navigate(ROUTES.PATIENTS.CREATE)}>
              <Plus className="mr-2 h-4 w-4" />
              Register Patient
            </Button>
          )}
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Patients</CardTitle>
          <CardDescription>
            View and manage all registered patients
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <BulkActionsToolbar
            selectedCount={selectedPatients.length}
            onClearSelection={() => setSelectedPatients([])}
            onExportCSV={handleBulkExportCSV}
            onExportPDF={handleBulkExportPDF}
            onDelete={canDelete ? () => setBulkDeleteDialogOpen(true) : undefined}
            canDelete={canDelete}
          />
          {isLoading ? (
            <div className="flex justify-center py-8">
              <LoadingSpinner size="lg" />
            </div>
          ) : (
            <DataTable
              columns={columns}
              data={patients}
              searchKey="name"
              searchPlaceholder="Search by patient name..."
              onRowClick={(patient) => navigate(ROUTES.PATIENTS.VIEW(patient.id))}
              enableRowSelection={true}
              onSelectionChange={setSelectedPatients}
            />
          )}
        </CardContent>
      </Card>

      {/* Bulk Delete Confirmation Dialog */}
      <ConfirmDialog
        open={bulkDeleteDialogOpen}
        onOpenChange={setBulkDeleteDialogOpen}
        onConfirm={handleBulkDelete}
        title="Deactivate Multiple Patients"
        description={`Are you sure you want to deactivate ${selectedPatients.length} patient(s)? This action will mark them as inactive but won't delete their records.`}
        confirmText={`Deactivate ${selectedPatients.length} Patient(s)`}
        variant="danger"
      />
    </div>
  )
}