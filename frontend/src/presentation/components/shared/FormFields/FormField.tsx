import React from 'react'
import { useFormContext, Controller } from 'react-hook-form'
import type { FieldPath, FieldValues } from 'react-hook-form'
import { Label } from '@presentation/components/ui/label'
import { Input } from '@presentation/components/ui/input'
import { cn } from '@shared/utils/cn'

interface FormFieldProps<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
> {
  name: TName
  label: string
  type?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  className?: string
  description?: string
}

export function FormField<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
>({
  name,
  label,
  type = 'text',
  placeholder,
  required,
  disabled,
  className,
  description,
}: FormFieldProps<TFieldValues, TName>) {
  const {
    control,
    formState: { errors },
  } = useFormContext<TFieldValues>()

  const error = errors[name]

  return (
    <Controller
      control={control}
      name={name}
      render={({ field }) => (
        <div className="space-y-2">
          <Label htmlFor={name} className={cn(required && 'required')}>
            {label}
          </Label>
          <Input
            {...field}
            id={name}
            type={type}
            placeholder={placeholder}
            disabled={disabled}
            className={cn(
              error && 'border-red-500 focus:border-red-500',
              className
            )}
          />
          {description && (
            <p className="text-sm text-muted-foreground">{description}</p>
          )}
          {error && (
            <p className="text-sm text-red-600">{error.message}</p>
          )}
        </div>
      )}
    />
  )
}