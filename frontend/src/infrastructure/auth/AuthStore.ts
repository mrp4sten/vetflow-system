import { create } from 'zustand'
import { AuthState, AuthActions } from '@shared/types/auth'
import { SystemUser, UserRole } from '@domain/models/SystemUser'
import { api } from '@infrastructure/api/axios-client'
import { API_ENDPOINTS } from '@infrastructure/api/endpoints'
import { TokenStorage } from '@infrastructure/storage/TokenStorage'
import { jwtDecode } from 'jwt-decode'

interface JwtPayload {
  sub: string
  userId: number
  username: string
  role: UserRole
  exp: number
  iat: number
}

interface LoginResponse {
  accessToken: string
  refreshToken?: string
  expiresIn: number
  tokenType: string
  user: SystemUser
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
      
      const { accessToken, refreshToken, expiresIn, user } = response.data
      
      // Store tokens
      TokenStorage.setTokens(accessToken, refreshToken, expiresIn)
      
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
        error: error.message || 'Login failed',
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
      // Try to decode the token to get user info
      const decoded = jwtDecode<JwtPayload>(token)
      
      // Fetch current user details
      const response = await api.get<SystemUser>(API_ENDPOINTS.AUTH.CURRENT_USER)
      
      set({
        user: response.data,
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