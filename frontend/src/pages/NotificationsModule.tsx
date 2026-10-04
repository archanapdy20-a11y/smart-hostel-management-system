import React, { useState } from 'react';
import { Badge } from '../components/ui/Badge';
import { Bell, CheckCircle2 } from 'lucide-react';

export const NotificationsModule: React.FC = () => {
  const [notifications, setNotifications] = useState([
    { id: '1', title: 'Leave Application Approved', desc: 'Your leave request for Oct 10 - Oct 15 was approved by Warden Vance.', category: 'LEAVE', time: '10 mins ago', unread: true },
    { id: '2', title: 'High Priority Complaint Update', desc: 'Plumber dispatched to Room A-204 for water leakage issue.', category: 'COMPLAINT', time: '1 hour ago', unread: true },
    { id: '3', title: 'Daily Attendance Recorded', desc: 'QR check-in marked PRESENT at 20:15 PM at Block A canvas.', category: 'ATTENDANCE', time: '3 hours ago', unread: false },
    { id: '4', title: 'Campus Maintenance Notice', desc: 'Power maintenance scheduled for tomorrow from 10 AM to 1 PM.', category: 'NOTICE', time: '1 day ago', unread: false }
  ]);

  const markAllRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, unread: false })));
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Notification Center</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            System updates, leave approvals, complaint status alerts, and announcements
          </p>
        </div>

        <button onClick={markAllRead} className="btn btn-outline btn-sm">
          <CheckCircle2 size={14} /> Mark All as Read
        </button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {notifications.map(n => (
          <div
            key={n.id}
            className="saas-card"
            style={{
              padding: '20px',
              backgroundColor: n.unread ? 'var(--primary-50)' : 'var(--bg-surface)',
              border: n.unread ? '1px solid var(--primary-200)' : '1px solid var(--border-default)',
              display: 'flex',
              alignItems: 'flex-start',
              gap: '16px'
            }}
          >
            <div
              style={{
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                backgroundColor: 'var(--bg-surface)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexShrink: 0
              }}
            >
              <Bell size={20} color="var(--primary-600)" />
            </div>

            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '4px' }}>
                <span style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--text-main)' }}>{n.title}</span>
                <Badge variant={n.category === 'LEAVE' ? 'success' : n.category === 'COMPLAINT' ? 'warning' : 'info'}>
                  {n.category}
                </Badge>
              </div>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>{n.desc}</p>
              <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)', marginTop: '8px', display: 'block' }}>{n.time}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
