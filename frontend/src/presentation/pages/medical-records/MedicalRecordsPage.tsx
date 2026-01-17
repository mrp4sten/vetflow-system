import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ColumnDef } from '@tanstack/react-table'
import { FileText, Plus, Eye } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Badge } from '@presentation/components/ui/badge'
import { DataTable } from '@presentation/components/shared/DataTable/DataTable'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useMedicalRecords } from '@presentation/hooks/useMedicalRecords'
import type { MedicalRecord } from '@domain/models/MedicalRecord'
import { ROUTES } from '@shared/constants/routes'
import { RECORD_TYPE_DISPLAY, RECORD_TYPE_COLORS } from '@shared/constants/medical-record-types'
import { formatDate } from '@infrastructure/utils/date-utils'

export const MedicalRecordsPage: React.FC = () => {
  const navigate = useNavigate()
  const [typeFilter, setTypeFilter] = useState<string>('')

  const { data: records = [], isLoading, error } = useMedicalRecords(
    typeFilter ? { type: typeFilter as any } : undefined
  )

  const columns: ColumnDef<MedicalRecord>[] = [
    {
      accessorKey: 'id',
      header: 'ID',
      cell: ({ row }) => <span className="font-mono text-sm">#{row.original.id}</span>,
    },
    {
      accessorKey: 'recordDate',
      header: 'Date',
      cell: ({ row }) => formatDate(row.original.recordDate),
    },
    {
      accessorKey: 'type',
      header: 'Type',
      cell: ({ row }) => {
        const type = row.original.type
        return (
          <Badge className={RECORD_TYPE_COLORS[type]}>
            {RECORD_TYPE_DISPLAY[type]}
          </Badge>
        )
      },
    },
    {
      accessorKey: 'patient',
      header: 'Patient',
      cell: ({ row }) => (
        <div>
          <div className="font-medium">{row.original.patient?.name || 'Unknown'}</div>
          <div className="text-sm text-muted-foreground">
            {row.original.patient?.species || 'N/A'}
          </div>
        </div>
      ),
    },
    {
      accessorKey: 'veterinarian',
      header: 'Veterinarian',
      cell: ({ row }) => {
        const vet = row.original.veterinarian
        return vet ? `Dr. ${vet.firstName} ${vet.lastName}` : 'N/A'
      },
    },
    {
      accessorKey: 'diagnosis',
      header: 'Diagnosis',
      cell: ({ row }) => {
        const diagnosis = row.original.diagnosis
        if (!diagnosis) return <span className="text-muted-foreground">-</span>
        return (
          <span className="line-clamp-1" title={diagnosis}>
            {diagnosis}
          </span>
        )
      },
    },
    {
      id: 'actions',
      header: 'Actions',
      cell: ({ row }) => (
        <div className="flex gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate(ROUTES.MEDICAL_RECORDS.VIEW(row.original.id))}
          >
            <Eye className="h-4 w-4 mr-1" />
            View
          </Button>
        </div>
      ),
    },
  ]

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4">
        <p className="text-lg text-red-600">Error loading medical records</p>
        <Button onClick={() => window.location.reload()}>Retry</Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <FileText className="h-8 w-8 text-primary" />
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Medical Records</h2>
            <p className="text-muted-foreground">
              View and manage patient medical records
            </p>
          </div>
        </div>
        <Button onClick={() => navigate(ROUTES.MEDICAL_RECORDS.CREATE)}>
          <Plus className="mr-2 h-4 w-4" />
          New Record
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={records}
        searchKey="patient.name"
        searchPlaceholder="Search by patient name..."
      />
    </div>
  )
}
