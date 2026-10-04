import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Building2,
  Search,
  Bell,
  LogOut,
  User as UserIcon,
  ChevronDown,
  Menu,
  Sparkles,
  CheckCircle2,
  AlertTriangle
} from 'lucide-react';

interface NavbarProps {
  onToggleSidebar?: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Notifications Mock Feed
  const [notifications, setNotifications] = useState([
    { id: '1', title: 'Leave Application Approved', desc: 'Your leave request for Oct 10 - Oct 12 was approved.', time: '10m ago', unread: true, type: 'success' },
    { id: '2', title: 'High Priority Complaint Logged', desc: 'Room 204 reported a major plumbing leak.', time: '1h ago', unread: true, type: 'warning' },
    { id: '3', title: 'System Notice Published', desc: 'Scheduled maintenance tomorrow from 10 AM to 1 PM.', time: '3h ago', unread: false, type: 'info' }
  ]);

  const unreadCount = notifications.filter(n => n.unread).length;

  const markAllRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, unread: false })));
  };

  // Map route path to human-readable breadcrumb title
  const getBreadcrumbs = () => {
    const path = location.pathname;
    switch (path) {
      case '/dashboard': return { title: 'Dashboard Overview', category: 'Campus Management' };
      case '/smart-allocation': return { title: 'Smart Room Allocation Engine', category: 'Automation Tools' };
      case '/hostels': return { title: 'Hostels & Room Infrastructure', category: 'Facility Management' };
      case '/students': return { title: 'Student Directory & Records', category: 'Academic Records' };
      case '/attendance': return { title: 'QR Attendance Verification', category: 'Daily Operations' };
      case '/complaints': return { title: 'Complaint & Incident Tracker', category: 'Helpdesk' };
      case '/leaves': return { title: 'Leave & Outing Approvals', category: 'Student Welfare' };
      case '/fees': return { title: 'Fees & Payment Ledger', category: 'Finance' };
      case '/notices': return { title: 'Notice Board & Announcements', category: 'Communication' };
      case '/notifications': return { title: 'Notification Center', category: 'System Alerts' };
      case '/reports': return { title: 'Reports & Export Center', category: 'Analytics' };
      case '/settings': return { title: 'Account Settings & Preferences', category: 'User Configuration' };
      default: return { title: 'SmartHostel System', category: 'SaaS Platform' };
    }
  };

  const breadcrumbs = getBreadcrumbs();

  return (
    <header
      style={{
        height: 'var(--header-height)',
        backgroundColor: 'var(--bg-surface)',
        borderBottom: '1px solid var(--border-default)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 24px',
        position: 'sticky',
        top: 0,
        zIndex: 200,
        boxShadow: 'var(--shadow-xs)'
      }}
    >
      {/* Left Brand & Breadcrumbs */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <button
            onClick={onToggleSidebar}
            className="btn btn-ghost mobile-nav-toggle"
            style={{ padding: '8px' }}
            aria-label="Toggle navigation menu"
          >
            <Menu size={20} />
          </button>

          <div
            onClick={() => navigate('/dashboard')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              cursor: 'pointer',
              userSelect: 'none'
            }}
          >
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--primary-600)',
                color: '#fff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 2px 6px rgba(79, 70, 229, 0.3)'
              }}
            >
              <Building2 size={20} />
            </div>
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <span style={{ fontSize: '1.05rem', fontWeight: 800, color: 'var(--text-main)', letterSpacing: '-0.02em', lineHeight: 1.1 }}>
                SmartHostel<span style={{ color: 'var(--primary-600)' }}>.io</span>
              </span>
              <span style={{ fontSize: '0.6875rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                Enterprise SaaS
              </span>
            </div>
          </div>
        </div>

        <div style={{ height: '24px', width: '1px', backgroundColor: 'var(--border-default)' }} />

        {/* Current Breadcrumb Title */}
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          <span style={{ fontSize: '0.6875rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
            {breadcrumbs.category}
          </span>
          <span style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--text-main)' }}>
            {breadcrumbs.title}
          </span>
        </div>
      </div>

      {/* Middle Global Search */}
      <div style={{ flex: 1, maxWidth: '400px', margin: '0 20px', display: 'none' }} className="desktop-search">
        <div style={{ position: 'relative', width: '100%' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            className="form-input"
            placeholder="Search students, rooms, complaints... (Ctrl+K)"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ paddingLeft: '36px', height: '38px', fontSize: '0.8125rem' }}
          />
        </div>
      </div>

      {/* Right Header Actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
        {user?.role !== 'ROLE_STUDENT' && (
          <button
            onClick={() => navigate('/smart-allocation')}
            className="btn btn-outline btn-sm"
            style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}
          >
            <Sparkles size={15} />
            <span>Smart Allocator</span>
          </button>
        )}

        {/* Notification Popover Dropdown */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="btn btn-ghost"
            style={{ position: 'relative', padding: '8px', borderRadius: '50%' }}
            aria-label="View notifications"
          >
            <Bell size={20} />
            {unreadCount > 0 && (
              <span
                style={{
                  position: 'absolute',
                  top: '4px',
                  right: '4px',
                  width: '9px',
                  height: '9px',
                  borderRadius: '50%',
                  backgroundColor: 'var(--danger)',
                  border: '2px solid var(--bg-surface)'
                }}
              />
            )}
          </button>

          {showNotifications && (
            <div
              style={{
                position: 'absolute',
                top: 'calc(100% + 8px)',
                right: 0,
                width: '340px',
                backgroundColor: 'var(--bg-surface)',
                border: '1px solid var(--border-default)',
                borderRadius: 'var(--radius-lg)',
                boxShadow: 'var(--shadow-lg)',
                zIndex: 300,
                padding: '16px'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px', paddingBottom: '8px', borderBottom: '1px solid var(--border-subtle)' }}>
                <span style={{ fontSize: '0.875rem', fontWeight: 700 }}>Notifications</span>
                {unreadCount > 0 && (
                  <button onClick={markAllRead} style={{ fontSize: '0.75rem', color: 'var(--primary-600)', background: 'none', border: 'none', fontWeight: 600 }}>
                    Mark all read
                  </button>
                )}
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxHeight: '300px', overflowY: 'auto' }}>
                {notifications.map((n) => (
                  <div
                    key={n.id}
                    style={{
                      padding: '10px',
                      borderRadius: 'var(--radius-md)',
                      backgroundColor: n.unread ? 'var(--primary-50)' : 'var(--bg-subtle)',
                      display: 'flex',
                      gap: '10px'
                    }}
                  >
                    {n.type === 'success' ? <CheckCircle2 size={16} color="var(--success)" /> : <AlertTriangle size={16} color="var(--warning)" />}
                    <div style={{ flex: 1 }}>
                      <span style={{ fontSize: '0.8125rem', fontWeight: 700, display: 'block', color: 'var(--text-main)' }}>{n.title}</span>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block', marginTop: '2px' }}>{n.desc}</span>
                      <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)', marginTop: '4px', display: 'block' }}>{n.time}</span>
                    </div>
                  </div>
                ))}
              </div>

              <div style={{ marginTop: '12px', paddingTop: '8px', borderTop: '1px solid var(--border-subtle)', textAlign: 'center' }}>
                <button
                  onClick={() => { setShowNotifications(false); navigate('/notifications'); }}
                  style={{ fontSize: '0.8125rem', color: 'var(--primary-600)', background: 'none', border: 'none', fontWeight: 700 }}
                >
                  View Notification Center →
                </button>
              </div>
            </div>
          )}
        </div>

        {/* User Account Menu */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="btn btn-ghost"
            style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '4px 8px', borderRadius: 'var(--radius-md)' }}
          >
            <div
              style={{
                width: '32px',
                height: '32px',
                borderRadius: '50%',
                backgroundColor: 'var(--primary-600)',
                color: '#fff',
                fontWeight: 700,
                fontSize: '0.8125rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              {user?.username?.[0]?.toUpperCase() || 'U'}
            </div>
            <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-main)' }}>
              {user?.username || 'User'}
            </span>
            <ChevronDown size={14} color="var(--text-muted)" />
          </button>

          {showUserMenu && (
            <div
              style={{
                position: 'absolute',
                top: 'calc(100% + 8px)',
                right: 0,
                width: '200px',
                backgroundColor: 'var(--bg-surface)',
                border: '1px solid var(--border-default)',
                borderRadius: 'var(--radius-lg)',
                boxShadow: 'var(--shadow-lg)',
                zIndex: 300,
                padding: '8px'
              }}
            >
              <button
                onClick={() => { setShowUserMenu(false); navigate('/settings'); }}
                className="btn btn-ghost"
                style={{ width: '100%', justifyContent: 'flex-start', fontSize: '0.8125rem' }}
              >
                <UserIcon size={16} />
                <span>Account Profile</span>
              </button>

              <div style={{ height: '1px', backgroundColor: 'var(--border-subtle)', margin: '4px 0' }} />

              <button
                onClick={() => { setShowUserMenu(false); logout(); }}
                className="btn btn-ghost"
                style={{ width: '100%', justifyContent: 'flex-start', fontSize: '0.8125rem', color: 'var(--danger)' }}
              >
                <LogOut size={16} />
                <span>Sign Out</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
