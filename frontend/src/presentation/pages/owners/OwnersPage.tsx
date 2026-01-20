import { useNavigate } from 'react-router-dom'
import type { ColumnDef } from '@tanstack/react-table'
import { User, MoreHorizontal, Plus, Eye, Edit, Trash, Mail, Phone } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { DataTable } from '@presentation/components/shared/DataTable/DataTable'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@presentation/components/ui/dropdown-menu'
import { useOwners, useDeleteOwner } from '@presentation/hooks/useOwners'
import type { Owner } from '@domain/models/Owner'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'

export const OwnersPage: React.FC = () => {
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()

  const { data: owners = [], isLoading } = useOwners()
  const deleteOwner = useDeleteOwner()

  const canEdit = hasAnyRole(['admin', 'veterinarian', 'assistant'])
  const canDelete = hasAnyRole(['admin'])

  const columns: ColumnDef<Owner>[] = [
    {
      accessorKey: 'fullName',
      header: 'Name',
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <User className="h-4 w-4 text-muted-foreground" />
          <div>
            <div className="font-medium">{row.original.fullName}</div>
            <div className="text-sm text-muted-foreground">
              ID: {row.original.id}
            </div>
          </div>
        </div>
      ),
    },
    {
      accessorKey: 'email',
      header: 'Email',
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <Mail className="h-4 w-4 text-muted-foreground" />
          <span className="text-sm">{row.original.email}</span>
        </div>
      ),
    },
    {
      accessorKey: 'phoneNumber',
      header: 'Phone',
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <Phone className="h-4 w-4 text-muted-foreground" />
          <span className="text-sm">{row.original.phoneNumber}</span>
        </div>
      ),
    },
    {
      accessorKey: 'address',
      header: 'Location',
      cell: ({ row }) => {
        const { city, state } = row.original
        if (!city && !state) return <span className="text-muted-foreground">-</span>
        return (
          <span className="text-sm">
            {city && state ? `${city}, ${state}` : city || state}
          </span>
        )
      },
    },
    {
      id: 'actions',
      cell: ({ row }) => {
        const owner = row.original

        const handleDelete = async () => {
          if (confirm(`Are you sure you want to delete ${owner.fullName}? This will also affect their patients.`)) {
            await deleteOwner.mutateAsync(owner.id)
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
                onClick={() => navigate(ROUTES.OWNERS.VIEW(owner.id))}
              >
                <Eye className="mr-2 h-4 w-4" />
                View Details
              </DropdownMenuItem>
              {canEdit && (
                <>
                  <DropdownMenuItem
                    onClick={() => navigate(ROUTES.OWNERS.EDIT(owner.id))}
                  >
                    <Edit className="mr-2 h-4 w-4" />
                    Edit
                  </DropdownMenuItem>
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
          <h2 className="text-3xl font-bold tracking-tight">Owners</h2>
          <p className="text-muted-foreground">
            Manage pet owner information and contacts
          </p>
        </div>
        {canEdit && (
          <Button onClick={() => navigate(ROUTES.OWNERS.CREATE)}>
            <Plus className="mr-2 h-4 w-4" />
            Add Owner
          </Button>
        )}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Owners</CardTitle>
          <CardDescription>
            View and manage all registered pet owners
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
              data={owners}
              searchKey="fullName"
              searchPlaceholder="Search by owner name..."
              onRowClick={(owner) => navigate(ROUTES.OWNERS.VIEW(owner.id))}
            />
          )}
        </CardContent>
      </Card>
    </div>
  )
}