import { useMemo } from 'react'
import { useAuth } from '@presentation/hooks/useAuth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Calendar, Heart, Users, FileText, TrendingUp, Activity } from 'lucide-react'
import { Link } from 'react-router-dom'
import { ROUTES } from '@shared/constants/routes'
import { useAppointments } from '@presentation/hooks/useAppointments'
import { usePatients } from '@presentation/hooks/usePatients'
import { useOwners } from '@presentation/hooks/useOwners'
import { useMedicalRecords } from '@presentation/hooks/useMedicalRecords'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts'
import { format, startOfWeek, endOfWeek, eachDayOfInterval, isToday, startOfMonth, endOfMonth } from 'date-fns'

const SPECIES_COLORS = {
  dog: '#3B82F6',      // blue
  cat: '#EF4444',      // red
  bird: '#10B981',     // green
  rabbit: '#F59E0B',   // amber
  hamster: '#8B5CF6',  // purple
  other: '#6B7280',    // gray
}

export const DashboardPage: React.FC = () => {
  const { user } = useAuth()
  
  // Fetch all data
  const { data: appointments = [], isLoading: loadingAppointments } = useAppointments()
  const { data: patients = [], isLoading: loadingPatients } = usePatients()
  const { data: owners = [], isLoading: loadingOwners } = useOwners()
  const { data: medicalRecords = [], isLoading: loadingRecords } = useMedicalRecords()
  
  const isLoading = loadingAppointments || loadingPatients || loadingOwners || loadingRecords
  
  // Calculate statistics
  const stats = useMemo(() => {
    const today = new Date()
    const todayStr = format(today, 'yyyy-MM-dd')
    const monthStart = startOfMonth(today)
    const weekStart = startOfWeek(today)
    
    // Today's appointments
    const todayAppointments = appointments.filter(apt => 
      format(new Date(apt.appointmentDate), 'yyyy-MM-dd') === todayStr
    )
    const completedToday = todayAppointments.filter(apt => apt.status === 'completed').length
    const scheduledToday = todayAppointments.filter(apt => apt.status === 'scheduled').length
    
    // Active patients
    const activePatients = patients.filter(p => p.isActive)
    const newPatientsThisMonth = patients.filter(p => 
      new Date(p.createdAt) >= monthStart
    ).length
    
    // New owners this month
    const newOwnersThisMonth = owners.filter(o => 
      new Date(o.createdAt) >= monthStart
    ).length
    
    // Records this week
    const recordsThisWeek = medicalRecords.filter(r => 
      new Date(r.createdAt) >= weekStart
    ).length
    
    return {
      todayAppointments: todayAppointments.length,
      completedToday,
      scheduledToday,
      activePatients: activePatients.length,
      newPatientsThisMonth,
      totalOwners: owners.length,
      newOwnersThisMonth,
      totalRecords: medicalRecords.length,
      recordsThisWeek,
    }
  }, [appointments, patients, owners, medicalRecords])
  
  // Appointments data for chart (last 7 days)
  const appointmentsChartData = useMemo(() => {
    const weekStart = startOfWeek(new Date())
    const weekEnd = endOfWeek(new Date())
    const days = eachDayOfInterval({ start: weekStart, end: weekEnd })
    
    return days.map(day => {
      const dayStr = format(day, 'yyyy-MM-dd')
      const dayAppointments = appointments.filter(apt => 
        format(new Date(apt.appointmentDate), 'yyyy-MM-dd') === dayStr
      )
      
      return {
        day: format(day, 'EEE'),
        count: dayAppointments.length,
        completed: dayAppointments.filter(a => a.status === 'completed').length,
        scheduled: dayAppointments.filter(a => a.status === 'scheduled').length,
      }
    })
  }, [appointments])
  
  // Patient species distribution
  const speciesChartData = useMemo(() => {
    const speciesCount = patients.reduce((acc, patient) => {
      const species = patient.species.toLowerCase()
      acc[species] = (acc[species] || 0) + 1
      return acc
    }, {} as Record<string, number>)
    
    return Object.entries(speciesCount).map(([species, count]) => ({
      name: species.charAt(0).toUpperCase() + species.slice(1),
      value: count,
      color: SPECIES_COLORS[species as keyof typeof SPECIES_COLORS] || SPECIES_COLORS.other,
    }))
  }, [patients])
  
  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

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

      {/* Statistics Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Link to={ROUTES.APPOINTMENTS.LIST}>
          <Card className="hover:shadow-md transition-shadow cursor-pointer">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Today's Appointments
              </CardTitle>
              <Calendar className="h-4 w-4 text-blue-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.todayAppointments}</div>
              <p className="text-xs text-muted-foreground">
                {stats.completedToday} completed, {stats.scheduledToday} scheduled
              </p>
            </CardContent>
          </Card>
        </Link>

        <Link to={ROUTES.PATIENTS.LIST}>
          <Card className="hover:shadow-md transition-shadow cursor-pointer">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Active Patients
              </CardTitle>
              <Heart className="h-4 w-4 text-green-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.activePatients}</div>
              <p className="text-xs text-muted-foreground">
                {stats.newPatientsThisMonth} new this month
              </p>
            </CardContent>
          </Card>
        </Link>

        <Link to={ROUTES.OWNERS.LIST}>
          <Card className="hover:shadow-md transition-shadow cursor-pointer">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Total Owners
              </CardTitle>
              <Users className="h-4 w-4 text-purple-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalOwners}</div>
              <p className="text-xs text-muted-foreground">
                {stats.newOwnersThisMonth} new this month
              </p>
            </CardContent>
          </Card>
        </Link>

        <Link to={ROUTES.MEDICAL_RECORDS.LIST}>
          <Card className="hover:shadow-md transition-shadow cursor-pointer">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Medical Records
              </CardTitle>
              <FileText className="h-4 w-4 text-orange-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalRecords}</div>
              <p className="text-xs text-muted-foreground">
                {stats.recordsThisWeek} this week
              </p>
            </CardContent>
          </Card>
        </Link>
      </div>

      {/* Charts Section */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* Appointments This Week Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5 text-blue-600" />
              Appointments This Week
            </CardTitle>
            <CardDescription>
              Daily appointment breakdown for the current week
            </CardDescription>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={appointmentsChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="day" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="completed" fill="#10B981" name="Completed" />
                <Bar dataKey="scheduled" fill="#3B82F6" name="Scheduled" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Patient Species Distribution Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Activity className="h-5 w-5 text-green-600" />
              Patient Species Distribution
            </CardTitle>
            <CardDescription>
              Breakdown of patients by species
            </CardDescription>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={speciesChartData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {speciesChartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* Recent Activity Section */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Recent Appointments</CardTitle>
            <CardDescription>
              Latest appointments from today
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {appointments
                .filter(apt => isToday(new Date(apt.appointmentDate)))
                .slice(0, 5)
                .map((apt) => (
                  <Link 
                    key={apt.id} 
                    to={ROUTES.APPOINTMENTS.VIEW(apt.id)}
                    className="flex items-center justify-between p-3 border rounded-lg hover:bg-accent transition-colors"
                  >
                    <div>
                      <p className="font-medium">{apt.patient?.name || 'Unknown Patient'}</p>
                      <p className="text-sm text-muted-foreground">
                        {format(new Date(apt.appointmentDate), 'h:mm a')} • {apt.type}
                      </p>
                    </div>
                    <span className={`text-xs px-2 py-1 rounded-full ${
                      apt.status === 'completed' ? 'bg-green-100 text-green-700' :
                      apt.status === 'scheduled' ? 'bg-blue-100 text-blue-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>
                      {apt.status}
                    </span>
                  </Link>
                ))
              }
              {appointments.filter(apt => isToday(new Date(apt.appointmentDate))).length === 0 && (
                <div className="text-sm text-muted-foreground">
                  No appointments for today.
                </div>
              )}
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
              {patients
                .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                .slice(0, 5)
                .map((patient) => (
                  <Link 
                    key={patient.id} 
                    to={ROUTES.PATIENTS.VIEW(patient.id)}
                    className="flex items-center justify-between p-3 border rounded-lg hover:bg-accent transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <Heart className="h-5 w-5 text-pink-500" />
                      <div>
                        <p className="font-medium">{patient.name}</p>
                        <p className="text-sm text-muted-foreground capitalize">
                          {patient.species} • {patient.owner?.fullName || 'No owner'}
                        </p>
                      </div>
                    </div>
                    {!patient.isActive && (
                      <span className="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-700">
                        Inactive
                      </span>
                    )}
                  </Link>
                ))
              }
              {patients.length === 0 && (
                <div className="text-sm text-muted-foreground">
                  No patients registered yet.
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}