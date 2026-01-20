import { useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { ArrowLeft, Plus, List } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@presentation/components/ui/card'
import { Badge } from '@presentation/components/ui/badge'
import { useAppointments, useUpdateAppointment } from '@presentation/hooks/useAppointments'
import { ROUTES } from '@shared/constants/routes'
import { LoadingSpinner } from '@presentation/components/shared/Loading/LoadingSpinner'
import { useAuth } from '@presentation/hooks/useAuth'
import { APPOINTMENT_STATUS_COLORS } from '@shared/constants/appointment-status'
import { toast } from 'sonner'
import './calendar.css'

export const AppointmentCalendarPage: React.FC = () => {
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const [currentView, setCurrentView] = useState<'dayGridMonth' | 'timeGridWeek' | 'timeGridDay'>('timeGridWeek')

  const { data: appointments = [], isLoading } = useAppointments({})
  const updateAppointment = useUpdateAppointment()

  const canEdit = hasAnyRole(['admin', 'veterinarian'])

  // Transform appointments to FullCalendar events
  const calendarEvents = useMemo(() => {
    return appointments.map((appointment) => ({
      id: appointment.id.toString(),
      title: `${appointment.patient?.name || 'Unknown'} - ${appointment.type}`,
      start: appointment.scheduledAt,
      end: new Date(new Date(appointment.scheduledAt).getTime() + 60 * 60 * 1000).toISOString(), // 1 hour duration
      backgroundColor: getStatusColor(appointment.status),
      borderColor: getStatusColor(appointment.status),
      extendedProps: {
        status: appointment.status,
        type: appointment.type,
        priority: appointment.priority,
        patientName: appointment.patient?.name,
        veterinarianName: appointment.veterinarian
          ? `Dr. ${appointment.veterinarian.firstName} ${appointment.veterinarian.lastName}`
          : 'Unassigned',
        reason: appointment.reason,
      },
    }))
  }, [appointments])

  const handleEventClick = (info: any) => {
    const appointmentId = parseInt(info.event.id)
    navigate(ROUTES.APPOINTMENTS.VIEW(appointmentId))
  }

  const handleEventDrop = async (info: any) => {
    if (!canEdit) {
      info.revert()
      toast.error('You do not have permission to reschedule appointments')
      return
    }

    const appointmentId = parseInt(info.event.id)
    const newDate = info.event.start.toISOString()

    try {
      await updateAppointment.mutateAsync({
        id: appointmentId,
        data: { scheduledAt: newDate },
      })
      toast.success('Appointment rescheduled successfully')
    } catch (error) {
      info.revert()
      toast.error('Failed to reschedule appointment')
    }
  }

  const handleDateClick = (info: any) => {
    if (canEdit) {
      navigate(ROUTES.APPOINTMENTS.CREATE, {
        state: { defaultDate: info.dateStr },
      })
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate(ROUTES.APPOINTMENTS.LIST)}
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Appointment Calendar</h2>
            <p className="text-muted-foreground">
              View and manage appointments in calendar format
            </p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={() => navigate(ROUTES.APPOINTMENTS.LIST)}
          >
            <List className="mr-2 h-4 w-4" />
            List View
          </Button>
          {canEdit && (
            <Button onClick={() => navigate(ROUTES.APPOINTMENTS.CREATE)}>
              <Plus className="mr-2 h-4 w-4" />
              New Appointment
            </Button>
          )}
        </div>
      </div>

      {/* Legend */}
      <Card>
        <CardHeader>
          <CardTitle className="text-sm">Status Legend</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-3">
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded" style={{ backgroundColor: '#3B82F6' }} />
            <span className="text-sm">Scheduled</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded" style={{ backgroundColor: '#10B981' }} />
            <span className="text-sm">Completed</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded" style={{ backgroundColor: '#EF4444' }} />
            <span className="text-sm">Cancelled</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded" style={{ backgroundColor: '#F59E0B' }} />
            <span className="text-sm">No Show</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded" style={{ backgroundColor: '#8B5CF6' }} />
            <span className="text-sm">Confirmed</span>
          </div>
        </CardContent>
      </Card>

      {/* Calendar */}
      <Card>
        <CardContent className="p-6">
          <FullCalendar
            plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
            initialView={currentView}
            headerToolbar={{
              left: 'prev,next today',
              center: 'title',
              right: 'dayGridMonth,timeGridWeek,timeGridDay',
            }}
            events={calendarEvents}
            eventClick={handleEventClick}
            eventDrop={handleEventDrop}
            dateClick={handleDateClick}
            editable={canEdit}
            droppable={canEdit}
            height="auto"
            slotMinTime="08:00:00"
            slotMaxTime="20:00:00"
            allDaySlot={false}
            nowIndicator={true}
            eventTimeFormat={{
              hour: '2-digit',
              minute: '2-digit',
              meridiem: 'short',
            }}
            slotLabelFormat={{
              hour: '2-digit',
              minute: '2-digit',
              meridiem: 'short',
            }}
            dayMaxEvents={3}
            moreLinkText="more"
            eventContent={renderEventContent}
          />
        </CardContent>
      </Card>

      {/* Info Card */}
      {canEdit && (
        <Card className="bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800">
          <CardContent className="p-4">
            <p className="text-sm text-blue-900 dark:text-blue-100">
              💡 <strong>Tip:</strong> Click on any date to create a new appointment, or drag and
              drop existing appointments to reschedule them.
            </p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}

// Helper function to get status color
function getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    scheduled: '#3B82F6', // blue
    confirmed: '#8B5CF6', // purple
    completed: '#10B981', // green
    cancelled: '#EF4444', // red
    no_show: '#F59E0B', // amber
  }
  return colors[status] || '#6B7280' // gray default
}

// Custom event content renderer
function renderEventContent(eventInfo: any) {
  return (
    <div className="fc-event-main-frame p-1">
      <div className="fc-event-time text-xs font-semibold">
        {eventInfo.timeText}
      </div>
      <div className="fc-event-title text-xs truncate">
        {eventInfo.event.extendedProps.patientName}
      </div>
      <div className="fc-event-description text-xs opacity-75 truncate">
        {eventInfo.event.extendedProps.type}
      </div>
    </div>
  )
}
