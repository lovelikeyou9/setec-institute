-- ============================================================
--  Guesthouse Management System  –  Full MySQL Setup Script
-- ============================================================

DROP DATABASE IF EXISTS guesthouse_db;
CREATE DATABASE guesthouse_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE guesthouse_db;

-- ============================================================
--  TABLE: tbl_staff
-- ============================================================
CREATE TABLE tbl_staff (
    staff_id   INT AUTO_INCREMENT PRIMARY KEY,
    staff_name VARCHAR(100),
    phone      VARCHAR(20),
    username   VARCHAR(50) UNIQUE,
    password   VARCHAR(50),
    role       VARCHAR(20)          -- 'Admin' | 'Staff'
);

-- ============================================================
--  TABLE: tbl_room
-- ============================================================
CREATE TABLE tbl_room (
    room_id       INT AUTO_INCREMENT PRIMARY KEY,
    building_name VARCHAR(50),
    room_number   VARCHAR(20) UNIQUE,
    room_type     VARCHAR(30),      -- 'Standard' | 'VIP' | 'Deluxe'
    room_price    DOUBLE,
    status        VARCHAR(20)       -- 'Available' | 'Booked' | 'Occupied'
);

-- ============================================================
--  TABLE: tbl_booking
--  customer_type : 'Walk-in' | 'Booked'
--  status        : 'Booked' | 'Checked-In' | 'Checked-Out'
--                  | 'Cancelled' | 'No-Show'
-- ============================================================
CREATE TABLE tbl_booking (
    booking_id      INT AUTO_INCREMENT PRIMARY KEY,
    room_number     VARCHAR(50),
    guest_name      VARCHAR(100),
    phone           VARCHAR(50),
    identity_card   VARCHAR(50),
    customer_type   VARCHAR(20),
    status          VARCHAR(20),
    checkin_date    DATETIME,
    checkout_date   DATETIME,
    actual_checkout DATETIME,       -- real time guest left
    booked_by       VARCHAR(50),
    checked_in_by   VARCHAR(50),
    checked_out_by  VARCHAR(50),
    deposit         DOUBLE DEFAULT 0,
    penalty         DOUBLE DEFAULT 0,
    total_paid      DOUBLE DEFAULT 0
);

-- ============================================================
--  TABLE: tbl_income
-- ============================================================
CREATE TABLE tbl_income (
    income_id      INT AUTO_INCREMENT PRIMARY KEY,
    record_date    DATE UNIQUE,     -- one row per calendar day
    booking_income DOUBLE DEFAULT 0,
    room_income    DOUBLE DEFAULT 0,
    total_income   DOUBLE DEFAULT 0
);

-- ============================================================
--  SEED: staff
-- ============================================================
INSERT INTO tbl_staff (staff_name, phone, username, password, role)
VALUES
    ('Admin User', '000', 'admin', 'admin', 'Admin'),
    ('Staff User', '111', 'staff', 'staff', 'Staff');

-- ============================================================
--  SEED: rooms
-- ============================================================
INSERT INTO tbl_room (building_name, room_number, room_type, room_price, status)
VALUES
    ('A Building', 'A-101', 'Standard',  25, 'Available'),
    ('A Building', 'A-102', 'Standard',  25, 'Available'),
    ('A Building', 'A-103', 'VIP',       40, 'Available'),
    ('B Building', 'B-201', 'Standard',  25, 'Available'),
    ('B Building', 'B-202', 'VIP',       50, 'Available'),
    ('B Building', 'B-203', 'VIP',       50, 'Available'),
    ('C Building', 'C-301', 'Deluxe',    70, 'Available'),
    ('C Building', 'C-302', 'Deluxe',    70, 'Available');

-- ============================================================
--  VERIFY
-- ============================================================
SELECT * FROM tbl_staff;
SELECT * FROM tbl_room;
SELECT * FROM tbl_booking;
SELECT * FROM tbl_income;
