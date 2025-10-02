-- database/schema.sql
-- VetFlow - Veterinary Management System
-- Database Schema v1.0
-- Designed by Mauricio Pasten (@MrP4sten)
-- Security by Design Implementation

-- =============================================
-- SECTION 1: CORE SECURITY TABLES
-- =============================================
-- Table: system_users
-- Purpose: Clinic staff authentication and authorization
-- Security: Passwords hashed, role-based access
CREATE TABLE system_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'veterinarian', 'assistant')),
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_valid_username CHECK (username ~* '^[a-zA-Z0-9_]+$')
);

-- Table: owners
-- Purpose: Manage pet owners information
CREATE TABLE owners (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_valid_phone CHECK (phone ~* '^\+?[0-9\s\-\(\)]{10,}$')
);

-- =============================================
-- SECTION 2: CORE BUSINESS TABLES
-- =============================================

-- Table: patients
-- Purpose: Store pet information and link to owners
CREATE TABLE patients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    birth_date DATE,
    weight DECIMAL(5,2),
    allergies TEXT,
    owner_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_patient_owner 
        FOREIGN KEY (owner_id) REFERENCES owners(id)
        ON DELETE RESTRICT,
    
    -- Data validation
    CONSTRAINT chk_valid_species 
        CHECK (species IN ('dog', 'cat')),
    CONSTRAINT chk_positive_weight 
        CHECK (weight > 0)
);

-- Table: appointments  
-- Purpose: Manage veterinary appointments
CREATE TABLE appointments (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'scheduled',
    priority VARCHAR(20) DEFAULT 'normal',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE CASCADE,
    
    -- Data validation
    CONSTRAINT chk_valid_type
        CHECK (type IN ('checkup', 'vaccination', 'surgery', 'grooming', 'emergency')),
    CONSTRAINT chk_valid_status
        CHECK (status IN ('scheduled', 'completed', 'cancelled', 'no_show')),
    CONSTRAINT chk_valid_priority
        CHECK (priority IN ('low', 'normal', 'high', 'critical'))
);

-- =============================================
-- SECTION 3: MEDICAL & AUDIT TABLES
-- =============================================

-- Table: medical_records
-- Purpose: Complete medical history for each patient visit
-- Table: medical_records (VERSIÓN MEJORADA)
CREATE TABLE medical_records (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL,
    veterinarian_id INTEGER NOT NULL,
    visit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diagnosis TEXT NOT NULL,
    treatment TEXT,
    medications TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_medical_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_medical_veterinarian
        FOREIGN KEY (veterinarian_id) REFERENCES system_users(id)
        ON DELETE RESTRICT,
    
    -- Data validation
    CONSTRAINT chk_diagnosis_not_empty 
        CHECK (LENGTH(TRIM(diagnosis)) > 0)
);

-- Table: audit_log  
-- Purpose: Track all data changes for security and compliance
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Data validation
    CONSTRAINT chk_valid_action
        CHECK (action IN ('INSERT', 'UPDATE', 'DELETE')),
    CONSTRAINT chk_valid_table
        CHECK (table_name IN ('owners', 'patients', 'appointments', 'medical_records', 'system_users'))
);

-- =============================================
-- SECTION 4: PERFORMANCE OPTIMIZATION
-- =============================================

-- Indexes for frequently queried fields
-- These speed up searches, joins, and filters

-- Owners table indexes
CREATE INDEX idx_owners_email ON owners(email);
CREATE INDEX idx_owners_phone ON owners(phone);
CREATE INDEX idx_owners_created_at ON owners(created_at);

-- Patients table indexes  
CREATE INDEX idx_patients_owner_id ON patients(owner_id);
CREATE INDEX idx_patients_species ON patients(species);
CREATE INDEX idx_patients_name ON patients(name);
CREATE INDEX idx_patients_created_at ON patients(created_at);

-- Appointments table indexes
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_priority ON appointments(priority);
CREATE INDEX idx_appointments_date_status ON appointments(appointment_date, status);

-- Medical records indexes
CREATE INDEX idx_medical_records_patient_id ON medical_records(patient_id);
CREATE INDEX idx_medical_records_visit_date ON medical_records(visit_date);
CREATE INDEX idx_medical_records_veterinarian ON medical_records(veterinarian);

-- System users indexes
CREATE INDEX idx_system_users_username ON system_users(username);
CREATE INDEX idx_system_users_email ON system_users(email);
CREATE INDEX idx_system_users_role ON system_users(role);

-- Audit log indexes (critical for performance)
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);