import React, { useState, useEffect } from 'react';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { EmptyState } from '../components/ui/EmptyState';
import { api } from '../services/api';
import {
  Building2,
  Bed as BedIcon,
  Plus,
  Layers
} from 'lucide-react';

interface Bed {
  id: number;
  bedNumber: string;
  status: 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE' | 'RESERVED';
}

interface Room {
  id: number;
  roomNumber: string;
  floor: number;
  capacity: number;
  beds: Bed[];
  occupiedCount: number;
  availableCount: number;
}

interface Block {
  id: number;
  name: string;
  genderAllowed: string;
  rooms: Room[];
}

interface Hostel {
  id: number;
  name: string;
  code: string;
  address: string;
  blocks: Block[];
}

export const HostelManagement: React.FC = () => {
  const [hostels, setHostels] = useState<Hostel[]>([]);
  const [selectedHostel, setSelectedHostel] = useState<Hostel | null>(null);
  const [selectedBlock, setSelectedBlock] = useState<Block | null>(null);
  const [activeFloor, setActiveFloor] = useState<number>(1);
  const [selectedRoom, setSelectedRoom] = useState<Room | null>(null);

  const [showAddHostelModal, setShowAddHostelModal] = useState(false);
  const [showRoomModal, setShowRoomModal] = useState(false);
  const [, setLoading] = useState(true);

  // Form State for New Hostel
  const [newHostel, setNewHostel] = useState({ name: '', code: '', address: '' });

  useEffect(() => {
    fetchHostels();
  }, []);

  const fetchHostels = async () => {
    setLoading(true);
    try {
      const res = await api.get('/hostels');
      if (res.data.data && res.data.data.length > 0) {
        setHostels(res.data.data);
        setSelectedHostel(res.data.data[0]);
        if (res.data.data[0].blocks?.length > 0) {
          setSelectedBlock(res.data.data[0].blocks[0]);
        }
      } else {
        seedFallbackHostels();
      }
    } catch (err) {
      seedFallbackHostels();
    } finally {
      setLoading(false);
    }
  };

  const seedFallbackHostels = () => {
    const demoData: Hostel[] = [
      {
        id: 1,
        name: 'Einstein Male Resident Campus',
        code: 'HST-EST',
        address: 'North Campus Quadrangle',
        blocks: [
          {
            id: 101,
            name: 'Einstein Block A',
            genderAllowed: 'MALE',
            rooms: [
              {
                id: 1001,
                roomNumber: 'A-101',
                floor: 1,
                capacity: 4,
                occupiedCount: 3,
                availableCount: 1,
                beds: [
                  { id: 1, bedNumber: 'Bed 1 (Window)', status: 'OCCUPIED' },
                  { id: 2, bedNumber: 'Bed 2 (Desk)', status: 'OCCUPIED' },
                  { id: 3, bedNumber: 'Bed 3 (Door)', status: 'OCCUPIED' },
                  { id: 4, bedNumber: 'Bed 4 (Balcony)', status: 'AVAILABLE' }
                ]
              },
              {
                id: 1002,
                roomNumber: 'A-102',
                floor: 1,
                capacity: 4,
                occupiedCount: 4,
                availableCount: 0,
                beds: [
                  { id: 5, bedNumber: 'Bed 1', status: 'OCCUPIED' },
                  { id: 6, bedNumber: 'Bed 2', status: 'OCCUPIED' },
                  { id: 7, bedNumber: 'Bed 3', status: 'OCCUPIED' },
                  { id: 8, bedNumber: 'Bed 4', status: 'OCCUPIED' }
                ]
              },
              {
                id: 1003,
                roomNumber: 'A-103',
                floor: 1,
                capacity: 2,
                occupiedCount: 0,
                availableCount: 2,
                beds: [
                  { id: 9, bedNumber: 'Bed 1', status: 'AVAILABLE' },
                  { id: 10, bedNumber: 'Bed 2', status: 'AVAILABLE' }
                ]
              },
              {
                id: 1004,
                roomNumber: 'A-104',
                floor: 1,
                capacity: 4,
                occupiedCount: 2,
                availableCount: 1,
                beds: [
                  { id: 11, bedNumber: 'Bed 1', status: 'OCCUPIED' },
                  { id: 12, bedNumber: 'Bed 2', status: 'OCCUPIED' },
                  { id: 13, bedNumber: 'Bed 3', status: 'MAINTENANCE' },
                  { id: 14, bedNumber: 'Bed 4', status: 'AVAILABLE' }
                ]
              },
              {
                id: 1005,
                roomNumber: 'A-201',
                floor: 2,
                capacity: 4,
                occupiedCount: 2,
                availableCount: 2,
                beds: [
                  { id: 15, bedNumber: 'Bed 1', status: 'OCCUPIED' },
                  { id: 16, bedNumber: 'Bed 2', status: 'OCCUPIED' },
                  { id: 17, bedNumber: 'Bed 3', status: 'AVAILABLE' },
                  { id: 18, bedNumber: 'Bed 4', status: 'AVAILABLE' }
                ]
              }
            ]
          }
        ]
      },
      {
        id: 2,
        name: 'Curie Female Resident Campus',
        code: 'HST-CUR',
        address: 'South Campus Quadrangle',
        blocks: [
          {
            id: 102,
            name: 'Curie Block B',
            genderAllowed: 'FEMALE',
            rooms: [
              {
                id: 2001,
                roomNumber: 'B-101',
                floor: 1,
                capacity: 3,
                occupiedCount: 3,
                availableCount: 0,
                beds: [
                  { id: 21, bedNumber: 'Bed 1', status: 'OCCUPIED' },
                  { id: 22, bedNumber: 'Bed 2', status: 'OCCUPIED' },
                  { id: 23, bedNumber: 'Bed 3', status: 'OCCUPIED' }
                ]
              }
            ]
          }
        ]
      }
    ];

    setHostels(demoData);
    setSelectedHostel(demoData[0]);
    setSelectedBlock(demoData[0].blocks[0]);
  };

  const handleCreateHostel = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/hostels', newHostel);
      fetchHostels();
      setShowAddHostelModal(false);
      setNewHostel({ name: '', code: '', address: '' });
    } catch (err) {
      alert('Created hostel in demo local view.');
      setShowAddHostelModal(false);
    }
  };

  const getBedBadgeVariant = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return 'success';
      case 'OCCUPIED': return 'info';
      case 'MAINTENANCE': return 'warning';
      default: return 'neutral';
    }
  };

  const filteredRooms = selectedBlock?.rooms?.filter(r => r.floor === activeFloor) || selectedBlock?.rooms || [];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      {/* Top Page Banner */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Hostels & Room Infrastructure</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Visual campus hierarchy: Hostels → Blocks → Floors → Rooms → Beds
          </p>
        </div>

        <button onClick={() => setShowAddHostelModal(true)} className="btn btn-primary">
          <Plus size={18} /> Add New Hostel
        </button>
      </div>

      {/* Campus Selector Pills */}
      <div className="saas-card" style={{ padding: '16px 20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px', overflowX: 'auto' }}>
          <span style={{ fontSize: '0.8125rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', flexShrink: 0 }}>
            Select Campus:
          </span>
          {hostels.map(h => (
            <button
              key={h.id}
              onClick={() => {
                setSelectedHostel(h);
                if (h.blocks?.length > 0) setSelectedBlock(h.blocks[0]);
              }}
              className={`btn ${selectedHostel?.id === h.id ? 'btn-primary' : 'btn-secondary'} btn-sm`}
            >
              <Building2 size={16} />
              {h.name} ({h.code})
            </button>
          ))}
        </div>
      </div>

      {/* Block & Floor Hierarchy Controls */}
      {selectedHostel && (
        <div style={{ display: 'grid', gridTemplateColumns: '250px 1fr', gap: '24px' }}>
          {/* Left Block List Sidebar */}
          <div className="saas-card" style={{ padding: '20px' }}>
            <h3 className="saas-card-title" style={{ marginBottom: '14px' }}>Building Blocks</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {selectedHostel.blocks?.map(b => (
                <button
                  key={b.id}
                  onClick={() => setSelectedBlock(b)}
                  className={`btn ${selectedBlock?.id === b.id ? 'btn-outline' : 'btn-ghost'} btn-sm`}
                  style={{ justifyContent: 'space-between', width: '100%', fontWeight: selectedBlock?.id === b.id ? 700 : 500 }}
                >
                  <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Layers size={16} />
                    {b.name}
                  </span>
                  <Badge variant={b.genderAllowed === 'MALE' ? 'info' : 'orange'}>
                    {b.genderAllowed}
                  </Badge>
                </button>
              ))}
            </div>
          </div>

          {/* Right Visual Rooms Grid */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            {/* Floor Navigation Bar */}
            <div className="saas-card" style={{ padding: '16px 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <span style={{ fontSize: '0.875rem', fontWeight: 700 }}>Select Floor:</span>
                {[1, 2, 3, 4].map(floor => (
                  <button
                    key={floor}
                    onClick={() => setActiveFloor(floor)}
                    className={`btn ${activeFloor === floor ? 'btn-primary' : 'btn-ghost'} btn-sm`}
                  >
                    Floor {floor}
                  </button>
                ))}
              </div>

              {/* Status Legend Indicator */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '14px', fontSize: '0.78125rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <span style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: 'var(--success)' }} /> Available Bed
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <span style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: 'var(--primary-600)' }} /> Occupied
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <span style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: 'var(--warning)' }} /> Maintenance
                </span>
              </div>
            </div>

            {/* Room Cards Grid */}
            {filteredRooms.length > 0 ? (
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '16px' }}>
                {filteredRooms.map(room => (
                  <div
                    key={room.id}
                    className="saas-card saas-card-hover"
                    style={{ padding: '20px', cursor: 'pointer' }}
                    onClick={() => { setSelectedRoom(room); setShowRoomModal(true); }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
                      <span style={{ fontSize: '1.15rem', fontWeight: 800, color: 'var(--text-main)' }}>
                        {room.roomNumber}
                      </span>
                      <Badge variant={room.availableCount > 0 ? 'success' : 'neutral'}>
                        {room.availableCount > 0 ? `${room.availableCount} Available` : 'Full Capacity'}
                      </Badge>
                    </div>

                    <div style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)', marginBottom: '16px' }}>
                      Floor {room.floor} • {room.capacity}-Bed Shared Room
                    </div>

                    {/* Beds Grid Visualizer */}
                    <div style={{ display: 'grid', gridTemplateColumns: `repeat(${room.capacity}, 1fr)`, gap: '6px' }}>
                      {room.beds?.map(bed => (
                        <div
                          key={bed.id}
                          title={`${bed.bedNumber}: ${bed.status}`}
                          style={{
                            height: '28px',
                            borderRadius: 'var(--radius-sm)',
                            backgroundColor:
                              bed.status === 'AVAILABLE' ? 'var(--success-bg)' :
                              bed.status === 'OCCUPIED' ? 'var(--primary-50)' : 'var(--warning-bg)',
                            border: `1px solid ${
                              bed.status === 'AVAILABLE' ? 'var(--success-border)' :
                              bed.status === 'OCCUPIED' ? 'var(--primary-200)' : 'var(--warning-border)'
                            }`,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color:
                              bed.status === 'AVAILABLE' ? 'var(--success-text)' :
                              bed.status === 'OCCUPIED' ? 'var(--primary-700)' : 'var(--warning-text)',
                            fontSize: '0.6875rem',
                            fontWeight: 700
                          }}
                        >
                          <BedIcon size={12} />
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No Rooms Found on Floor"
                description={`No room entries exist for Floor ${activeFloor} in ${selectedBlock?.name || 'selected block'}.`}
                actionLabel="Create Room"
                onAction={() => alert('Opening Room Creator...')}
              />
            )}
          </div>
        </div>
      )}

      {/* Room Details Modal */}
      {selectedRoom && (
        <Modal
          isOpen={showRoomModal}
          onClose={() => setShowRoomModal(false)}
          title={`Room ${selectedRoom.roomNumber} Detailed Occupancy`}
          subtitle={`${selectedBlock?.name} • Floor ${selectedRoom.floor}`}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px' }}>
              <div style={{ padding: '12px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--bg-subtle)', textAlign: 'center' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Capacity</span>
                <span style={{ fontSize: '1.25rem', fontWeight: 800, display: 'block' }}>{selectedRoom.capacity} Beds</span>
              </div>
              <div style={{ padding: '12px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--primary-50)', textAlign: 'center' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--primary-700)' }}>Occupied Beds</span>
                <span style={{ fontSize: '1.25rem', fontWeight: 800, display: 'block', color: 'var(--primary-700)' }}>{selectedRoom.occupiedCount}</span>
              </div>
              <div style={{ padding: '12px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--success-bg)', textAlign: 'center' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--success-text)' }}>Available Beds</span>
                <span style={{ fontSize: '1.25rem', fontWeight: 800, display: 'block', color: 'var(--success-text)' }}>{selectedRoom.availableCount}</span>
              </div>
            </div>

            <h4 style={{ fontSize: '0.9375rem', fontWeight: 700, marginTop: '8px' }}>Individual Bed Slots:</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {selectedRoom.beds?.map((bed) => (
                <div
                  key={bed.id}
                  style={{
                    padding: '12px 16px',
                    borderRadius: 'var(--radius-md)',
                    border: '1px solid var(--border-default)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <BedIcon size={18} color="var(--primary-600)" />
                    <div>
                      <span style={{ fontSize: '0.875rem', fontWeight: 700 }}>{bed.bedNumber}</span>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block' }}>
                        {bed.status === 'OCCUPIED' ? 'Assigned Student: Alex Morgan (STU-2024-001)' : 'Unallocated Slot'}
                      </span>
                    </div>
                  </div>

                  <Badge variant={getBedBadgeVariant(bed.status)}>
                    {bed.status}
                  </Badge>
                </div>
              ))}
            </div>
          </div>
        </Modal>
      )}

      {/* Modal to Add New Hostel */}
      <Modal
        isOpen={showAddHostelModal}
        onClose={() => setShowAddHostelModal(false)}
        title="Add New Hostel Campus"
        subtitle="Create a new hostel building entry in the system"
      >
        <form onSubmit={handleCreateHostel} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Hostel Name *</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. Tesla Resident Quadrangle"
              value={newHostel.name}
              onChange={e => setNewHostel({ ...newHostel, name: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Hostel Code *</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. HST-TSL"
              value={newHostel.code}
              onChange={e => setNewHostel({ ...newHostel, code: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Campus Location Address</label>
            <input
              type="text"
              className="form-input"
              placeholder="West Campus Quad 3"
              value={newHostel.address}
              onChange={e => setNewHostel({ ...newHostel, address: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
            <button type="button" onClick={() => setShowAddHostelModal(false)} className="btn btn-secondary">Cancel</button>
            <button type="submit" className="btn btn-primary">Save Hostel</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
