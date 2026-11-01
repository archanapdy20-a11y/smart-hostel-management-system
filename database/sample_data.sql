-- Smart Hostel Management System Sample Seed Data
-- Demo: admin/admin123, warden/warden123, accountant/account123, student/student123

USE smart_hostel_db;

-- 1. Insert Roles
INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'ADMIN', 'System Administrator with full access to all system modules'),
(2, 'WARDEN', 'Hostel Warden managing room allocations, student welfare, and complaints'),
(3, 'ACCOUNTANT', 'Hostel Accountant managing fees, payments, and financial reports'),
(4, 'STUDENT', 'Hostel Resident with access to personal room, fees, menu, and complaints')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 2. Insert Users with pre-hashed BCrypt credentials
INSERT INTO users (user_id, username, password_hash, role_id, full_name, email, phone, status) VALUES
(1, 'admin', '$2a$10$V5p5BI9TsCFd3N.XE9B8XeAlaPC3dBm.Odk.lLUA2xL.vK7mMKzcG', 1, 'Chief Administrator', 'admin@smarthostel.edu', '9876543210', 'ACTIVE'),
(2, 'warden', '$2a$10$3CWOaIJKFytvrkOfdlrTM.XwWm4KZXACW9q7Hf5X8Ls2yhxUi5VI6', 2, 'Dr. Robert Vance', 'warden@smarthostel.edu', '9876543211', 'ACTIVE'),
(3, 'accountant', '$2a$10$R7JSA5WeT1uPQq.MGLZaz.SqAMBiz5C6674jtxeQN5JRIP6mYLafa', 3, 'Sarah Jenkins', 'accounts@smarthostel.edu', '9876543212', 'ACTIVE'),
(4, 'student', '$2a$10$6VGeHUE1tOgvj.FdU2GsLO/T8PDskFuJtArRgLViJXR.wQgOiEys.', 4, 'Alex Morgan', 'alex.morgan@smarthostel.edu', '9876543213', 'ACTIVE'),
(5, 'rahul.k', '$2a$10$6VGeHUE1tOgvj.FdU2GsLO/T8PDskFuJtArRgLViJXR.wQgOiEys.', 4, 'Rahul Kumar', 'rahul.k@smarthostel.edu', '9876543214', 'ACTIVE'),
(6, 'priya.s', '$2a$10$6VGeHUE1tOgvj.FdU2GsLO/T8PDskFuJtArRgLViJXR.wQgOiEys.', 4, 'Priya Sharma', 'priya.s@smarthostel.edu', '9876543215', 'ACTIVE')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), password_hash=VALUES(password_hash);

-- 3. Insert Students
INSERT INTO students (student_id, user_id, register_number, full_name, gender, department, academic_year, date_of_birth, phone_number, email, address, parent_name, parent_contact, admission_date) VALUES
(1, 4, 'REG-2023-CS-001', 'Alex Morgan', 'Male', 'Computer Science', 3, '2003-05-14', '9876543213', 'alex.morgan@smarthostel.edu', '45 Innovation Lane, Silicon City', 'David Morgan', '9123456780', '2023-08-01'),
(2, 5, 'REG-2023-EC-042', 'Rahul Kumar', 'Male', 'Electronics & Comm.', 3, '2003-08-22', '9876543214', 'rahul.k@smarthostel.edu', '12 Greenfield Avenue, Bangalore', 'Suresh Kumar', '9123456781', '2023-08-01'),
(3, 6, 'REG-2024-IT-015', 'Priya Sharma', 'Female', 'Information Tech', 2, '2004-11-10', '9876543215', 'priya.s@smarthostel.edu', '78 Heritage Park, Delhi', 'Ramesh Sharma', '9123456782', '2024-07-25')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 4. Insert Rooms
INSERT INTO rooms (room_id, room_number, block_name, floor_number, room_type, total_capacity, occupied_capacity, monthly_rent, status) VALUES
(1, 'A-101', 'Block A (Aryabhata)', 1, 'Single', 1, 1, 6500.00, 'OCCUPIED'),
(2, 'A-102', 'Block A (Aryabhata)', 1, 'Double', 2, 1, 4500.00, 'AVAILABLE'),
(3, 'A-103', 'Block A (Aryabhata)', 1, 'Triple', 3, 0, 3500.00, 'AVAILABLE'),
(4, 'B-201', 'Block B (Bhaskara)', 2, 'Double', 2, 1, 4800.00, 'AVAILABLE'),
(5, 'B-202', 'Block B (Bhaskara)', 2, 'Deluxe', 2, 0, 7500.00, 'AVAILABLE'),
(6, 'C-301', 'Block C (Curie Girls)', 3, 'Double', 2, 1, 5000.00, 'AVAILABLE')
ON DUPLICATE KEY UPDATE room_type=VALUES(room_type);

-- 5. Insert Active Allocations
INSERT INTO allocations (allocation_id, student_id, room_id, allocation_date, vacate_date, status, allocated_by, notes) VALUES
(1, 1, 1, '2023-08-05', NULL, 'ACTIVE', 1, 'Single occupancy allocated upon request'),
(2, 2, 4, '2023-08-06', NULL, 'ACTIVE', 2, 'Regular allotment in Bhaskara Block'),
(3, 3, 6, '2024-08-01', NULL, 'ACTIVE', 2, 'Girls wing allotment')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 6. Insert Fees
INSERT INTO fees (fee_id, student_id, academic_year, semester, fee_type, total_amount, paid_amount, pending_amount, due_date, status) VALUES
(1, 1, '2024-2025', 'Semester 5', 'Hostel & Mess Fee', 35000.00, 35000.00, 0.00, '2024-09-15', 'PAID'),
(2, 2, '2024-2025', 'Semester 5', 'Hostel & Mess Fee', 32000.00, 20000.00, 12000.00, '2024-09-15', 'PARTIALLY_PAID'),
(3, 3, '2024-2025', 'Semester 3', 'Hostel & Mess Fee', 34000.00, 0.00, 34000.00, '2024-09-30', 'UNPAID')
ON DUPLICATE KEY UPDATE total_amount=VALUES(total_amount);

-- 7. Insert Payments
INSERT INTO payments (payment_id, fee_id, student_id, amount_paid, payment_date, payment_method, transaction_id, recorded_by, receipt_number, remarks) VALUES
(1, 1, 1, 35000.00, '2024-09-02', 'Bank Transfer', 'NEFT-884920482', 3, 'REC-2024-001', 'Full payment verified via bank reconciliation'),
(2, 2, 2, 20000.00, '2024-09-10', 'UPI', 'UPI/4092840182/HDFC', 3, 'REC-2024-002', 'First installment received')
ON DUPLICATE KEY UPDATE receipt_number=VALUES(receipt_number);

-- 8. Insert Complaints
INSERT INTO complaints (complaint_id, student_id, category, description, status, priority, submitted_at, resolved_at, resolution_notes, resolved_by) VALUES
(1, 1, 'Electricity Problem', 'Ceiling fan regulator not working in Room A-101 and sparking sound noticed.', 'Resolved', 'HIGH', '2024-10-12 09:30:00', '2024-10-13 14:00:00', 'Electrician replaced the capacitor and switch unit.', 2),
(2, 2, 'Water Problem', 'Low water pressure in the 2nd floor common bathroom during morning hours.', 'In Progress', 'MEDIUM', '2024-10-25 08:15:00', NULL, 'Plumber dispatched to check the overhead rooftop booster pump.', 2),
(3, 3, 'Food Quality', 'Dinner rice quality on Thursday night was undercooked.', 'Pending', 'LOW', '2024-10-30 20:45:00', NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 9. Insert Food Menu
INSERT INTO food_menu (menu_id, menu_date, day_of_week, meal_type, menu_items, special_notes, created_by) VALUES
(1, CURRENT_DATE, DAYNAME(CURRENT_DATE), 'Breakfast', 'Idli, Medu Vada, Coconut Chutney, Hot Sambar, Tea / Coffee', 'Healthy breakfast with fresh coconut chutney', 1),
(2, CURRENT_DATE, DAYNAME(CURRENT_DATE), 'Lunch', 'Steamed Rice, Dal Tadka, Paneer Butter Masala, Mixed Veg Poriyal, Curd, Papad', 'Special Paneer dish for lunch', 1),
(3, CURRENT_DATE, DAYNAME(CURRENT_DATE), 'Dinner', 'Phulka Chapati, Veg Kurma, Jeera Rice, Dal Fry, Fresh Fruit Custard', 'Warm chapati with seasonal dessert', 1)
ON DUPLICATE KEY UPDATE menu_items=VALUES(menu_items);

-- 10. Insert Food Feedback
INSERT INTO food_feedback (feedback_id, student_id, menu_date, meal_type, rating, comments) VALUES
(1, 1, CURRENT_DATE, 'Breakfast', 5, 'Crispy vadas and excellent sambar today! Highly appreciated.'),
(2, 2, CURRENT_DATE, 'Lunch', 4, 'Paneer butter masala was tasty. Good portion size.')
ON DUPLICATE KEY UPDATE rating=VALUES(rating);

-- 11. Insert System Notifications
INSERT INTO notifications (notification_id, recipient_role, recipient_user_id, title, message) VALUES
(1, 'ALL', NULL, 'Hostel Maintenance Schedule', 'Power backup generator test will be conducted this Saturday from 2 PM to 4 PM.'),
(2, 'STUDENT', 5, 'Fee Reminder', 'Reminder: Second installment of INR 12,000 for Semester 5 is due soon.'),
(3, 'WARDEN', 2, 'New Complaint Registered', 'Priya Sharma registered a complaint regarding Food Quality.')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 12. Insert Audit Logs
INSERT INTO audit_logs (log_id, user_id, username, action_type, table_affected, record_id, details) VALUES
(1, 1, 'admin', 'SYSTEM_INIT', 'ALL', 0, 'Database tables and initial system roles initialized successfully.'),
(2, 1, 'admin', 'ALLOCATE_ROOM', 'allocations', 1, 'Student Alex Morgan allocated to Room A-101.'),
(3, 3, 'accountant', 'FEE_PAYMENT', 'payments', 1, 'Payment of INR 35000 recorded under Receipt REC-2024-001.')
ON DUPLICATE KEY UPDATE action_type=VALUES(action_type);
