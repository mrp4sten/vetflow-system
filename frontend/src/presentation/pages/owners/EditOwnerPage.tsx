import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Input } from '@presentation/components/ui/input'
import { Label } from '@presentation/components/ui/label'
import { ownerSchema } from '@shared/schemas/owner.schema'
import type { OwnerFormData } from '@shared/schemas/owner.schema'
import { useOwner, useUpdateOwner } from '@presentation/hooks/useOwners'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'

export const EditOwnerPage: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const ownerId = parseInt(id || '0')

  const { data: owner, isLoading: loadingOwner } = useOwner(ownerId)
  const updateOwner = useUpdateOwner()

  const methods = useForm<OwnerFormData>({
    resolver: zodResolver(ownerSchema),
  })

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = methods

  // Pre-populate form when owner data is loaded
  useEffect(() => {
    if (owner) {
      reset({
        name: owner.name,
        email: owner.email,
        phone: owner.phone,
        address: owner.address || '',
      })
    }
  }, [owner, reset])

  const onSubmit = async (data: OwnerFormData) => {
    try {
      await updateOwner.mutateAsync({ id: ownerId, data })
      navigate(ROUTES.OWNERS.VIEW(ownerId))
    } catch (error) {
      // Error is handled by mutation
    }
  }

  if (loadingOwner) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!owner) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4">
        <p className="text-lg text-muted-foreground">Owner not found</p>
        <Button onClick={() => navigate(ROUTES.OWNERS.LIST)}>
          Back to Owners
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate(ROUTES.OWNERS.VIEW(ownerId))}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Edit Owner</h2>
          <p className="text-muted-foreground">
            Update {owner.name}'s information
          </p>
        </div>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="grid gap-6 max-w-2xl">
            {/* Personal Information */}
            <Card>
              <CardHeader>
                <CardTitle>Personal Information</CardTitle>
                <CardDescription>
                  Update the owner's basic details
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6">
                {/* Full Name */}
                <div className="space-y-2">
                  <Label htmlFor="name" className="required">
                    Full Name
                  </Label>
                  <Input
                    id="name"
                    {...register('name')}
                    placeholder="John Doe"
                    className={errors.name ? 'border-red-500' : ''}
                  />
                  {errors.name && (
                    <p className="text-sm text-red-600">{errors.name.message}</p>
                  )}
                </div>
              </CardContent>
            </Card>

            {/* Contact Information */}
            <Card>
              <CardHeader>
                <CardTitle>Contact Information</CardTitle>
                <CardDescription>
                  Update contact details
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6 md:grid-cols-2">
                {/* Email */}
                <div className="space-y-2 md:col-span-2">
                  <Label htmlFor="email" className="required">
                    Email Address
                  </Label>
                  <Input
                    id="email"
                    type="email"
                    {...register('email')}
                    placeholder="john.doe@example.com"
                    className={errors.email ? 'border-red-500' : ''}
                  />
                  {errors.email && (
                    <p className="text-sm text-red-600">{errors.email.message}</p>
                  )}
                </div>

                {/* Phone Number */}
                <div className="space-y-2 md:col-span-2">
                  <Label htmlFor="phone" className="required">
                    Phone Number
                  </Label>
                  <Input
                    id="phone"
                    type="tel"
                    {...register('phone')}
                    placeholder="5551234567"
                    className={errors.phone ? 'border-red-500' : ''}
                  />
                  {errors.phone && (
                    <p className="text-sm text-red-600">{errors.phone.message}</p>
                  )}
                </div>
              </CardContent>
            </Card>

            {/* Address Information */}
            <Card>
              <CardHeader>
                <CardTitle>Address (Optional)</CardTitle>
                <CardDescription>
                  Update full residential address
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6">
                {/* Full Address */}
                <div className="space-y-2">
                  <Label htmlFor="address">Full Address</Label>
                  <Input
                    id="address"
                    {...register('address')}
                    placeholder="123 Main Street, City, State ZIP"
                  />
                  {errors.address && (
                    <p className="text-sm text-red-600">{errors.address.message}</p>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="flex justify-end gap-4 mt-6 max-w-2xl">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate(ROUTES.OWNERS.VIEW(ownerId))}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Updating...
                </>
              ) : (
                'Update Owner'
              )}
            </Button>
          </div>
        </form>
      </FormProvider>
    </div>
  )
}
