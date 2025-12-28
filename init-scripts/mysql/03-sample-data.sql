-- =====================================================
-- Smart Clinic Sample Data
-- =====================================================

USE cms;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. Insert Sample Patients
-- =====================================================
INSERT INTO patients (username, email, password_hash, phone, date_of_birth, gender, address, allergies, is_active) VALUES
('john_doe', 'john.doe@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0101', '1985-03-15', 'Male', '123 Main St, New York, NY 10001', 'Penicillin', TRUE),
('jane_smith', 'jane.smith@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0102', '1990-07-22', 'Female', '456 Oak Ave, Los Angeles, CA 90001', NULL, TRUE),
('michael_johnson', 'michael.j@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0103', '1978-11-30', 'Male', '789 Pine Rd, Chicago, IL 60601', 'Aspirin, Pollen', TRUE),
('emily_brown', 'emily.brown@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0104', '1995-05-18', 'Female', '321 Elm St, Houston, TX 77001', NULL, TRUE),
('david_williams', 'david.w@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0105', '1982-09-25', 'Male', '654 Maple Dr, Phoenix, AZ 85001', 'Latex', TRUE),
('sarah_jones', 'sarah.jones@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0106', '1988-12-10', 'Female', '987 Cedar Ln, Philadelphia, PA 19101', NULL, TRUE),
('robert_garcia', 'robert.g@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0107', '1975-04-08', 'Male', '147 Birch Ct, San Antonio, TX 78201', 'Sulfa drugs', TRUE),
('lisa_martinez', 'lisa.m@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0108', '1992-08-14', 'Female', '258 Spruce Way, San Diego, CA 92101', NULL, TRUE),
('james_rodriguez', 'james.r@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0109', '1980-01-20', 'Male', '369 Willow Blvd, Dallas, TX 75201', 'Iodine', TRUE),
('mary_lopez', 'mary.lopez@email.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-0110', '1987-06-12', 'Female', '741 Ash St, San Jose, CA 95101', NULL, TRUE);

-- =====================================================
-- 2. Insert Sample Doctors
-- =====================================================
INSERT INTO doctors (username, email, password_hash, phone, specialization, bio, profile_photo_url, consultation_fee, years_of_experience, is_active) VALUES
('dr_anderson', 'dr.anderson@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-1001', 'Cardiology', 'Board-certified cardiologist with 15 years of experience', '/images/doctors/anderson.jpg', 200.00, 15, TRUE),
('dr_patel', 'dr.patel@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-1002', 'Pediatrics', 'Specialized in child healthcare and development', '/images/doctors/patel.jpg', 150.00, 10, TRUE),
('dr_lee', 'dr.lee@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-1003', 'Dermatology', 'Expert in skin conditions and cosmetic dermatology', '/images/doctors/lee.jpg', 180.00, 12, TRUE),
('dr_thompson', 'dr.thompson@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-1004', 'Orthopedics', 'Specializing in sports injuries and joint replacement', '/images/doctors/thompson.jpg', 220.00, 18, TRUE),
('dr_nguyen', 'dr.nguyen@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-1005', 'Internal Medicine', 'General internal medicine and chronic disease management', '/images/doctors/nguyen.jpg', 170.00, 8, TRUE),
('dr_wilson', 'dr.wilson@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-1006', 'Neurology', 'Neurological disorders and brain health specialist', '/images/doctors/wilson.jpg', 250.00, 20, TRUE);

-- =====================================================
-- 3. Insert Sample Admin
-- =====================================================
INSERT INTO admin (username, email, password_hash, phone, role, is_active) VALUES
('admin', 'admin@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-9999', 'super_admin', TRUE),
('manager', 'manager@smartclinic.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '555-9998', 'admin', TRUE);

-- =====================================================
-- 4. Insert Sample Appointments - December 2025
-- =====================================================
-- Week 1 (Dec 1-7, 2025)
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, duration_minutes, status, appointment_reason, consultation_notes, completed_at) VALUES
(1, 1, '2025-12-01', '09:00:00', 30, 'completed', 'Annual checkup', 'Patient in good health. Blood pressure normal.', '2025-12-01 09:30:00'),
(2, 2, '2025-12-01', '10:00:00', 45, 'completed', 'Child vaccination', 'Administered flu vaccine. No adverse reactions.', '2025-12-01 10:45:00'),
(3, 3, '2025-12-01', '11:00:00', 30, 'completed', 'Skin rash consultation', 'Prescribed topical ointment for eczema.', '2025-12-01 11:30:00'),
(4, 1, '2025-12-01', '14:00:00', 30, 'completed', 'Follow-up visit', 'Cardiac stress test scheduled.', '2025-12-01 14:30:00'),
(5, 4, '2025-12-01', '15:00:00', 60, 'completed', 'Knee pain evaluation', 'Recommended physical therapy and X-ray.', '2025-12-01 16:00:00'),

(1, 2, '2025-12-02', '09:00:00', 30, 'completed', 'Flu symptoms', 'Prescribed antiviral medication.', '2025-12-02 09:30:00'),
(6, 1, '2025-12-02', '10:00:00', 30, 'completed', 'Chest pain evaluation', 'ECG normal. Anxiety-related symptoms.', '2025-12-02 10:30:00'),
(7, 5, '2025-12-02', '11:00:00', 45, 'completed', 'Diabetes management', 'A1C levels improved. Continue current medication.', '2025-12-02 11:45:00'),
(8, 3, '2025-12-02', '14:00:00', 30, 'completed', 'Acne treatment', 'Started on new topical regimen.', '2025-12-02 14:30:00'),
(9, 6, '2025-12-02', '15:00:00', 45, 'completed', 'Migraine consultation', 'Prescribed preventive medication.', '2025-12-02 15:45:00'),

(2, 1, '2025-12-03', '09:00:00', 30, 'completed', 'Heart palpitations', 'Holter monitor ordered for 24-hour recording.', '2025-12-03 09:30:00'),
(3, 2, '2025-12-03', '10:00:00', 30, 'completed', 'Well-child visit', 'Growth and development on track.', '2025-12-03 10:30:00'),
(4, 5, '2025-12-03', '11:00:00', 30, 'completed', 'High blood pressure', 'Adjusted medication dosage.', '2025-12-03 11:30:00'),
(5, 4, '2025-12-03', '14:00:00', 45, 'completed', 'Physical therapy follow-up', 'Good progress. Continue exercises.', '2025-12-03 14:45:00'),
(10, 1, '2025-12-03', '15:00:00', 30, 'completed', 'Annual cardiac screening', 'All tests within normal limits.', '2025-12-03 15:30:00'),

-- Week 2 (Dec 8-14, 2025)
(1, 1, '2025-12-08', '09:00:00', 30, 'completed', 'Stress test results', 'Results normal. No intervention needed.', '2025-12-08 09:30:00'),
(6, 2, '2025-12-08', '10:00:00', 30, 'completed', 'Pediatric consultation', 'Ear infection diagnosed. Antibiotics prescribed.', '2025-12-08 10:30:00'),
(7, 3, '2025-12-08', '11:00:00', 30, 'completed', 'Skin cancer screening', 'All moles benign. Annual follow-up recommended.', '2025-12-08 11:30:00'),
(8, 6, '2025-12-08', '14:00:00', 45, 'completed', 'Headache follow-up', 'Medication working well. Continue treatment.', '2025-12-08 14:45:00'),
(9, 4, '2025-12-08', '15:00:00', 60, 'completed', 'Shoulder injury', 'MRI scheduled. Possible rotator cuff tear.', '2025-12-08 16:00:00'),

(2, 5, '2025-12-09', '09:00:00', 30, 'completed', 'Thyroid check', 'TSH levels normal. Continue current dose.', '2025-12-09 09:30:00'),
(3, 1, '2025-12-09', '10:00:00', 30, 'completed', 'Cholesterol management', 'Lipid panel improved. Diet working.', '2025-12-09 10:30:00'),
(4, 2, '2025-12-09', '11:00:00', 30, 'completed', 'Immunization update', 'Tetanus booster administered.', '2025-12-09 11:30:00'),
(5, 3, '2025-12-09', '14:00:00', 30, 'completed', 'Psoriasis treatment', 'New biologic therapy initiated.', '2025-12-09 14:30:00'),
(10, 6, '2025-12-09', '15:00:00', 45, 'completed', 'Memory concerns', 'Cognitive testing scheduled.', '2025-12-09 15:45:00'),

-- Week 3 (Dec 15-21, 2025)
(1, 4, '2025-12-15', '09:00:00', 45, 'completed', 'Back pain', 'Physical therapy referral provided.', '2025-12-15 09:45:00'),
(6, 1, '2025-12-15', '10:00:00', 30, 'completed', 'Hypertension follow-up', 'Blood pressure controlled. Continue meds.', '2025-12-15 10:30:00'),
(7, 5, '2025-12-15', '11:00:00', 30, 'completed', 'Asthma management', 'Inhaler technique reviewed.', '2025-12-15 11:30:00'),
(8, 2, '2025-12-15', '14:00:00', 30, 'completed', 'Growth assessment', 'Height and weight appropriate for age.', '2025-12-15 14:30:00'),
(9, 3, '2025-12-15', '15:00:00', 30, 'completed', 'Hair loss consultation', 'Minoxidil treatment started.', '2025-12-15 15:30:00'),

-- Week 4 (Dec 22-28, 2025) - Current Week
(2, 1, '2025-12-22', '09:00:00', 30, 'completed', 'Pre-surgery clearance', 'Cleared for elective procedure.', '2025-12-22 09:30:00'),
(3, 6, '2025-12-22', '10:00:00', 45, 'completed', 'Seizure evaluation', 'EEG ordered. Adjust medication.', '2025-12-22 10:45:00'),
(4, 4, '2025-12-22', '11:00:00', 60, 'completed', 'Hip replacement consultation', 'Surgery scheduled for next month.', '2025-12-22 12:00:00'),
(5, 5, '2025-12-22', '14:00:00', 30, 'completed', 'Chronic fatigue', 'Lab work ordered to rule out anemia.', '2025-12-22 14:30:00'),
(10, 2, '2025-12-22', '15:00:00', 30, 'completed', 'Sports physical', 'Cleared for athletic participation.', '2025-12-22 15:30:00'),

(1, 1, '2025-12-23', '09:00:00', 30, 'completed', 'Medication review', 'Updated prescription list.', '2025-12-23 09:30:00'),
(6, 3, '2025-12-23', '10:00:00', 30, 'completed', 'Wart removal', 'Cryotherapy performed successfully.', '2025-12-23 10:30:00'),
(7, 2, '2025-12-23', '11:00:00', 30, 'completed', 'Allergy testing', 'Pollen allergies confirmed.', '2025-12-23 11:30:00'),
(8, 5, '2025-12-23', '14:00:00', 30, 'completed', 'Weight management', 'Nutrition plan discussed.', '2025-12-23 14:30:00'),
(9, 1, '2025-12-23', '15:00:00', 30, 'completed', 'Arrhythmia monitoring', 'Holter results reviewed. Normal.', '2025-12-23 15:30:00'),

-- Today's appointments (Dec 28, 2025)
(1, 1, '2025-12-28', '09:00:00', 30, 'confirmed', 'Follow-up cardiac consultation', NULL, NULL),
(2, 2, '2025-12-28', '09:00:00', 30, 'confirmed', 'Pediatric check-up', NULL, NULL),
(3, 3, '2025-12-28', '10:00:00', 30, 'scheduled', 'Dermatology consultation', NULL, NULL),
(4, 4, '2025-12-28', '10:00:00', 45, 'scheduled', 'Orthopedic follow-up', NULL, NULL),
(5, 5, '2025-12-28', '11:00:00', 30, 'confirmed', 'Internal medicine check', NULL, NULL),
(6, 1, '2025-12-28', '14:00:00', 30, 'scheduled', 'Blood pressure check', NULL, NULL),
(7, 6, '2025-12-28', '14:00:00', 45, 'scheduled', 'Neurology follow-up', NULL, NULL),
(8, 2, '2025-12-28', '15:00:00', 30, 'scheduled', 'Vaccination appointment', NULL, NULL),

-- Some cancelled and no-show appointments
(9, 3, '2025-12-20', '09:00:00', 30, 'cancelled', 'Skin check', NULL, NULL),
(10, 4, '2025-12-20', '10:00:00', 45, 'no_show', 'Joint pain', NULL, NULL),
(1, 5, '2025-12-21', '11:00:00', 30, 'cancelled', 'Routine check', NULL, NULL);

-- =====================================================
-- 5. Insert Sample Appointments - November 2025
-- =====================================================
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, duration_minutes, status, appointment_reason, consultation_notes, completed_at) VALUES
-- Week 1 November
(1, 1, '2025-11-03', '09:00:00', 30, 'completed', 'Cardiac evaluation', 'Patient stable. Continue medication.', '2025-11-03 09:30:00'),
(2, 2, '2025-11-03', '10:00:00', 30, 'completed', 'Child wellness visit', 'Vaccinations up to date.', '2025-11-03 10:30:00'),
(3, 3, '2025-11-03', '11:00:00', 30, 'completed', 'Skin examination', 'No abnormalities found.', '2025-11-03 11:30:00'),
(4, 1, '2025-11-04', '09:00:00', 30, 'completed', 'Blood pressure check', 'Hypertension controlled.', '2025-11-04 09:30:00'),
(5, 4, '2025-11-04', '10:00:00', 60, 'completed', 'Sports injury', 'Sprained ankle. Rest recommended.', '2025-11-04 11:00:00'),
(6, 5, '2025-11-05', '09:00:00', 30, 'completed', 'Diabetes screening', 'Pre-diabetic. Lifestyle changes advised.', '2025-11-05 09:30:00'),
(7, 6, '2025-11-05', '10:00:00', 45, 'completed', 'Headache evaluation', 'Tension headaches. Stress management.', '2025-11-05 10:45:00'),
(8, 1, '2025-11-06', '09:00:00', 30, 'completed', 'Heart palpitations', 'Anxiety-related. No cardiac issues.', '2025-11-06 09:30:00'),
(9, 2, '2025-11-06', '10:00:00', 30, 'completed', 'Flu symptoms', 'Viral infection. Rest and fluids.', '2025-11-06 10:30:00'),
(10, 3, '2025-11-06', '11:00:00', 30, 'completed', 'Mole check', 'All benign. Annual screening advised.', '2025-11-06 11:30:00'),

-- Week 2 November
(1, 1, '2025-11-10', '09:00:00', 30, 'completed', 'Follow-up', 'All tests normal.', '2025-11-10 09:30:00'),
(2, 4, '2025-11-11', '10:00:00', 45, 'completed', 'Knee pain', 'Osteoarthritis. Pain management plan.', '2025-11-11 10:45:00'),
(3, 5, '2025-11-12', '09:00:00', 30, 'completed', 'Annual physical', 'Healthy. Preventive care discussed.', '2025-11-12 09:30:00'),
(4, 6, '2025-11-13', '10:00:00', 45, 'completed', 'Memory issues', 'Cognitive testing scheduled.', '2025-11-13 10:45:00'),
(5, 1, '2025-11-14', '09:00:00', 30, 'completed', 'Chest discomfort', 'Gastric reflux. Prescribed PPI.', '2025-11-14 09:30:00');

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Sample data inserted successfully!' AS status;
SELECT 
    (SELECT COUNT(*) FROM patients) AS total_patients,
    (SELECT COUNT(*) FROM doctors) AS total_doctors,
    (SELECT COUNT(*) FROM admin) AS total_admins,
    (SELECT COUNT(*) FROM appointments) AS total_appointments;
