import { create } from 'zustand'
import type { AuthState, AuthActions } from '@shared/types/auth'
import type { SystemUser, UserRole } from '@domain/models/SystemUser'
import { api } from '@infrastructure/api/axios-client'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'
import { TokenStorage } from '@infrastructure/storage/TokenStorage'
import { jwtDecode } from 'jwt-decode'

interface JwtPayload {
  sub: string
  roles: string[]
  exp: number
  iat: number
}

interface LoginResponse {
  accessToken: string
  expiresInSeconds: number
}

type AuthStore = AuthState & AuthActions

export const useAuthStore = create<AuthStore>((set, get) => ({
  // State
  user: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,

  // Actions
  login: async (username: string, password: string, rememberMe = false) => {
    set({ isLoading: true, error: null })
    
    try {
      const response = await api.post<LoginResponse>(API_ENDPOINTS.AUTH.LOGIN, {
        username,
        password,
      })
      
      const { accessToken, expiresInSeconds } = response.data
      
      // Store tokens
      TokenStorage.setTokens(accessToken, undefined, expiresInSeconds)
      
      // Decode token to get user info
      const decoded = jwtDecode<JwtPayload>(accessToken)
      
      // Map backend roles to frontend role format
      const roleMapping: Record<string, UserRole> = {
        'ADMIN': 'admin',
        'VETERINARIAN': 'veterinarian',
        'ASSISTANT': 'assistant',
      }
      
      const backendRole = decoded.roles[0] || 'ASSISTANT'
      const userRole = roleMapping[backendRole] || 'assistant'
      
      // Create user object from token
      const user: SystemUser = {
        id: 0, // Backend doesn't provide user ID in token
        username: decoded.sub,
        email: `${decoded.sub}@vetflow.local`,
        role: userRole,
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      }
      
      // If remember me is not checked, clear tokens on browser close
      if (!rememberMe) {
        window.addEventListener('beforeunload', () => {
          TokenStorage.clearTokens()
        })
      }
      
      set({
        user,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      })
    } catch (error: any) {
      set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: error.response?.data?.message || error.message || 'Login failed',
      })
      throw error
    }
  },

  logout: async () => {
    try {
      // Call logout endpoint if available
      await api.post(API_ENDPOINTS.AUTH.LOGOUT).catch(() => {
        // Ignore errors during logout
      })
    } finally {
      // Clear local state regardless
      TokenStorage.clearTokens()
      set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      })
    }
  },

  checkAuth: async () => {
    const token = TokenStorage.getAccessToken()
    
    if (!token || TokenStorage.isTokenExpired()) {
      set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
      })
      return
    }
    
    set({ isLoading: true })
    
    try {
      // Decode the token to get user info
      const decoded = jwtDecode<JwtPayload>(token)
      
      // Map backend roles to frontend role format
      const roleMapping: Record<string, UserRole> = {
        'ADMIN': 'admin',
        'VETERINARIAN': 'veterinarian',
        'ASSISTANT': 'assistant',
      }
      
      const backendRole = decoded.roles[0] || 'ASSISTANT'
      const userRole = roleMapping[backendRole] || 'assistant'
      
      // Create user object from token
      const user: SystemUser = {
        id: 0,
        username: decoded.sub,
        email: `${decoded.sub}@vetflow.local`,
        role: userRole,
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      }
      
      set({
        user,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      })
    } catch (error) {
      // Token is invalid or expired
      TokenStorage.clearTokens()
      set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      })
    }
  },

  clearError: () => {
    set({ error: null })
  },

  hasRole: (role: UserRole): boolean => {
    const { user } = get()
    return user?.role === role
  },

  hasAnyRole: (roles: UserRole[]): boolean => {
    const { user } = get()
    return user ? roles.includes(user.role) : false
  },
}))