import { NavLink } from 'react-router-dom'
import { 
  Calendar, 
  Users, 
  Heart, 
  FileText, 
  Home,
  UserCog,
  ClipboardList,
  Settings,
  Stethoscope
} from 'lucide-react'
import { cn } from '@shared/utils/cn'
import { ROUTES } from '@shared/constants/routes'
import { useAuth } from '@presentation/hooks/useAuth'

interface SidebarProps {
  collapsed: boolean
}

interface NavItem {
  title: string
  href: string
  icon: React.ElementType
  requiredRoles?: string[]
}

const navItems: NavItem[] = [
  {
    title: 'Dashboard',
    href: ROUTES.DASHBOARD,
    icon: Home,
  },
  {
    title: 'Appointments',
    href: ROUTES.APPOINTMENTS.LIST,
    icon: Calendar,
  },
  {
    title: 'Patients',
    href: ROUTES.PATIENTS.LIST,
    icon: Heart,
  },
  {
    title: 'Owners',
    href: ROUTES.OWNERS.LIST,
    icon: Users,
  },
  {
    title: 'Veterinarians',
    href: ROUTES.VETERINARIANS.LIST,
    icon: Stethoscope,
  },
  {
    title: 'Medical Records',
    href: ROUTES.MEDICAL_RECORDS.LIST,
    icon: FileText,
    requiredRoles: ['admin', 'veterinarian'],
  },
  {
    title: 'Users',
    href: ROUTES.ADMIN.USERS,
    icon: UserCog,
    requiredRoles: ['admin'],
  },
  {
    title: 'Audit Logs',
    href: ROUTES.ADMIN.AUDIT_LOGS,
    icon: ClipboardList,
    requiredRoles: ['admin'],
  },
  {
    title: 'Settings',
    href: ROUTES.SETTINGS,
    icon: Settings,
  },
]

export const Sidebar: React.FC<SidebarProps> = ({ collapsed }) => {
  const { hasAnyRole } = useAuth()

  const filteredNavItems = navItems.filter(item => {
    if (!item.requiredRoles) return true
    return hasAnyRole(item.requiredRoles as any)
  })

  return (
    <aside className={cn(
      'fixed left-0 top-16 bottom-0 z-30 bg-background border-r transition-all duration-300',
      collapsed ? 'w-16' : 'w-64'
    )}>
      <nav className="p-2 space-y-1">
        {filteredNavItems.map((item) => (
          <NavLink
            key={item.href}
            to={item.href}
            className={({ isActive }) => cn(
              'flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition-all hover:bg-accent',
              isActive ? 'bg-accent text-accent-foreground' : 'text-muted-foreground',
              collapsed && 'justify-center'
            )}
            title={collapsed ? item.title : undefined}
          >
            <item.icon className="h-4 w-4" />
            {!collapsed && <span>{item.title}</span>}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}