import { useEffect } from 'react'
import { useAuthStore } from '@infrastructure/auth/AuthStore'
import { UserRole } from '@domain/models/SystemUser'

export const useAuth = () => {
  const store = useAuthStore()
  
  useEffect(() => {
    // Check authentication status on mount
    store.checkAuth()
  }, [])
  
  return {
    user: store.user,
    isAuthenticated: store.isAuthenticated,
    isLoading: store.isLoading,
    error: store.error,
    login: store.login,
    logout: store.logout,
    clearError: store.clearError,
    hasRole: store.hasRole,
    hasAnyRole: store.hasAnyRole,
    isAdmin: () => store.hasRole('admin'),
    isVeterinarian: () => store.hasRole('veterinarian'),
    isAssistant: () => store.hasRole('assistant'),
  }
}

// Hook for checking permissions
export const usePermission = (requiredRoles: UserRole | UserRole[]) => {
  const { hasRole, hasAnyRole } = useAuth()
  
  const roles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles]
  
  return hasAnyRole(roles)
}