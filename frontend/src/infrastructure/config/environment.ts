interface Environment {
  API_BASE_URL: string
  APP_NAME: string
  IS_PRODUCTION: boolean
  IS_DEVELOPMENT: boolean
}

export const env: Environment = {
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  APP_NAME: import.meta.env.VITE_APP_NAME || 'VetFlow',
  IS_PRODUCTION: import.meta.env.PROD,
  IS_DEVELOPMENT: import.meta.env.DEV,
}