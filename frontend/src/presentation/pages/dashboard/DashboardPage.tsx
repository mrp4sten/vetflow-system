import { useAuth } from '@presentation/hooks/useAuth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Calendar, Heart, Users, FileText } from 'lucide-react'
import { Link } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'

interface DashboardCard {
  title: string
  value: string
  description: string
  icon: React.ElementType
  href: string
  color: string
}

const dashboardCards: DashboardCard[] = [
  {
    title: 'Today\'s Appointments',
    value: '12',
    description: '4 completed, 8 scheduled',
    icon: Calendar,
    href: ROUTES.APPOINTMENTS.LIST,
    color: 'text-blue-600',
  },
  {
    title: 'Active Patients',
    value: '245',
    description: '15 new this month',
    icon: Heart,
    href: ROUTES.PATIENTS.LIST,
    color: 'text-green-600',
  },
  {
    title: 'Total Owners',
    value: '189',
    description: '8 new this month',
    icon: Users,
    href: ROUTES.OWNERS.LIST,
    color: 'text-purple-600',
  },
  {
    title: 'Medical Records',
    value: '1,234',
    description: '45 this week',
    icon: FileText,
    href: ROUTES.MEDICAL_RECORDS.LIST,
    color: 'text-orange-600',
  },
]

export const DashboardPage: React.FC = () => {
  const { user } = useAuth()

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">
          Welcome back, {user?.firstName || user?.username}!
        </h2>
        <p className="text-muted-foreground">
          Here's an overview of your veterinary clinic today.
        </p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {dashboardCards.map((card) => (
          <Link key={card.href} to={card.href}>
            <Card className="hover:shadow-md transition-shadow cursor-pointer">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  {card.title}
                </CardTitle>
                <card.icon className={`h-4 w-4 ${card.color}`} />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{card.value}</div>
                <p className="text-xs text-muted-foreground">
                  {card.description}
                </p>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Recent Appointments</CardTitle>
            <CardDescription>
              Your upcoming appointments for today
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {/* Placeholder for appointment list */}
              <div className="text-sm text-muted-foreground">
                No appointments to display yet.
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Recent Patients</CardTitle>
            <CardDescription>
              Recently registered patients
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {/* Placeholder for patient list */}
              <div className="text-sm text-muted-foreground">
                No patients to display yet.
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}