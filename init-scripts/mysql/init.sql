-- Smart Clinic MySQL Initialization Script

-- Set character set and collation
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Use the database
USE cms;

-- Create indexes explicitly if needed (JPA will create tables)
-- This script is mainly for additional setup or seed data

-- Optional: Insert sample admin user (password should be hashed in production)
-- INSERT INTO admin (username, email, password_hash, phone, role, is_active) 
-- VALUES ('admin', 'admin@smartclinic.com', '$2a$10$...', '1234567890', 'super_admin', TRUE);

-- Optional: Insert sample doctor
-- INSERT INTO doctors (username, email, password_hash, phone, specialization, bio, is_active)
-- VALUES ('dr.john', 'dr.john@smartclinic.com', '$2a$10$...', '1234567891', 'Cardiology', 'Experienced cardiologist', TRUE);

-- Optional: Insert sample patient
-- INSERT INTO patients (username, email, password_hash, phone, date_of_birth, gender, is_active)
-- VALUES ('patient1', 'patient1@email.com', '$2a$10$...', '1234567892', '1990-01-01', 'Male', TRUE);

SELECT 'MySQL initialization completed for Smart Clinic' AS message;
