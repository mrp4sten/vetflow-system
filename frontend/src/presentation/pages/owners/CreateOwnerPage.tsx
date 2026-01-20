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
    try {
      await createOwner.mutateAsync(data)
      navigate(ROUTES.OWNERS.LIST)
    } catch (error) {
      // Error is handled by mutation
    }
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
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="grid gap-6 max-w-2xl">
            {/* Personal Information */}
            <Card>
              <CardHeader>
                <CardTitle>Personal Information</CardTitle>
                <CardDescription>
                  Enter the owner's basic details
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6 md:grid-cols-2">
                {/* First Name */}
                <div className="space-y-2">
                  <Label htmlFor="firstName" className="required">
                    First Name
                  </Label>
                  <Input
                    id="firstName"
                    {...register('firstName')}
                    placeholder="John"
                    className={errors.firstName ? 'border-red-500' : ''}
                  />
                  {errors.firstName && (
                    <p className="text-sm text-red-600">{errors.firstName.message}</p>
                  )}
                </div>

                {/* Last Name */}
                <div className="space-y-2">
                  <Label htmlFor="lastName" className="required">
                    Last Name
                  </Label>
                  <Input
                    id="lastName"
                    {...register('lastName')}
                    placeholder="Doe"
                    className={errors.lastName ? 'border-red-500' : ''}
                  />
                  {errors.lastName && (
                    <p className="text-sm text-red-600">{errors.lastName.message}</p>
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
                  <Label htmlFor="phoneNumber" className="required">
                    Phone Number
                  </Label>
                  <Input
                    id="phoneNumber"
                    type="tel"
                    {...register('phoneNumber')}
                    placeholder="1234567890 (10 digits)"
                    maxLength={10}
                    className={errors.phoneNumber ? 'border-red-500' : ''}
                  />
                  {errors.phoneNumber && (
                    <p className="text-sm text-red-600">{errors.phoneNumber.message}</p>
                  )}
                  <p className="text-sm text-muted-foreground">
                    Enter 10 digits without dashes or spaces
                  </p>
                </div>
              </CardContent>
            </Card>

            {/* Address Information */}
            <Card>
              <CardHeader>
                <CardTitle>Address (Optional)</CardTitle>
                <CardDescription>
                  Owner's residential address
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6">
                {/* Street Address */}
                <div className="space-y-2">
                  <Label htmlFor="address">Street Address</Label>
                  <Input
                    id="address"
                    {...register('address')}
                    placeholder="123 Main Street"
                  />
                </div>

                <div className="grid gap-6 md:grid-cols-3">
                  {/* City */}
                  <div className="space-y-2">
                    <Label htmlFor="city">City</Label>
                    <Input
                      id="city"
                      {...register('city')}
                      placeholder="New York"
                    />
                  </div>

                  {/* State */}
                  <div className="space-y-2">
                    <Label htmlFor="state">State</Label>
                    <Input
                      id="state"
                      {...register('state')}
                      placeholder="NY"
                      maxLength={2}
                    />
                  </div>

                  {/* ZIP Code */}
                  <div className="space-y-2">
                    <Label htmlFor="zipCode">ZIP Code</Label>
                    <Input
                      id="zipCode"
                      {...register('zipCode')}
                      placeholder="12345"
                      className={errors.zipCode ? 'border-red-500' : ''}
                    />
                    {errors.zipCode && (
                      <p className="text-sm text-red-600">{errors.zipCode.message}</p>
                    )}
                  </div>
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