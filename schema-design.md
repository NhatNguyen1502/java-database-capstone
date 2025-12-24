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
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    blood_type VARCHAR(5),
    allergies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_last_name (last_name)
);
```

**Design Decisions:**
- `email` is UNIQUE to prevent duplicate registrations (US-P001)
- `password_hash` stores encrypted passwords, never plain text
- `is_active` allows soft deletion - preserves data for audit/history
- Blood type and allergies support medical safety requirements (US-D006)
- Indexes on email and phone for fast lookup during login and search
- Emergency contact fields support patient safety (US-P003)

---

### Table: doctors

Stores doctor profiles, credentials, and professional information.

```sql
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    specialization VARCHAR(100) NOT NULL,
    qualifications TEXT,
    medical_license_number VARCHAR(50) UNIQUE,
    bio TEXT,
    profile_photo_url VARCHAR(255),
    consultation_fee DECIMAL(10, 2),
    years_of_experience INT,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_reviews INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_approved BOOLEAN DEFAULT FALSE,
    INDEX idx_email (email),
    INDEX idx_specialization (specialization),
    INDEX idx_rating (rating),
    INDEX idx_last_name (last_name)
);
```

**Design Decisions:**
- `medical_license_number` is UNIQUE for regulatory compliance
- `is_approved` flag requires admin approval before doctor becomes visible (US-A008, US-D002)
- Rating system (0-5 scale) supports patient decision-making (US-P004)
- Specialization indexed for fast search filtering
- Soft deletion via `is_active` preserves historical appointment data
- `consultation_fee` supports future payment integration

---

### Table: admin

Stores administrator account information.

```sql
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('super_admin', 'admin') DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email)
);
```

**Design Decisions:**
- Separate table from patients/doctors for clear role separation and security
- `role` field allows hierarchical admin permissions (super_admin has elevated privileges)
- `last_login` tracks admin activity (US-A003)
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

### Table: clinic_locations

Stores multiple clinic location information (optional, supports multi-location systems).

```sql
CREATE TABLE clinic_locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'USA',
    phone VARCHAR(20),
    email VARCHAR(100),
    operating_hours TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city)
);
```

**Design Decisions:**
- Supports multi-location clinic chains
- Can be linked to doctors via a junction table (doctor_locations) if needed
- `operating_hours` stored as text/JSON for flexible formatting
- Enables location-based doctor search (US-P004)

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

### Table: system_settings

Stores configurable system-wide settings.

```sql
CREATE TABLE system_settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    data_type ENUM('string', 'integer', 'boolean', 'json') DEFAULT 'string',
    description TEXT,
    updated_by INT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by) REFERENCES admin(id) ON DELETE SET NULL,
    INDEX idx_key (setting_key)
);
```

**Design Decisions:**
- Key-value store for system configuration (US-A005)
- Examples: appointment_duration_options, cancellation_hours_limit, max_appointments_per_day
- `data_type` helps application parse values correctly
- Tracks which admin made configuration changes
- Flexible JSON type supports complex settings

**Example rows:**
```sql
('default_appointment_duration', '30', 'integer', 'Default appointment length in minutes')
('allow_same_day_booking', 'true', 'boolean', 'Allow patients to book same-day appointments')
('business_hours', '{"start": "08:00", "end": "18:00"}', 'json', 'Clinic operating hours')
```

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

## Indexing Strategy

Indexes are strategically placed to optimize common queries:

- **Patient login**: `patients(email)`
- **Doctor search**: `doctors(specialization, rating, last_name)`
- **Appointment lookups**: `appointments(patient_id, doctor_id, appointment_date)`
- **Availability checks**: Composite index `appointments(doctor_id, appointment_date, appointment_time)`
- **Audit queries**: `audit_logs(user_type, user_id, created_at)`

---

## Data Retention Policy

**Appointments**: Retained indefinitely for medical/legal records
- Soft deletion via status change to 'cancelled'
- Physical deletion only via admin-initiated data archival process

**Audit Logs**: Permanent retention (7+ years for healthcare compliance)

**Inactive Users**: Soft deleted, retained for minimum 7 years

---

## Security Considerations

1. **Password Storage**: Only hashed passwords (`password_hash`) stored, never plain text
2. **Email Validation**: Enforced at application layer (format checking)
3. **Phone Validation**: Enforced at application layer (format checking)
4. **Access Control**: Role-based permissions enforced in application code
5. **Sensitive Data**: Patient allergies, medical info encrypted at application layer if required by regulations

---

## Future Enhancements

Potential additional tables for expanded functionality:

- **payments**: Track consultation fees and payment status
- **doctor_locations**: Many-to-many relationship for doctors working at multiple clinics
- **patient_reviews**: Allow patients to rate and review doctors (currently just aggregated in doctors table)
- **appointment_reminders**: Track notification delivery status
- **insurance_providers**: Patient insurance information
- **medical_tests**: Lab tests ordered by doctors

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
      "sideEffects": ["nausea", "diarrhea", "rash"],
      "refillsAllowed": 0,
      "refillsRemaining": 0
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
      "sideEffects": ["stomach upset", "heartburn"],
      "refillsAllowed": 2,
      "refillsRemaining": 2
    }
  ],
  "diagnosis": "Acute bacterial sinusitis",
  "doctorNotes": "Patient presents with facial pain, nasal congestion, and fever. Prescribed antibiotics for bacterial infection. Follow-up in 2 weeks if symptoms persist.",
  "pharmacy": {
    "name": "CVS Pharmacy",
    "address": "123 Main Street, San Francisco, CA 94102",
    "phone": "+1-415-555-0198",
    "faxNumber": "+1-415-555-0199"
  },
  "allergies": ["Penicillin - mild rash"],
  "warnings": [
    "Patient has documented penicillin allergy - monitor for cross-reactivity",
    "Avoid alcohol while taking antibiotics"
  ],
  "fulfillmentHistory": [
    {
      "medicationName": "Amoxicillin",
      "filledDate": ISODate("2025-12-15T16:45:00Z"),
      "pharmacy": "CVS Pharmacy",
      "pharmacist": "Jane Martinez, RPh",
      "refillNumber": 0
    }
  ],
  "tags": ["antibiotics", "sinusitis", "acute"],
  "isElectronic": true,
  "signatureUrl": "https://clinic-storage.s3.amazonaws.com/signatures/dr-chen-12152025.png",
  "createdAt": ISODate("2025-12-15T14:30:00Z"),
  "updatedAt": ISODate("2025-12-15T16:45:00Z"),
  "metadata": {
    "version": 1,
    "lastModifiedBy": "doctor",
    "sentToPharmacy": true,
    "pharmacyConfirmed": true
  }
}
```

**Design Decisions:**
- **Arrays for multiple medications**: Single prescription can include multiple drugs
- **Nested pharmacy object**: Captures complete pharmacy information
- **Fulfillment history array**: Tracks when/where prescriptions are filled
- **Flexible metadata**: Can evolve without schema migration
- **References to MySQL**: `appointmentId`, `patientId`, `doctorId` link back to relational data
- **Denormalized names**: Stores patient/doctor names for quick access (reduces joins)
- **Tags array**: Enables flexible categorization and searching
- **Status field**: Tracks lifecycle (active, fulfilled, expired, cancelled)

**Schema Evolution Support:**
- New medication fields can be added without affecting existing documents
- Optional fields like `signatureUrl` or `warnings` can be absent in older records
- Future additions: `insuranceCoverage`, `priorAuthorizationRequired`, `substitutionsAllowed`

---

### Collection: medical_notes

Stores free-form consultation notes, observations, and clinical documentation.

**Example Document:**

```json
{
  "_id": ObjectId("676a8f7890abcdef12345678"),
  "appointmentId": 1523,
  "patientId": 245,
  "doctorId": 78,
  "noteType": "consultation",
  "noteDate": ISODate("2025-12-15T14:30:00Z"),
  "chiefComplaint": "Persistent headaches and facial pain for 5 days",
  "presentIllness": "Patient reports throbbing frontal headache, worse in the morning. Associated with nasal congestion, post-nasal drip, and low-grade fever (100.2°F). No vision changes, neck stiffness, or photophobia.",
  "vitalSigns": {
    "bloodPressure": "128/82 mmHg",
    "heartRate": 78,
    "temperature": 100.2,
    "temperatureUnit": "Fahrenheit",
    "respiratoryRate": 16,
    "oxygenSaturation": 98,
    "weight": 154,
    "weightUnit": "lbs",
    "height": 66,
    "heightUnit": "inches",
    "bmi": 24.8
  },
  "physicalExamination": {
    "general": "Alert and oriented, mild distress",
    "heent": "Tenderness over maxillary sinuses bilaterally. Nasal mucosa erythematous with purulent discharge. Posterior pharynx with post-nasal drip.",
    "cardiovascular": "Regular rate and rhythm, no murmurs",
    "respiratory": "Clear to auscultation bilaterally",
    "neurological": "Cranial nerves II-XII intact, no focal deficits"
  },
  "assessment": [
    {
      "condition": "Acute bacterial sinusitis",
      "icdCode": "J01.90",
      "severity": "moderate",
      "notes": "Clinical presentation consistent with bacterial rather than viral etiology due to duration and purulent discharge"
    }
  ],
  "plan": [
    "Amoxicillin 500mg TID x 10 days",
    "Ibuprofen 400mg Q6-8H PRN pain",
    "Increase fluid intake",
    "Nasal saline irrigation BID",
    "Follow-up in 2 weeks if no improvement",
    "Return immediately if symptoms worsen or new symptoms develop"
  ],
  "patientInstructions": "Complete full course of antibiotics even if feeling better. Use saline spray to help with congestion. Return if fever persists beyond 3 days or symptoms worsen.",
  "attachments": [
    {
      "type": "image",
      "description": "Sinus X-ray - AP view",
      "url": "https://clinic-storage.s3.amazonaws.com/images/patient-245-20251215.jpg",
      "uploadedAt": ISODate("2025-12-15T14:45:00Z")
    }
  ],
  "followUpRequired": true,
  "followUpDate": ISODate("2025-12-29T14:00:00Z"),
  "tags": ["sinusitis", "antibiotics", "follow-up-needed"],
  "isConfidential": false,
  "sharedWith": ["patient"],
  "createdAt": ISODate("2025-12-15T14:30:00Z"),
  "updatedAt": ISODate("2025-12-15T14:55:00Z"),
  "signedBy": {
    "doctorId": 78,
    "doctorName": "Dr. Michael Chen",
    "signedAt": ISODate("2025-12-15T14:55:00Z"),
    "electronicSignature": true
  }
}
```

**Design Decisions:**
- **Nested objects for structured data**: Vital signs, physical exam, assessment grouped logically
- **Arrays for assessments and plans**: Multiple diagnoses and treatment steps
- **Flexible text fields**: Free-form clinical notes without rigid structure
- **Attachments array**: Support for images, PDFs, lab results
- **ICD codes**: Medical coding for billing and reporting
- **Sharing controls**: `isConfidential` and `sharedWith` for privacy
- **Complete medical record**: Comprehensive documentation in single document

---

### Collection: patient_messages

Stores chat messages and secure communications between patients, doctors, and staff.

**Example Document:**

```json
{
  "_id": ObjectId("676a9012345678abcdef9012"),
  "conversationId": "conv_245_78_20251215",
  "appointmentId": 1523,
  "participantType": "patient-doctor",
  "participants": [
    {
      "userId": 245,
      "userType": "patient",
      "userName": "Sarah Johnson",
      "email": "sarah.johnson@email.com"
    },
    {
      "userId": 78,
      "userType": "doctor",
      "userName": "Dr. Michael Chen",
      "email": "dr.chen@smartclinic.com"
    }
  ],
  "messages": [
    {
      "messageId": "msg_001",
      "senderId": 245,
      "senderType": "patient",
      "senderName": "Sarah Johnson",
      "content": "Hi Dr. Chen, I've been taking the Amoxicillin for 3 days now but still have a headache. Is this normal?",
      "timestamp": ISODate("2025-12-18T09:15:00Z"),
      "isRead": true,
      "readAt": ISODate("2025-12-18T10:30:00Z"),
      "attachments": []
    },
    {
      "messageId": "msg_002",
      "senderId": 78,
      "senderType": "doctor",
      "senderName": "Dr. Michael Chen",
      "content": "Hello Sarah, it's normal for symptoms to persist for the first few days. Antibiotics typically take 48-72 hours to show improvement. Continue the full course. If headache worsens or you develop fever above 101°F, please call the office.",
      "timestamp": ISODate("2025-12-18T10:35:00Z"),
      "isRead": true,
      "readAt": ISODate("2025-12-18T10:37:00Z"),
      "attachments": [],
      "isUrgent": false
    },
    {
      "messageId": "msg_003",
      "senderId": 245,
      "senderType": "patient",
      "senderName": "Sarah Johnson",
      "content": "Thank you! That's reassuring. I'll continue the medication.",
      "timestamp": ISODate("2025-12-18T10:40:00Z"),
      "isRead": true,
      "readAt": ISODate("2025-12-18T11:05:00Z"),
      "attachments": []
    }
  ],
  "status": "active",
  "lastMessageAt": ISODate("2025-12-18T10:40:00Z"),
  "unreadCount": {
    "patient": 0,
    "doctor": 0
  },
  "tags": ["post-appointment", "medication-question"],
  "priority": "normal",
  "isArchived": false,
  "createdAt": ISODate("2025-12-18T09:15:00Z"),
  "updatedAt": ISODate("2025-12-18T10:40:00Z"),
  "metadata": {
    "source": "patient-portal",
    "relatedCondition": "sinusitis",
    "requiresFollowUp": false
  }
}
```

**Design Decisions:**
- **Embedded messages array**: All messages in one conversation document (efficient for small-medium conversations)
- **Conversation-level metadata**: Status, participants, unread counts at top level
- **Message-level tracking**: Individual read status, timestamps, attachments
- **Flexible participants**: Array allows group conversations in future
- **Rich metadata**: Tags, priority, source tracking
- **Reference to appointment**: Links message thread to medical context

**Alternative Design for High-Volume Messaging:**
For systems with thousands of messages per conversation, split into separate `messages` collection with `conversationId` reference to avoid document size limits (16MB in MongoDB).

---

### Collection: patient_feedback

Stores patient reviews, ratings, and satisfaction surveys.

**Example Document:**

```json
{
  "_id": ObjectId("676a91abcdef123456789abc"),
  "appointmentId": 1523,
  "patientId": 245,
  "doctorId": 78,
  "patientName": "Sarah J.",
  "doctorName": "Dr. Michael Chen",
  "feedbackType": "post-appointment",
  "submittedAt": ISODate("2025-12-20T18:30:00Z"),
  "ratings": {
    "overall": 5,
    "doctorProfessionalism": 5,
    "waitTime": 4,
    "facilityCleanness": 5,
    "staffCourtesy": 5,
    "communicationClarity": 5
  },
  "overallScore": 4.8,
  "reviewText": "Dr. Chen was thorough and took time to explain my diagnosis and treatment plan. The wait was a bit long but the care I received was excellent. Feeling much better after following his recommendations!",
  "wouldRecommend": true,
  "visitDate": ISODate("2025-12-15T14:30:00Z"),
  "treatmentEffectiveness": "very-effective",
  "tags": ["positive", "thorough-explanation", "effective-treatment"],
  "isVerified": true,
  "isPublic": true,
  "isAnonymous": false,
  "status": "approved",
  "moderatedBy": 12,
  "moderatedAt": ISODate("2025-12-20T19:00:00Z"),
  "helpfulVotes": 8,
  "notHelpfulVotes": 0,
  "doctorResponse": {
    "responseText": "Thank you for your kind words, Sarah! I'm glad you're feeling better. Don't hesitate to reach out if you have any concerns.",
    "respondedBy": 78,
    "respondedAt": ISODate("2025-12-21T09:15:00Z")
  },
  "sentiment": "positive",
  "sentimentScore": 0.92,
  "createdAt": ISODate("2025-12-20T18:30:00Z"),
  "updatedAt": ISODate("2025-12-21T09:15:00Z")
}
```

**Design Decisions:**
- **Granular ratings**: Multiple aspects rated separately (overall, professionalism, wait time, etc.)
- **Text review**: Free-form feedback for detailed patient experience
- **Privacy controls**: `isPublic`, `isAnonymous` options
- **Moderation workflow**: Status (pending, approved, rejected) and moderator tracking
- **Doctor response capability**: Nested object for professional replies
- **Social features**: Helpful votes for community-driven quality
- **Sentiment analysis**: Automated sentiment scoring for analytics
- **Verification**: Links to actual appointment to prevent fake reviews

**Aggregation Use Case:**
This structure enables easy aggregation to update `doctors.rating` and `doctors.total_reviews` in MySQL periodically.

---

### Collection: activity_logs

Stores detailed user activity and system events for analytics and debugging.

**Example Document:**

```json
{
  "_id": ObjectId("676a92cdef1234567890abcd"),
  "userId": 245,
  "userType": "patient",
  "userName": "Sarah Johnson",
  "action": "appointment.booked",
  "timestamp": ISODate("2025-12-14T16:45:00Z"),
  "ipAddress": "192.168.1.105",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
  "deviceType": "desktop",
  "browser": "Chrome",
  "platform": "Windows",
  "location": {
    "city": "San Francisco",
    "state": "CA",
    "country": "USA",
    "coordinates": {
      "latitude": 37.7749,
      "longitude": -122.4194
    }
  },
  "sessionId": "sess_abc123xyz789",
  "eventDetails": {
    "appointmentId": 1523,
    "doctorId": 78,
    "doctorName": "Dr. Michael Chen",
    "appointmentDate": "2025-12-15",
    "appointmentTime": "14:30",
    "specialization": "General Practice",
    "bookingMethod": "online-portal"
  },
  "previousState": null,
  "newState": {
    "status": "scheduled",
    "confirmationSent": true
  },
  "duration": 245,
  "success": true,
  "errorDetails": null,
  "tags": ["appointment", "patient-action", "successful"],
  "category": "appointment-management",
  "severity": "info",
  "source": "web-portal",
  "metadata": {
    "referrer": "https://smartclinic.com/doctors/search",
    "experimentGroup": "default",
    "features": ["online-booking-v2", "instant-confirmation"]
  }
}
```

**Design Decisions:**
- **Rich context**: Captures device, location, session information
- **Event details**: Flexible nested object for action-specific data
- **State tracking**: Before/after snapshots for important changes
- **Performance metrics**: Duration tracking for optimization
- **Categorization**: Tags, category, severity for filtering and alerting
- **Success tracking**: Distinguishes successful actions from errors
- **Geolocation**: Location data for analytics and security
- **Experiment tracking**: A/B test group information in metadata

**Use Cases:**
- User behavior analytics
- Security monitoring (unusual login patterns)
- Performance optimization (slow actions)
- Feature usage tracking
- Debugging user-reported issues

---

## MongoDB Design Principles Applied

### 1. Embed vs. Reference Decision

**When to Embed:**
- Patient/doctor names in prescriptions (reduces joins, data rarely changes)
- Messages within conversations (bounded, related data)
- Vital signs within medical notes (always accessed together)

**When to Reference:**
- `patientId`, `doctorId`, `appointmentId` (links to authoritative MySQL data)
- Avoid duplicating frequently-changing data
- Large or unbounded collections (user's all-time messages)

### 2. Schema Evolution Support

All collections support evolution without migration:
- **Optional fields**: New fields can be added, old documents work without them
- **Versioning**: `metadata.version` tracks schema version
- **Flexible arrays**: Can add new array items with additional fields
- **Nested objects**: Can add new properties to existing objects

Example evolution:
```javascript
// Version 1
{medication: "Aspirin", dosage: "100mg"}

// Version 2 (backward compatible)
{
  medication: "Aspirin", 
  dosage: "100mg",
  ndc: "12345-678-90",  // New field
  manufacturer: "Bayer"  // New field
}
```

### 3. Denormalization Strategy

**Denormalized data** (patient/doctor names):
- **Pros**: Faster queries, reduced joins, better read performance
- **Cons**: Data duplication, potential inconsistency
- **Solution**: Update names in documents when changed in MySQL (eventual consistency acceptable)

**When denormalization is worth it:**
- Data rarely changes (names, historical records)
- Read-heavy operations
- Acceptable slight inconsistency (display names vs. critical medical data)

### 4. Query Optimization

**Indexes recommended:**
```javascript
// prescriptions
db.prescriptions.createIndex({patientId: 1, prescriptionDate: -1})
db.prescriptions.createIndex({doctorId: 1})
db.prescriptions.createIndex({status: 1, expiryDate: 1})
db.prescriptions.createIndex({"medications.name": "text"})

// medical_notes
db.medical_notes.createIndex({patientId: 1, noteDate: -1})
db.medical_notes.createIndex({appointmentId: 1})
db.medical_notes.createIndex({tags: 1})

// patient_messages
db.patient_messages.createIndex({conversationId: 1})
db.patient_messages.createIndex({"participants.userId": 1})
db.patient_messages.createIndex({lastMessageAt: -1})

// patient_feedback
db.patient_feedback.createIndex({doctorId: 1, status: 1})
db.patient_feedback.createIndex({isPublic: 1, status: 1, submittedAt: -1})

// activity_logs
db.activity_logs.createIndex({userId: 1, timestamp: -1})
db.activity_logs.createIndex({action: 1, timestamp: -1})
db.activity_logs.createIndex({timestamp: -1})
```

### 5. Document Size Considerations

**MongoDB document size limit: 16MB**

**Risk areas:**
- `patient_messages`: Long conversations with many messages
- `medical_notes.attachments`: Large file references (store files in S3, only URLs in MongoDB)
- `activity_logs`: Avoid storing large request/response bodies

**Mitigation:**
- Cap message arrays (e.g., latest 100 messages per conversation)
- Store large attachments externally (S3), only metadata in MongoDB
- Implement archival strategies for old logs

### 6. Data Consistency with MySQL

**Strategy:** Eventual consistency acceptable for non-critical data

**Synchronization points:**
1. **Prescriptions**: Created when doctor completes appointment (triggered by `appointments.status = 'completed'`)
2. **Medical notes**: Saved during/after consultation
3. **Feedback**: Created 24 hours after appointment completion
4. **Activity logs**: Real-time logging on each action

**Reference integrity:**
- Application layer validates that `appointmentId`, `patientId`, `doctorId` exist in MySQL before inserting MongoDB documents
- Orphaned documents handled by periodic cleanup jobs

---

## Security Considerations

1. **Field-level encryption**: Sensitive fields (doctor notes, patient messages) encrypted at application layer
2. **Access control**: MongoDB user roles restrict collection access
3. **Audit trail**: All document modifications logged with user and timestamp
4. **Data retention**: Automated archival of old activity logs (>2 years)
5. **Backup strategy**: Daily automated backups, 30-day retention

---

## Summary

The MongoDB collections provide:
- **Flexibility**: Schema-free design adapts to changing requirements
- **Performance**: Denormalized data reduces cross-database joins
- **Rich structure**: Nested objects and arrays model complex relationships naturally
- **Scalability**: Horizontal scaling for high-volume data (logs, messages)

Together with MySQL, this polyglot persistence approach leverages the best of both worlds: **structured relational data** for core operations and **flexible document storage** for evolving, semi-structured content.
