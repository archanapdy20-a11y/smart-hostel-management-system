import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { StatCard } from '../components/ui/StatCard';
import { Badge } from '../components/ui/Badge';
import { useNavigate } from 'react-router-dom';
import {
  Users,
  Building2,
  Bed,
  CheckCircle2,
  AlertCircle,
  Sparkles,
  QrCode,
  CalendarDays,
  CreditCard,
  ChevronRight,
  ShieldCheck,
  HelpCircle,
  FileText
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import { api } from '../services/api';

export const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const role = user?.role || 'ROLE_STUDENT';

  const [summaryData, setSummaryData] = useState<any>(null);
  const [, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardSummary();
  }, []);

  const fetchDashboardSummary = async () => {
    setLoading(true);
    try {
      const res = await api.get('/analytics/summary');
      setSummaryData(res.data.data);
    } catch (err) {
      // Demo Fallback Data for rich presentation
      setSummaryData({
        totalStudents: 342,
        totalHostels: 4,
        totalBeds: 400,
        occupiedBeds: 342,
        availableBeds: 58,
        occupancyPercentage: 85.5,
        pendingComplaints: 6,
        emergencyComplaints: 1,
        pendingLeaveRequests: 4,
        complaintsByPriority: { EMERGENCY: 1, HIGH: 2, MEDIUM: 2, LOW: 1 }
      });
    } finally {
      setLoading(false);
    }
  };

  // Chart Mock Data
  const hostelOccupancyData = [
    { name: 'Einstein Block A', Occupied: 95, Total: 100 },
    { name: 'Curie Block B', Occupied: 88, Total: 100 },
    { name: 'Tesla Block C', Occupied: 79, Total: 100 },
    { name: 'Kalam Block D', Occupied: 80, Total: 100 },
  ];

  const complaintPriorityPieData = [
    { name: 'Emergency', value: summaryData?.complaintsByPriority?.EMERGENCY || 1, color: '#EF4444' },
    { name: 'High', value: summaryData?.complaintsByPriority?.HIGH || 2, color: '#F97316' },
    { name: 'Medium', value: summaryData?.complaintsByPriority?.MEDIUM || 2, color: '#F59E0B' },
    { name: 'Low', value: summaryData?.complaintsByPriority?.LOW || 1, color: '#10B981' },
  ];

  // ================= ADMIN DASHBOARD =================
  const renderAdminDashboard = () => (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Top Banner Quick Greeting & Smart Allocation CTA */}
      <div
        style={{
          background: 'linear-gradient(135deg, var(--primary-900) 0%, var(--primary-800) 100%)',
          borderRadius: 'var(--radius-xl)',
          padding: '28px 32px',
          color: '#FFFFFF',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: 'var(--shadow-md)',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--primary-200)', backgroundColor: 'rgba(255,255,255,0.1)', padding: '3px 10px', borderRadius: 'var(--radius-full)' }}>
              Enterprise Admin Suite
            </span>
          </div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800, color: '#FFFFFF', marginBottom: '6px' }}>
            Welcome back, {user?.username || 'Administrator'} 👋
          </h1>
          <p style={{ color: 'var(--primary-100)', fontSize: '0.9375rem', maxWidth: '600px' }}>
            Campus hostel capacity is currently at <strong>{summaryData?.occupancyPercentage || 85.5}%</strong>. You have <strong>{summaryData?.pendingLeaveRequests || 4} pending leave approvals</strong> and <strong>{summaryData?.pendingComplaints || 6} active issues</strong> requiring attention.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px' }}>
          <button onClick={() => navigate('/smart-allocation')} className="btn btn-primary btn-lg" style={{ backgroundColor: '#FFFFFF', color: 'var(--primary-900)' }}>
            <Sparkles size={18} color="var(--primary-600)" />
            Run Smart Allocation
          </button>
        </div>
      </div>

      {/* KPI Metric Cards Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard
          title="Total Residents"
          value={summaryData?.totalStudents || 342}
          subtitle="Registered active students"
          icon={<Users size={22} />}
          accentColor="var(--primary-600)"
          trend={{ value: '+4.2% vs last month', isPositive: true }}
        />
        <StatCard
          title="Hostel Blocks"
          value={summaryData?.totalHostels || 4}
          subtitle="Active campus blocks"
          icon={<Building2 size={22} />}
          accentColor="var(--info)"
        />
        <StatCard
          title="Occupied Beds"
          value={summaryData?.occupiedBeds || 342}
          subtitle={`Out of ${summaryData?.totalBeds || 400} total capacity`}
          icon={<Bed size={22} />}
          accentColor="var(--success)"
        />
        <StatCard
          title="Available Beds"
          value={summaryData?.availableBeds || 58}
          subtitle="Ready for allocation"
          icon={<CheckCircle2 size={22} />}
          accentColor="var(--warning)"
        />
        <StatCard
          title="Pending Complaints"
          value={summaryData?.pendingComplaints || 6}
          subtitle={`${summaryData?.emergencyComplaints || 1} emergency level`}
          icon={<AlertCircle size={22} />}
          accentColor="var(--danger)"
        />
      </div>

      {/* Visual Analytics Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        {/* Hostel Occupancy Bar Chart */}
        <div className="saas-card">
          <div className="saas-card-header">
            <div>
              <h3 className="saas-card-title">Hostel Capacity & Occupancy Breakdown</h3>
              <p style={{ fontSize: '0.8125rem', marginTop: '2px' }}>Occupied beds vs available allocation limits by hostel block</p>
            </div>
            <button onClick={() => navigate('/hostels')} className="btn btn-ghost btn-sm">
              View Hostels <ChevronRight size={14} />
            </button>
          </div>
          <div style={{ width: '100%', height: 280 }}>
            <ResponsiveContainer>
              <BarChart data={hostelOccupancyData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-subtle)" />
                <XAxis dataKey="name" stroke="var(--text-muted)" fontSize={12} />
                <YAxis stroke="var(--text-muted)" fontSize={12} />
                <Tooltip contentStyle={{ backgroundColor: 'var(--bg-surface)', borderColor: 'var(--border-default)', borderRadius: 'var(--radius-md)' }} />
                <Bar dataKey="Occupied" fill="var(--primary-600)" radius={[4, 4, 0, 0]} />
                <Bar dataKey="Total" fill="var(--primary-100)" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Complaint Priority Pie Breakdown */}
        <div className="saas-card">
          <div className="saas-card-header">
            <div>
              <h3 className="saas-card-title">Complaint Priority Breakdown</h3>
              <p style={{ fontSize: '0.8125rem', marginTop: '2px' }}>Categorized issue severity queue</p>
            </div>
          </div>
          <div style={{ width: '100%', height: 220, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <ResponsiveContainer>
              <PieChart>
                <Pie data={complaintPriorityPieData} innerRadius={55} outerRadius={85} paddingAngle={4} dataKey="value">
                  {complaintPriorityPieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip contentStyle={{ backgroundColor: 'var(--bg-surface)', borderColor: 'var(--border-default)', borderRadius: 'var(--radius-md)' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginTop: '12px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78125rem' }}>
              <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#EF4444' }} /> Emergency (1)
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78125rem' }}>
              <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#F97316' }} /> High Priority (2)
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78125rem' }}>
              <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#F59E0B' }} /> Medium (2)
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78125rem' }}>
              <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#10B981' }} /> Low Priority (1)
            </div>
          </div>
        </div>
      </div>

      {/* Urgent Action Queue & Activity Table */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
        {/* Pending Action Requests */}
        <div className="saas-card">
          <div className="saas-card-header">
            <div>
              <h3 className="saas-card-title">Pending Action Items</h3>
              <p style={{ fontSize: '0.8125rem', marginTop: '2px' }}>Requests waiting for administrative review</p>
            </div>
            <Badge variant="warning">{summaryData?.pendingLeaveRequests || 4} Pending</Badge>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ padding: '14px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <CalendarDays size={20} color="var(--primary-600)" />
                <div>
                  <span style={{ fontSize: '0.875rem', fontWeight: 700, display: 'block' }}>Alex Morgan — Leave Request</span>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Oct 10 to Oct 15 • Reason: Family Visit</span>
                </div>
              </div>
              <button onClick={() => navigate('/leaves')} className="btn btn-outline btn-sm">Review</button>
            </div>

            <div style={{ padding: '14px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <AlertCircle size={20} color="var(--danger)" />
                <div>
                  <span style={{ fontSize: '0.875rem', fontWeight: 700, display: 'block' }}>Room 204 — Water Leak Complaint</span>
                  <span style={{ fontSize: '0.75rem', color: 'var(--danger-text)', fontWeight: 600 }}>Emergency Priority • Assigned to Warden Vance</span>
                </div>
              </div>
              <button onClick={() => navigate('/complaints')} className="btn btn-outline btn-sm">Resolve</button>
            </div>
          </div>
        </div>

        {/* Recent Activity Log */}
        <div className="saas-card">
          <div className="saas-card-header">
            <div>
              <h3 className="saas-card-title">Recent Audit Log</h3>
              <p style={{ fontSize: '0.8125rem', marginTop: '2px' }}>Latest campus system events</p>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px', fontSize: '0.8125rem' }}>
              <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: 'var(--success)', marginTop: '6px' }} />
              <div>
                <span style={{ fontWeight: 700, color: 'var(--text-main)' }}>Student Check-in Recorded</span>
                <p style={{ fontSize: '0.75rem' }}>Alex Morgan scanned QR at Block A canvas. Marked PRESENT.</p>
                <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)' }}>10 minutes ago</span>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px', fontSize: '0.8125rem' }}>
              <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: 'var(--primary-600)', marginTop: '6px' }} />
              <div>
                <span style={{ fontWeight: 700, color: 'var(--text-main)' }}>Room Allocation Completed</span>
                <p style={{ fontSize: '0.75rem' }}>Smart allocation assigned Student STU-2024-002 to Bed 3, Room 304.</p>
                <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)' }}>45 minutes ago</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  // ================= WARDEN DASHBOARD =================
  const renderWardenDashboard = () => (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      <div
        style={{
          background: 'linear-gradient(135deg, #1E293B 0%, #0F172A 100%)',
          borderRadius: 'var(--radius-xl)',
          padding: '28px 32px',
          color: '#FFFFFF',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: 'var(--shadow-md)'
        }}
      >
        <div>
          <span style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--primary-300)', backgroundColor: 'rgba(255,255,255,0.1)', padding: '3px 10px', borderRadius: 'var(--radius-full)' }}>
            Warden Control Center
          </span>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800, color: '#FFFFFF', marginTop: '6px' }}>
            Welcome, Warden {user?.username || 'Vance'}
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9375rem', marginTop: '4px' }}>
            Assigned Block: <strong>Einstein Block A (Rooms 101 - 412)</strong> • Total Residents: 95 Students
          </p>
        </div>

        <button onClick={() => navigate('/attendance')} className="btn btn-primary btn-lg">
          <QrCode size={18} />
          Generate Live Block QR
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard title="Block Residents" value={95} subtitle="Einstein Block A" icon={<Users size={22} />} accentColor="var(--primary-600)" />
        <StatCard title="Today's Attendance" value="96.8%" subtitle="92 Present / 3 Absent" icon={<CheckCircle2 size={22} />} accentColor="var(--success)" />
        <StatCard title="Pending Leaves" value={4} subtitle="Awaiting review" icon={<CalendarDays size={22} />} accentColor="var(--warning)" />
        <StatCard title="Active Complaints" value={2} subtitle="1 Emergency priority" icon={<AlertCircle size={22} />} accentColor="var(--danger)" />
      </div>

      {/* Action Sections */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
        <div className="saas-card">
          <div className="saas-card-header">
            <h3 className="saas-card-title">Leave Request Review</h3>
            <button onClick={() => navigate('/leaves')} className="btn btn-ghost btn-sm">View All</button>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ padding: '14px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <span style={{ fontSize: '0.875rem', fontWeight: 700, display: 'block' }}>Alex Morgan (CSE - Yr 3)</span>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Oct 10 - Oct 15 • Reason: Medical Checkup</span>
              </div>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button onClick={() => navigate('/leaves')} className="btn btn-primary btn-sm">Approve</button>
              </div>
            </div>
          </div>
        </div>

        <div className="saas-card">
          <div className="saas-card-header">
            <h3 className="saas-card-title">Assigned Complaints</h3>
            <button onClick={() => navigate('/complaints')} className="btn btn-ghost btn-sm">View All</button>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ padding: '14px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <span style={{ fontSize: '0.875rem', fontWeight: 700, display: 'block' }}>Room A-204 — Water Leakage</span>
                <Badge variant="danger">EMERGENCY</Badge>
              </div>
              <button onClick={() => navigate('/complaints')} className="btn btn-outline btn-sm">Update Status</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  // ================= STUDENT DASHBOARD =================
  const renderStudentDashboard = () => (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Student Welcome & Room Info Hero */}
      <div
        style={{
          backgroundColor: 'var(--bg-surface)',
          borderRadius: 'var(--radius-xl)',
          padding: '28px 32px',
          border: '1px solid var(--border-default)',
          boxShadow: 'var(--shadow-sm)',
          display: 'grid',
          gridTemplateColumns: '1.5fr 1fr',
          gap: '24px'
        }}
      >
        <div>
          <span style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--primary-600)', backgroundColor: 'var(--primary-50)', padding: '4px 10px', borderRadius: 'var(--radius-full)' }}>
            Student Resident Portal
          </span>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800, marginTop: '8px' }}>
            Welcome, Alex Morgan 👋
          </h1>
          <p style={{ fontSize: '0.875rem', marginTop: '4px' }}>
            Roll No: <strong>STU-2024-001</strong> • Department: <strong>Computer Science (Year 3)</strong>
          </p>

          <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
            <button onClick={() => navigate('/attendance')} className="btn btn-primary">
              <QrCode size={16} />
              Scan Attendance QR
            </button>
            <button onClick={() => navigate('/leaves')} className="btn btn-secondary">
              <CalendarDays size={16} />
              Apply Leave
            </button>
            <button onClick={() => navigate('/complaints')} className="btn btn-secondary">
              <AlertCircle size={16} />
              Report Issue
            </button>
          </div>
        </div>

        {/* My Hostel Room Card */}
        <div
          style={{
            backgroundColor: 'var(--primary-900)',
            color: '#FFFFFF',
            borderRadius: 'var(--radius-lg)',
            padding: '20px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.15)', paddingBottom: '10px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: 'var(--primary-300)' }}>My Allocation</span>
            <Badge variant="success">ACTIVE RESIDENT</Badge>
          </div>

          <div style={{ margin: '14px 0' }}>
            <span style={{ fontSize: '1.35rem', fontWeight: 800, display: 'block', color: '#FFFFFF' }}>
              Einstein Block A • Room 204
            </span>
            <span style={{ fontSize: '0.875rem', color: 'var(--primary-200)' }}>
              Bed #2 (Window Side) • 4-Bed Shared Room
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.8125rem', color: 'var(--primary-200)' }}>
            <Users size={15} /> Roommates: David K., Samuel P., Ryan T.
          </div>
        </div>
      </div>

      {/* Student Status Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard title="Monthly Attendance" value="96.5%" subtitle="28 Present / 1 Absent" icon={<CheckCircle2 size={22} />} accentColor="var(--success)" />
        <StatCard title="Active Leave Status" value="Approved" subtitle="Oct 10 - Oct 15" icon={<CalendarDays size={22} />} accentColor="var(--info)" />
        <StatCard title="Logged Complaints" value="1 Active" subtitle="In Progress (Plumbing)" icon={<AlertCircle size={22} />} accentColor="var(--warning)" />
        <StatCard title="Fee Status" value="PAID" subtitle="Semester 1 Cleared" icon={<CreditCard size={22} />} accentColor="var(--primary-600)" />
      </div>

      {/* Notice Board Feed & Recent Complaints */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        <div className="saas-card">
          <div className="saas-card-header">
            <h3 className="saas-card-title">Campus Announcements</h3>
            <button onClick={() => navigate('/notices')} className="btn btn-ghost btn-sm">View Notice Board</button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '6px' }}>
                <span style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--text-main)' }}>📢 Welcome to Smart Hostel System</span>
                <Badge variant="info">GENERAL</Badge>
              </div>
              <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                All residents are advised to register their attendance using the QR code scanner in their respective blocks every evening between 8:00 PM and 10:00 PM.
              </p>
              <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)', marginTop: '8px', display: 'block' }}>Published by Chief Warden • Today at 09:00 AM</span>
            </div>

            <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', border: '1px solid var(--border-default)' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '6px' }}>
                <span style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--text-main)' }}>⚡ Scheduled Electrical Maintenance</span>
                <Badge variant="warning">MAINTENANCE</Badge>
              </div>
              <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                Power supply will be interrupted tomorrow from 10:00 AM to 1:00 PM for transformer inspection.
              </p>
              <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)', marginTop: '8px', display: 'block' }}>Published by Estate Office • Yesterday</span>
            </div>
          </div>
        </div>

        {/* Quick Links / Help */}
        <div className="saas-card">
          <div className="saas-card-header">
            <h3 className="saas-card-title">Resident Support</h3>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <button onClick={() => navigate('/complaints')} className="btn btn-secondary" style={{ justifyContent: 'flex-start' }}>
              <HelpCircle size={16} /> Helpdesk & Complaints
            </button>
            <button onClick={() => navigate('/leaves')} className="btn btn-secondary" style={{ justifyContent: 'flex-start' }}>
              <FileText size={16} /> Outing Pass Rules
            </button>
            <button onClick={() => navigate('/settings')} className="btn btn-secondary" style={{ justifyContent: 'flex-start' }}>
              <ShieldCheck size={16} /> Update Guardian Contacts
            </button>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="dashboard-wrapper">
      {role === 'ROLE_ADMIN' && renderAdminDashboard()}
      {role === 'ROLE_WARDEN' && renderWardenDashboard()}
      {role === 'ROLE_STUDENT' && renderStudentDashboard()}
    </div>
  );
};
