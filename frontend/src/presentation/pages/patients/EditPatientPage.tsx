import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft } from 'lucide-react'
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
import { patientSchema } from '@shared/schemas/patient.schema'
import type { PatientFormData } from '@shared/schemas/patient.schema'
import { usePatient, useUpdatePatient } from '@presentation/hooks/usePatients'
import { useOwners } from '@presentation/hooks/useOwners'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'

export const EditPatientPage: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const patientId = parseInt(id || '0')

  const { data: patient, isLoading: loadingPatient } = usePatient(patientId)
  const { data: owners = [], isLoading: loadingOwners } = useOwners()
  const updatePatient = useUpdatePatient()

  const methods = useForm<PatientFormData>({
    resolver: zodResolver(patientSchema),
    defaultValues: {
      gender: 'unknown',
      species: 'dog',
    },
  })

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    reset,
    formState: { errors, isSubmitting },
  } = methods

  // Pre-populate form when patient data is loaded
  useEffect(() => {
    if (patient) {
      reset({
        name: patient.name,
        species: patient.species,
        breed: patient.breed || '',
        gender: patient.gender,
        birthDate: patient.birthDate || '',
        weight: patient.weight || undefined,
        color: patient.color || '',
        microchipNumber: patient.microchipNumber || '',
        ownerId: patient.ownerId,
      })
    }
  }, [patient, reset])

  const onSubmit = async (data: PatientFormData) => {
    try {
      await updatePatient.mutateAsync({ id: patientId, data })
      navigate(ROUTES.PATIENTS.VIEW(patientId))
    } catch (error) {
      // Error is handled by mutation
    }
  }

  if (loadingPatient || loadingOwners) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!patient) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4">
        <p className="text-lg text-muted-foreground">Patient not found</p>
        <Button onClick={() => navigate(ROUTES.PATIENTS.LIST)}>
          Back to Patients
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
          onClick={() => navigate(ROUTES.PATIENTS.VIEW(patientId))}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Edit Patient</h2>
          <p className="text-muted-foreground">
            Update {patient.name}'s information
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
                  Update the patient's basic details
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
                  Update the pet owner if needed
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
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
              </CardContent>
            </Card>
          </div>

          <div className="flex justify-end gap-4 mt-6">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate(ROUTES.PATIENTS.VIEW(patientId))}
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
                'Update Patient'
              )}
            </Button>
          </div>
        </form>
      </FormProvider>
    </div>
  )
}
