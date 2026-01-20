import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ColumnDef } from '@tanstack/react-table'
import { format } from 'date-fns'
import { Calendar, Clock, MoreHorizontal, Plus, Eye, Edit, Trash } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { DataTable } from '@presentation/components/shared/DataTable/DataTable'
import { Badge } from '@presentation/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@presentation/components/ui/dropdown-menu'
import { useAppointments, useDeleteAppointment, useUpdateAppointmentStatus } from '@presentation/hooks/useAppointments'
import type { Appointment, AppointmentStatus } from '@domain/models/Appointment'
import { APPOINTMENT_STATUS_COLORS, APPOINTMENT_STATUS_DISPLAY, APPOINTMENT_TYPE_DISPLAY, APPOINTMENT_PRIORITY_COLORS } from '@shared/constants/appointment-status'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'
import { AdvancedFilter } from '@presentation/components/shared/AdvancedFilter/AdvancedFilter'

interface FilterPreset {
  id: string
  name: string
  filters: Record<string, any>
}

export const AppointmentsPage: React.FC = () => {
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const [statusFilter, setStatusFilter] = useState<AppointmentStatus | undefined>()
  const [activeFilters, setActiveFilters] = useState<Record<string, any>>({})
  const [filterPresets, setFilterPresets] = useState<FilterPreset[]>([])

  // Load presets from localStorage
  useEffect(() => {
    const stored = localStorage.getItem('appointment-filter-presets')
    if (stored) {
      setFilterPresets(JSON.parse(stored))
    }
  }, [])

  const { data: appointments = [], isLoading } = useAppointments({ status: statusFilter })
  const deleteAppointment = useDeleteAppointment()
  const updateStatus = useUpdateAppointmentStatus()

  const canEdit = hasAnyRole(['admin', 'veterinarian'])
  const canDelete = hasAnyRole(['admin'])

  const handleApplyFilters = (filters: Record<string, any>) => {
    setActiveFilters(filters)
    if (filters.status) {
      setStatusFilter(filters.status as AppointmentStatus)
    } else {
      setStatusFilter(undefined)
    }
  }

  const handleSavePreset = (preset: FilterPreset) => {
    const updated = [...filterPresets, preset]
    setFilterPresets(updated)
    localStorage.setItem('appointment-filter-presets', JSON.stringify(updated))
    toast.success(`Filter preset "${preset.name}" saved`)
  }

  const handleDeletePreset = (presetId: string) => {
    const updated = filterPresets.filter((p) => p.id !== presetId)
    setFilterPresets(updated)
    localStorage.setItem('appointment-filter-presets', JSON.stringify(updated))
    toast.success('Filter preset deleted')
  }

  const filterFields = [
    {
      name: 'status',
      label: 'Status',
      type: 'select' as const,
      options: Object.entries(APPOINTMENT_STATUS_DISPLAY).map(([value, label]) => ({
        value,
        label,
      })),
    },
    {
      name: 'type',
      label: 'Type',
      type: 'select' as const,
      options: Object.entries(APPOINTMENT_TYPE_DISPLAY).map(([value, label]) => ({
        value,
        label,
      })),
    },
    {
      name: 'date',
      label: 'Date',
      type: 'date' as const,
    },
  ]

  const columns: ColumnDef<Appointment>[] = [
    {
      accessorKey: 'scheduledAt',
      header: 'Date & Time',
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <Calendar className="h-4 w-4 text-muted-foreground" />
          <div>
            <div className="font-medium">
              {format(new Date(row.original.scheduledAt), 'MMM dd, yyyy')}
            </div>
            <div className="text-sm text-muted-foreground">
              {format(new Date(row.original.scheduledAt), 'h:mm a')}
            </div>
          </div>
        </div>
      ),
    },
    {
      accessorKey: 'patient',
      header: 'Patient',
      cell: ({ row }) => (
        <div>
          <div className="font-medium">{row.original.patient?.name}</div>
          <div className="text-sm text-muted-foreground">
            {row.original.patient?.owner?.fullName}
          </div>
        </div>
      ),
    },
    {
      accessorKey: 'type',
      header: 'Type',
      cell: ({ row }) => (
        <Badge variant="outline">
          {APPOINTMENT_TYPE_DISPLAY[row.original.type]}
        </Badge>
      ),
    },
    {
      accessorKey: 'status',
      header: 'Status',
      cell: ({ row }) => {
        const status = row.original.status
        const color = APPOINTMENT_STATUS_COLORS[status]
        return (
          <Badge className={`bg-${color}-100 text-${color}-800 hover:bg-${color}-200`}>
            {APPOINTMENT_STATUS_DISPLAY[status]}
          </Badge>
        )
      },
    },
    {
      accessorKey: 'priority',
      header: 'Priority',
      cell: ({ row }) => {
        const priority = row.original.priority
        const color = APPOINTMENT_PRIORITY_COLORS[priority]
        return (
          <Badge variant="outline" className={`border-${color}-300 text-${color}-700`}>
            {priority}
          </Badge>
        )
      },
    },
    {
      accessorKey: 'duration',
      header: 'Duration',
      cell: ({ row }) => (
        <div className="flex items-center gap-1">
          <Clock className="h-3 w-3 text-muted-foreground" />
          <span className="text-sm">{row.original.duration} min</span>
        </div>
      ),
    },
    {
      id: 'actions',
      cell: ({ row }) => {
        const appointment = row.original

        const handleStatusChange = async (status: AppointmentStatus) => {
          await updateStatus.mutateAsync({ id: appointment.id, status })
        }

        const handleDelete = async () => {
          if (confirm('Are you sure you want to delete this appointment?')) {
            await deleteAppointment.mutateAsync(appointment.id)
          }
        }

        return (
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
                onClick={() => navigate(ROUTES.APPOINTMENTS.VIEW(appointment.id))}
              >
                <Eye className="mr-2 h-4 w-4" />
                View Details
              </DropdownMenuItem>
              {canEdit && (
                <>
                  <DropdownMenuItem
                    onClick={() => navigate(ROUTES.APPOINTMENTS.EDIT(appointment.id))}
                  >
                    <Edit className="mr-2 h-4 w-4" />
                    Edit
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuLabel>Update Status</DropdownMenuLabel>
                  {Object.entries(APPOINTMENT_STATUS_DISPLAY).map(([key, value]) => (
                    <DropdownMenuItem
                      key={key}
                      onClick={() => handleStatusChange(key as AppointmentStatus)}
                      disabled={appointment.status === key}
                    >
                      {value}
                    </DropdownMenuItem>
                  ))}
                </>
              )}
              {canDelete && (
                <>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    className="text-red-600"
                    onClick={handleDelete}
                  >
                    <Trash className="mr-2 h-4 w-4" />
                    Delete
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        )
      },
    },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Appointments</h2>
          <p className="text-muted-foreground">
            Manage veterinary appointments and schedules
          </p>
        </div>
        <div className="flex gap-2">
          <AdvancedFilter
            fields={filterFields}
            onApplyFilters={handleApplyFilters}
            activeFilters={activeFilters}
            presets={filterPresets}
            onSavePreset={handleSavePreset}
            onDeletePreset={handleDeletePreset}
          />
          <Button
            variant="outline"
            onClick={() => navigate(ROUTES.APPOINTMENTS.CALENDAR)}
          >
            <Calendar className="mr-2 h-4 w-4" />
            Calendar View
          </Button>
          {canEdit && (
            <Button onClick={() => navigate(ROUTES.APPOINTMENTS.CREATE)}>
              <Plus className="mr-2 h-4 w-4" />
              New Appointment
            </Button>
          )}
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Appointments</CardTitle>
          <CardDescription>
            View and manage all scheduled appointments
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex justify-center py-8">
              <LoadingSpinner size="lg" />
            </div>
          ) : (
            <DataTable
              columns={columns}
              data={appointments}
              searchKey="patient.name"
              searchPlaceholder="Search by patient name..."
              onRowClick={(appointment) => navigate(ROUTES.APPOINTMENTS.VIEW(appointment.id))}
            />
          )}
        </CardContent>
      </Card>
    </div>
  )
}