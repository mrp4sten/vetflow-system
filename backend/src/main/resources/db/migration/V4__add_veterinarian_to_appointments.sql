-- VetFlow - Veterinary Management System
-- Flyway Migration: V4__add_veterinarian_to_appointments.sql
-- Author: Mauricio Pasten (@MrP4sten)
-- Description: Add veterinarian assignment to appointments table

-- =============================================
-- SECTION 1: ADD VETERINARIAN COLUMN
-- =============================================

-- Add veterinarian_id column to appointments table
ALTER TABLE appointments 
ADD COLUMN veterinarian_id BIGINT;

-- Add foreign key constraint to ensure veterinarian exists in system_users
ALTER TABLE appointments
ADD CONSTRAINT fk_appointment_veterinarian 
FOREIGN KEY (veterinarian_id) 
REFERENCES system_users(id) 
ON DELETE RESTRICT;

-- Add check constraint to ensure assigned user has veterinarian role
-- Note: This is enforced at application level, but good to document intent
COMMENT ON COLUMN appointments.veterinarian_id IS 
    'References system_users.id where role = veterinarian. Nullable to allow unassigned appointments.';

-- =============================================
-- SECTION 2: INDEXES FOR PERFORMANCE
-- =============================================

-- Add index for querying appointments by veterinarian
CREATE INDEX IF NOT EXISTS idx_appointments_veterinarian_id 
ON appointments(veterinarian_id);

-- Add composite index for veterinarian + date queries (e.g., vet schedule view)
CREATE INDEX IF NOT EXISTS idx_appointments_veterinarian_date 
ON appointments(veterinarian_id, appointment_date);

-- Add composite index for veterinarian + status queries (e.g., vet's active appointments)
CREATE INDEX IF NOT EXISTS idx_appointments_veterinarian_status 
ON appointments(veterinarian_id, status);
