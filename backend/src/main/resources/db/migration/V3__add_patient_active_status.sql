-- Add is_active column to patients table for soft delete functionality

ALTER TABLE patients
ADD COLUMN is_active BOOLEAN DEFAULT TRUE NOT NULL;

-- Add index for better query performance when filtering active patients
CREATE INDEX idx_patients_is_active ON patients(is_active);

-- Add comment to document the column purpose
COMMENT ON COLUMN patients.is_active IS 'Indicates whether the patient record is active (soft delete flag)';
