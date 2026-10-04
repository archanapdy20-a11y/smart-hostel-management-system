import React from 'react';
import { FileSpreadsheet, Download } from 'lucide-react';

export const ReportsModule: React.FC = () => {
  const reports = [
    { title: 'Campus Occupancy & Room Capacity Report', desc: 'Detailed breakdown of occupied beds, available beds, and block capacity percentage.', format: 'PDF / CSV' },
    { title: 'Monthly Resident Attendance Ledger', desc: 'Timestamped check-in logs, absent counts, and attendance percentage per student.', format: 'CSV' },
    { title: 'Complaint Resolution & Priority Audit', desc: 'Maintenance response time metrics, emergency incident logs, and resolution rate.', format: 'PDF' },
    { title: 'Fee Collection & Outstanding Balance Report', desc: 'Paid vs outstanding accommodation charges breakdown by hostel block.', format: 'CSV' }
  ];

  const handleExport = (title: string) => {
    alert(`Generating & Exporting report: ${title}...`);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Reports & Analytics Export Center</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Generate and export official campus management audit reports
          </p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px' }}>
        {reports.map((r, idx) => (
          <div key={idx} className="saas-card saas-card-hover" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div>
              <div style={{ width: '40px', height: '40px', borderRadius: 'var(--radius-md)', backgroundColor: 'var(--primary-50)', color: 'var(--primary-600)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '16px' }}>
                <FileSpreadsheet size={20} />
              </div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '8px' }}>{r.title}</h3>
              <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>{r.desc}</p>
            </div>

            <div style={{ marginTop: '20px', paddingTop: '16px', borderTop: '1px solid var(--border-subtle)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)' }}>Format: {r.format}</span>
              <button onClick={() => handleExport(r.title)} className="btn btn-primary btn-sm">
                <Download size={14} /> Export Report
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
