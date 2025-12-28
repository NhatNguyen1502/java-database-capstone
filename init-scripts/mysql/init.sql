-- Smart Clinic MySQL Initialization Script

-- Set character set and collation
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Use the database
USE cms;

-- This file is executed first during Docker initialization
-- Stored procedures and sample data are loaded from separate files:
-- - 02-stored-procedures.sql
-- - 03-sample-data.sql

SELECT 'MySQL database initialized. Loading stored procedures and sample data...' AS message;
