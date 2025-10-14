-- VetFlow - Sample Data (Idempotent)
-- Safe, realistic data for portfolio/demo
-- Run as: psql "$DB_URL" -f database/seed-data.sql

BEGIN;

-- =============================================
-- SECTION 1: SYSTEM USERS (Clinic Staff)
-- =============================================
-- Note: password_hash values are bcrypt placeholders. Replace for real environments.
INSERT INTO system_users (username, email, password_hash, role)
VALUES 
  ('dr_rodriguez', 'dr.rodriguez@vetflow.com', '$2a$10$1X6n3yI1iy5Ue2k0zqz6uO5yQyS1xW6n3g5b2vJt1m9w8LZ2S7lXu', 'veterinarian'),
  ('asistente_ana', 'ana@vetflow.com',         '$2a$10$9m8kQyH0t3Y2b8LZ6n1q5O2pI7v9xC3e5F6h7J8k9L0m1N2o3P4', 'assistant'),
  ('admin_maria',   'maria.admin@vetflow.com', '$2a$10$Z7yX3qL9n0B5c2D4e6F8hJ1K2l3M4n5O6p7Q8r9S0t1U2v3W4x5', 'admin')
ON CONFLICT (email) DO NOTHING;

-- =============================================
-- SECTION 2: PET OWNERS (Clients)
-- =============================================
INSERT INTO owners (name, phone, email, address)
VALUES
  ('María García',  '+525512345678', 'maria.garcia@email.com',  'Av. Reforma 123, CDMX'),
  ('Carlos López',  '+525598765432', 'carlos.lopez@email.com',  'Calle Morelos 456, Guadalajara'),
  ('Ana Martínez',  '+525555443322', 'ana.martinez@email.com',  'Blvd. Díaz Ordaz 789, Monterrey')
ON CONFLICT (email) DO NOTHING;

-- =============================================
-- SECTION 3: PATIENTS (Pets)
-- =============================================
-- Resolve FK via owner email to avoid hardcoded IDs
INSERT INTO patients (name, species, breed, birth_date, weight, allergies, owner_id)
VALUES
  (
    'Max', 'dog', 'Labrador Retriever', '2020-05-15', 28.50, 'None',
    (SELECT id FROM owners WHERE email = 'maria.garcia@email.com')
  ),
  (
    'Luna', 'cat', 'Siamese', '2021-02-20', 4.20, 'Pollen',
    (SELECT id FROM owners WHERE email = 'maria.garcia@email.com')
  ),
  (
    'Rocky', 'dog', 'Bulldog', '2019-11-10', 22.00, 'Chicken',
    (SELECT id FROM owners WHERE email = 'carlos.lopez@email.com')
  ),
  (
    'Mimi', 'cat', 'Persian', '2022-03-08', 3.80, 'None',
    (SELECT id FROM owners WHERE email = 'ana.martinez@email.com')
  )
ON CONFLICT DO NOTHING;

-- =============================================
-- SECTION 4: APPOINTMENTS (Visits)
-- =============================================
-- Join patients by (name + owner email) to avoid ambiguity
INSERT INTO appointments (patient_id, appointment_date, type, status, priority, notes)
VALUES
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Max' AND o.email = 'maria.garcia@email.com'),
    '2024-01-15 10:00:00', 'checkup', 'completed', 'normal', 'Annual checkup - healthy'
  ),
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Luna' AND o.email = 'maria.garcia@email.com'),
    '2024-01-15 11:30:00', 'vaccination', 'completed', 'normal', 'Rabies vaccine administered'
  ),
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Rocky' AND o.email = 'carlos.lopez@email.com'),
    '2024-01-16 09:00:00', 'surgery', 'scheduled', 'high', 'Dental cleaning with anesthesia'
  ),
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Max' AND o.email = 'maria.garcia@email.com'),
    '2024-01-20 14:00:00', 'checkup', 'scheduled', 'normal', 'Follow-up on weight management'
  )
ON CONFLICT DO NOTHING;

-- =============================================
-- SECTION 5: MEDICAL RECORDS (History)
-- =============================================
INSERT INTO medical_records (patient_id, veterinarian_id, diagnosis, treatment, medications, notes)
VALUES
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Max' AND o.email = 'maria.garcia@email.com'),
    (SELECT id FROM system_users WHERE email = 'dr.rodriguez@vetflow.com'),
    'Healthy annual checkup', 'Routine physical examination', 'None', 'Patient in excellent condition'
  ),
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Luna' AND o.email = 'maria.garcia@email.com'),
    (SELECT id FROM system_users WHERE email = 'dr.rodriguez@vetflow.com'),
    'Vaccination update', 'Rabies vaccine administered', 'Rabies vaccine 1mL', 'No adverse reactions observed'
  ),
  (
    (SELECT p.id FROM patients p
      JOIN owners o ON o.id = p.owner_id
     WHERE p.name = 'Rocky' AND o.email = 'carlos.lopez@email.com'),
    (SELECT id FROM system_users WHERE email = 'dr.rodriguez@vetflow.com'),
    'Dental plaque buildup', 'Scheduled for dental cleaning', 'Pre-op bloodwork completed', 'Procedure scheduled for next week'
  )
ON CONFLICT DO NOTHING;

COMMIT;