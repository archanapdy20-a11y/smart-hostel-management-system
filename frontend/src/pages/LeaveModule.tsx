import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { StatCard } from '../components/ui/StatCard';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { EmptyState } from '../components/ui/EmptyState';
import { api } from '../services/api';
import {
  CalendarDays,
  Plus,
  CheckCircle2,
  XCircle,
  Clock
} from 'lucide-react';

interface LeaveRequest {
  id: number;
  studentName: string;
  rollNumber: string;
  department: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  appliedAt: string;
}

export const LeaveModule: React.FC = () => {
  const { user } = useAuth();
  const role = user?.role || 'ROLE_STUDENT';

  const [leaves, setLeaves] = useState<LeaveRequest[]>([]);
  const [showApplyModal, setShowApplyModal] = useState(false);

  // Apply Form
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [reason, setReason] = useState('');

  useEffect(() => {
    fetchLeaves();
  }, []);

  const fetchLeaves = async () => {
    try {
      const res = await api.get('/leaves');
      if (res.data.data && res.data.data.length > 0) {
        setLeaves(res.data.data);
      } else {
        seedDemoLeaves();
      }
    } catch (err) {
      seedDemoLeaves();
    }
  };

  const seedDemoLeaves = () => {
    const demo: LeaveRequest[] = [
      {
        id: 1,
        studentName: 'Alex Morgan',
        rollNumber: 'STU-2024-001',
        department: 'Computer Science',
        startDate: '2026-10-10',
        endDate: '2026-10-15',
        reason: 'Attending elder sister marriage ceremony in hometown.',
        status: 'PENDING',
        appliedAt: '2026-10-04 09:00 AM'
      },
      {
        id: 2,
        studentName: 'Sophia Chen',
        rollNumber: 'STU-2024-003',
        department: 'Computer Science',
        startDate: '2026-10-02',
        endDate: '2026-10-03',
        reason: 'Medical checkup and dental appointment.',
        status: 'APPROVED',
        appliedAt: '2026-10-01 11:30 AM'
      }
    ];
    setLeaves(demo);
  };

  const handleApplySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/leaves', { startDate, endDate, reason });
      fetchLeaves();
      setShowApplyModal(false);
      resetForm();
    } catch (err) {
      const newEntry: LeaveRequest = {
        id: Date.now(),
        studentName: user?.username || 'Alex Morgan',
        rollNumber: 'STU-2024-001',
        department: 'Computer Science',
        startDate,
        endDate,
        reason,
        status: 'PENDING',
        appliedAt: 'Just now'
      };
      setLeaves([newEntry, ...leaves]);
      setShowApplyModal(false);
      resetForm();
    }
  };

  const resetForm = () => {
    setStartDate('');
    setEndDate('');
    setReason('');
  };

  const handleUpdateLeaveStatus = async (id: number, status: 'APPROVED' | 'REJECTED') => {
    try {
      await api.patch(`/leaves/${id}/approve?status=${status}`);
      fetchLeaves();
    } catch (err) {
      setLeaves(prev => prev.map(l => l.id === id ? { ...l, status } : l));
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Header Banner */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Leave & Outing Approval Workflow</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Automated campus leave applications, warden approvals, and student outing history
          </p>
        </div>

        {role === 'ROLE_STUDENT' && (
          <button onClick={() => setShowApplyModal(true)} className="btn btn-primary">
            <Plus size={18} /> Apply for Outing / Leave
          </button>
        )}
      </div>

      {/* KPI Stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard title="Total Applications" value={leaves.length} subtitle="Campus leave requests" icon={<CalendarDays size={22} />} accentColor="var(--primary-600)" />
        <StatCard title="Pending Review" value={leaves.filter(l => l.status === 'PENDING').length} subtitle="Awaiting warden action" icon={<Clock size={22} />} accentColor="var(--warning)" />
        <StatCard title="Approved Leaves" value={leaves.filter(l => l.status === 'APPROVED').length} subtitle="Cleared for outing" icon={<CheckCircle2 size={22} />} accentColor="var(--success)" />
      </div>

      {/* Leave Table */}
      <div className="table-container">
        <table className="saas-table">
          <thead>
            <tr>
              <th>Student Info</th>
              <th>Leave Dates</th>
              <th>Reason</th>
              <th>Applied Date</th>
              <th>Status</th>
              {role !== 'ROLE_STUDENT' && <th style={{ textAlign: 'right' }}>Warden Actions</th>}
            </tr>
          </thead>
          <tbody>
            {leaves.length > 0 ? (
              leaves.map(item => (
                <tr key={item.id}>
                  <td>
                    <span style={{ fontWeight: 700, display: 'block' }}>{item.studentName}</span>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{item.rollNumber} • {item.department}</span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 600, display: 'block' }}>{item.startDate} to {item.endDate}</span>
                  </td>
                  <td style={{ maxWidth: '280px' }}>
                    <span style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>{item.reason}</span>
                  </td>
                  <td>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{item.appliedAt}</span>
                  </td>
                  <td>
                    <Badge variant={item.status === 'APPROVED' ? 'success' : item.status === 'PENDING' ? 'warning' : 'danger'}>
                      {item.status}
                    </Badge>
                  </td>
                  {role !== 'ROLE_STUDENT' && (
                    <td style={{ textAlign: 'right' }}>
                      {item.status === 'PENDING' ? (
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '8px' }}>
                          <button onClick={() => handleUpdateLeaveStatus(item.id, 'APPROVED')} className="btn btn-primary btn-sm">
                            <CheckCircle2 size={14} /> Approve
                          </button>
                          <button onClick={() => handleUpdateLeaveStatus(item.id, 'REJECTED')} className="btn btn-danger btn-sm">
                            <XCircle size={14} /> Reject
                          </button>
                        </div>
                      ) : (
                        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Actioned</span>
                      )}
                    </td>
                  )}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={6} style={{ padding: 0 }}>
                  <EmptyState
                    title="No Leave Applications Found"
                    description="No leave records exist for your account."
                    actionLabel={role === 'ROLE_STUDENT' ? 'Apply for Leave' : undefined}
                    onAction={() => setShowApplyModal(true)}
                  />
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Apply Leave Modal */}
      <Modal
        isOpen={showApplyModal}
        onClose={() => setShowApplyModal(false)}
        title="Apply for Hostel Outing / Leave"
        subtitle="Submit dates and reason for warden approval"
      >
        <form onSubmit={handleApplySubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Start Date *</label>
              <input type="date" className="form-input" value={startDate} onChange={e => setStartDate(e.target.value)} required />
            </div>
            <div className="form-group">
              <label className="form-label">End Date *</label>
              <input type="date" className="form-input" value={endDate} onChange={e => setEndDate(e.target.value)} required />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Reason for Leave / Outing *</label>
            <textarea
              className="form-input"
              placeholder="State your reason for leave, destination address, and emergency contact..."
              value={reason}
              onChange={e => setReason(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
            <button type="button" onClick={() => setShowApplyModal(false)} className="btn btn-secondary">Cancel</button>
            <button type="submit" className="btn btn-primary">Submit Application</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
