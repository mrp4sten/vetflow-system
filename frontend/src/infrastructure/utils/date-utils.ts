import { format, parse, isValid, differenceInYears, differenceInMonths, addMinutes, isAfter, isBefore, startOfDay, endOfDay } from 'date-fns'

export const DATE_FORMATS = {
  DATE: 'yyyy-MM-dd',
  DATETIME: "yyyy-MM-dd'T'HH:mm:ss",
  DISPLAY_DATE: 'MMM dd, yyyy',
  DISPLAY_DATETIME: 'MMM dd, yyyy h:mm a',
  TIME: 'HH:mm',
  DISPLAY_TIME: 'h:mm a',
} as const

export const formatDate = (date: string | Date, formatStr: string = DATE_FORMATS.DISPLAY_DATE): string => {
  if (!date) return ''
  
  const dateObj = typeof date === 'string' ? new Date(date) : date
  
  if (!isValid(dateObj)) return ''
  
  return format(dateObj, formatStr)
}

export const parseDate = (dateStr: string, formatStr: string = DATE_FORMATS.DATE): Date | null => {
  try {
    const date = parse(dateStr, formatStr, new Date())
    return isValid(date) ? date : null
  } catch {
    return null
  }
}

export const calculateAge = (birthDate: string | Date): { years: number; months: number } | null => {
  if (!birthDate) return null
  
  const birth = typeof birthDate === 'string' ? new Date(birthDate) : birthDate
  
  if (!isValid(birth)) return null
  
  const now = new Date()
  const years = differenceInYears(now, birth)
  const months = differenceInMonths(now, birth) % 12
  
  return { years, months }
}

export const formatAge = (birthDate: string | Date): string => {
  const age = calculateAge(birthDate)
  
  if (!age) return 'Unknown'
  
  if (age.years === 0) {
    return `${age.months} month${age.months !== 1 ? 's' : ''}`
  }
  
  const yearStr = `${age.years} year${age.years !== 1 ? 's' : ''}`
  
  if (age.months === 0) {
    return yearStr
  }
  
  return `${yearStr}, ${age.months} month${age.months !== 1 ? 's' : ''}`
}

export const addDurationToDate = (date: Date, durationInMinutes: number): Date => {
  return addMinutes(date, durationInMinutes)
}

export const isDateInRange = (date: Date, start: Date, end: Date): boolean => {
  return isAfter(date, start) && isBefore(date, end)
}

export const getDayBounds = (date: Date): { start: Date; end: Date } => {
  return {
    start: startOfDay(date),
    end: endOfDay(date),
  }
}

// Re-export date-fns functions that are used directly by mappers
export { isAfter, isBefore }