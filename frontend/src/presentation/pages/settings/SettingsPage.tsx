import { useAuth } from '@presentation/hooks/useAuth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Button } from '@presentation/components/ui/button'
import { Badge } from '@presentation/components/ui/badge'
import { Separator } from '@presentation/components/ui/separator'
import { User, Mail, Shield, Calendar, LogOut } from 'lucide-react'
import { format } from 'date-fns'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'

export const SettingsPage: React.FC = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate(ROUTES.AUTH.LOGIN)
  }

  if (!user) {
    return null
  }

  const roleColors = {
    admin: 'bg-red-100 text-red-700',
    veterinarian: 'bg-blue-100 text-blue-700',
    assistant: 'bg-green-100 text-green-700',
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Settings</h2>
        <p className="text-muted-foreground">
          Manage your account settings and preferences
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {/* Profile Information */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              Profile Information
            </CardTitle>
            <CardDescription>
              Your account details and role
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <p className="text-sm text-muted-foreground">Full Name</p>
              <p className="font-medium">
                {user.firstName} {user.lastName}
              </p>
            </div>

            <Separator />

            <div>
              <p className="text-sm text-muted-foreground">Username</p>
              <p className="font-medium">{user.username}</p>
            </div>

            <Separator />

            <div>
              <p className="text-sm text-muted-foreground">Email</p>
              <div className="flex items-center gap-2">
                <Mail className="h-4 w-4 text-muted-foreground" />
                <p className="font-medium">{user.email}</p>
              </div>
            </div>

            <Separator />

            <div>
              <p className="text-sm text-muted-foreground mb-2">Role</p>
              <div className="flex items-center gap-2">
                <Shield className="h-4 w-4 text-muted-foreground" />
                <Badge className={roleColors[user.role]}>
                  {user.role.charAt(0).toUpperCase() + user.role.slice(1)}
                </Badge>
              </div>
            </div>

            <Separator />

            <div>
              <p className="text-sm text-muted-foreground">Account Status</p>
              <Badge variant={user.isActive ? 'default' : 'secondary'}>
                {user.isActive ? 'Active' : 'Inactive'}
              </Badge>
            </div>
          </CardContent>
        </Card>

        {/* Account Activity */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Account Activity
            </CardTitle>
            <CardDescription>
              Your account timeline
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <p className="text-sm text-muted-foreground">Account Created</p>
              <p className="font-medium">
                {format(new Date(user.createdAt), 'MMMM dd, yyyy')}
              </p>
              <p className="text-xs text-muted-foreground">
                {format(new Date(user.createdAt), 'h:mm a')}
              </p>
            </div>

            <Separator />

            <div>
              <p className="text-sm text-muted-foreground">Last Updated</p>
              <p className="font-medium">
                {format(new Date(user.updatedAt), 'MMMM dd, yyyy')}
              </p>
              <p className="text-xs text-muted-foreground">
                {format(new Date(user.updatedAt), 'h:mm a')}
              </p>
            </div>

            <Separator />

            <div className="space-y-2 pt-4">
              <p className="text-sm text-muted-foreground mb-2">Quick Actions</p>
              <Button
                variant="outline"
                className="w-full"
                onClick={handleLogout}
              >
                <LogOut className="mr-2 h-4 w-4" />
                Logout
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Additional Settings Sections */}
      <Card>
        <CardHeader>
          <CardTitle>Application Preferences</CardTitle>
          <CardDescription>
            Customize your VetFlow experience
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">Theme</p>
                <p className="text-sm text-muted-foreground">
                  Choose your preferred color scheme
                </p>
              </div>
              <Badge variant="outline">Light (Default)</Badge>
            </div>

            <Separator />

            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">Language</p>
                <p className="text-sm text-muted-foreground">
                  Select your display language
                </p>
              </div>
              <Badge variant="outline">English (Default)</Badge>
            </div>

            <Separator />

            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">Notifications</p>
                <p className="text-sm text-muted-foreground">
                  Manage notification preferences
                </p>
              </div>
              <Badge variant="outline">Coming Soon</Badge>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* System Information */}
      <Card>
        <CardHeader>
          <CardTitle>System Information</CardTitle>
          <CardDescription>
            VetFlow application details
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-muted-foreground">Version:</span>
              <span className="font-medium">v0.8.0</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Environment:</span>
              <span className="font-medium">{import.meta.env.MODE}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">User ID:</span>
              <span className="font-medium font-mono">{user.id}</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
