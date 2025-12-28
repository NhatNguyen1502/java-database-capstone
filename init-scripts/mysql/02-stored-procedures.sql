-- =====================================================
-- Smart Clinic Stored Procedures
-- =====================================================

USE cms;

DELIMITER $$

-- =====================================================
-- 1. Daily Appointments Report Grouped by Doctor
-- =====================================================
DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor$$

CREATE PROCEDURE GetDailyAppointmentReportByDoctor(
    IN report_date DATE
)
BEGIN
    SELECT 
        d.id AS doctor_id,
        d.username AS doctor_name,
        d.specialization,
        COUNT(a.id) AS total_appointments,
        SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END) AS completed_appointments,
        SUM(CASE WHEN a.status = 'cancelled' THEN 1 ELSE 0 END) AS cancelled_appointments,
        SUM(CASE WHEN a.status = 'no_show' THEN 1 ELSE 0 END) AS no_show_appointments,
        SUM(CASE WHEN a.status = 'scheduled' THEN 1 ELSE 0 END) AS scheduled_appointments,
        SUM(CASE WHEN a.status = 'confirmed' THEN 1 ELSE 0 END) AS confirmed_appointments,
        ROUND(d.consultation_fee * SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END), 2) AS revenue_generated,
        MIN(a.appointment_time) AS first_appointment,
        MAX(a.appointment_time) AS last_appointment
    FROM doctors d
    LEFT JOIN appointments a ON d.id = a.doctor_id 
        AND a.appointment_date = report_date
    WHERE d.is_active = TRUE
    GROUP BY d.id, d.username, d.specialization, d.consultation_fee
    HAVING total_appointments > 0
    ORDER BY total_appointments DESC, doctor_name;
END$$

-- =====================================================
-- 2. Doctor with Most Patients in Specific Month
-- =====================================================
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth$$

CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(
    IN target_year INT,
    IN target_month INT
)
BEGIN
    SELECT 
        d.id AS doctor_id,
        d.username AS doctor_name,
        d.email,
        d.specialization,
        d.years_of_experience,
        COUNT(DISTINCT a.patient_id) AS unique_patients,
        COUNT(a.id) AS total_appointments,
        SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END) AS completed_appointments,
        ROUND(AVG(a.duration_minutes), 2) AS avg_appointment_duration,
        ROUND(d.consultation_fee * SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END), 2) AS total_revenue,
        CONCAT(target_year, '-', LPAD(target_month, 2, '0')) AS report_period
    FROM doctors d
    INNER JOIN appointments a ON d.id = a.doctor_id
    WHERE YEAR(a.appointment_date) = target_year
        AND MONTH(a.appointment_date) = target_month
        AND d.is_active = TRUE
    GROUP BY d.id, d.username, d.email, d.specialization, d.years_of_experience, d.consultation_fee
    ORDER BY unique_patients DESC, total_appointments DESC
    LIMIT 1;
END$$

-- =====================================================
-- 3. Doctor with Most Patients in Given Year
-- =====================================================
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear$$

CREATE PROCEDURE GetDoctorWithMostPatientsByYear(
    IN target_year INT
)
BEGIN
    SELECT 
        d.id AS doctor_id,
        d.username AS doctor_name,
        d.email,
        d.specialization,
        d.years_of_experience,
        COUNT(DISTINCT a.patient_id) AS unique_patients,
        COUNT(a.id) AS total_appointments,
        SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END) AS completed_appointments,
        SUM(CASE WHEN a.status = 'cancelled' THEN 1 ELSE 0 END) AS cancelled_appointments,
        SUM(CASE WHEN a.status = 'no_show' THEN 1 ELSE 0 END) AS no_show_appointments,
        ROUND(AVG(a.duration_minutes), 2) AS avg_appointment_duration,
        ROUND(d.consultation_fee * SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END), 2) AS total_revenue,
        ROUND(100.0 * SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END) / COUNT(a.id), 2) AS completion_rate,
        target_year AS report_year
    FROM doctors d
    INNER JOIN appointments a ON d.id = a.doctor_id
    WHERE YEAR(a.appointment_date) = target_year
        AND d.is_active = TRUE
    GROUP BY d.id, d.username, d.email, d.specialization, d.years_of_experience, d.consultation_fee
    ORDER BY unique_patients DESC, total_appointments DESC
    LIMIT 1;
END$$
DELIMITER ;

-- Grant execute permissions
GRANT EXECUTE ON PROCEDURE cms.GetDailyAppointmentReportByDoctor TO 'root'@'%';
GRANT EXECUTE ON PROCEDURE cms.GetDoctorWithMostPatientsByMonth TO 'root'@'%';
GRANT EXECUTE ON PROCEDURE cms.GetDoctorWithMostPatientsByYear TO 'root'@'%';

SELECT 'Stored procedures created successfully!' AS status;
