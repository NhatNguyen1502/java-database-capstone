-- =====================================================
-- Smart Clinic Stored Procedures - Test Scripts
-- =====================================================

USE cms;

-- =====================================================
-- TEST 1: Daily Appointments Report
-- =====================================================
SELECT '==================== TEST 1: Daily Appointments Report ====================' AS test_name;

-- Test for today (December 28, 2025)
SELECT 'Today: December 28, 2025' AS test_description;
CALL GetDailyAppointmentReportByDoctor('2025-12-28');

-- Test for a busy day
SELECT 'Busy Day: December 1, 2025' AS test_description;
CALL GetDailyAppointmentReportByDoctor('2025-12-01');

-- Test for December 22, 2025
SELECT 'December 22, 2025' AS test_description;
CALL GetDailyAppointmentReportByDoctor('2025-12-22');

-- Test for a day with no appointments
SELECT 'No Appointments: December 25, 2025 (Christmas)' AS test_description;
CALL GetDailyAppointmentReportByDoctor('2025-12-25');

-- =====================================================
-- TEST 2: Top Doctor by Month
-- =====================================================
SELECT '==================== TEST 2: Top Doctor by Month ====================' AS test_name;

-- Test for December 2025
SELECT 'December 2025' AS test_description;
CALL GetDoctorWithMostPatientsByMonth(2025, 12);

-- Test for November 2025
SELECT 'November 2025' AS test_description;
CALL GetDoctorWithMostPatientsByMonth(2025, 11);
-- =====================================================
-- TEST 3: Top Doctor by Year
-- =====================================================
SELECT '==================== TEST 3: Top Doctor by Year ====================' AS test_name;

-- Test for 2025
SELECT 'Year 2025' AS test_description;
CALL GetDoctorWithMostPatientsByYear(2025);

SELECT '==================== ALL TESTS COMPLETED ====================' AS status;
