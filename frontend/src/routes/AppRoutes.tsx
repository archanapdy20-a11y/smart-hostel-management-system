import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '../components/ProtectedRoute';
import { Navbar } from '../components/Navbar';
import { Sidebar } from '../components/Sidebar';

import { Login } from '../pages/Login';
import { Register } from '../pages/Register';
import { Dashboard } from '../pages/Dashboard';
import { HostelManagement } from '../pages/HostelManagement';
import { SmartAllocation } from '../pages/SmartAllocation';
import { StudentManagement } from '../pages/StudentManagement';
import { AttendanceModule } from '../pages/AttendanceModule';
import { ComplaintsModule } from '../pages/ComplaintsModule';
import { LeaveModule } from '../pages/LeaveModule';
import { FeesModule } from '../pages/FeesModule';
import { NoticesModule } from '../pages/NoticesModule';
import { NotificationsModule } from '../pages/NotificationsModule';
import { ReportsModule } from '../pages/ReportsModule';
import { SettingsModule } from '../pages/SettingsModule';

const MainLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [sidebarCollapsed, setSidebarCollapsed] = React.useState(false);

  return (
    <div className="app-shell">
      <Navbar onToggleSidebar={() => setSidebarCollapsed(!sidebarCollapsed)} />
      <div className="app-body">
        <Sidebar isCollapsed={sidebarCollapsed} />
        <main className="app-main">
          {children}
        </main>
      </div>
    </div>
  );
};

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Authentication Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Protected Enterprise Routes */}
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<MainLayout><Dashboard /></MainLayout>} />
        <Route path="/smart-allocation" element={<MainLayout><SmartAllocation /></MainLayout>} />
        <Route path="/hostels" element={<MainLayout><HostelManagement /></MainLayout>} />
        <Route path="/students" element={<MainLayout><StudentManagement /></MainLayout>} />
        <Route path="/attendance" element={<MainLayout><AttendanceModule /></MainLayout>} />
        <Route path="/complaints" element={<MainLayout><ComplaintsModule /></MainLayout>} />
        <Route path="/leaves" element={<MainLayout><LeaveModule /></MainLayout>} />
        <Route path="/fees" element={<MainLayout><FeesModule /></MainLayout>} />
        <Route path="/notices" element={<MainLayout><NoticesModule /></MainLayout>} />
        <Route path="/notifications" element={<MainLayout><NotificationsModule /></MainLayout>} />
        <Route path="/reports" element={<MainLayout><ReportsModule /></MainLayout>} />
        <Route path="/settings" element={<MainLayout><SettingsModule /></MainLayout>} />
      </Route>

      {/* Default Fallback Redirect */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
