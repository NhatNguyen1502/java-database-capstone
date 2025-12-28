# User Stories

## Project Overview
This document contains user stories for the Healthcare Appointment Management System. User stories are organized by user role: Admin, Patient, and Doctor.

---

## Admin User Stories

### User Management

#### **US-A001: Manage User Accounts**
**Title:**
_As an admin, I want to create, view, update, and delete user accounts for patients and doctors, so that I can control system access and maintain accurate user records._

**Acceptance Criteria:**
1. Admin can INSERT into patients, doctors, or admin tables
2. Required fields: full_name, email, password_hash, phone
3. Can UPDATE user information
4. Can set is_active = FALSE for soft deletion
5. All actions logged in audit_logs with user_type='admin'

**Priority:** High

**Story Points:** 5

---

#### **US-A002: Assign User Roles**
**Title:**
_As an admin, I want to assign and modify user roles (patient, doctor, admin), so that users have appropriate access permissions based on their responsibilities._

**Acceptance Criteria:**
1. Admin can set role field in admin table ('super_admin' or 'admin')
2. Cannot delete last active super_admin (business rule in code)
3. Role changes logged in audit_logs

**Priority:** Medium

**Story Points:** 3

---

#### **US-A003: View User Activity**
**Title:**
_As an admin, I want to view user login history and activity logs, so that I can monitor system usage and identify potential security issues._

**Acceptance Criteria:**
1. Query audit_logs table for all user activities
2. Filter by: user_type, user_id, action, created_at
3. Display: action, entity_type, entity_id, details, ip_address, user_agent, created_at
4. Indexed queries for fast filtering

**Priority:** Medium

**Story Points:** 3

---

### System Management

#### **US-A004: View System Dashboard**
**Title:**
_As an admin, I want to access a comprehensive dashboard with system statistics, so that I can monitor overall system performance and usage metrics._

**Acceptance Criteria:**
1. COUNT queries on patients, doctors, appointments tables
2. Display: total active users (is_active = TRUE), appointments by status
3. Filter appointments by date range
4. Simple aggregation queries

**Priority:** High

**Story Points:** 3

---

#### **US-A005: Generate Reports**
**Title:**
_As an admin, I want to generate basic reports on appointments and users, so that I can track system usage._

**Acceptance Criteria:**
1. Query appointments with filters: date range, doctor_id, status
2. Aggregate queries: COUNT by status, GROUP BY doctor_id
3. Query users: COUNT active patients/doctors
4. Simple SELECT statements with WHERE clauses

**Priority:** Low

**Story Points:** 5

---

### Data Management

#### **US-A007: Backup and Restore Data**
**Title:**
_As an admin, I want to perform data backups and restorations, so that I can protect against data loss and ensure business continuity._

**Acceptance Criteria:**
1. Admin can initiate manual database backup with one click
2. System performs automatic scheduled backups (daily/weekly)
3. Backup files are stored securely with encryption
4. Admin can view list of available backups with timestamps
5. Admin can restore system from a selected backup point
6. System validates backup integrity before restoration

**Priority:** Low

**Story Points:** 13

**Notes:**
- Implement off-site backup storage for disaster recovery
- Test restoration process regularly to ensure reliability

---

#### **US-A008: Manage Doctor Profiles**
**Title:**
_As an admin, I want to create and update doctor profiles, so that patients can find and book appointments._

**Acceptance Criteria:**
1. Admin can INSERT/UPDATE doctors table
2. Fields: full_name, email, password_hash, phone, specialization, bio, profile_photo_url, consultation_fee, years_of_experience
3. Can set is_active flag
4. Changes logged in audit_logs

**Priority:** Medium

**Story Points:** 3

---

## Patient User Stories

### Account Management

#### **US-P001: Register Account**
**Title:**
_As a patient, I want to create a new account with my personal information, so that I can access the appointment booking system._

**Acceptance Criteria:**
1. Patient can access registration form from the home page
2. Required fields include: full_name, email, password, phone, date_of_birth, gender, address
3. Email validation ensures valid format and prevents duplicate registrations
4. Password must meet security requirements (minimum 8 characters)
5. Patient is redirected to dashboard after successful registration

**Priority:** High

**Story Points:** 5

---

#### **US-P002: Login to System**
**Title:**
_As a patient, I want to securely login to my account, so that I can access my appointments and medical records._

**Acceptance Criteria:**
1. Login form accepts email and password
2. System validates credentials against database
3. Patient is redirected to their dashboard upon successful login
4. System logs login attempts in audit_logs

**Priority:** High

**Story Points:** 3

---

#### **US-P003: Update Profile**
**Title:**
_As a patient, I want to update my personal information and contact details, so that my records remain accurate and up-to-date._

**Acceptance Criteria:**
1. Patient can edit profile fields (full_name, phone, address, allergies)
2. Password changes require current password for security
3. Changes are saved with confirmation message
4. Updated information is reflected immediately (updated_at timestamp)

**Priority:** Medium

**Story Points:** 3

---

### Appointment Management

#### **US-P004: Search for Doctors**
**Title:**
_As a patient, I want to search for doctors by specialization, name, or availability, so that I can find the most suitable healthcare provider for my needs._

**Acceptance Criteria:**
1. Search interface allows filtering by specialization and name
2. Search results display doctor name, specialization, years_of_experience
3. Search results show doctor availability based on doctor_schedules
4. Patient can view detailed doctor profiles (bio, consultation_fee)
5. Only active doctors (is_active = TRUE) appear in results

**Priority:** Medium

**Story Points:** 5

---

#### **US-P005: Book Appointment**
**Title:**
_As a patient, I want to book an appointment with a doctor at an available time slot, so that I can receive medical consultation at a convenient time._

**Acceptance Criteria:**
1. Patient selects doctor and views available time slots based on doctor_schedules
2. System checks schedule_exceptions to exclude blocked times
3. Patient can add appointment_reason and patient_notes during booking
4. System prevents double-booking via UNIQUE constraint (doctor_id, appointment_date, appointment_time)
5. Appointment status defaults to 'scheduled'
6. created_at timestamp is automatically recorded

**Priority:** High

**Story Points:** 8

---

#### **US-P006: View My Appointments**
**Title:**
_As a patient, I want to view a list of all my upcoming and past appointments, so that I can keep track of my medical consultations._

**Acceptance Criteria:**
1. Appointments are displayed in chronological order (ORDER BY appointment_date, appointment_time)
2. Each appointment shows: appointment_date, appointment_time, doctor name, status
3. Patient can filter by status (scheduled, confirmed, completed, cancelled, no_show)
4. Shows appointment_reason, patient_notes, and consultation_notes (if completed)
5. Query filters by patient_id

**Priority:** High

**Story Points:** 3

---

#### **US-P007: Reschedule Appointment**
**Title:**
_As a patient, I want to change the date or time of my scheduled appointment, so that I can accommodate changes in my schedule._

**Acceptance Criteria:**
1. System validates 24-hour rule before allowing reschedule
2. Patient selects new available time slot (checks doctor_schedules and schedule_exceptions)
3. System updates appointment_date and appointment_time
4. updated_at timestamp is automatically updated
5. Change is logged in audit_logs

**Priority:** Medium

**Story Points:** 3

---

#### **US-P008: Cancel Appointment**
**Title:**
_As a patient, I want to cancel an appointment I can no longer attend, so that the time slot becomes available for other patients._

**Acceptance Criteria:**
1. System validates 24-hour rule before allowing cancellation
2. Patient can provide cancellation_reason (optional TEXT field)
3. Status changes to 'cancelled'
4. cancelled_at timestamp is recorded
5. Change is logged in audit_logs

**Priority:** Medium

**Story Points:** 2

---

### Medical Records

#### **US-P009: View Prescriptions**
**Title:**
_As a patient, I want to access my medical records and appointment history, so that I can review my health information and treatment history._

**Acceptance Criteria:**
1. Patient can access medical records section from dashboard
2. Records display appointment history with dates and doctors
3. Each appointment record shows consultation notes (if added by doctor)
4. Patient can view test results and diagnoses
5. Records are organized chronologically with most recent first
6. Download option allows exporting records as PDF

**Priority:** Low

**Story Points:** 8

**Notes:**
- Ensure compliance with health data privacy regulations (HIPAA)
- Implement secure access controls and audit logging

---

#### **US-P011: View Prescriptions**
**Title:**
_As a patient, I want to view prescriptions provided by my doctors, so that I can understand my medication requirements and follow treatment plans._

**Acceptance Criteria:**
1. Prescriptions are listed with medication name, dosage, frequency
2. Each prescription shows prescribing doctor and date
3. Active and expired prescriptions are clearly distinguished
4. Patient can view detailed medication instructions
5. Prescriptions can be printed or downloaded as PDF
6. System displays warnings for expired prescriptions

**Priority:** Medium

**Story Points:** 5

**Notes:**
- Add medication reminder functionality
- Consider integrating with pharmacy systems for refills

---

## Doctor User Stories

### Account and Profile Management

#### **US-D001: Login to System**
**Title:**
_As a doctor, I want to securely login to my professional account, so that I can access my schedule and patient information._

**Acceptance Criteria:**
1. Doctor can login using email and password_hash credentials
2. System validates against doctors table
3. Doctor is redirected to dashboard upon successful login
4. Login action logged in audit_logs with user_type='doctor'

**Priority:** High

**Story Points:** 3

---

#### **US-D002: Update Professional Profile**
**Title:**
_As a doctor, I want to update my specialization, qualifications, and contact information, so that patients can make informed decisions when booking appointments._

**Acceptance Criteria:**
1. Doctor can edit: full_name, phone, specialization, bio, consultation_fee, years_of_experience
2. Can update profile_photo_url
3. Changes update updated_at timestamp automatically
4. Profile changes logged in audit_logs

**Priority:** Medium

**Story Points:** 3

---

### Schedule Management

#### **US-D003: Set Availability**
**Title:**
_As a doctor, I want to define my working hours and available time slots, so that patients can only book appointments during my available times._

**Acceptance Criteria:**
1. Doctor creates records in doctor_schedules table
2. Each record specifies: day_of_week (ENUM), start_time, end_time
3. Multiple rows allow complex schedules (morning/afternoon shifts)
4. is_available flag allows temporary disabling
5. CHECK constraint enforces end_time > start_time
6. Changes update updated_at timestamp

**Priority:** High

**Story Points:** 5

---

#### **US-D004: Block Time Slots**
**Title:**
_As a doctor, I want to block specific time slots for personal time or meetings, so that patients cannot book appointments during unavailable periods._

**Acceptance Criteria:**
1. Doctor creates record in schedule_exceptions table
2. Specifies: exception_date, start_time, end_time, reason
3. is_available = FALSE blocks time (default)
4. CHECK constraint enforces end_time > start_time
5. Indexed by (doctor_id, exception_date) for fast lookups

**Priority:** Medium

**Story Points:** 3

---

#### **US-D005: View My Schedule**
**Title:**
_As a doctor, I want to view my daily, weekly, and monthly appointment schedule, so that I can plan my workday and prepare for patient consultations._

**Acceptance Criteria:**
1. Query appointments by doctor_id
2. Display: appointment_date, appointment_time, patient name, status, appointment_reason
3. Filter by status (scheduled, confirmed, completed, cancelled, no_show)
4. Show consultation_notes if status = 'completed'
5. Order by appointment_date, appointment_time

**Priority:** High

**Story Points:** 3

---

### Appointment Management

#### **US-D006: View Appointment Details**
**Title:**
_As a doctor, I want to view detailed information about each appointment including patient details, so that I can prepare for consultations and provide personalized care._

**Acceptance Criteria:**
1. Doctor views appointment with JOIN to patients table
2. Displays: patient full_name, phone, date_of_birth, gender, allergies
3. Shows: appointment_reason, patient_notes, duration_minutes
4. Can access patient's appointment history (previous records by patient_id)
5. Displays created_at, updated_at timestamps

**Priority:** Medium

**Story Points:** 3

---

#### **US-D007: Confirm or Reschedule Appointments**
**Title:**
_As a doctor, I want to confirm, reschedule, or cancel appointments when necessary, so that I can manage unexpected schedule changes or emergencies._

**Acceptance Criteria:**
1. Doctor updates status from 'scheduled' to 'confirmed'
2. Can update appointment_date/appointment_time if rescheduling
3. Can set status to 'cancelled' and add cancellation_reason
4. All changes update updated_at timestamp
5. Changes logged in audit_logs

**Priority:** Medium

**Story Points:** 3

---

#### **US-D008: Mark Appointment as Complete**
**Title:**
_As a doctor, I want to mark appointments as completed after consultations, so that appointment records remain accurate and up-to-date._

**Acceptance Criteria:**
1. Doctor updates status to 'completed' or 'no_show'
2. Can add consultation_notes (TEXT field)
3. completed_at timestamp is recorded
4. Change logged in audit_logs

**Priority:** Medium

**Story Points:** 2

---

### Patient Care

#### **US-D009: Access Patient Records**
**Title:**
_As a doctor, I want to view patient medical history and previous appointment records, so that I can provide informed medical care and track patient progress._

**Acceptance Criteria:**
1. Doctor searches patients by full_name or email
2. View patient record: full_name, date_of_birth, gender, phone, address, allergies
3. Query appointment history by patient_id
4. View prescriptions from MongoDB by patientId
5. Access logged in audit_logs for compliance

**Priority:** Medium

**Story Points:** 3

---

#### **US-D010: Add Prescriptions**
**Title:**
_As a doctor, I want to create and add prescriptions for patients after consultations, so that patients receive proper medication instructions and treatment plans._

**Acceptance Criteria:**
1. Doctor creates prescription document in MongoDB
2. Document includes: appointmentId, patientId, doctorId, prescriptionDate, expiryDate
3. medications array contains: name, genericName, dosage, form, frequency, duration, quantity, instructions
4. Can add diagnosis and doctorNotes fields
5. status defaults to 'active'
6. Denormalized patientName and doctorName for quick access

**Priority:** High

**Story Points:** 5

---

### Dashboard and Analytics

#### **US-D011: View Dashboard Statistics**
**Title:**
_As a doctor, I want to view basic statistics about my appointments, so that I can track my schedule._

**Acceptance Criteria:**
1. COUNT appointments by status for current doctor
2. Display totals: scheduled, confirmed, completed, cancelled, no_show
3. Filter by date range using appointment_date
4. Simple aggregation queries on appointments table

**Priority:** Low

**Story Points:** 3

---

## User Story Summary

### By Priority

#### **High Priority (MVP - Minimum Viable Product)**
- US-P001: Register Account (5 pts)
- US-P002: Login to System (3 pts)
- US-P005: Book Appointment (8 pts)
- US-P006: View My Appointments (3 pts)
- US-D001: Login to System (3 pts)
- US-D003: Set Availability (5 pts)
- US-D005: View My Schedule (3 pts)
- US-D010: Add Prescriptions (5 pts)
- US-A001: Manage User Accounts (5 pts)
- US-A004: View System Dashboard (3 pts)

**Total High Priority: 43 Story Points**

#### **Medium Priority**
- US-P003: Update Profile (3 pts)
- US-P004: Search for Doctors (5 pts)
- US-P007: Reschedule Appointment (3 pts)
- US-P008: Cancel Appointment (2 pts)
- US-P009: View Prescriptions (3 pts)
- US-D002: Update Professional Profile (3 pts)
- US-D004: Block Time Slots (3 pts)
- US-D006: View Appointment Details (3 pts)
- US-D007: Confirm or Reschedule Appointments (3 pts)
- US-D008: Mark Appointment as Complete (2 pts)
- US-D009: Access Patient Records (3 pts)
- US-A002: Assign User Roles (3 pts)
- US-A003: View User Activity (3 pts)
- US-A006: Manage Doctor Profiles (3 pts)

**Total Medium Priority: 42 Story Points**

#### **Low Priority (Future Enhancements)**
- US-D011: View Dashboard Statistics (3 pts)
- US-A005: Generate Reports (5 pts)

**Total Low Priority: 8 Story Points**

### **Grand Total: 93 Story Points**

---

## Implementation Guidelines

### Story Point Reference
- **1-2 Points:** Trivial tasks (< 4 hours)
- **3-5 Points:** Small features (1-2 days)
- **8 Points:** Medium features (3-5 days)
- **13 Points:** Large features (1-2 weeks)
- **21+ Points:** Epic - should be broken down into smaller stories

### Development Best Practices
1. Complete high-priority stories first for MVP
2. Each story should be independently testable
3. Include security and performance testing in each story
4. Document API changes and database schema updates
5. Conduct code reviews before marking stories complete
6. Update user documentation as features are implemented
