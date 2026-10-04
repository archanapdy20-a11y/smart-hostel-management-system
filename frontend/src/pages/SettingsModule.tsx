import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { User, Lock } from 'lucide-react';

export const SettingsModule: React.FC = () => {
  const { user } = useAuth();
  const [successMsg, setSuccessMsg] = useState('');

  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handlePasswordSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSuccessMsg('Password updated successfully!');
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
    setTimeout(() => setSuccessMsg(''), 4000);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px', maxWidth: '800px' }}>
      <div>
        <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Account Settings & Preferences</h1>
        <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
          Manage your account profile, security credentials, and system notification preferences
        </p>
      </div>

      {successMsg && (
        <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--success-bg)', border: '1px solid var(--success-border)', color: 'var(--success-text)', fontSize: '0.8125rem', fontWeight: 600 }}>
          {successMsg}
        </div>
      )}

      {/* User Profile Card */}
      <div className="saas-card">
        <h3 className="saas-card-title" style={{ marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <User size={18} color="var(--primary-600)" />
          User Profile Overview
        </h3>

        <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '20px' }}>
          <div style={{ width: '54px', height: '54px', borderRadius: '50%', backgroundColor: 'var(--primary-600)', color: '#fff', fontSize: '1.25rem', fontWeight: 800, display: 'flex', alignItems: 'center', justifySelf: 'center', justifyContent: 'center' }}>
            {user?.username?.[0]?.toUpperCase() || 'U'}
          </div>
          <div>
            <span style={{ fontSize: '1.1rem', fontWeight: 800, display: 'block', color: 'var(--text-main)' }}>{user?.username}</span>
            <span style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>Role: {user?.role}</span>
          </div>
        </div>
      </div>

      {/* Security & Password Form */}
      <div className="saas-card">
        <h3 className="saas-card-title" style={{ marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Lock size={18} color="var(--primary-600)" />
          Change Password
        </h3>

        <form onSubmit={handlePasswordSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Current Password *</label>
            <input type="password" className="form-input" value={currentPassword} onChange={e => setCurrentPassword(e.target.value)} required />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">New Password *</label>
              <input type="password" className="form-input" value={newPassword} onChange={e => setNewPassword(e.target.value)} required />
            </div>
            <div className="form-group">
              <label className="form-label">Confirm New Password *</label>
              <input type="password" className="form-input" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} required />
            </div>
          </div>

          <button type="submit" className="btn btn-primary" style={{ alignSelf: 'flex-end', marginTop: '8px' }}>
            Update Password
          </button>
        </form>
      </div>
    </div>
  );
};
