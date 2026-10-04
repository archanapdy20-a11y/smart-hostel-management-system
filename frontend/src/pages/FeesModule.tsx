import React, { useState } from 'react';
import { StatCard } from '../components/ui/StatCard';
import { Badge } from '../components/ui/Badge';
import { CreditCard, CheckCircle2, Download, DollarSign } from 'lucide-react';

export const FeesModule: React.FC = () => {
  const [transactions] = useState([
    { id: 'TXN-9081', date: '2026-09-01', description: 'Semester 1 Hostel Accommodation Fee', amount: '$1,200.00', status: 'PAID', method: 'Online Banking' },
    { id: 'TXN-9082', date: '2026-09-01', description: 'Mess & Dining Hall Charges', amount: '$450.00', status: 'PAID', method: 'Debit Card' },
    { id: 'TXN-9083', date: '2026-10-01', description: 'Laundry & Maintenance Deposit', amount: '$100.00', status: 'PENDING', method: 'Pending' }
  ]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Fees & Payment Ledger</h1>
          <p style={{ fontSize: '0.875rem', marginTop: '2px' }}>
            Financial overview, accommodation fee receipts, and payment status
          </p>
        </div>
      </div>

      {/* KPI Stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        <StatCard title="Total Annual Fee" value="$1,750.00" subtitle="Academic Year 2026-27" icon={<CreditCard size={22} />} accentColor="var(--primary-600)" />
        <StatCard title="Total Amount Paid" value="$1,650.00" subtitle="Cleared balance" icon={<CheckCircle2 size={22} />} accentColor="var(--success)" />
        <StatCard title="Pending Outstanding" value="$100.00" subtitle="Due Oct 15, 2026" icon={<DollarSign size={22} />} accentColor="var(--warning)" />
      </div>

      {/* Payment History Table */}
      <div className="saas-card">
        <div className="saas-card-header">
          <h3 className="saas-card-title">Fee Ledger & Transaction History</h3>
          <button onClick={() => alert('Simulating PDF Receipt Download...')} className="btn btn-outline btn-sm">
            <Download size={14} /> Download Statement
          </button>
        </div>

        <div className="table-container" style={{ border: 'none', boxShadow: 'none' }}>
          <table className="saas-table">
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>Description</th>
                <th>Date</th>
                <th>Amount</th>
                <th>Method</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map(txn => (
                <tr key={txn.id}>
                  <td style={{ fontWeight: 700 }}>{txn.id}</td>
                  <td>{txn.description}</td>
                  <td style={{ fontSize: '0.8125rem' }}>{txn.date}</td>
                  <td style={{ fontWeight: 800, color: 'var(--text-main)' }}>{txn.amount}</td>
                  <td style={{ fontSize: '0.8125rem' }}>{txn.method}</td>
                  <td>
                    <Badge variant={txn.status === 'PAID' ? 'success' : 'warning'}>
                      {txn.status}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
