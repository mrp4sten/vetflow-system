import { Menu, User, LogOut } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { useAuth } from '@presentation/hooks/useAuth'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'

interface HeaderProps {
  onMenuToggle: () => void
  sidebarCollapsed: boolean
}

export const Header: React.FC<HeaderProps> = ({ onMenuToggle, sidebarCollapsed }) => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate(ROUTES.AUTH.LOGIN)
  }

  return (
    <header className="sticky top-0 z-40 border-b bg-background">
      <div className="flex h-16 items-center px-4 gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={onMenuToggle}
          className="md:flex"
        >
          <Menu className="h-5 w-5" />
        </Button>

        <div className="flex-1">
          <h1 className="text-xl font-semibold">VetFlow</h1>
        </div>

        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <User className="h-4 w-4 text-muted-foreground" />
            <span className="text-sm font-medium">
              {user?.firstName || user?.username}
            </span>
            <span className="text-xs text-muted-foreground">
              ({user?.role})
            </span>
          </div>

          <Button
            variant="ghost"
            size="icon"
            onClick={handleLogout}
            title="Logout"
          >
            <LogOut className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </header>
  )
}