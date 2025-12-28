# Smart Clinic – Database Schema Design

This document defines the complete database schema for the Smart Clinic application, including both MySQL (relational data) and MongoDB (document-based data) structures.

---

## MySQL Database Design

MySQL stores the core operational and relational data for the clinic system. The following tables are designed with normalization, referential integrity, and business rule enforcement in mind.

---

### Table: patients

Stores patient account information and personal details.

```sql
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    address TEXT,
    allergies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_username (username)
);
```

**Design Decisions:**
- `email` is UNIQUE to prevent duplicate registrations (US-P001)
- `password_hash` stores encrypted passwords, never plain text
- `is_active` allows soft deletion - preserves data for audit/history
- Indexes on email and phone for fast lookup during login and search

---

### Table: doctors

Stores doctor profiles, credentials, and professional information.

```sql
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    specialization VARCHAR(100) NOT NULL,
    bio TEXT,
    profile_photo_url VARCHAR(255),
    consultation_fee DECIMAL(10, 2),
    years_of_experience INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_username (username)
);
```

**Design Decisions:**
- Soft deletion via `is_active` preserves historical appointment data
- `consultation_fee` supports future payment integration

---

### Table: admin

Stores administrator account information.

```sql
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('super_admin', 'admin') DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email)
);
```

**Design Decisions:**
- Separate table from patients/doctors for clear role separation and security
- `role` field allows hierarchical admin permissions (super_admin has elevated privileges)
- Cannot be soft-deleted if they're the last active super_admin (business rule enforced in code)

---

### Table: appointments

Stores all appointment bookings and their status.

```sql
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    duration_minutes INT DEFAULT 30,
    status ENUM('scheduled', 'confirmed', 'completed', 'cancelled', 'no_show') DEFAULT 'scheduled',
    appointment_reason TEXT,
    patient_notes TEXT,
    cancellation_reason TEXT,
    consultation_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_status (status),
    INDEX idx_doctor_date_time (doctor_id, appointment_date, appointment_time),
    UNIQUE KEY unique_doctor_slot (doctor_id, appointment_date, appointment_time),
    CONSTRAINT chk_appointment_time CHECK (appointment_time BETWEEN '00:00:00' AND '23:59:59'),
    CONSTRAINT chk_duration CHECK (duration_minutes > 0 AND duration_minutes <= 480)
);
```

**Design Decisions:**
- **RESTRICT on DELETE**: If patient/doctor is deleted, appointments are preserved (US-A007 - data retention)
- **UNIQUE constraint** on (doctor_id, appointment_date, appointment_time) prevents double-booking (US-P005)
- Separate date and time columns for easier querying and indexing
- `duration_minutes` allows flexible appointment lengths (US-A005)
- Status enum tracks appointment lifecycle (US-P006, US-D008)
- `consultation_notes` filled by doctor after completion (US-D008)
- `cancellation_reason` supports cancellation policy enforcement (US-P008)
- Composite index on (doctor_id, appointment_date, appointment_time) optimizes availability checks
- `completed_at` and `cancelled_at` timestamps for audit trails

**Business Rules (enforced in application code):**
- No overlapping appointments for same doctor (enforced by unique constraint)
- Cancellation only allowed 24+ hours before appointment (US-P008)
- Rescheduling only allowed 24+ hours before appointment (US-P007)

---

### Table: doctor_schedules

Defines doctor availability and working hours.

```sql
CREATE TABLE doctor_schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_doctor (doctor_id),
    INDEX idx_day (day_of_week),
    CONSTRAINT chk_time_range CHECK (end_time > start_time)
);
```

**Design Decisions:**
- Recurring weekly schedule pattern (US-D003)
- Multiple rows per doctor allow complex schedules (e.g., morning and afternoon shifts)
- `is_available` allows temporary disabling without deletion
- CASCADE delete: If doctor is removed, their schedules are also removed
- Check constraint ensures logical time ranges

---

### Table: schedule_exceptions

Handles blocked time slots and special availability changes.

```sql
CREATE TABLE schedule_exceptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    exception_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    reason VARCHAR(255),
    is_available BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_doctor_date (doctor_id, exception_date),
    CONSTRAINT chk_exception_time_range CHECK (end_time > start_time)
);
```

**Design Decisions:**
- Overrides regular schedule for specific dates (US-D004)
- `is_available = FALSE` blocks time, `TRUE` adds extra availability
- Supports vacation days, holidays, meetings, emergency blocks
- Examples: "Block Dec 25 for Christmas", "Add extra hours on Dec 31"

---

### Table: audit_logs

Tracks all critical system actions for security and compliance.

```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_type ENUM('patient', 'doctor', 'admin') NOT NULL,
    user_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    details TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_type, user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at),
    INDEX idx_entity (entity_type, entity_id)
);
```

**Design Decisions:**
- Comprehensive audit trail for compliance (HIPAA, GDPR) (US-A003)
- Stores who did what, when, and from where
- `user_type` and `user_id` reference the appropriate user table
- IP address and user agent for security monitoring
- BIGINT primary key supports high-volume logging
- Never deleted - permanent record for legal/regulatory requirements

**Example log entries:**
- "Admin changed doctor status to approved"
- "Patient cancelled appointment ID 123"
- "Doctor completed consultation for appointment 456"

---

## Relationship Summary

```
patients (1) ─────< (N) appointments (N) >───── (1) doctors
                         │
                         └──> Prevented by UNIQUE(doctor_id, date, time)
                              from overlapping bookings

doctors (1) ─────< (N) doctor_schedules

doctors (1) ─────< (N) schedule_exceptions

admin (1) ─────< (N) audit_logs
patients (1) ─────< (N) audit_logs  
doctors (1) ─────< (N) audit_logs

admin (1) ─────< (N) system_settings (updated_by)
```

---

## Critical Business Rules Enforced by Schema

1. **No Double Booking**: UNIQUE constraint on `appointments(doctor_id, appointment_date, appointment_time)`

2. **Data Preservation**: Foreign keys use RESTRICT on appointments to prevent accidental data loss when users are deactivated

3. **Soft Deletion**: `is_active` flags allow deactivation without losing historical data

4. **Audit Trail**: All critical actions logged in `audit_logs` for compliance

5. **Time Validation**: CHECK constraints ensure end_time > start_time

6. **Unique Identification**: Email addresses and medical license numbers are unique

---

## Notes on MongoDB Integration

Per the architecture document, **prescriptions** are stored in MongoDB as document-based data. This allows:
- Flexible schema for different medication types
- Nested structures for dosage instructions
- Easy evolution as prescription requirements change

The MongoDB `prescriptions` collection links back to MySQL via `appointment_id` or `patient_id` references.

---

## MongoDB Collection Design

MongoDB stores flexible, document-based data that doesn't fit well into rigid relational tables. These collections complement the MySQL schema with nested structures, arrays, and schema-free designs that can evolve over time.

---

### Collection: prescriptions

Stores medication prescriptions with flexible nested structures and metadata.

**Example Document:**

```json
{
  "_id": ObjectId("676a8f1234567890abcdef12"),
  "appointmentId": 1523,
  "patientId": 245,
  "doctorId": 78,
  "patientName": "Sarah Johnson",
  "doctorName": "Dr. Michael Chen",
  "prescriptionDate": ISODate("2025-12-15T14:30:00Z"),
  "expiryDate": ISODate("2026-12-15T14:30:00Z"),
  "status": "active",
  "medications": [
    {
      "name": "Amoxicillin",
      "genericName": "Amoxicillin Trihydrate",
      "dosage": "500mg",
      "form": "capsule",
      "frequency": "3 times daily",
      "duration": "10 days",
      "quantity": 30,
      "instructions": "Take with food. Complete full course even if symptoms improve.",
    },
    {
      "name": "Ibuprofen",
      "genericName": "Ibuprofen",
      "dosage": "400mg",
      "form": "tablet",
      "frequency": "every 6-8 hours as needed",
      "duration": "7 days",
      "quantity": 20,
      "instructions": "Take with food or milk to reduce stomach upset. Do not exceed 1200mg per day.",
    }
  ],
  "diagnosis": "Acute bacterial sinusitis",
  "doctorNotes": "Patient presents with facial pain, nasal congestion, and fever. Prescribed antibiotics for bacterial infection. Follow-up in 2 weeks if symptoms persist.", 
}
```

**Design Decisions:**
- **Arrays for multiple medications**: Single prescription can include multiple drugs
- **Flexible metadata**: Can evolve without schema migration
- **References to MySQL**: `appointmentId`, `patientId`, `doctorId` link back to relational data
- **Denormalized names**: Stores patient/doctor names for quick access (reduces joins)

---

## Summary

The MongoDB collections provide:
- **Flexibility**: Schema-free design adapts to changing requirements
- **Performance**: Denormalized data reduces cross-database joins
- **Rich structure**: Nested objects and arrays model complex relationships naturally
- **Scalability**: Horizontal scaling for high-volume data (logs, messages)

Together with MySQL, this polyglot persistence approach leverages the best of both worlds: **structured relational data** for core operations and **flexible document storage** for evolving, semi-structured content.
