import React, { useState, useEffect } from 'react';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { StatCard } from '../components/ui/StatCard';
import { EmptyState } from '../components/ui/EmptyState';
import { api } from '../services/api';
import {
  Users,
  Search,
  Plus,
  Eye,
  Building2,
  UserCheck
} from 'lucide-react';

interface Student {
  id: number;
  rollNumber: string;
  name: string;
  email: string;
  department: string;
  academicYear: number;
  gender: string;
  guardianName: string;
  guardianPhone: string;
  address: string;
  roomAllocation?: string;
  status: 'ALLOCATED' | 'UNALLOCATED';
}

export const StudentManagement: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');
  const [yearFilter, setYearFilter] = useState('ALL');
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);

  const [showProfileModal, setShowProfileModal] = useState(false);
  const [, setShowAddStudentModal] = useState(false);
  const [activeTab, setActiveTab] = useState<'overview' | 'academic' | 'guardian' | 'allocation'>('overview');

  useEffect(() => {
    fetchStudents();
  }, []);

  const fetchStudents = async () => {
    try {
      const res = await api.get('/students');
      if (res.data.data && res.data.data.length > 0) {
        setStudents(res.data.data);
      } else {
        seedDemoStudents();
      }
    } catch (err) {
      seedDemoStudents();
    }
  };

  const seedDemoStudents = () => {
    const demo: Student[] = [
      {
        id: 1,
        rollNumber: 'STU-2024-001',
        name: 'Alex Morgan',
        email: 'alex.morgan@campus.edu',
        department: 'Computer Science',
        academicYear: 3,
        gender: 'MALE',
        guardianName: 'David Morgan',
        guardianPhone: '+1 987 654 3210',
        address: '123 Tech Campus Road, Block B',
        roomAllocation: 'Einstein Block A — Room 204 (Bed 2)',
        status: 'ALLOCATED'
      },
      {
        id: 2,
        rollNumber: 'STU-2024-002',
        name: 'Jordan Hayes',
        email: 'jordan.hayes@campus.edu',
        department: 'Electrical Engineering',
        academicYear: 2,
        gender: 'MALE',
        guardianName: 'Robert Hayes',
        guardianPhone: '+1 987 654 3211',
        address: '456 Innovation Blvd, Suite 4',
        roomAllocation: undefined,
        status: 'UNALLOCATED'
      },
      {
        id: 3,
        rollNumber: 'STU-2024-003',
        name: 'Sophia Chen',
        email: 'sophia.chen@campus.edu',
        department: 'Computer Science',
        academicYear: 1,
        gender: 'FEMALE',
        guardianName: 'Wei Chen',
        guardianPhone: '+1 987 654 3212',
        address: '789 Science Park Lane',
        roomAllocation: 'Curie Block B — Room 101 (Bed 1)',
        status: 'ALLOCATED'
      }
    ];
    setStudents(demo);
  };

  const filteredStudents = students.filter(s => {
    const matchesSearch = s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          s.rollNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          s.email.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesDept = departmentFilter === 'ALL' || s.department === departmentFilter;
    const matchesYear = yearFilter === 'ALL' || String(s.academicYear) === yearFilter;
    return matchesSearch && matchesDept && matchesYear;
  });

  const totalStudents = students.length;
  const allocatedCount = students.filter(s => s.status === 'ALLOCATED').length;
  const unallocatedCount = students.filter(s => s.status === 'UNALLOCATED').length;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Top Banner */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Student Directory & Records</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Comprehensive directory of registered hostel residents, academic details, and room assignments
          </p>
        </div>

        <button onClick={() => setShowAddStudentModal(true)} className="btn btn-primary">
          <Plus size={18} /> Register New Student
        </button>
      </div>

      {/* KPI Stats Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard title="Total Registered" value={totalStudents} subtitle="Active campus accounts" icon={<Users size={22} />} accentColor="var(--primary-600)" />
        <StatCard title="Room Allocated" value={allocatedCount} subtitle="Assigned to hostel room" icon={<UserCheck size={22} />} accentColor="var(--success)" />
        <StatCard title="Unallocated Residents" value={unallocatedCount} subtitle="Pending room placement" icon={<Building2 size={22} />} accentColor="var(--warning)" />
      </div>

      {/* Table Filter Controls Header */}
      <div className="saas-card" style={{ padding: '16px 20px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '16px' }}>
          {/* Search Box */}
          <div style={{ position: 'relative' }}>
            <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
            <input
              type="text"
              className="form-input"
              placeholder="Search by student name, roll number, or email..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              style={{ paddingLeft: '36px' }}
            />
          </div>

          {/* Department Filter */}
          <div className="form-group" style={{ margin: 0 }}>
            <select className="form-input" value={departmentFilter} onChange={e => setDepartmentFilter(e.target.value)}>
              <option value="ALL">All Departments</option>
              <option value="Computer Science">Computer Science</option>
              <option value="Electrical Engineering">Electrical Engineering</option>
              <option value="Information Technology">Information Technology</option>
            </select>
          </div>

          {/* Year Filter */}
          <div className="form-group" style={{ margin: 0 }}>
            <select className="form-input" value={yearFilter} onChange={e => setYearFilter(e.target.value)}>
              <option value="ALL">All Academic Years</option>
              <option value="1">1st Year</option>
              <option value="2">2nd Year</option>
              <option value="3">3rd Year</option>
              <option value="4">4th Year</option>
            </select>
          </div>
        </div>
      </div>

      {/* Student Table */}
      <div className="table-container">
        <table className="saas-table">
          <thead>
            <tr>
              <th>Student Info</th>
              <th>Roll Number</th>
              <th>Department & Year</th>
              <th>Room Allocation</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredStudents.length > 0 ? (
              filteredStudents.map(student => (
                <tr key={student.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <div
                        style={{
                          width: '36px',
                          height: '36px',
                          borderRadius: '50%',
                          backgroundColor: 'var(--primary-100)',
                          color: 'var(--primary-700)',
                          fontWeight: 700,
                          fontSize: '0.875rem',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}
                      >
                        {student.name[0]}
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-main)', display: 'block' }}>{student.name}</span>
                        <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{student.email}</span>
                      </div>
                    </div>
                  </td>
                  <td style={{ fontWeight: 600 }}>{student.rollNumber}</td>
                  <td>
                    <span style={{ display: 'block', fontWeight: 600 }}>{student.department}</span>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Year {student.academicYear}</span>
                  </td>
                  <td>
                    {student.roomAllocation ? (
                      <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--text-main)' }}>{student.roomAllocation}</span>
                    ) : (
                      <span style={{ fontSize: '0.8125rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>Not Allocated</span>
                    )}
                  </td>
                  <td>
                    <Badge variant={student.status === 'ALLOCATED' ? 'success' : 'warning'}>
                      {student.status}
                    </Badge>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '8px' }}>
                      <button
                        onClick={() => { setSelectedStudent(student); setShowProfileModal(true); }}
                        className="btn btn-ghost btn-sm"
                        title="View Profile Details"
                      >
                        <Eye size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={6} style={{ padding: '0' }}>
                  <EmptyState
                    title="No Students Found"
                    description="No student records match your current search query or filter settings."
                    actionLabel="Clear Filters"
                    onAction={() => { setSearchQuery(''); setDepartmentFilter('ALL'); setYearFilter('ALL'); }}
                  />
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Student Detailed Profile Modal */}
      {selectedStudent && (
        <Modal
          isOpen={showProfileModal}
          onClose={() => setShowProfileModal(false)}
          title={`Student Profile — ${selectedStudent.name}`}
          subtitle={`Roll No: ${selectedStudent.rollNumber} • ${selectedStudent.department}`}
          maxWidth="lg"
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            {/* Modal Tabs */}
            <div className="tab-nav" style={{ marginBottom: 0 }}>
              <button onClick={() => setActiveTab('overview')} className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}>
                Overview
              </button>
              <button onClick={() => setActiveTab('academic')} className={`tab-btn ${activeTab === 'academic' ? 'active' : ''}`}>
                Academic Record
              </button>
              <button onClick={() => setActiveTab('guardian')} className={`tab-btn ${activeTab === 'guardian' ? 'active' : ''}`}>
                Guardian & Address
              </button>
              <button onClick={() => setActiveTab('allocation')} className={`tab-btn ${activeTab === 'allocation' ? 'active' : ''}`}>
                Room Allocation
              </button>
            </div>

            {/* Tab 1: Overview */}
            {activeTab === 'overview' && (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', fontSize: '0.875rem' }}>
                <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>FULL NAME</span>
                  <span style={{ display: 'block', fontWeight: 700, fontSize: '1rem', marginTop: '2px' }}>{selectedStudent.name}</span>
                </div>
                <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>EMAIL ADDRESS</span>
                  <span style={{ display: 'block', fontWeight: 700, fontSize: '1rem', marginTop: '2px' }}>{selectedStudent.email}</span>
                </div>
                <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>DEPARTMENT</span>
                  <span style={{ display: 'block', fontWeight: 700, marginTop: '2px' }}>{selectedStudent.department}</span>
                </div>
                <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>CURRENT ROOM</span>
                  <span style={{ display: 'block', fontWeight: 700, marginTop: '2px' }}>{selectedStudent.roomAllocation || 'Unallocated'}</span>
                </div>
              </div>
            )}

            {/* Tab 2: Academic */}
            {activeTab === 'academic' && (
              <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)' }}>
                <p>Roll Number: <strong>{selectedStudent.rollNumber}</strong></p>
                <p>Academic Year: <strong>Year {selectedStudent.academicYear}</strong></p>
                <p>Department: <strong>{selectedStudent.department}</strong></p>
              </div>
            )}

            {/* Tab 3: Guardian */}
            {activeTab === 'guardian' && (
              <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)' }}>
                <p>Guardian Name: <strong>{selectedStudent.guardianName}</strong></p>
                <p>Guardian Phone: <strong>{selectedStudent.guardianPhone}</strong></p>
                <p>Address: <strong>{selectedStudent.address}</strong></p>
              </div>
            )}

            {/* Tab 4: Room Allocation */}
            {activeTab === 'allocation' && (
              <div style={{ padding: '16px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--primary-50)', border: '1px solid var(--primary-200)' }}>
                <span style={{ fontWeight: 700, color: 'var(--primary-900)' }}>Hostel Status:</span>
                <p style={{ color: 'var(--primary-800)', marginTop: '4px' }}>
                  {selectedStudent.roomAllocation ? `Currently assigned to ${selectedStudent.roomAllocation}` : 'Student currently has no room assigned.'}
                </p>
              </div>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
};
