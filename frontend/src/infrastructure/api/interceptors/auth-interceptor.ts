import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse, AxiosError, AxiosInstance } from 'axios'
import { TokenStorage } from '../../storage/TokenStorage'
import { API_ENDPOINTS } from '../endpoints'

// Request interceptor to add auth token
export const authInterceptor = (config: AxiosRequestConfig): AxiosRequestConfig => {
  const token = TokenStorage.getAccessToken()
  
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  
  return config
}

// Response interceptor to handle token refresh
export const refreshInterceptor = (axiosInstance: AxiosInstance) => {
  return async (error: AxiosError): Promise<AxiosResponse> => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean }
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      const refreshToken = TokenStorage.getRefreshToken()
      
      if (refreshToken) {
        try {
          const response = await axiosInstance.post(API_ENDPOINTS.AUTH.REFRESH, {
            refreshToken,
          })
          
          const { accessToken, refreshToken: newRefreshToken, expiresIn } = response.data
          
          TokenStorage.setTokens(accessToken, newRefreshToken, expiresIn)
          
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${accessToken}`
          }
          
          return axiosInstance(originalRequest)
        } catch (refreshError) {
          // Refresh failed, redirect to login
          TokenStorage.clearTokens()
          window.location.href = '/auth/login'
          return Promise.reject(refreshError)
        }
      } else {
        // No refresh token, redirect to login
        TokenStorage.clearTokens()
        window.location.href = '/auth/login'
      }
    }
    
    return Promise.reject(error)
  }
}
