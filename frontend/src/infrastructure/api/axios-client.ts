import axios from 'axios'
import type { AxiosInstance } from 'axios'
import { env } from '../config/environment'
import { TokenStorage } from '../storage/TokenStorage'
import { authInterceptor, refreshInterceptor } from './interceptors/auth-interceptor'
import { errorInterceptor } from './interceptors/error-interceptor'

class ApiClient {
  private axiosInstance: AxiosInstance

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: env.API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    this.setupInterceptors()
  }

  private setupInterceptors(): void {
    // Request interceptor for auth
    this.axiosInstance.interceptors.request.use(authInterceptor)

    // Response interceptors
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      refreshInterceptor(this.axiosInstance)
    )

    // Error interceptor (should be last)
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      errorInterceptor
    )
  }

  get client(): AxiosInstance {
    return this.axiosInstance
  }

  // Convenience methods
  setAuthToken(token: string): void {
    TokenStorage.setTokens(token)
  }

  clearAuthToken(): void {
    TokenStorage.clearTokens()
  }

  isAuthenticated(): boolean {
    return !!TokenStorage.getAccessToken() && !TokenStorage.isTokenExpired()
  }
}

// Export singleton instance
export const apiClient = new ApiClient()
export const api = apiClient.client