-- Smart Hostel Management System Database Schema
-- Database Name: smart_hostel_db

CREATE DATABASE IF NOT EXISTS smart_hostel_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE smart_hostel_db;

-- 1. Roles Table
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS food_feedback;
DROP TABLE IF EXISTS food_menu;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS fees;
DROP TABLE IF EXISTS allocations;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- 3. Students Table
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL UNIQUE,
    register_number VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    department VARCHAR(100) NOT NULL,
    academic_year INT NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    parent_name VARCHAR(100) NOT NULL,
    parent_contact VARCHAR(20) NOT NULL,
    admission_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_students_reg_no ON students(register_number);
CREATE INDEX idx_students_dept ON students(department);

-- 4. Rooms Table (Virtual generated available_beds)
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL,
    block_name VARCHAR(50) NOT NULL,
    floor_number INT NOT NULL,
    room_type ENUM('Single', 'Double', 'Triple', 'Deluxe') NOT NULL DEFAULT 'Double',
    total_capacity INT NOT NULL DEFAULT 2,
    occupied_capacity INT NOT NULL DEFAULT 0,
    available_beds INT GENERATED ALWAYS AS (total_capacity - occupied_capacity) STORED,
    monthly_rent DECIMAL(10, 2) NOT NULL DEFAULT 5000.00,
    status ENUM('AVAILABLE', 'OCCUPIED', 'UNDER_MAINTENANCE') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_block_room UNIQUE (block_name, room_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_rooms_block ON rooms(block_name);
CREATE INDEX idx_rooms_status ON rooms(status);

-- 5. Allocations Table
CREATE TABLE allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    allocation_date DATE NOT NULL,
    vacate_date DATE NULL,
    status ENUM('ACTIVE', 'VACATED', 'CANCELLED') DEFAULT 'ACTIVE',
    allocated_by INT NULL,
    notes VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_allocations_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_allocations_room FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE RESTRICT,
    CONSTRAINT fk_allocations_user FOREIGN KEY (allocated_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_alloc_student ON allocations(student_id);
CREATE INDEX idx_alloc_room ON allocations(room_id);
CREATE INDEX idx_alloc_status ON allocations(status);

-- 6. Fees Table
CREATE TABLE fees (
    fee_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    fee_type VARCHAR(50) NOT NULL DEFAULT 'Hostel & Mess Fee',
    total_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    pending_amount DECIMAL(10, 2) NOT NULL,
    due_date DATE NOT NULL,
    status ENUM('UNPAID', 'PARTIALLY_PAID', 'PAID', 'OVERDUE') DEFAULT 'UNPAID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fees_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_fees_student ON fees(student_id);
CREATE INDEX idx_fees_status ON fees(status);

-- 7. Payments Table
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    fee_id INT NOT NULL,
    student_id INT NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method ENUM('Cash', 'UPI', 'Bank Transfer') NOT NULL,
    transaction_id VARCHAR(100) NULL,
    recorded_by INT NULL,
    receipt_number VARCHAR(50) NOT NULL UNIQUE,
    remarks VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_fee FOREIGN KEY (fee_id) REFERENCES fees(fee_id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_recorder FOREIGN KEY (recorded_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_payments_student ON payments(student_id);
CREATE INDEX idx_payments_receipt ON payments(receipt_number);

-- 8. Complaints Table
CREATE TABLE complaints (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    category ENUM('Water Problem', 'Electricity Problem', 'Room Maintenance', 'Bathroom Cleaning', 'Food Quality', 'Other Issues') NOT NULL,
    description TEXT NOT NULL,
    status ENUM('Pending', 'In Progress', 'Resolved') DEFAULT 'Pending',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'EMERGENCY') DEFAULT 'MEDIUM',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    resolution_notes TEXT NULL,
    resolved_by INT NULL,
    CONSTRAINT fk_complaints_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_complaints_resolver FOREIGN KEY (resolved_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_complaints_student ON complaints(student_id);
CREATE INDEX idx_complaints_status ON complaints(status);

-- 9. Food Menu Table
CREATE TABLE food_menu (
    menu_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_date DATE NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    meal_type ENUM('Breakfast', 'Lunch', 'Dinner') NOT NULL,
    menu_items TEXT NOT NULL,
    special_notes VARCHAR(255) NULL,
    created_by INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_date_meal UNIQUE (menu_date, meal_type),
    CONSTRAINT fk_food_menu_creator FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_food_menu_date ON food_menu(menu_date);

-- 10. Food Feedback Table
CREATE TABLE food_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    menu_date DATE NOT NULL,
    meal_type ENUM('Breakfast', 'Lunch', 'Dinner') NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments TEXT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_date_meal UNIQUE (student_id, menu_date, meal_type),
    CONSTRAINT fk_feedback_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_feedback_student ON food_feedback(student_id);
CREATE INDEX idx_feedback_date ON food_feedback(menu_date);

-- 11. Notifications Table
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_role VARCHAR(50) NULL,
    recipient_user_id INT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (recipient_user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_notifications_user ON notifications(recipient_user_id);

-- 12. Audit Logs Table
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    username VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    table_affected VARCHAR(50) NOT NULL,
    record_id INT NULL,
    details TEXT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_time ON audit_logs(timestamp);
