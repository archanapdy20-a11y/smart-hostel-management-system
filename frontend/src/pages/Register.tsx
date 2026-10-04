import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Building2, User, Phone, ArrowRight, ShieldCheck } from 'lucide-react';

export const Register: React.FC = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    rollNumber: '',
    department: 'Computer Science',
    academicYear: 1,
    gender: 'MALE' as const,
    guardianName: '',
    guardianPhone: '',
    address: ''
  });

  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');

    if (formData.password !== formData.confirmPassword) {
      setErrorMsg('Passwords do not match.');
      return;
    }

    if (!formData.username || !formData.email || !formData.password || !formData.rollNumber) {
      setErrorMsg('Please fill in all required fields marked with *.');
      return;
    }

    setLoading(true);
    try {
      await register({
        username: formData.username,
        email: formData.email,
        password: formData.password,
        role: 'ROLE_STUDENT',
        firstName: formData.firstName,
        lastName: formData.lastName,
        phoneNumber: formData.phoneNumber,
        rollNumber: formData.rollNumber,
        department: formData.department,
        academicYear: Number(formData.academicYear),
        gender: formData.gender,
        guardianName: formData.guardianName,
        guardianPhone: formData.guardianPhone,
        address: formData.address
      });
      navigate('/dashboard');
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Registration failed. Username or email may already exist.');
    } finally {
      setLoading(false);
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
        padding: '32px 24px'
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '900px',
          backgroundColor: 'var(--bg-surface)',
          borderRadius: 'var(--radius-xl)',
          border: '1px solid var(--border-default)',
          boxShadow: 'var(--shadow-xl)',
          overflow: 'hidden'
        }}
      >
        {/* Header Banner */}
        <div
          style={{
            backgroundColor: 'var(--primary-900)',
            color: '#FFFFFF',
            padding: '32px 40px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            backgroundImage: 'radial-gradient(circle at 90% 10%, rgba(99, 102, 241, 0.3) 0%, transparent 60%)'
          }}
        >
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
              <Building2 size={22} color="var(--primary-400)" />
              <span style={{ fontSize: '1.15rem', fontWeight: 800, color: '#FFFFFF' }}>SmartHostel SaaS</span>
            </div>
            <h1 style={{ fontSize: '1.5rem', fontWeight: 800, color: '#FFFFFF' }}>Student Registration Portal</h1>
            <p style={{ color: 'var(--primary-200)', fontSize: '0.875rem', marginTop: '4px' }}>
              Register your account to access room allocation, QR attendance, and leave management.
            </p>
          </div>
          <Link to="/login" className="btn btn-outline" style={{ color: '#FFFFFF', borderColor: 'rgba(255,255,255,0.3)' }}>
            Already registered? Sign In
          </Link>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} style={{ padding: '40px' }}>
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
                marginBottom: '24px'
              }}
            >
              {errorMsg}
            </div>
          )}

          {/* Section 1: Account Credentials */}
          <div style={{ marginBottom: '32px' }}>
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '16px', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <ShieldCheck size={18} />
              1. Account Credentials
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Username *</label>
                <input type="text" name="username" className="form-input" placeholder="e.g. alex_morgan" value={formData.username} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label className="form-label">Email Address *</label>
                <input type="email" name="email" className="form-input" placeholder="alex@campus.edu" value={formData.email} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label className="form-label">Password *</label>
                <input type="password" name="password" className="form-input" placeholder="••••••••" value={formData.password} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label className="form-label">Confirm Password *</label>
                <input type="password" name="confirmPassword" className="form-input" placeholder="••••••••" value={formData.confirmPassword} onChange={handleChange} required />
              </div>
            </div>
          </div>

          {/* Section 2: Student Academic Details */}
          <div style={{ marginBottom: '32px' }}>
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '16px', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <User size={18} />
              2. Student Academic Details
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">First Name</label>
                <input type="text" name="firstName" className="form-input" placeholder="Alex" value={formData.firstName} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Last Name</label>
                <input type="text" name="lastName" className="form-input" placeholder="Morgan" value={formData.lastName} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <input type="text" name="phoneNumber" className="form-input" placeholder="+1 999 888 777" value={formData.phoneNumber} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Roll Number / Student ID *</label>
                <input type="text" name="rollNumber" className="form-input" placeholder="STU-2024-001" value={formData.rollNumber} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label className="form-label">Department</label>
                <select name="department" className="form-input" value={formData.department} onChange={handleChange}>
                  <option value="Computer Science">Computer Science</option>
                  <option value="Electrical Engineering">Electrical Engineering</option>
                  <option value="Mechanical Engineering">Mechanical Engineering</option>
                  <option value="Information Technology">Information Technology</option>
                  <option value="Civil Engineering">Civil Engineering</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Academic Year</label>
                <select name="academicYear" className="form-input" value={formData.academicYear} onChange={handleChange}>
                  <option value={1}>1st Year (Freshman)</option>
                  <option value={2}>2nd Year (Sophomore)</option>
                  <option value={3}>3rd Year (Junior)</option>
                  <option value={4}>4th Year (Senior)</option>
                </select>
              </div>
            </div>
          </div>

          {/* Section 3: Guardian & Contact Details */}
          <div style={{ marginBottom: '32px' }}>
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '16px', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Phone size={18} />
              3. Guardian & Contact Info
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Gender</label>
                <select name="gender" className="form-input" value={formData.gender} onChange={handleChange}>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Guardian Name</label>
                <input type="text" name="guardianName" className="form-input" placeholder="David Morgan" value={formData.guardianName} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Guardian Phone</label>
                <input type="text" name="guardianPhone" className="form-input" placeholder="+1 987 654 321" value={formData.guardianPhone} onChange={handleChange} />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Permanent Address</label>
              <input type="text" name="address" className="form-input" placeholder="123 Tech Campus Road, Block B" value={formData.address} onChange={handleChange} />
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '16px' }}>
            <Link to="/login" className="btn btn-secondary">
              Cancel
            </Link>
            <button type="submit" className="btn btn-primary btn-lg" disabled={loading}>
              {loading ? 'Creating Account...' : 'Complete Registration'}
              <ArrowRight size={18} />
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
