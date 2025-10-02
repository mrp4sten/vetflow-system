-- database/seed-data.sql
-- VetFlow - Sample Data for Demonstration
-- Safe, realistic data for portfolio showcase

-- =============================================
-- SECTION 1: SYSTEM USERS (Clinic Staff)
-- =============================================
INSERT INTO system_users (username, email, password_hash, role) VALUES 
('dr_rodriguez', 'dr.rodriguez@vetflow.com', '$2a$10$exampleHashedPassword1', 'veterinarian'),
('asistente_ana', 'ana@vetflow.com', '$2a$10$exampleHashedPassword2', 'assistant'),
('admin_maria', 'maria.admin@vetflow.com', '$2a$10$exampleHashedPassword3', 'admin');

-- =============================================
-- SECTION 2: PET OWNERS (Clientes)
-- =============================================
INSERT INTO owners (name, phone, email, address) VALUES
('María García', '+525512345678', 'maria.garcia@email.com', 'Av. Reforma 123, CDMX'),
('Carlos López', '+525598765432', 'carlos.lopez@email.com', 'Calle Morelos 456, Guadalajara'),
('Ana Martínez', '+525555443322', 'ana.martinez@email.com', 'Blvd. Díaz Ordaz 789, Monterrey');

-- =============================================
-- SECTION 3: PATIENTS (Mascotas)
-- =============================================
INSERT INTO patients (name, species, breed, birth_date, weight, allergies, owner_id) VALUES
('Max', 'dog', 'Labrador Retriever', '2020-05-15', 28.5, 'None', 1),
('Luna', 'cat', 'Siamese', '2021-02-20', 4.2, 'Pollen', 1),
('Rocky', 'dog', 'Bulldog', '2019-11-10', 22.0, 'Chicken', 2),
('Mimi', 'cat', 'Persian', '2022-03-08', 3.8, 'None', 3);

-- =============================================
-- SECTION 4: APPOINTMENTS (Citas)
-- =============================================
INSERT INTO appointments (patient_id, appointment_date, type, status, priority, notes) VALUES
(1, '2024-01-15 10:00:00', 'checkup', 'completed', 'normal', 'Annual checkup - healthy'),
(2, '2024-01-15 11:30:00', 'vaccination', 'completed', 'normal', 'Rabies vaccine administered'),
(3, '2024-01-16 09:00:00', 'surgery', 'scheduled', 'high', 'Dental cleaning with anesthesia'),
(1, '2024-01-20 14:00:00', 'checkup', 'scheduled', 'normal', 'Follow-up on weight management');

-- =============================================
-- SECTION 5: MEDICAL RECORDS (Historial Médico)
-- =============================================
INSERT INTO medical_records (patient_id, veterinarian_id, diagnosis, treatment, medications, notes) VALUES
(1, 1, 'Healthy annual checkup', 'Routine physical examination', 'None', 'Patient in excellent condition'),
(2, 1, 'Vaccination update', 'Rabies vaccine administered', 'Rabies vaccine 1mL', 'No adverse reactions observed'),
(3, 1, 'Dental plaque buildup', 'Scheduled for dental cleaning', 'Pre-op bloodwork completed', 'Procedure scheduled for next week');