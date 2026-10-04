# 🏫 Smart Hostel Management System — Enterprise SaaS Platform

A production-ready, full-stack **Smart Hostel Management System** built with **Java 21 / Spring Boot 3.3.4** and **React 18 + TypeScript + Vite**. Designed with an enterprise SaaS dashboard aesthetic, role-based access control, smart room allocation algorithms, and live QR attendance verification.

---

## 🌟 Key System Features

### 🤖 1. Smart Multi-Criteria Room Allocation Engine
- Suitability scoring algorithm based on department synergy, academic year compatibility, gender constraints, and room capacity.
- Returns ranked recommendation cards with match percentages (*e.g., 96% Match*) and detailed suitability checklists.

### 📱 2. Live Dynamic QR Attendance Verification
- **Warden View**: Generates time-bounded HMAC-signed QR token canvas with a live 120-second countdown timer.
- **Student View**: QR code scanner / token check-in form with timestamped attendance logs.

### 🚨 3. Intelligent Complaint & Incident Helpdesk
- Auto-calculates ticket priority (`EMERGENCY`, `HIGH`, `MEDIUM`, `LOW`) by scanning keywords (*e.g., water leak, fire, spark*).
- Lifecycle tracking: `Submitted → Assigned → In Progress → Resolved → Closed`.

### 📅 4. Outing & Leave Approval Workflow
- Student leave application modal with date ranges and emergency contact details.
- Warden approval and rejection queue with real-time status updates.

### 📊 5. Role-Aware Enterprise Dashboards
- **Admin Dashboard**: System-wide KPIs, Recharts capacity analytics, complaint priority pie charts, and audit logs.
- **Warden Dashboard**: Block resident metrics, live QR generator trigger, and assigned maintenance tickets.
- **Student Dashboard**: *"My Hostel Room"* hero card, quick action shortcuts, attendance rings, and notice feed.

---

## 🛠️ Technology Stack

### **Backend Framework**
- **Java 21** & **Spring Boot 3.3.4**
- **Spring Security** (Stateless JWT authentication with silent refresh token rotation)
- **Spring Data JPA** & **Hibernate ORM**
- **H2 In-Memory Database** (Zero-Config mode) & **MySQL 8.0**
- **Swagger OpenAPI 3** (`http://localhost:8085/swagger-ui.html`)

### **Frontend Framework**
- **React 18** & **TypeScript 5** & **Vite 8**
- **React Router DOM 7** (Protected role-aware routes)
- **Recharts** (Interactive financial & occupancy analytics)
- **Lucide Icons** (Clean outline icon suite)
- **Vanilla CSS Tokens** (Deep Indigo SaaS theme)

---

## 🚀 Quick Start Guide

### Prerequisites
- **JDK 21** or higher
- **Node.js 18+** & **npm**
- **Apache Maven 3.9+** (Included in system path)

### 1. Running the Backend Server
```powershell
# Navigate to backend directory
cd backend

# Option A: Run with Zero-Config H2 Database (Recommended)
java -jar target/smart-hostel-backend-1.0.0.jar --spring.profiles.active=h2

# Option B: Run with Local MySQL Database
java -jar target/smart-hostel-backend-1.0.0.jar --SPRING_DATASOURCE_PASSWORD=your_mysql_password
```
- **Backend Port**: `8085`
- **Swagger API Docs**: `http://localhost:8085/swagger-ui.html`

### 2. Running the Frontend Application
```powershell
# Navigate to frontend directory
cd frontend

# Step 1: Install frontend dependencies (required on first run)
npm install

# Step 2: Start Vite development server
npm run dev
```
- **Frontend App**: `http://localhost:5173`

---

## 🔐 Default Demo Accounts

Upon initial startup, default accounts are seeded automatically:

| Role | Username | Password | Default Capabilities |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full system analytics, hostel management, smart allocation engine |
| **Warden** | `warden` | `warden123` | Block attendance generator, leave approvals, complaint resolution |
| **Student** | `student` | `student123` | Apply for leave, report complaints, scan QR attendance |

*(Passwords are securely hashed using BCrypt).*

---

## 📁 Repository Structure

```text
Smart-Hostel-Management-System/
├── backend/
│   ├── src/main/java/com/smarthostel/
│   │   ├── config/          # Security & DataInitializer
│   │   ├── controller/      # REST API Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA Entities & Enums
│   │   ├── repository/      # Spring Data Repositories
│   │   └── service/         # Business Logic & Allocation Engine
│   └── src/main/resources/  # Application yml configs
├── frontend/
│   ├── src/
│   │   ├── components/      # Navbar, Sidebar, UI Badges & Modals
│   │   ├── context/         # AuthContext with silent token refresh
│   │   ├── pages/           # Dashboard, Hostels, Smart Allocation, etc.
│   │   ├── routes/          # AppRoutes & ProtectedRoute
│   │   └── index.css        # Enterprise Design Tokens
│   └── vite.config.ts       # API Proxy configuration
└── README.md
```

---

## 📄 License & Author

- **Author**: Archana M (`archanapdy20@gmail.com`)
- **Repository**: [smart-hostel-management-system](https://github.com/archanapdy20-a11y/smart-hostel-management-system.git)
