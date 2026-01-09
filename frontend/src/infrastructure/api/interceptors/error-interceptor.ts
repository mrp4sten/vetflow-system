import { AxiosError } from 'axios'

export interface ApiError {
  message: string
  code?: string
  details?: Record<string, any>
  timestamp: string
  path?: string
}

export class ApiErrorException extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly code?: string,
    public readonly details?: Record<string, any>
  ) {
    super(message)
    this.name = 'ApiErrorException'
  }
}

export const errorInterceptor = (error: AxiosError<ApiError>): Promise<never> => {
  if (error.response) {
    // Server responded with error
    const { status, data } = error.response
    const message = data?.message || getDefaultErrorMessage(status)
    
    // Log error in development
    if (import.meta.env.DEV) {
      console.error('API Error:', {
        status,
        message,
        code: data?.code,
        details: data?.details,
        path: data?.path,
      })
    }
    
    throw new ApiErrorException(message, status, data?.code, data?.details)
  } else if (error.request) {
    // Request made but no response
    throw new ApiErrorException(
      'Unable to connect to the server. Please check your internet connection.',
      0,
      'NETWORK_ERROR'
    )
  } else {
    // Request setup error
    throw new ApiErrorException(
      error.message || 'An unexpected error occurred',
      0,
      'REQUEST_ERROR'
    )
  }
}

function getDefaultErrorMessage(status: number): string {
  switch (status) {
    case 400:
      return 'Invalid request. Please check your input.'
    case 401:
      return 'You are not authenticated. Please log in.'
    case 403:
      return 'You do not have permission to perform this action.'
    case 404:
      return 'The requested resource was not found.'
    case 409:
      return 'A conflict occurred. The resource may already exist.'
    case 422:
      return 'The provided data is invalid.'
    case 500:
      return 'An internal server error occurred. Please try again later.'
    case 502:
    case 503:
    case 504:
      return 'The service is temporarily unavailable. Please try again later.'
    default:
      return `An error occurred (${status})`
  }
}