import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft, Plus } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Input } from '@presentation/components/ui/input'
import { Label } from '@presentation/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@presentation/components/ui/select'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@presentation/components/ui/dialog'
import { patientSchema } from '@shared/schemas/patient.schema'
import type { PatientFormData } from '@shared/schemas/patient.schema'
import { ownerSchema } from '@shared/schemas/owner.schema'
import type { OwnerFormData } from '@shared/schemas/owner.schema'
import { useCreatePatient } from '@presentation/hooks/usePatients'
import { useOwners, useCreateOwner } from '@presentation/hooks/useOwners'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'

export const CreatePatientPage: React.FC = () => {
  const navigate = useNavigate()
  const [showOwnerDialog, setShowOwnerDialog] = useState(false)

  const { data: owners = [], isLoading: loadingOwners } = useOwners()
  const createPatient = useCreatePatient()
  const createOwner = useCreateOwner()

  const methods = useForm<PatientFormData>({
    resolver: zodResolver(patientSchema),
    defaultValues: {
      gender: 'unknown',
      species: 'dog',
    },
  })

  const ownerMethods = useForm<OwnerFormData>({
    resolver: zodResolver(ownerSchema),
  })

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = methods

  const onSubmit = async (data: PatientFormData) => {
    try {
      await createPatient.mutateAsync(data)
      navigate(ROUTES.PATIENTS.LIST)
    } catch (error) {
      // Error is handled by mutation
    }
  }

  const onCreateOwner = async (data: OwnerFormData) => {
    try {
      const newOwner = await createOwner.mutateAsync(data)
      setValue('ownerId', newOwner.id)
      setShowOwnerDialog(false)
      ownerMethods.reset()
    } catch (error) {
      // Error is handled by mutation
    }
  }

  if (loadingOwners) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate(ROUTES.PATIENTS.LIST)}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Register Patient</h2>
          <p className="text-muted-foreground">
            Add a new patient to the system
          </p>
        </div>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="grid gap-6 md:grid-cols-2">
            {/* Basic Information */}
            <Card className="md:col-span-2">
              <CardHeader>
                <CardTitle>Basic Information</CardTitle>
                <CardDescription>
                  Enter the patient's basic details
                </CardDescription>
              </CardHeader>
              <CardContent className="grid gap-6 md:grid-cols-2">
                {/* Name */}
                <div className="space-y-2">
                  <Label htmlFor="name" className="required">
                    Name
                  </Label>
                  <Input
                    id="name"
                    {...register('name')}
                    placeholder="e.g., Max, Bella, Charlie"
                    className={errors.name ? 'border-red-500' : ''}
                  />
                  {errors.name && (
                    <p className="text-sm text-red-600">{errors.name.message}</p>
                  )}
                </div>

                {/* Species */}
                <div className="space-y-2">
                  <Label htmlFor="species" className="required">
                    Species
                  </Label>
                  <Select
                    value={watch('species')}
                    onValueChange={(value: any) => setValue('species', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="dog">Dog</SelectItem>
                      <SelectItem value="cat">Cat</SelectItem>
                      <SelectItem value="bird">Bird</SelectItem>
                      <SelectItem value="rabbit">Rabbit</SelectItem>
                      <SelectItem value="other">Other</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                {/* Breed */}
                <div className="space-y-2">
                  <Label htmlFor="breed">Breed</Label>
                  <Input
                    id="breed"
                    {...register('breed')}
                    placeholder="e.g., Golden Retriever, Persian"
                  />
                </div>

                {/* Gender */}
                <div className="space-y-2">
                  <Label htmlFor="gender" className="required">
                    Gender
                  </Label>
                  <Select
                    value={watch('gender')}
                    onValueChange={(value: any) => setValue('gender', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="male">Male</SelectItem>
                      <SelectItem value="female">Female</SelectItem>
                      <SelectItem value="unknown">Unknown</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                {/* Birth Date */}
                <div className="space-y-2">
                  <Label htmlFor="birthDate">Birth Date</Label>
                  <Input
                    id="birthDate"
                    type="date"
                    {...register('birthDate')}
                  />
                </div>

                {/* Weight */}
                <div className="space-y-2">
                  <Label htmlFor="weight">Weight (kg)</Label>
                  <Input
                    id="weight"
                    type="number"
                    step="0.1"
                    {...register('weight', { valueAsNumber: true })}
                    placeholder="e.g., 25.5"
                  />
                  {errors.weight && (
                    <p className="text-sm text-red-600">{errors.weight.message}</p>
                  )}
                </div>

                {/* Color */}
                <div className="space-y-2">
                  <Label htmlFor="color">Color/Markings</Label>
                  <Input
                    id="color"
                    {...register('color')}
                    placeholder="e.g., Brown, White with black spots"
                  />
                </div>

                {/* Microchip */}
                <div className="space-y-2">
                  <Label htmlFor="microchipNumber">Microchip Number</Label>
                  <Input
                    id="microchipNumber"
                    {...register('microchipNumber')}
                    placeholder="15-digit microchip number"
                    maxLength={15}
                  />
                  {errors.microchipNumber && (
                    <p className="text-sm text-red-600">{errors.microchipNumber.message}</p>
                  )}
                </div>
              </CardContent>
            </Card>

            {/* Owner Information */}
            <Card className="md:col-span-2">
              <CardHeader>
                <CardTitle>Owner Information</CardTitle>
                <CardDescription>
                  Select the pet owner or create a new one
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex gap-2">
                  <div className="flex-1 space-y-2">
                    <Label htmlFor="ownerId" className="required">
                      Owner
                    </Label>
                    <Select
                      value={watch('ownerId')?.toString()}
                      onValueChange={(value) => setValue('ownerId', parseInt(value))}
                    >
                      <SelectTrigger className={errors.ownerId ? 'border-red-500' : ''}>
                        <SelectValue placeholder="Select an owner" />
                      </SelectTrigger>
                      <SelectContent>
                        {owners.map((owner) => (
                          <SelectItem key={owner.id} value={owner.id.toString()}>
                            {owner.fullName} - {owner.phoneNumber}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    {errors.ownerId && (
                      <p className="text-sm text-red-600">{errors.ownerId.message}</p>
                    )}
                  </div>
                  <div className="pt-8">
                    <Button
                      type="button"
                      variant="outline"
                      onClick={() => setShowOwnerDialog(true)}
                    >
                      <Plus className="mr-2 h-4 w-4" />
                      New Owner
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="flex justify-end gap-4 mt-6">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate(ROUTES.PATIENTS.LIST)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Registering...
                </>
              ) : (
                'Register Patient'
              )}
            </Button>
          </div>
        </form>
      </FormProvider>

      {/* Create Owner Dialog */}
      <Dialog open={showOwnerDialog} onOpenChange={setShowOwnerDialog}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Create New Owner</DialogTitle>
            <DialogDescription>
              Enter the owner's contact information
            </DialogDescription>
          </DialogHeader>

          <FormProvider {...ownerMethods}>
            <form onSubmit={ownerMethods.handleSubmit(onCreateOwner)}>
              <div className="grid gap-4 py-4 md:grid-cols-2">
                <div className="space-y-2">
                  <Label htmlFor="firstName">First Name</Label>
                  <Input
                    id="firstName"
                    {...ownerMethods.register('firstName')}
                  />
                  {ownerMethods.formState.errors.firstName && (
                    <p className="text-sm text-red-600">
                      {ownerMethods.formState.errors.firstName.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="lastName">Last Name</Label>
                  <Input
                    id="lastName"
                    {...ownerMethods.register('lastName')}
                  />
                  {ownerMethods.formState.errors.lastName && (
                    <p className="text-sm text-red-600">
                      {ownerMethods.formState.errors.lastName.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    {...ownerMethods.register('email')}
                  />
                  {ownerMethods.formState.errors.email && (
                    <p className="text-sm text-red-600">
                      {ownerMethods.formState.errors.email.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="phoneNumber">Phone Number</Label>
                  <Input
                    id="phoneNumber"
                    {...ownerMethods.register('phoneNumber')}
                    placeholder="10 digits"
                  />
                  {ownerMethods.formState.errors.phoneNumber && (
                    <p className="text-sm text-red-600">
                      {ownerMethods.formState.errors.phoneNumber.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2 md:col-span-2">
                  <Label htmlFor="address">Address</Label>
                  <Input
                    id="address"
                    {...ownerMethods.register('address')}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="city">City</Label>
                  <Input
                    id="city"
                    {...ownerMethods.register('city')}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="state">State</Label>
                  <Input
                    id="state"
                    {...ownerMethods.register('state')}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="zipCode">ZIP Code</Label>
                  <Input
                    id="zipCode"
                    {...ownerMethods.register('zipCode')}
                  />
                  {ownerMethods.formState.errors.zipCode && (
                    <p className="text-sm text-red-600">
                      {ownerMethods.formState.errors.zipCode.message}
                    </p>
                  )}
                </div>
              </div>

              <DialogFooter>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setShowOwnerDialog(false)}
                >
                  Cancel
                </Button>
                <Button type="submit" disabled={ownerMethods.formState.isSubmitting}>
                  {ownerMethods.formState.isSubmitting ? (
                    <>
                      <LoadingSpinner size="sm" className="mr-2" />
                      Creating...
                    </>
                  ) : (
                    'Create Owner'
                  )}
                </Button>
              </DialogFooter>
            </form>
          </FormProvider>
        </DialogContent>
      </Dialog>
    </div>
  )
}