import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { EmptyState } from '../components/ui/EmptyState';
import { api } from '../services/api';
import {
  Megaphone,
  Plus,
  Search
} from 'lucide-react';

interface Notice {
  id: number;
  title: string;
  content: string;
  category: string;
  targetRole: string;
  createdAt: string;
}

export const NoticesModule: React.FC = () => {
  const { user } = useAuth();
  const role = user?.role || 'ROLE_STUDENT';

  const [notices, setNotices] = useState<Notice[]>([]);
  const [showPublishModal, setShowPublishModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Form State
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('General');
  const [targetRole, setTargetRole] = useState('ALL');

  useEffect(() => {
    fetchNotices();
  }, []);

  const fetchNotices = async () => {
    try {
      const res = await api.get('/notices');
      if (res.data.data && res.data.data.length > 0) {
        setNotices(res.data.data);
      } else {
        seedDemoNotices();
      }
    } catch (err) {
      seedDemoNotices();
    }
  };

  const seedDemoNotices = () => {
    const demo: Notice[] = [
      {
        id: 1,
        title: '📢 Welcome to Smart Hostel System',
        content: 'All residents are advised to register their attendance using the QR code scanner in their respective blocks every evening between 8:00 PM and 10:00 PM.',
        category: 'General',
        targetRole: 'ALL',
        createdAt: 'Today at 09:00 AM'
      },
      {
        id: 2,
        title: '⚡ Scheduled Electrical Maintenance',
        content: 'Power supply will be interrupted tomorrow from 10:00 AM to 1:00 PM for transformer inspection. Please charge your devices in advance.',
        category: 'Maintenance',
        targetRole: 'ALL',
        createdAt: 'Yesterday'
      },
      {
        id: 3,
        title: '🏆 Annual Campus Sports Tournament Registration',
        content: 'Inter-hostel sports registration is now open. Contact Warden Vance or block representatives to join football, cricket, or badminton teams.',
        category: 'Events',
        targetRole: 'ALL',
        createdAt: '2 days ago'
      }
    ];
    setNotices(demo);
  };

  const handlePublishSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/notices', { title, content, category, targetRole });
      fetchNotices();
      setShowPublishModal(false);
      resetForm();
    } catch (err) {
      const newNotice: Notice = {
        id: Date.now(),
        title,
        content,
        category,
        targetRole,
        createdAt: 'Just now'
      };
      setNotices([newNotice, ...notices]);
      setShowPublishModal(false);
      resetForm();
    }
  };

  const resetForm = () => {
    setTitle('');
    setContent('');
    setCategory('General');
    setTargetRole('ALL');
  };

  const filteredNotices = notices.filter(n =>
    n.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    n.content.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Header Banner */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Campus Notice Board</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Official announcements, maintenance schedules, and campus guidelines
          </p>
        </div>

        {role !== 'ROLE_STUDENT' && (
          <button onClick={() => setShowPublishModal(true)} className="btn btn-primary">
            <Plus size={18} /> Publish New Notice
          </button>
        )}
      </div>

      {/* Search Header */}
      <div className="saas-card" style={{ padding: '16px 20px' }}>
        <div style={{ position: 'relative', width: '100%' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            className="form-input"
            placeholder="Search announcements by keyword..."
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            style={{ paddingLeft: '36px' }}
          />
        </div>
      </div>

      {/* Notice Feed List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {filteredNotices.length > 0 ? (
          filteredNotices.map(notice => (
            <div key={notice.id} className="saas-card saas-card-hover" style={{ padding: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <Megaphone size={20} color="var(--primary-600)" />
                  <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-main)' }}>{notice.title}</h3>
                </div>

                <div style={{ display: 'flex', gap: '8px' }}>
                  <Badge variant="info">{notice.category}</Badge>
                  <Badge variant="neutral">Target: {notice.targetRole}</Badge>
                </div>
              </div>

              <p style={{ fontSize: '0.9375rem', color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '16px' }}>
                {notice.content}
              </p>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '12px', borderTop: '1px solid var(--border-subtle)', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                <span>Published by Campus Administrator</span>
                <span>{notice.createdAt}</span>
              </div>
            </div>
          ))
        ) : (
          <EmptyState
            title="No Notices Found"
            description="No announcements match your search query."
          />
        )}
      </div>

      {/* Publish Notice Modal */}
      <Modal
        isOpen={showPublishModal}
        onClose={() => setShowPublishModal(false)}
        title="Publish Campus Announcement"
        subtitle="Broadcast notice to students and wardens"
      >
        <form onSubmit={handlePublishSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Notice Title *</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. ⚡ Scheduled Electrical Maintenance"
              value={title}
              onChange={e => setTitle(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Category</label>
              <select className="form-input" value={category} onChange={e => setCategory(e.target.value)}>
                <option value="General">General</option>
                <option value="Maintenance">Maintenance</option>
                <option value="Events">Events</option>
                <option value="Urgent">Urgent Warning</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Target Audience</label>
              <select className="form-input" value={targetRole} onChange={e => setTargetRole(e.target.value)}>
                <option value="ALL">All Campus Residents</option>
                <option value="STUDENT">Students Only</option>
                <option value="WARDEN">Wardens Only</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Notice Content Body *</label>
            <textarea
              className="form-input"
              placeholder="Write the complete announcement text..."
              value={content}
              onChange={e => setContent(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
            <button type="button" onClick={() => setShowPublishModal(false)} className="btn btn-secondary">Cancel</button>
            <button type="submit" className="btn btn-primary">Publish Announcement</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
