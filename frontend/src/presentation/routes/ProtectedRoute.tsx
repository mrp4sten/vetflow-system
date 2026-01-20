import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '@presentation/hooks/useAuth'
import type { UserRole } from '@domain/models/SystemUser'
import { ROUTES } from '@shared/constants/routes'
import { LoadingOverlay } from '@presentation/components/shared/Loading/LoadingOverlay'

interface ProtectedRouteProps {
  requiredRoles?: UserRole[]
  redirectTo?: string
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  requiredRoles,
  redirectTo = ROUTES.AUTH.LOGIN,
}) => {
  const location = useLocation()
  const { isAuthenticated, isLoading, hasAnyRole } = useAuth()

  if (isLoading) {
    return <LoadingOverlay message="Checking authentication..." />
  }

  if (!isAuthenticated) {
    return <Navigate to={redirectTo} state={{ from: location }} replace />
  }

  if (requiredRoles && !hasAnyRole(requiredRoles)) {
    return <Navigate to={ROUTES.DASHBOARD} replace />
  }

  return <Outlet />
}