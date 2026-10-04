import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { StatCard } from '../components/ui/StatCard';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { EmptyState } from '../components/ui/EmptyState';
import { api } from '../services/api';
import {
  AlertCircle,
  Plus,
  Search,
  CheckCircle2,
  Wrench,
  Flame
} from 'lucide-react';

interface Complaint {
  id: number;
  title: string;
  category: string;
  description: string;
  priority: 'EMERGENCY' | 'HIGH' | 'MEDIUM' | 'LOW';
  status: 'PENDING' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
  studentName: string;
  roomNumber: string;
  assignedWarden: string;
  createdAt: string;
}

export const ComplaintsModule: React.FC = () => {
  const { user } = useAuth();
  const role = user?.role || 'ROLE_STUDENT';

  const [complaints, setComplaints] = useState<Complaint[]>([]);
  const [selectedComplaint, setSelectedComplaint] = useState<Complaint | null>(null);

  const [filterPriority, setFilterPriority] = useState<string>('ALL');
  const [filterStatus, setFilterStatus] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');

  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [showDetailModal, setShowDetailModal] = useState<boolean>(false);

  // New Complaint Form
  const [newTitle, setNewTitle] = useState('');
  const [newCategory, setNewCategory] = useState('Plumbing');
  const [newDescription, setNewDescription] = useState('');
  const [calculatedPriority, setCalculatedPriority] = useState<'EMERGENCY' | 'HIGH' | 'MEDIUM' | 'LOW'>('LOW');

  useEffect(() => {
    fetchComplaints();
  }, []);

  // Auto-calculate priority based on description keywords
  useEffect(() => {
    const text = (newTitle + ' ' + newDescription).toLowerCase();
    if (text.includes('fire') || text.includes('spark') || text.includes('flood') || text.includes('gas') || text.includes('electric shock')) {
      setCalculatedPriority('EMERGENCY');
    } else if (text.includes('leak') || text.includes('broken lock') || text.includes('power cut') || text.includes('water')) {
      setCalculatedPriority('HIGH');
    } else if (text.includes('wifi') || text.includes('clean') || text.includes('fan')) {
      setCalculatedPriority('MEDIUM');
    } else {
      setCalculatedPriority('LOW');
    }
  }, [newTitle, newDescription]);

  const fetchComplaints = async () => {
    try {
      const res = await api.get('/complaints');
      if (res.data.data && res.data.data.length > 0) {
        setComplaints(res.data.data);
      } else {
        seedDemoComplaints();
      }
    } catch (err) {
      seedDemoComplaints();
    }
  };

  const seedDemoComplaints = () => {
    const demo: Complaint[] = [
      {
        id: 1,
        title: 'Major Water Leakage in Bathroom',
        category: 'Plumbing',
        description: 'Main pipe connector burst in Room A-204 bathroom. Water flooding floor.',
        priority: 'EMERGENCY',
        status: 'IN_PROGRESS',
        studentName: 'Alex Morgan',
        roomNumber: 'A-204',
        assignedWarden: 'Warden Vance',
        createdAt: '2026-10-04 08:30 AM'
      },
      {
        id: 2,
        title: 'Ceiling Fan Making Loud Noise',
        category: 'Electrical',
        description: 'Regulator is stuck on speed 5 and fan bearing is rattling loudly.',
        priority: 'HIGH',
        status: 'PENDING',
        studentName: 'Sophia Chen',
        roomNumber: 'B-101',
        assignedWarden: 'Warden Vance',
        createdAt: '2026-10-03 14:15 PM'
      },
      {
        id: 3,
        title: 'Wi-Fi Access Point Offline on Floor 3',
        category: 'IT & Internet',
        description: 'High ping and disconnections on Block A 3rd floor router.',
        priority: 'MEDIUM',
        status: 'RESOLVED',
        studentName: 'Jordan Hayes',
        roomNumber: 'A-308',
        assignedWarden: 'IT Helpdesk',
        createdAt: '2026-10-01 10:00 AM'
      }
    ];
    setComplaints(demo);
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/complaints', {
        title: newTitle,
        categoryId: 1,
        description: newDescription
      });
      fetchComplaints();
      setShowCreateModal(false);
      resetForm();
    } catch (err) {
      // Demo Add Fallback
      const newEntry: Complaint = {
        id: Date.now(),
        title: newTitle,
        category: newCategory,
        description: newDescription,
        priority: calculatedPriority,
        status: 'PENDING',
        studentName: user?.username || 'Alex Morgan',
        roomNumber: 'A-204',
        assignedWarden: 'Warden Vance',
        createdAt: 'Just now'
      };
      setComplaints([newEntry, ...complaints]);
      setShowCreateModal(false);
      resetForm();
    }
  };

  const resetForm = () => {
    setNewTitle('');
    setNewDescription('');
    setNewCategory('Plumbing');
  };

  const handleUpdateStatus = async (id: number, newStatus: Complaint['status']) => {
    try {
      await api.patch(`/complaints/${id}/status?status=${newStatus}`);
      fetchComplaints();
      if (selectedComplaint) setSelectedComplaint({ ...selectedComplaint, status: newStatus });
    } catch (err) {
      setComplaints(prev => prev.map(c => c.id === id ? { ...c, status: newStatus } : c));
      if (selectedComplaint) setSelectedComplaint({ ...selectedComplaint, status: newStatus });
    }
  };

  const getPriorityBadgeVariant = (p: string) => {
    switch (p) {
      case 'EMERGENCY': return 'danger';
      case 'HIGH': return 'orange';
      case 'MEDIUM': return 'warning';
      default: return 'success';
    }
  };

  const filteredComplaints = complaints.filter(c => {
    const matchesSearch = c.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          c.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          c.roomNumber.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesPriority = filterPriority === 'ALL' || c.priority === filterPriority;
    const matchesStatus = filterStatus === 'ALL' || c.status === filterStatus;
    return matchesSearch && matchesPriority && matchesStatus;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Header Banner */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Complaint & Incident Helpdesk</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Intelligent priority triage and maintenance lifecycle tracking
          </p>
        </div>

        <button onClick={() => setShowCreateModal(true)} className="btn btn-primary">
          <Plus size={18} /> Report New Issue
        </button>
      </div>

      {/* KPI Stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard title="Total Complaints" value={complaints.length} subtitle="Active queue records" icon={<AlertCircle size={22} />} accentColor="var(--primary-600)" />
        <StatCard title="Emergency Level" value={complaints.filter(c => c.priority === 'EMERGENCY').length} subtitle="Immediate action required" icon={<Flame size={22} />} accentColor="var(--danger)" />
        <StatCard title="In Progress" value={complaints.filter(c => c.status === 'IN_PROGRESS').length} subtitle="Assigned technician working" icon={<Wrench size={22} />} accentColor="var(--warning)" />
        <StatCard title="Resolved Issues" value={complaints.filter(c => c.status === 'RESOLVED').length} subtitle="Completed maintenance" icon={<CheckCircle2 size={22} />} accentColor="var(--success)" />
      </div>

      {/* Filter Controls Header */}
      <div className="saas-card" style={{ padding: '16px 20px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '16px' }}>
          <div style={{ position: 'relative' }}>
            <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
            <input
              type="text"
              className="form-input"
              placeholder="Search by issue title, description, or room..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              style={{ paddingLeft: '36px' }}
            />
          </div>

          <select className="form-input" value={filterPriority} onChange={e => setFilterPriority(e.target.value)}>
            <option value="ALL">All Priorities</option>
            <option value="EMERGENCY">EMERGENCY</option>
            <option value="HIGH">HIGH</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="LOW">LOW</option>
          </select>

          <select className="form-input" value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
            <option value="ALL">All Statuses</option>
            <option value="PENDING">PENDING</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="RESOLVED">RESOLVED</option>
          </select>
        </div>
      </div>

      {/* Complaints Table */}
      <div className="table-container">
        <table className="saas-table">
          <thead>
            <tr>
              <th>Issue Title</th>
              <th>Category</th>
              <th>Room & Resident</th>
              <th>Priority Level</th>
              <th>Lifecycle Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredComplaints.length > 0 ? (
              filteredComplaints.map(item => (
                <tr key={item.id}>
                  <td>
                    <span style={{ fontWeight: 700, color: 'var(--text-main)', display: 'block' }}>{item.title}</span>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{item.createdAt}</span>
                  </td>
                  <td>
                    <Badge variant="neutral">{item.category}</Badge>
                  </td>
                  <td>
                    <span style={{ fontWeight: 600, display: 'block' }}>Room {item.roomNumber}</span>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{item.studentName}</span>
                  </td>
                  <td>
                    <Badge variant={getPriorityBadgeVariant(item.priority)}>
                      {item.priority}
                    </Badge>
                  </td>
                  <td>
                    <Badge variant={item.status === 'RESOLVED' ? 'success' : item.status === 'IN_PROGRESS' ? 'warning' : 'neutral'}>
                      {item.status}
                    </Badge>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <button
                      onClick={() => { setSelectedComplaint(item); setShowDetailModal(true); }}
                      className="btn btn-outline btn-sm"
                    >
                      View Details
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={6} style={{ padding: '0' }}>
                  <EmptyState
                    title="No Complaints Logged"
                    description="No complaint records match your current filter settings."
                    actionLabel="Report New Issue"
                    onAction={() => setShowCreateModal(true)}
                  />
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Create Complaint Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="Report Campus / Room Issue"
        subtitle="Submit a maintenance complaint ticket with auto-calculated priority"
      >
        <form onSubmit={handleCreateSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Issue Category *</label>
            <select className="form-input" value={newCategory} onChange={e => setNewCategory(e.target.value)}>
              <option value="Plumbing">Plumbing & Water</option>
              <option value="Electrical">Electrical & Appliances</option>
              <option value="Carpentry">Furniture & Doors</option>
              <option value="IT & Internet">Wi-Fi & Network</option>
              <option value="Cleanliness">Cleanliness & Sanitation</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Issue Title *</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. Water leak under sink in Room A-204"
              value={newTitle}
              onChange={e => setNewTitle(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Detailed Description *</label>
            <textarea
              className="form-input"
              placeholder="Describe the problem, exact location, and urgency..."
              value={newDescription}
              onChange={e => setNewDescription(e.target.value)}
              required
            />
          </div>

          {/* Auto-Calculated Priority Preview Banner */}
          <div
            style={{
              padding: '12px 16px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-subtle)',
              border: '1px solid var(--border-default)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}
          >
            <span style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
              Auto-Calculated Priority:
            </span>
            <Badge variant={getPriorityBadgeVariant(calculatedPriority)}>
              {calculatedPriority}
            </Badge>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
            <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">Cancel</button>
            <button type="submit" className="btn btn-primary">Submit Ticket</button>
          </div>
        </form>
      </Modal>

      {/* Complaint Detail & Status Update Modal */}
      {selectedComplaint && (
        <Modal
          isOpen={showDetailModal}
          onClose={() => setShowDetailModal(false)}
          title={`Ticket #${selectedComplaint.id}: ${selectedComplaint.title}`}
          subtitle={`Reported by ${selectedComplaint.studentName} (Room ${selectedComplaint.roomNumber})`}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <Badge variant={getPriorityBadgeVariant(selectedComplaint.priority)}>
                {selectedComplaint.priority} PRIORITY
              </Badge>
              <span style={{ fontSize: '0.8125rem', color: 'var(--text-muted)' }}>{selectedComplaint.createdAt}</span>
            </div>

            <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)' }}>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 700, textTransform: 'uppercase' }}>DESCRIPTION</span>
              <p style={{ marginTop: '4px', fontSize: '0.9375rem', color: 'var(--text-main)' }}>{selectedComplaint.description}</p>
            </div>

            {/* Lifecycle Status Action Bar for Wardens/Admin */}
            {role !== 'ROLE_STUDENT' && (
              <div style={{ paddingTop: '16px', borderTop: '1px solid var(--border-subtle)' }}>
                <span style={{ fontSize: '0.8125rem', fontWeight: 700, display: 'block', marginBottom: '10px' }}>
                  Update Complaint Status:
                </span>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button onClick={() => handleUpdateStatus(selectedComplaint.id, 'IN_PROGRESS')} className="btn btn-secondary btn-sm">
                    Set In Progress
                  </button>
                  <button onClick={() => handleUpdateStatus(selectedComplaint.id, 'RESOLVED')} className="btn btn-primary btn-sm">
                    Mark Resolved
                  </button>
                </div>
              </div>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
};
