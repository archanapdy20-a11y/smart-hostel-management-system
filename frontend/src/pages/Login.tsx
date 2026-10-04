import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Building2, Lock, User as UserIcon, ShieldCheck, ArrowRight, CheckCircle2 } from 'lucide-react';

export const Login: React.FC = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');

    if (!usernameOrEmail || !password) {
      setErrorMsg('Please enter both username/email and password.');
      return;
    }

    setLoading(true);
    try {
      await login({ usernameOrEmail, password });
      navigate('/dashboard');
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Invalid username/email or password.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickDemoFill = (role: 'admin' | 'warden' | 'student') => {
    if (role === 'admin') {
      setUsernameOrEmail('admin');
      setPassword('admin123');
    } else if (role === 'warden') {
      setUsernameOrEmail('warden');
      setPassword('warden123');
    } else {
      setUsernameOrEmail('student');
      setPassword('student123');
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: 'var(--bg-app)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px'
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '1000px',
          backgroundColor: 'var(--bg-surface)',
          borderRadius: 'var(--radius-xl)',
          border: '1px solid var(--border-default)',
          boxShadow: 'var(--shadow-xl)',
          overflow: 'hidden',
          display: 'grid',
          gridTemplateColumns: '1fr 1fr'
        }}
        className="auth-grid-container"
      >
        {/* Left SaaS Hero Banner Panel */}
        <div
          style={{
            backgroundColor: 'var(--primary-900)',
            color: '#FFFFFF',
            padding: '48px 40px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
            backgroundImage: 'radial-gradient(circle at 10% 20%, rgba(99, 102, 241, 0.35) 0%, transparent 50%)'
          }}
        >
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '40px' }}>
              <div
                style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--primary-600)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 4px 12px rgba(0,0,0,0.2)'
                }}
              >
                <Building2 size={24} color="#FFFFFF" />
              </div>
              <span style={{ fontSize: '1.25rem', fontWeight: 800, letterSpacing: '-0.02em', color: '#FFFFFF' }}>
                SmartHostel<span style={{ color: 'var(--primary-400)' }}>.io</span>
              </span>
            </div>

            <h2 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#FFFFFF', lineHeight: 1.25, marginBottom: '16px' }}>
              Enterprise Smart Campus & Hostel Infrastructure
            </h2>
            <p style={{ color: 'var(--primary-200)', fontSize: '0.9375rem', lineHeight: 1.6, marginBottom: '32px' }}>
              Automated multi-criteria room allocation engine, live HMAC-signed QR attendance verification, and intelligent complaint triage.
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <CheckCircle2 size={18} color="var(--primary-400)" />
                <span style={{ fontSize: '0.875rem', color: 'var(--primary-100)' }}>Role-aware dashboard controls for Admins & Wardens</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <CheckCircle2 size={18} color="var(--primary-400)" />
                <span style={{ fontSize: '0.875rem', color: 'var(--primary-100)' }}>Real-time leave application & complaint tracking</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <CheckCircle2 size={18} color="var(--primary-400)" />
                <span style={{ fontSize: '0.875rem', color: 'var(--primary-100)' }}>Dynamic JWT dual-token silent refresh authentication</span>
              </div>
            </div>
          </div>

          <div style={{ marginTop: '40px', paddingTop: '20px', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--primary-300)' }}>
              © 2026 SmartHostel SaaS Platform • Built with Java 21 & React 18
            </span>
          </div>
        </div>

        {/* Right Form Container */}
        <div style={{ padding: '48px 40px', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
          <div style={{ marginBottom: '28px' }}>
            <h1 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '6px' }}>Sign in to your account</h1>
            <p style={{ fontSize: '0.875rem' }}>Welcome back! Enter your portal credentials below.</p>
          </div>

          {errorMsg && (
            <div
              style={{
                padding: '12px 14px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--danger-bg)',
                border: '1px solid var(--danger-border)',
                color: 'var(--danger-text)',
                fontSize: '0.8125rem',
                fontWeight: 600,
                marginBottom: '20px'
              }}
            >
              {errorMsg}
            </div>
          )}

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label className="form-label">Username or Email</label>
              <div style={{ position: 'relative' }}>
                <UserIcon size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
                <input
                  type="text"
                  className="form-input"
                  placeholder="admin, warden, or student"
                  value={usernameOrEmail}
                  onChange={(e) => setUsernameOrEmail(e.target.value)}
                  style={{ paddingLeft: '36px' }}
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <div style={{ position: 'relative' }}>
                <Lock size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
                <input
                  type="password"
                  className="form-input"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  style={{ paddingLeft: '36px' }}
                />
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', fontSize: '0.8125rem' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', userSelect: 'none', color: 'var(--text-secondary)' }}>
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                  style={{ accentColor: 'var(--primary-600)' }}
                />
                Remember me
              </label>
              <a href="#forgot" onClick={(e) => { e.preventDefault(); alert('Password reset link has been dispatched to your registered email.'); }} style={{ color: 'var(--primary-600)', textDecoration: 'none', fontWeight: 600 }}>
                Forgot password?
              </a>
            </div>

            <button type="submit" className="btn btn-primary btn-lg" style={{ width: '100%', marginBottom: '24px' }} disabled={loading}>
              {loading ? 'Authenticating...' : 'Sign In to Dashboard'}
              <ArrowRight size={18} />
            </button>
          </form>

          {/* Quick Demo Credentials Autofill */}
          <div style={{ marginTop: '8px', paddingTop: '20px', borderTop: '1px solid var(--border-subtle)' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em', display: 'block', marginBottom: '10px' }}>
              Quick Demo Autofill
            </span>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
              <button onClick={() => handleQuickDemoFill('admin')} className="btn btn-secondary btn-sm" type="button">
                <ShieldCheck size={14} color="var(--primary-600)" />
                Admin
              </button>
              <button onClick={() => handleQuickDemoFill('warden')} className="btn btn-secondary btn-sm" type="button">
                Warden
              </button>
              <button onClick={() => handleQuickDemoFill('student')} className="btn btn-secondary btn-sm" type="button">
                Student
              </button>
            </div>
          </div>

          <div style={{ marginTop: '24px', textAlign: 'center', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
            Don't have an account?{' '}
            <Link to="/register" style={{ color: 'var(--primary-600)', fontWeight: 700, textDecoration: 'none' }}>
              Create Student Account
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
