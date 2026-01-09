// Common validation utilities

export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

export const isValidPhoneNumber = (phone: string): boolean => {
  // Remove all non-digit characters
  const cleaned = phone.replace(/\D/g, '')
  
  // Check if it's a valid US phone number (10 digits)
  return cleaned.length === 10
}

export const formatPhoneNumber = (phone: string): string => {
  const cleaned = phone.replace(/\D/g, '')
  
  if (cleaned.length === 10) {
    return `(${cleaned.slice(0, 3)}) ${cleaned.slice(3, 6)}-${cleaned.slice(6)}`
  }
  
  return phone
}

export const isValidZipCode = (zipCode: string): boolean => {
  // US ZIP code (5 digits or 5+4 format)
  const zipRegex = /^\d{5}(-\d{4})?$/
  return zipRegex.test(zipCode)
}

export const isValidMicrochipNumber = (microchip: string): boolean => {
  // Microchip numbers are typically 15 digits
  const cleaned = microchip.replace(/\D/g, '')
  return cleaned.length === 15
}

export const isValidWeight = (weight: number): boolean => {
  return weight > 0 && weight < 1000 // Reasonable weight range in kg
}

export const convertKgToLbs = (kg: number): number => {
  return Math.round(kg * 2.20462 * 10) / 10 // Round to 1 decimal place
}

export const convertLbsToKg = (lbs: number): number => {
  return Math.round(lbs / 2.20462 * 10) / 10 // Round to 1 decimal place
}