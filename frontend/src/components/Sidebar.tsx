import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Building2,
  Sparkles,
  Users,
  QrCode,
  AlertCircle,
  CalendarDays,
  CreditCard,
  Bell,
  FileSpreadsheet,
  Settings,
  Megaphone,
  LogOut,
  ShieldCheck,
  UserCheck,
  GraduationCap
} from 'lucide-react';

interface SidebarProps {
  isCollapsed?: boolean;
  onToggleCollapse?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isCollapsed = false }) => {
  const { user, logout } = useAuth();
  const role = user?.role || 'ROLE_STUDENT';

  const getRoleBadge = () => {
    switch (role) {
      case 'ROLE_ADMIN':
        return { label: 'Admin', icon: <ShieldCheck size={12} />, bg: 'var(--primary-100)', color: 'var(--primary-800)' };
      case 'ROLE_WARDEN':
        return { label: 'Warden', icon: <UserCheck size={12} />, bg: 'var(--warning-bg)', color: 'var(--warning-text)' };
      default:
        return { label: 'Student', icon: <GraduationCap size={12} />, bg: 'var(--info-bg)', color: 'var(--info-text)' };
    }
  };

  const roleInfo = getRoleBadge();

  // Navigation Items by Role
  const mainNav = [
    { title: 'Dashboard', path: '/dashboard', icon: <LayoutDashboard size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Smart Allocation', path: '/smart-allocation', icon: <Sparkles size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN'] },
    { title: 'Hostels & Rooms', path: '/hostels', icon: <Building2 size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Students', path: '/students', icon: <Users size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN'] },
  ];

  const opsNav = [
    { title: 'Attendance & QR', path: '/attendance', icon: <QrCode size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Complaints', path: '/complaints', icon: <AlertCircle size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Leave Requests', path: '/leaves', icon: <CalendarDays size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
  ];

  const finNav = [
    { title: 'Fees & Payments', path: '/fees', icon: <CreditCard size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Notice Board', path: '/notices', icon: <Megaphone size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Reports & Analytics', path: '/reports', icon: <FileSpreadsheet size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN'] },
  ];

  const systemNav = [
    { title: 'Notifications', path: '/notifications', icon: <Bell size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
    { title: 'Settings', path: '/settings', icon: <Settings size={18} />, roles: ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT'] },
  ];

  const filterNav = (items: typeof mainNav) => items.filter(item => item.roles.includes(role));

  return (
    <aside
      className="app-sidebar"
      style={{
        width: isCollapsed ? 'var(--sidebar-collapsed-width)' : 'var(--sidebar-width)',
        backgroundColor: 'var(--bg-surface)',
        borderRight: '1px solid var(--border-default)',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: isCollapsed ? '20px 10px' : '20px 16px',
        transition: 'width 0.2s ease',
        zIndex: 100,
        position: 'sticky',
        top: 'var(--header-height)',
        height: 'calc(100vh - var(--header-height))',
        overflowY: 'auto'
      }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {/* Navigation Sections */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>

          {/* Section: Core */}
          <div>
            {!isCollapsed && (
              <span style={{ fontSize: '0.6875rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', paddingLeft: '10px', display: 'block', marginBottom: '8px' }}>
                Main Core
              </span>
            )}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {filterNav(mainNav).map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  style={({ isActive }) => ({
                    display: 'flex',
                    alignItems: 'center',
                    gap: '12px',
                    padding: isCollapsed ? '10px' : '10px 12px',
                    justifyContent: isCollapsed ? 'center' : 'flex-start',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '0.875rem',
                    fontWeight: isActive ? 700 : 500,
                    color: isActive ? 'var(--primary-600)' : 'var(--text-secondary)',
                    backgroundColor: isActive ? 'var(--primary-50)' : 'transparent',
                    textDecoration: 'none',
                    transition: 'all 0.15s ease'
                  })}
                >
                  {item.icon}
                  {!isCollapsed && <span>{item.title}</span>}
                </NavLink>
              ))}
            </div>
          </div>

          {/* Section: Operations */}
          <div>
            {!isCollapsed && (
              <span style={{ fontSize: '0.6875rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', paddingLeft: '10px', display: 'block', marginBottom: '8px' }}>
                Campus Operations
              </span>
            )}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {filterNav(opsNav).map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  style={({ isActive }) => ({
                    display: 'flex',
                    alignItems: 'center',
                    gap: '12px',
                    padding: isCollapsed ? '10px' : '10px 12px',
                    justifyContent: isCollapsed ? 'center' : 'flex-start',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '0.875rem',
                    fontWeight: isActive ? 700 : 500,
                    color: isActive ? 'var(--primary-600)' : 'var(--text-secondary)',
                    backgroundColor: isActive ? 'var(--primary-50)' : 'transparent',
                    textDecoration: 'none',
                    transition: 'all 0.15s ease'
                  })}
                >
                  {item.icon}
                  {!isCollapsed && <span>{item.title}</span>}
                </NavLink>
              ))}
            </div>
          </div>

          {/* Section: Finance & Notice */}
          <div>
            {!isCollapsed && (
              <span style={{ fontSize: '0.6875rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', paddingLeft: '10px', display: 'block', marginBottom: '8px' }}>
                Finance & Reports
              </span>
            )}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {filterNav(finNav).map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  style={({ isActive }) => ({
                    display: 'flex',
                    alignItems: 'center',
                    gap: '12px',
                    padding: isCollapsed ? '10px' : '10px 12px',
                    justifyContent: isCollapsed ? 'center' : 'flex-start',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '0.875rem',
                    fontWeight: isActive ? 700 : 500,
                    color: isActive ? 'var(--primary-600)' : 'var(--text-secondary)',
                    backgroundColor: isActive ? 'var(--primary-50)' : 'transparent',
                    textDecoration: 'none',
                    transition: 'all 0.15s ease'
                  })}
                >
                  {item.icon}
                  {!isCollapsed && <span>{item.title}</span>}
                </NavLink>
              ))}
            </div>
          </div>

          {/* Section: Preferences */}
          <div>
            {!isCollapsed && (
              <span style={{ fontSize: '0.6875rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', paddingLeft: '10px', display: 'block', marginBottom: '8px' }}>
                System
              </span>
            )}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {filterNav(systemNav).map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  style={({ isActive }) => ({
                    display: 'flex',
                    alignItems: 'center',
                    gap: '12px',
                    padding: isCollapsed ? '10px' : '10px 12px',
                    justifyContent: isCollapsed ? 'center' : 'flex-start',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '0.875rem',
                    fontWeight: isActive ? 700 : 500,
                    color: isActive ? 'var(--primary-600)' : 'var(--text-secondary)',
                    backgroundColor: isActive ? 'var(--primary-50)' : 'transparent',
                    textDecoration: 'none',
                    transition: 'all 0.15s ease'
                  })}
                >
                  {item.icon}
                  {!isCollapsed && <span>{item.title}</span>}
                </NavLink>
              ))}
            </div>
          </div>

        </div>
      </div>

      {/* User Footer Account Pill */}
      {!isCollapsed && (
        <div
          style={{
            padding: '12px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-subtle)',
            border: '1px solid var(--border-default)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginTop: '20px'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', overflow: 'hidden' }}>
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '50%',
                backgroundColor: 'var(--primary-600)',
                color: '#fff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 700,
                fontSize: '0.875rem',
                flexShrink: 0
              }}
            >
              {user?.username?.[0]?.toUpperCase() || 'U'}
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
              <span style={{ fontSize: '0.8125rem', fontWeight: 700, color: 'var(--text-main)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {user?.username || 'User'}
              </span>
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '4px',
                  fontSize: '0.6875rem',
                  fontWeight: 600,
                  color: roleInfo.color,
                  marginTop: '1px'
                }}
              >
                {roleInfo.icon}
                {roleInfo.label}
              </span>
            </div>
          </div>

          <button
            onClick={logout}
            className="btn btn-ghost"
            style={{ padding: '6px', color: 'var(--danger)' }}
            title="Log out"
          >
            <LogOut size={16} />
          </button>
        </div>
      )}
    </aside>
  );
};
