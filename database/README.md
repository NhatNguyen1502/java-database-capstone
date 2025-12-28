# Database Stored Procedures Documentation

## Overview
This document describes the stored procedures created for the Smart Clinic database to generate reports and analyze appointment data.

---

## Stored Procedures

### 1. `GetDailyAppointmentReportByDoctor`
**Purpose**: Generate a comprehensive daily report of appointments grouped by doctor.

**Parameters**:
- `report_date` (DATE): The date for which to generate the report

**Returns**:
- `doctor_id`: Doctor's unique identifier
- `doctor_name`: Doctor's username
- `specialization`: Doctor's medical specialization
- `total_appointments`: Total number of appointments
- `completed_appointments`: Number of completed appointments
- `cancelled_appointments`: Number of cancelled appointments
- `no_show_appointments`: Number of no-show appointments
- `scheduled_appointments`: Number of scheduled appointments
- `confirmed_appointments`: Number of confirmed appointments
- `revenue_generated`: Total revenue from completed appointments
- `first_appointment`: Time of first appointment
- `last_appointment`: Time of last appointment

**Usage Example**:
```sql
CALL GetDailyAppointmentReportByDoctor('2025-12-28');
```

---

### 2. `GetDoctorWithMostPatientsByMonth`
**Purpose**: Identify the doctor who saw the most unique patients in a specific month.

**Parameters**:
- `target_year` (INT): The year (e.g., 2025)
- `target_month` (INT): The month (1-12)

**Returns**:
- `doctor_id`: Doctor's unique identifier
- `doctor_name`: Doctor's username
- `email`: Doctor's email address
- `specialization`: Doctor's medical specialization
- `years_of_experience`: Years of professional experience
- `unique_patients`: Number of unique patients seen
- `total_appointments`: Total number of appointments
- `completed_appointments`: Number of completed appointments
- `avg_appointment_duration`: Average appointment duration in minutes
- `total_revenue`: Total revenue generated
- `report_period`: Month and year of the report (YYYY-MM format)

**Usage Example**:
```sql
CALL GetDoctorWithMostPatientsByMonth(2025, 12);
```

---

### 3. `GetDoctorWithMostPatientsByYear`
**Purpose**: Identify the doctor who saw the most unique patients in a given year.

**Parameters**:
- `target_year` (INT): The year (e.g., 2025)

**Returns**:
- `doctor_id`: Doctor's unique identifier
- `doctor_name`: Doctor's username
- `email`: Doctor's email address
- `specialization`: Doctor's medical specialization
- `years_of_experience`: Years of professional experience
- `unique_patients`: Number of unique patients seen
- `total_appointments`: Total number of appointments
- `completed_appointments`: Number of completed appointments
- `cancelled_appointments`: Number of cancelled appointments
- `no_show_appointments`: Number of no-show appointments
- `avg_appointment_duration`: Average appointment duration
- `total_revenue`: Total revenue generated
- `completion_rate`: Percentage of completed appointments
- `report_year`: Year of the report

**Usage Example**:
```sql
CALL GetDoctorWithMostPatientsByYear(2025);
```

---

## Testing the Procedures

Run the test script to verify all procedures:

```bash
# From Docker
docker-compose exec mysql mysql -uroot -prootpassword cms < database/test-stored-procedures.sql

# Or from MySQL client
mysql -uroot -prootpassword cms < database/test-stored-procedures.sql
```

The test script includes:
1. Daily reports for multiple dates
2. Monthly top doctor identification
3. Yearly top doctor identification
4. Top N doctors ranking
5. Monthly summary reports
6. Verification queries

---

## Integration with Application

These procedures can be called from Spring Boot using:

```java
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Procedure(name = "GetDailyAppointmentReportByDoctor")
    List<Object[]> getDailyReport(@Param("report_date") LocalDate reportDate);
    
    @Procedure(name = "GetDoctorWithMostPatientsByMonth")
    Object[] getTopDoctorByMonth(
        @Param("target_year") Integer year,
        @Param("target_month") Integer month
    );
    
    @Procedure(name = "GetDoctorWithMostPatientsByYear")
    Object[] getTopDoctorByYear(@Param("target_year") Integer year);
}
```
