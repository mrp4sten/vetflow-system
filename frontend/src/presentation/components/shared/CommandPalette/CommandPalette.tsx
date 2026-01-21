import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { Command } from 'cmdk'
import { 
  Search, 
  Calendar, 
  User, 
  Users, 
  FileText,
  X
} from 'lucide-react'
import { useAppointments } from '@presentation/hooks/useAppointments'
import { usePatients } from '@presentation/hooks/usePatients'
import { useOwners } from '@presentation/hooks/useOwners'
import { useMedicalRecords } from '@presentation/hooks/useMedicalRecords'
import { ROUTES } from '@shared/constants/routes'
import { formatDate } from '@infrastructure/utils/date-utils'
import './CommandPalette.css'

interface CommandPaletteProps {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export const CommandPalette: React.FC<CommandPaletteProps> = ({ open, onOpenChange }) => {
  const navigate = useNavigate()
  const [search, setSearch] = useState('')

  const { data: appointments = [] } = useAppointments()
  const { data: patients = [] } = usePatients()
  const { data: owners = [] } = useOwners()
  const { data: medicalRecords = [] } = useMedicalRecords()

  // Filter results based on search
  const filteredAppointments = appointments
    .filter(apt => 
      apt.patient?.name.toLowerCase().includes(search.toLowerCase()) ||
      apt.veterinarian?.firstName.toLowerCase().includes(search.toLowerCase()) ||
      apt.veterinarian?.lastName.toLowerCase().includes(search.toLowerCase()) ||
      apt.reason?.toLowerCase().includes(search.toLowerCase())
    )
    .slice(0, 5)

  const filteredPatients = patients
    .filter(patient =>
      patient.name?.toLowerCase().includes(search.toLowerCase()) ||
      patient.species?.toLowerCase().includes(search.toLowerCase())
    )
    .slice(0, 5)

  const filteredOwners = owners
    .filter(owner =>
      owner.name?.toLowerCase().includes(search.toLowerCase()) ||
      owner.email?.toLowerCase().includes(search.toLowerCase()) ||
      owner.phone?.includes(search)
    )
    .slice(0, 5)

  const filteredRecords = medicalRecords
    .filter(record =>
      record.patient?.name.toLowerCase().includes(search.toLowerCase()) ||
      record.diagnosis?.toLowerCase().includes(search.toLowerCase()) ||
      record.type.toLowerCase().includes(search.toLowerCase())
    )
    .slice(0, 5)

  const handleSelect = useCallback((callback: () => void) => {
    callback()
    onOpenChange(false)
    setSearch('')
  }, [onOpenChange])

  // Close on escape
  useEffect(() => {
    const down = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onOpenChange(false)
        setSearch('')
      }
    }

    document.addEventListener('keydown', down)
    return () => document.removeEventListener('keydown', down)
  }, [onOpenChange])

  if (!open) return null

  return (
    <div className="command-palette-overlay" onClick={() => onOpenChange(false)}>
      <Command 
        className="command-palette"
        onClick={(e) => e.stopPropagation()}
        shouldFilter={false}
      >
        <div className="command-header">
          <Search className="command-icon" />
          <Command.Input
            value={search}
            onValueChange={setSearch}
            placeholder="Search appointments, patients, owners, records..."
            className="command-input"
            autoFocus
          />
          <button 
            className="command-close"
            onClick={() => onOpenChange(false)}
          >
            <X className="h-4 w-4" />
          </button>
        </div>
        
        <Command.List className="command-list">
          {search.length === 0 ? (
            <Command.Empty className="command-empty">
              Type to search across all entities...
            </Command.Empty>
          ) : (
            <>
              {/* Appointments */}
              {filteredAppointments.length > 0 && (
                <Command.Group heading="Appointments" className="command-group">
                  {filteredAppointments.map((apt) => (
                    <Command.Item
                      key={`apt-${apt.id}`}
                      value={`appointment-${apt.id}`}
                      onSelect={() => handleSelect(() => navigate(ROUTES.APPOINTMENTS.VIEW(apt.id)))}
                      className="command-item"
                    >
                      <Calendar className="command-item-icon" />
                      <div className="command-item-content">
                        <div className="command-item-title">
                          {apt.patient?.name} - {formatDate(apt.scheduledAt, 'MMM dd, yyyy')}
                        </div>
                        <div className="command-item-subtitle">
                          {apt.reason || 'No reason provided'}
                        </div>
                      </div>
                    </Command.Item>
                  ))}
                </Command.Group>
              )}

              {/* Patients */}
              {filteredPatients.length > 0 && (
                <Command.Group heading="Patients" className="command-group">
                  {filteredPatients.map((patient) => (
                    <Command.Item
                      key={`patient-${patient.id}`}
                      value={`patient-${patient.id}`}
                      onSelect={() => handleSelect(() => navigate(ROUTES.PATIENTS.VIEW(patient.id)))}
                      className="command-item"
                    >
                      <User className="command-item-icon" />
                      <div className="command-item-content">
                        <div className="command-item-title">
                          {patient.name}
                        </div>
                        <div className="command-item-subtitle">
                          {patient.species} - Owner: {patient.owner?.fullName || 'Unknown'}
                        </div>
                      </div>
                    </Command.Item>
                  ))}
                </Command.Group>
              )}

              {/* Owners */}
              {filteredOwners.length > 0 && (
                <Command.Group heading="Owners" className="command-group">
                  {filteredOwners.map((owner) => (
                    <Command.Item
                      key={`owner-${owner.id}`}
                      value={`owner-${owner.id}`}
                      onSelect={() => handleSelect(() => navigate(ROUTES.OWNERS.VIEW(owner.id)))}
                      className="command-item"
                    >
                      <Users className="command-item-icon" />
                      <div className="command-item-content">
                        <div className="command-item-title">
                          {owner.fullName}
                        </div>
                        <div className="command-item-subtitle">
                          {owner.email} • {owner.phoneNumber}
                        </div>
                      </div>
                    </Command.Item>
                  ))}
                </Command.Group>
              )}

              {/* Medical Records */}
              {filteredRecords.length > 0 && (
                <Command.Group heading="Medical Records" className="command-group">
                  {filteredRecords.map((record) => (
                    <Command.Item
                      key={`record-${record.id}`}
                      value={`record-${record.id}`}
                      onSelect={() => handleSelect(() => navigate(ROUTES.MEDICAL_RECORDS.VIEW(record.id)))}
                      className="command-item"
                    >
                      <FileText className="command-item-icon" />
                      <div className="command-item-content">
                        <div className="command-item-title">
                          {record.patient?.name} - {formatDate(record.recordDate, 'MMM dd, yyyy')}
                        </div>
                        <div className="command-item-subtitle">
                          {record.diagnosis || record.type}
                        </div>
                      </div>
                    </Command.Item>
                  ))}
                </Command.Group>
              )}

              {/* No results */}
              {filteredAppointments.length === 0 && 
               filteredPatients.length === 0 && 
               filteredOwners.length === 0 && 
               filteredRecords.length === 0 && (
                <Command.Empty className="command-empty">
                  No results found for "{search}"
                </Command.Empty>
              )}
            </>
          )}
        </Command.List>

        <div className="command-footer">
          <div className="command-footer-hint">
            <kbd>↑↓</kbd> Navigate <kbd>↵</kbd> Select <kbd>ESC</kbd> Close
          </div>
        </div>
      </Command>
    </div>
  )
}
