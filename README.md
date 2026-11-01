# 🏫 SMART HOSTEL MANAGEMENT AND ROOM ALLOCATION SYSTEM USING JAVA

An enterprise-grade, desktop-based Hostel Administration, Room Allocation, and Financial Management platform built with **Java Swing**, **Direct MySQL JDBC Connectivity**, and **BCrypt Password Security**.

> **Academic Project Submission Specification**  
> **Course / Degree**: M.Sc. Computer Science / MCA / B.Tech CSE Final-Year Academic Project  
> **Architecture**: Tiered Enterprise Layered Architecture (Presentation, Service, DAO, Model, Database)  
> **Frontend**: Java Swing + Modern FlatLaf Enterprise Theme  
> **Backend & Database**: Core Java 17/21/24 + MySQL 8.0 via Direct JDBC (No Spring, No ORM overhead)  
> **Authentication**: Secure Role-Based Access Control (RBAC) + BCrypt Password Hashing  

---

## 📋 1. Project Overview & Objective

Hostel administration in universities and colleges often suffers from manual paperwork, duplicate room bookings, untracked fee payments, and uncoordinated complaint resolution. 

This desktop application delivers a complete, secure, robust digital solution designed to:
- **Prevent Duplicate Room Allotment**: Database transactions with pessimistic row locks (`SELECT ... FOR UPDATE`) prevent simultaneous over-allocation of room capacities.
- **Enforce Role-Based Access Control (RBAC)**: Strict segregation between System Administrator, Hostel Warden, Accountant, and Students.
- **Automate Financial Records**: Fee invoice generation, real-time pending dues tracking, verified payment recording, and official receipt generation.
- **Streamline Maintenance**: Categorized complaint tracking (*Water Problem, Electricity Problem, Room Maintenance, Bathroom Cleaning, Food Quality, Other Issues*) with auto-detected urgency priority.
- **Mess & Dining Management**: Daily and weekly food menus with resident rating feedback to maintain meal quality standards.
- **User & Staff Account Administration**: Admin-exclusive creation and management of warden, accountant, and administrative accounts.

---

## 🛠️ 2. Technology Stack

| Component | Technology | Version | Description |
| :--- | :--- | :--- | :--- |
| **Language** | Java | JDK 17 / 21 / 24 | Core Java, Multi-threading, Collections, JDBC API |
| **Desktop GUI** | Java Swing + FlatLaf | 3.5.4 | Modern Flat Look and Feel, CardLayout, JTable models |
| **Database** | MySQL | 8.0+ | Relational schema, InnoDB engine, Generated columns, Indexes |
| **Connectivity** | MySQL Connector/J | 8.3.0 | Direct PreparedStatement JDBC drivers |
| **Security** | jBCrypt | 0.4 | Adaptive salt password hashing (10 rounds) |
| **Build Tool** | Apache Maven | 3.9+ | Standardized dependency management & executable fat JAR packaging |
| **IDE Compatibility** | NetBeans / IntelliJ / Eclipse | Any | Standard Maven desktop project structure |

---

## 👥 3. User Roles & Permission Matrix

The application strictly implements desktop session-level **Role-Based Access Control (RBAC)**:

| Feature / Module | 👑 Admin | 🛡️ Warden | 💼 Accountant | 🎓 Student |
| :--- | :---: | :---: | :---: | :---: |
| **Role Dashboard** | Full KPI Dashboard | Operations Dashboard | Financial Dashboard | Resident Dashboard |
| **Student Management** | Full CRUD | View & Search Only | View & Search Only | View Own Profile |
| **Room Inventory** | Add, Edit, Delete | View Availability | — | View Listings |
| **Room Allocation** | Allocate & Vacate | Allocate & Vacate | — | View Allocated Room |
| **Fee Invoices** | Generate Invoices | — | Full Access | View Invoices & Dues |
| **Payment Recording** | Record & Receipts | — | Record & Receipts | View Receipts |
| **Complaints** | View & Resolve | View & Resolve | — | Submit & Track Own |
| **Food & Mess Menu** | Publish & Edit | View Menus | — | View Today & Weekly |
| **Food Feedback** | View Analytics | View Reviews | — | Submit 1–5 Star Rating |
| **Staff & User Management** | Full Control (Rule 9) | — | — | — |
| **System Reports** | All Reports (CSV/PDF) | Hostel Reports | Fee Reports | — |
| **Audit Logs** | View All Logs | — | — | — |
| **Profile & Password** | Self Manage | Self Manage | Self Manage | Self Manage |

---

## 🏢 4. Main Application Modules

### 1. Student Management Module
- Auto-generated Student ID, Register Number, Name, Gender, Department, Year, DOB, Contact Numbers, Email, Address, Parent/Guardian details, Admission Date.
- Real-time search by Student Name, Register Number, Department, or Phone.
- Automatic creation of student user login account upon registration.

### 2. Room Management Module
- Block Name, Room Number, Floor, Room Type (*Single, Double, Triple, Deluxe*), Capacity, Occupied beds, Available beds, Monthly Rent, Status (*AVAILABLE, OCCUPIED, UNDER_MAINTENANCE*).
- Automatic calculation of available beds (`total_capacity - occupied_capacity`).
- Prevents room deletion while students remain actively accommodated.

### 3. Smart Room Allocation Module
- Dynamic student dropdown (filters only unallocated students).
- Available room selection with instant capacity badges.
- **Transactional Allocation**: Enforces `SELECT ... FOR UPDATE` row locks; updates room occupancy counter atomically.
- **Room Vacating**: Vacates room, logs date/reason, and decrements room occupancy automatically.

### 4. Fee & Payment Management Module
- Generates semester-wise fee invoices (*Total, Paid, Pending*).
- Payment recording with verification (*Cash, UPI, Bank Transfer*).
- Unique receipt numbers (`REC-YYYYMMDD-XXXXX`).
- Printable official payment receipt modal window.

### 5. Complaint Management & Helpdesk Module
- Categories: *Water Problem, Electricity Problem, Room Maintenance, Bathroom Cleaning, Food Quality, Other Issues*.
- Auto-detects issue priority based on diagnostic keywords (*HIGH, EMERGENCY, MEDIUM, LOW*).
- Workflow tracking: `Pending → In Progress → Resolved`.
- Warden/Admin resolution notes and technician dispatch logging.
- Students can only view and submit their own complaints.

### 6. Food & Mess Menu Module
- Multi-meal categories: *Breakfast, Lunch, Dinner*.
- Daily view, Weekly view, and full menu scheduling.
- Dietary and seasonal notes published directly to student dashboards.
- Admin can publish and edit menus; students and wardens have read-only access.

### 7. Food Quality Feedback Module
- 1 to 5 star rating submission with resident comments.
- **Duplicate Protection**: Prevents duplicate submissions for the same student, date, and meal type.
- Average rating calculation displayed on warden and admin dashboards.

### 8. Staff and User Account Management Module
- Exclusive to System Administrator (Rule 9: Only Admin can create staff accounts).
- Lists all system users with role filtering and real-time search.
- Create new Staff Accounts (Admin, Warden, Accountant) with secure BCrypt password hashing.
- Edit user profile, role, and account status (*ACTIVE, INACTIVE, SUSPENDED*).
- Administrative password reset and account deletion with self-deletion protection.

### 9. Role-Aware Dashboard Module
- **Admin**: Student counts, room capacity gauges, collected revenue, open complaints, quick action buttons, and live audit activity trails.
- **Warden**: Resident metrics, available beds, pending maintenance queue, quick actions, and mess feedback score.
- **Accountant**: Invoiced total, collected revenue, outstanding dues, quick actions, and recent verified transaction logs.
- **Student**: "My Room" hero card, fee status, today's meal schedule, and active complaint trackers.

### 10. Report Management & CSV/PDF Export Module
- Generates 8 structured reports:
  1. Student Details Directory
  2. Room Availability & Capacity
  3. Room Allocations Record
  4. Hostel Fee Collection Overview
  5. Pending Fees & Defaulters
  6. Payment Transaction History
  7. Complaints & Resolution Log
  8. Food Quality & Feedback Summary
- Real-time instant table search filtering.
- Date range filtering (*From Date* and *To Date*).
- One-click **Export to CSV** and **Export to PDF / Native Print**.

### 11. Profile & Security Module
- User profile updates (Name, Email, Phone).
- Secure password change workflow with current password verification and BCrypt re-hashing.
- Secure session logout.

---

## 🗄️ 5. MySQL Database Architecture

**Database Name**: `smart_hostel_db`

```text
roles ──────────┐
                ▼
            ┌─────────┐
            │  users  │◄────────────┐
            └─────────┘             │
                ▲                   │
                │                   │
            ┌─────────┐             │
            │students │────────┐    │
            └─────────┘        │    │
                │              ▼    │
                │        ┌─────────────┐
                ├───────►│ allocations │◄─── rooms
                │        └─────────────┘
                │
                ├───────► fees ──────► payments
                │
                ├───────► complaints
                │
                ├───────► food_feedback
                │
                └───────► notifications

audit_logs (Central System Audit Trail)
food_menu  (Hostel Mess Schedule)
```

The database schema is fully defined in [`database/schema.sql`](file:///d:/Smart-Hostel-Management-System/database/schema.sql) with 12 normalized tables, foreign keys, stored generated columns for capacity calculations, and indexes for fast lookups.

---

## 🔐 6. Default Demo Credentials

Upon running `setup_database.bat` or launching the application:

| Role | Username | Default Password | Description |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin` | `admin123` | Full access to all administrative modules |
| **Hostel Warden** | `warden` | `warden123` | Manages rooms, allocations, complaints & feedback |
| **Accountant** | `accountant` | `account123` | Manages fee invoices, collections & payment receipts |
| **Student Resident** | `student` | `student123` | Views room, fees, menu, and submits complaints |

*(All passwords are encrypted with BCrypt before storage; plain-text passwords are never stored).*

---

## 🚀 7. Step-by-Step Setup & Execution Guide

### Prerequisites
1. **Java JDK 17, 21, or 24** installed (`java -version`).
2. **MySQL Server 8.0+** running on `localhost:3306`.
3. **Apache Maven 3.8+** installed (or auto-detected in standard paths).

---

### Step 1: Configure MySQL Database
Run the included database setup script:
```powershell
.\setup_database.bat
```
Or manually run the SQL scripts in MySQL:
```sql
SOURCE d:/Smart-Hostel-Management-System/database/schema.sql;
SOURCE d:/Smart-Hostel-Management-System/database/sample_data.sql;
```

---

### Step 2: Configure Database Credentials
Edit [`config/db.properties`](file:///d:/Smart-Hostel-Management-System/config/db.properties) with your local MySQL password:
```properties
db.url=jdbc:mysql://localhost:3306/smart_hostel_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=Ari@2003
db.driver=com.mysql.cj.jdbc.Driver
```

---

### Step 3: Build & Run the Application

#### Option A: One-Click Launch
Double-click `run.bat` or run:
```powershell
.\run.bat
```

#### Option B: Build Fat JAR
Double-click `build.bat` or run:
```powershell
.\build.bat
```
Then execute:
```powershell
java -jar target\smart-hostel-swing-1.0.0-jar-with-dependencies.jar
```

---

### Step 4: Opening in IDEs

#### **IntelliJ IDEA**
1. Click **File → Open...**
2. Select the `Smart-Hostel-Management-System` directory.
3. IntelliJ will detect `pom.xml` as a Maven project and download dependencies automatically.
4. Open `src/main/java/com/smarthostel/Main.java`, right-click and choose **Run 'Main.main()'**.

#### **Apache NetBeans**
1. Click **File → Open Project...**
2. Choose `Smart-Hostel-Management-System` (marked with a Maven icon).
3. Right-click the project → **Clean and Build**.
4. Right-click the project → **Run**.

#### **Eclipse IDE**
1. Click **File → Import... → Existing Maven Projects**.
2. Browse to `Smart-Hostel-Management-System` and click **Finish**.
3. Right-click `Main.java` → **Run As → Java Application**.

---

## 📂 8. Repository Structure

```text
Smart-Hostel-Management-System/
├── pom.xml                                  # Maven dependencies (FlatLaf, MySQL, jBCrypt)
├── build.bat                                # Automated build script (creates fat JAR)
├── run.bat                                  # Automated launcher script
├── setup_database.bat                       # Interactive MySQL database importer
├── config/
│   ├── db.properties                        # Active MySQL database credentials
│   └── db.properties.example                # Example template configuration
├── database/
│   ├── schema.sql                           # 12 MySQL tables, constraints & indexes
│   └── sample_data.sql                      # Demo seed data (Roles, Users, Students, Rooms)
├── src/main/
│   ├── java/com/smarthostel/
│   │   ├── Main.java                        # Application entry point & FlatLaf setup
│   │   ├── config/
│   │   │   ├── AppConfig.java               # Config reader (external & classpath)
│   │   │   ├── DatabaseConnection.java      # Singleton JDBC manager & transaction helper
│   │   │   └── SessionManager.java          # Role-based desktop session manager
│   │   ├── dao/                             # JDBC Data Access Objects (PreparedStatements)
│   │   │   ├── UserDAO.java                 # Users & roles CRUD
│   │   │   ├── StudentDAO.java              # Student directory CRUD
│   │   │   ├── RoomDAO.java                 # Room & bed inventory CRUD
│   │   │   ├── AllocationDAO.java           # Transactional allocation & vacate
│   │   │   ├── FeeDAO.java                  # Fee invoicing CRUD
│   │   │   ├── PaymentDAO.java              # Atomic payment & balance updates
│   │   │   ├── ComplaintDAO.java            # Incident ticket tracking
│   │   │   ├── FoodMenuDAO.java             # Daily/weekly dining schedule
│   │   │   ├── FoodFeedbackDAO.java         # Duplicate feedback validation
│   │   │   ├── NotificationDAO.java         # Notification manager
│   │   │   └── AuditLogDAO.java             # Immutable audit trail
│   │   ├── model/                           # Entity POJOs
│   │   │   ├── Role.java, User.java, Student.java, Room.java, Allocation.java,
│   │   │   ├── Fee.java, Payment.java, Complaint.java, FoodMenu.java,
│   │   │   └── FoodFeedback.java, Notification.java, AuditLog.java
│   │   ├── service/                         # Layered Business Logic
│   │   │   ├── AuthService.java             # BCrypt login & self-seeding
│   │   │   ├── UserService.java             # Staff & User account business logic
│   │   │   ├── StudentService.java
│   │   │   ├── RoomService.java
│   │   │   ├── AllocationService.java
│   │   │   ├── FeeService.java
│   │   │   ├── ComplaintService.java
│   │   │   ├── FoodService.java
│   │   │   ├── ReportService.java           # CSV generation & TableModel exports
│   │   │   └── AuditService.java
│   │   ├── ui/                              # Java Swing Presentation Layer
│   │   │   ├── LoginFrame.java              # Common authentication window
│   │   │   ├── MainDashboardFrame.java      # Master shell with CardLayout navigation
│   │   │   ├── components/                  # Custom Modern UI widgets
│   │   │   │   ├── HeaderPanel.java
│   │   │   │   ├── SidebarPanel.java        # Role-based menu items
│   │   │   │   ├── StatCard.java            # Metric statistics cards
│   │   │   │   └── ModernTable.java         # Padded, striped custom JTable
│   │   │   ├── dashboards/                  # 4 Role-Specific Dashboards
│   │   │   │   ├── AdminDashboardPanel.java
│   │   │   │   ├── WardenDashboardPanel.java
│   │   │   │   ├── AccountantDashboardPanel.java
│   │   │   │   └── StudentDashboardPanel.java
│   │   │   └── modules/                     # 10 Functional Screens
│   │   │       ├── StudentManagementPanel.java
│   │   │       ├── RoomManagementPanel.java
│   │   │       ├── RoomAllocationPanel.java
│   │   │       ├── FeeManagementPanel.java
│   │   │       ├── ComplaintManagementPanel.java
│   │   │       ├── FoodMenuPanel.java
│   │   │       ├── FoodFeedbackPanel.java
│   │   │       ├── UserManagementPanel.java
│   │   │       ├── ReportsPanel.java
│   │   │       └── ProfilePanel.java
│   │   └── util/                            # Security & Styling Utilities
│   │       ├── PasswordUtil.java            # BCrypt hashing & checking
│   │       ├── ValidationUtil.java          # Regex & numeric validations
│   │       ├── DateUtil.java                # Date parsing & formatting
│   │       └── UITheme.java                 # Deep Indigo enterprise design tokens
│   └── resources/
│       └── db.properties                    # Classpath fallback configuration
└── README.md
```

---

## 📜 9. Academic Project Certification Notes

This project satisfies all requirements for an **M.Sc. Computer Science / MCA / B.E. Computer Science** final-year project:
1. **No External Heavy Web Frameworks**: Pure Java Swing and direct JDBC.
2. **ACID Transactions**: Atomic operations ensure database consistency during room allocation and fee collection.
3. **Strong Security**: Password salt hashing via BCrypt ensures compliance with modern software security standards.
4. **Normalized Database Design**: Complete 3NF relational schema with referential integrity.
5. **Modern Human-Computer Interaction (HCI)**: Built on FlatLaf with high-contrast text, clear typography, and responsive component sizing.
