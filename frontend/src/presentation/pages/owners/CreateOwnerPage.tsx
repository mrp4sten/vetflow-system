import { useNavigate } from 'react-router-dom'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Input } from '@presentation/components/ui/input'
import { Label } from '@presentation/components/ui/label'
import { ownerSchema } from '@shared/schemas/owner.schema'
import type { OwnerFormData } from '@shared/schemas/owner.schema'
import { useCreateOwner } from '@presentation/hooks/useOwners'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'

export const CreateOwnerPage: React.FC = () => {
  const navigate = useNavigate()
  const createOwner = useCreateOwner()

  const methods = useForm<OwnerFormData>({
    resolver: zodResolver(ownerSchema),
  })

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = methods

  const onSubmit = async (data: OwnerFormData) => {
    console.log('Form submitted with data:', data)
    try {
      await createOwner.mutateAsync(data)
      navigate(ROUTES.OWNERS.LIST)
    } catch (error) {
      console.error('Error creating owner:', error)
      // Error is handled by mutation
    }
  }

  const onError = (errors: any) => {
    console.log('Form validation errors:', errors)
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate(ROUTES.OWNERS.LIST)}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Add Owner</h2>
          <p className="text-muted-foreground">
            Register a new pet owner in the system
          </p>
        </div>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit, onError)}>
          {Object.keys(errors).length > 0 && (
            <div className="p-4 mb-4 text-sm text-red-800 bg-red-50 rounded-lg max-w-2xl" role="alert">
              <span className="font-medium">Please fix the following errors:</span>
              <ul className="mt-2 ml-4 list-disc">
                {Object.entries(errors).map(([field, error]: [string, any]) => (
                  <li key={field}>{field}: {error.message}</li>
                ))}
              </ul>
            </div>
          )}
          <div className="grid gap-6 max-w-2xl">
            {/* Personal Information */}
            <Card>
              <CardHeader>
                <CardTitle>Personal Information</CardTitle>
                <CardDescription>
                  Enter the owner's basic details
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
                  How can we reach this owner?
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
                    placeholder="+1 (555) 123-4567"
                    className={errors.phone ? 'border-red-500' : ''}
                  />
                  {errors.phone && (
                    <p className="text-sm text-red-600">{errors.phone.message}</p>
                  )}
                  <p className="text-sm text-muted-foreground">
                    Enter phone number with area code (10-20 digits)
                  </p>
                </div>
              </CardContent>
            </Card>

            {/* Address Information */}
            <Card>
              <CardHeader>
                <CardTitle>Address</CardTitle>
                <CardDescription>
                  Owner's residential address
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6">
                {/* Street Address */}
                <div className="space-y-2">
                  <Label htmlFor="address" className="required">Complete Address</Label>
                  <Input
                    id="address"
                    {...register('address')}
                    placeholder="123 Main Street, City, State, ZIP"
                    className={errors.address ? 'border-red-500' : ''}
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
              onClick={() => navigate(ROUTES.OWNERS.LIST)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Creating...
                </>
              ) : (
                'Create Owner'
              )}
            </Button>
          </div>
        </form>
      </FormProvider>
    </div>
  )
}