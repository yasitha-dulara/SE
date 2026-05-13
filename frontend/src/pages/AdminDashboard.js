import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  adminGetUsers, adminGetUser, adminChangeRole, adminDeleteUser, adminCreateAdmin,
  adminGetPayments, adminUpdatePayStatus, adminDeletePayment
} from '../services/api';

export default function AdminDashboard() {
  const { user, logoutUser } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('users');
  const [msg, setMsg] = useState({ text: '', type: '' });

  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);

  const [payments, setPayments] = useState([]);
  const [payFilter, setPayFilter] = useState('ALL');

  const [showCreateAdmin, setShowCreateAdmin] = useState(false);
  const [adminForm, setAdminForm] = useState({ name: '', email: '', password: '', phoneNumber: '' });
  const [adminFormLoading, setAdminFormLoading] = useState(false);

  useEffect(() => { loadUsers(); }, []);
  useEffect(() => { if (activeTab === 'payments') loadPayments(); }, [activeTab]);

  const showMsg = (text, type = 'success') => {
    setMsg({ text, type });
    setTimeout(() => setMsg({ text: '', type: '' }), 3500);
  };

  const loadUsers = async () => {
    try { const res = await adminGetUsers(); setUsers(res.data); }
    catch { showMsg('Failed to load users', 'error'); }
  };

  const loadPayments = async () => {
    try { const res = await adminGetPayments(); setPayments(res.data); }
    catch { showMsg('Failed to load payments', 'error'); }
  };

  const handleViewUser = async (id) => {
    try { const res = await adminGetUser(id); setSelectedUser(res.data); }
    catch { showMsg('Failed to load user', 'error'); }
  };

  const handleChangeRole = async (id, role) => {
    if (!window.confirm(`Change role to ${role}?`)) return;
    try {
      await adminChangeRole(id, role);
      await loadUsers();
      if (selectedUser?.id === id) setSelectedUser((u) => ({ ...u, role }));
      showMsg('Role updated!');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const handleDeleteUser = async (id) => {
    if (!window.confirm('Delete this user permanently?')) return;
    try {
      await adminDeleteUser(id);
      setSelectedUser(null);
      await loadUsers();
      showMsg('User deleted.');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const handleCreateAdmin = async (e) => {
    e.preventDefault(); setAdminFormLoading(true);
    try {
      await adminCreateAdmin(adminForm);
      setAdminForm({ name: '', email: '', password: '', phoneNumber: '' });
      setShowCreateAdmin(false);
      await loadUsers();
      showMsg('Admin account created!');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
    finally { setAdminFormLoading(false); }
  };

  const handleUpdatePayStatus = async (id, status) => {
    try {
      await adminUpdatePayStatus(id, status);
      await loadPayments();
      showMsg('Payment status updated!');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const handleDeletePayment = async (id) => {
    if (!window.confirm('Delete this payment record?')) return;
    try {
      await adminDeletePayment(id);
      await loadPayments();
      showMsg('Payment deleted.');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const statusColor = (s) => {
    const map = { PENDING:'#f59e0b', COMPLETED:'#10b981', FAILED:'#ef4444', CANCELLED:'#6b7280', REFUNDED:'#8b5cf6' };
    return map[s] || '#6b7280';
  };

  const filteredPayments = payFilter === 'ALL' ? payments : payments.filter(p => p.status === payFilter);

  const stats = {
    totalUsers: users.length,
    admins: users.filter(u => u.role === 'ADMIN').length,
    customers: users.filter(u => u.role === 'CUSTOMER').length,
    totalPayments: payments.length,
    completed: payments.filter(p => p.status === 'COMPLETED').length,
    revenue: payments.filter(p => p.status === 'COMPLETED').reduce((sum, p) => sum + parseFloat(p.amount), 0),
  };

  return (
    <div style={S.page}>
      {/* Sidebar */}
      <nav style={S.sidebar}>
        <div style={S.sidebarTop}>
          <div style={{ ...S.avatar, background: '#e74c3c' }}>🛡</div>
          <p style={S.sidebarName}>{user?.name}</p>
          <p style={S.sidebarEmail}>{user?.email}</p>
          <span style={{ ...S.badge, background: '#e74c3c' }}>ADMIN</span>
        </div>
        <div style={S.nav}>
          {['users', 'payments'].map((t) => (
            <button key={t} onClick={() => setActiveTab(t)}
              style={{ ...S.navItem, ...(activeTab === t ? S.navItemActive : {}) }}>
              {t === 'users' ? '👥 Users' : '💳 Payments'}
            </button>
          ))}
        </div>
        <button onClick={() => { logoutUser(); navigate('/login'); }} style={S.logoutBtn}>🚪 Logout</button>
      </nav>

      {/* Main */}
      <main style={S.main}>
        {msg.text && (
          <div style={{ ...S.toast, background: msg.type === 'error' ? '#fef2f2' : '#f0fdf4',
            color: msg.type === 'error' ? '#dc2626' : '#16a34a',
            border: `1px solid ${msg.type === 'error' ? '#fecaca' : '#bbf7d0'}` }}>
            {msg.text}
          </div>
        )}

        {/* Stats bar */}
        <div style={S.statsRow}>
          {[
            { label: 'Total Users', val: stats.totalUsers, icon: '👥' },
            { label: 'Admins', val: stats.admins, icon: '🛡' },
            { label: 'Customers', val: stats.customers, icon: '👤' },
            { label: 'Payments', val: stats.totalPayments, icon: '💳' },
            { label: 'Completed', val: stats.completed, icon: '✅' },
            { label: 'Revenue', val: `LKR ${stats.revenue.toFixed(0)}`, icon: '💰' },
          ].map((s) => (
            <div key={s.label} style={S.statCard}>
              <div style={S.statIcon}>{s.icon}</div>
              <div style={S.statVal}>{s.val}</div>
              <div style={S.statLabel}>{s.label}</div>
            </div>
          ))}
        </div>

        {/* USERS TAB */}
        {activeTab === 'users' && (
          <div style={{ display: 'flex', gap: '20px' }}>
            <section style={{ ...S.section, flex: selectedUser ? '1' : '1 1 100%' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2 style={{ ...S.sectionTitle, marginBottom: 0 }}>👥 All Users</h2>
                <button onClick={() => setShowCreateAdmin(!showCreateAdmin)} style={S.btnAdmin}>
                  {showCreateAdmin ? 'Cancel' : '+ New Admin'}
                </button>
              </div>

              {showCreateAdmin && (
                <form onSubmit={handleCreateAdmin} style={S.createAdminForm}>
                  <h3 style={{ margin: '0 0 14px', fontSize: '15px' }}>Create Admin Account</h3>
                  <div style={S.grid2}>
                    <div style={S.field}>
                      <label style={S.label}>Name</label>
                      <input value={adminForm.name} onChange={(e) => setAdminForm({ ...adminForm, name: e.target.value })} required style={S.input} />
                    </div>
                    <div style={S.field}>
                      <label style={S.label}>Email</label>
                      <input type="email" value={adminForm.email} onChange={(e) => setAdminForm({ ...adminForm, email: e.target.value })} required style={S.input} />
                    </div>
                  </div>
                  <div style={S.grid2}>
                    <div style={S.field}>
                      <label style={S.label}>Password</label>
                      <input type="password" value={adminForm.password} onChange={(e) => setAdminForm({ ...adminForm, password: e.target.value })} required minLength={6} style={S.input} />
                    </div>
                    <div style={S.field}>
                      <label style={S.label}>Phone (optional)</label>
                      <input value={adminForm.phoneNumber} onChange={(e) => setAdminForm({ ...adminForm, phoneNumber: e.target.value })} style={S.input} />
                    </div>
                  </div>
                  <button type="submit" disabled={adminFormLoading} style={S.btnAdmin}>{adminFormLoading ? 'Creating...' : 'Create Admin'}</button>
                </form>
              )}

              <table style={S.table}>
                <thead>
                  <tr style={S.theadRow}>
                    {['ID', 'Name', 'Email', 'Role', 'Addresses', 'Actions'].map((h) => (
                      <th key={h} style={S.th}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id} style={S.tr} onClick={() => handleViewUser(u.id)}>
                      <td style={S.td}>{u.id}</td>
                      <td style={S.td}>{u.name}</td>
                      <td style={{ ...S.td, fontSize: '12px', color: '#6b7280' }}>{u.email}</td>
                      <td style={S.td}>
                        <span style={{ background: u.role === 'ADMIN' ? '#fee2e2' : '#dbeafe', color: u.role === 'ADMIN' ? '#dc2626' : '#2563eb', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: '700' }}>{u.role}</span>
                      </td>
                      <td style={{ ...S.td, textAlign: 'center' }}>{u.addressCount}</td>
                      <td style={S.td} onClick={(e) => e.stopPropagation()}>
                        <div style={{ display: 'flex', gap: '6px' }}>
                          {u.role === 'CUSTOMER'
                            ? <button onClick={() => handleChangeRole(u.id, 'ADMIN')} style={S.btnSmallAdmin}>Make Admin</button>
                            : <button onClick={() => handleChangeRole(u.id, 'CUSTOMER')} style={S.btnSmallGray}>Revoke Admin</button>
                          }
                          <button onClick={() => handleDeleteUser(u.id)} style={S.btnSmallDanger}>Delete</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>

            {selectedUser && (
              <aside style={{ ...S.section, width: '280px', flexShrink: 0, alignSelf: 'flex-start' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                  <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '700' }}>User Details</h3>
                  <button onClick={() => setSelectedUser(null)} style={{ background: 'none', border: 'none', fontSize: '18px', cursor: 'pointer' }}>✕</button>
                </div>
                <div style={S.detailRow}><span style={S.detailLabel}>ID</span><span>#{selectedUser.id}</span></div>
                <div style={S.detailRow}><span style={S.detailLabel}>Name</span><span>{selectedUser.name}</span></div>
                <div style={S.detailRow}><span style={S.detailLabel}>Email</span><span style={{ fontSize: '12px', wordBreak: 'break-all' }}>{selectedUser.email}</span></div>
                <div style={S.detailRow}><span style={S.detailLabel}>Phone</span><span>{selectedUser.phoneNumber || '—'}</span></div>
                <div style={S.detailRow}><span style={S.detailLabel}>Role</span><span style={{ fontWeight: '600', color: selectedUser.role === 'ADMIN' ? '#dc2626' : '#2563eb' }}>{selectedUser.role}</span></div>
                <div style={S.detailRow}><span style={S.detailLabel}>Joined</span><span style={{ fontSize: '12px' }}>{new Date(selectedUser.createdAt).toLocaleDateString()}</span></div>
                {selectedUser.deliveryAddresses?.length > 0 && (
                  <div style={{ marginTop: '12px' }}>
                    <p style={{ ...S.detailLabel, marginBottom: '8px' }}>Addresses ({selectedUser.deliveryAddresses.length})</p>
                    {selectedUser.deliveryAddresses.map((a) => (
                      <div key={a.id} style={{ background: '#f9fafb', borderRadius: '8px', padding: '8px 10px', marginBottom: '6px', fontSize: '12px' }}>
                        <strong>{a.label}</strong> — {a.street}{a.city ? `, ${a.city}` : ''}
                      </div>
                    ))}
                  </div>
                )}
              </aside>
            )}
          </div>
        )}

        {/* PAYMENTS TAB */}
        {activeTab === 'payments' && (
          <section style={S.section}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ ...S.sectionTitle, marginBottom: 0 }}>💳 All Payments</h2>
              <div style={{ display: 'flex', gap: '8px' }}>
                {['ALL', 'PENDING', 'COMPLETED', 'CANCELLED', 'FAILED', 'REFUNDED'].map((f) => (
                  <button key={f} onClick={() => setPayFilter(f)}
                    style={{ padding: '5px 12px', borderRadius: '20px', border: '1.5px solid', fontSize: '12px', cursor: 'pointer', fontWeight: '600',
                      background: payFilter === f ? '#1a3c4e' : '#fff', color: payFilter === f ? '#fff' : '#6b7280',
                      borderColor: payFilter === f ? '#1a3c4e' : '#e5e7eb' }}>
                    {f}
                  </button>
                ))}
              </div>
            </div>

            {filteredPayments.length === 0 ? (
              <div style={S.emptyState}><p>No payments found.</p></div>
            ) : (
              <table style={S.table}>
                <thead>
                  <tr style={S.theadRow}>
                    {['Receipt', 'Customer', 'Amount', 'Method', 'Status', 'Reference', 'Date', 'Actions'].map((h) => (
                      <th key={h} style={S.th}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filteredPayments.map((p) => (
                    <tr key={p.id} style={S.tr}>
                      <td style={{ ...S.td, fontSize: '11px', color: '#6b7280' }}>{p.receiptNumber}</td>
                      <td style={S.td}>
                        <div style={{ fontSize: '13px', fontWeight: '600' }}>{p.userName}</div>
                        <div style={{ fontSize: '11px', color: '#9ca3af' }}>{p.userEmail}</div>
                      </td>
                      <td style={{ ...S.td, fontWeight: '700' }}>LKR {parseFloat(p.amount).toFixed(2)}</td>
                      <td style={S.td}>{p.method}{p.cardLastFour ? ` ****${p.cardLastFour}` : ''}</td>
                      <td style={S.td}>
                        <span style={{ background: statusColor(p.status) + '20', color: statusColor(p.status), padding: '2px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: '700' }}>
                          {p.status}
                        </span>
                      </td>
                      <td style={{ ...S.td, fontSize: '12px' }}>{p.orderReference || '—'}</td>
                      <td style={{ ...S.td, fontSize: '12px', color: '#6b7280' }}>{new Date(p.createdAt).toLocaleDateString()}</td>
                      <td style={S.td} onClick={(e) => e.stopPropagation()}>
                        <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                          {p.status === 'PENDING' && <>
                            <button onClick={() => handleUpdatePayStatus(p.id, 'COMPLETED')} style={S.btnSmallGreen}>Complete</button>
                            <button onClick={() => handleUpdatePayStatus(p.id, 'FAILED')} style={S.btnSmallDanger}>Fail</button>
                          </>}
                          {p.status === 'COMPLETED' && (
                            <button onClick={() => handleUpdatePayStatus(p.id, 'REFUNDED')} style={S.btnSmallGray}>Refund</button>
                          )}
                          <button onClick={() => handleDeletePayment(p.id)} style={S.btnSmallDanger}>Del</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </section>
        )}
      </main>
    </div>
  );
}

const S = {
  page: { display: 'flex', minHeight: '100vh', background: '#f3f4f6', fontFamily: 'Inter, sans-serif' },
  sidebar: { width: '220px', background: '#1a1a2e', color: '#fff', display: 'flex', flexDirection: 'column', padding: '28px 16px', flexShrink: 0 },
  sidebarTop: { textAlign: 'center', marginBottom: '28px' },
  avatar: { width: '64px', height: '64px', borderRadius: '50%', background: '#5f9ea0', fontSize: '24px', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 10px' },
  sidebarName: { margin: '0 0 2px', fontWeight: '600', fontSize: '14px' },
  sidebarEmail: { margin: '0 0 6px', fontSize: '11px', color: '#94a3b8', wordBreak: 'break-all' },
  badge: { background: '#5f9ea0', color: '#fff', padding: '2px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: '600' },
  nav: { flex: 1 },
  navItem: { width: '100%', padding: '11px 14px', background: 'transparent', color: '#cbd5e1', border: 'none', borderRadius: '8px', textAlign: 'left', fontSize: '13px', cursor: 'pointer', marginBottom: '4px' },
  navItemActive: { background: '#e74c3c', color: '#fff', fontWeight: '600' },
  logoutBtn: { width: '100%', padding: '10px', background: 'rgba(255,255,255,0.1)', color: '#fff', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' },
  main: { flex: 1, padding: '32px', overflow: 'auto' },
  toast: { padding: '12px 16px', borderRadius: '8px', marginBottom: '16px', fontSize: '14px', fontWeight: '500' },
  statsRow: { display: 'flex', gap: '12px', marginBottom: '24px', flexWrap: 'wrap' },
  statCard: { background: '#fff', borderRadius: '12px', padding: '16px 20px', boxShadow: '0 1px 4px rgba(0,0,0,0.07)', textAlign: 'center', minWidth: '100px', flex: '1' },
  statIcon: { fontSize: '22px', marginBottom: '4px' },
  statVal: { fontWeight: '700', fontSize: '20px', color: '#1a1a2e' },
  statLabel: { fontSize: '11px', color: '#9ca3af', marginTop: '2px' },
  section: { background: '#fff', borderRadius: '16px', padding: '28px', boxShadow: '0 1px 4px rgba(0,0,0,0.07)' },
  sectionTitle: { margin: '0 0 20px', fontSize: '18px', fontWeight: '700', color: '#1a1a2e' },
  table: { width: '100%', borderCollapse: 'collapse', fontSize: '13px' },
  theadRow: { borderBottom: '2px solid #f3f4f6' },
  th: { padding: '10px 12px', textAlign: 'left', fontSize: '11px', fontWeight: '700', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.05em' },
  tr: { borderBottom: '1px solid #f9fafb', cursor: 'pointer', transition: 'background 0.15s' },
  td: { padding: '12px', verticalAlign: 'middle' },
  field: { marginBottom: '12px' },
  label: { display: 'block', fontSize: '12px', fontWeight: '600', color: '#374151', marginBottom: '5px' },
  input: { width: '100%', padding: '9px 12px', border: '1.5px solid #e5e7eb', borderRadius: '8px', fontSize: '14px', outline: 'none', boxSizing: 'border-box' },
  grid2: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
  emptyState: { textAlign: 'center', padding: '40px', color: '#6b7280' },
  detailRow: { display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid #f3f4f6', fontSize: '13px' },
  detailLabel: { fontWeight: '600', color: '#6b7280', fontSize: '12px' },
  createAdminForm: { background: '#faf9ff', border: '1.5px solid #e5e7eb', borderRadius: '12px', padding: '18px', marginBottom: '20px' },
  btnPrimary: { padding: '9px 20px', background: '#5f9ea0', color: '#fff', border: 'none', borderRadius: '8px', fontWeight: '600', fontSize: '13px', cursor: 'pointer' },
  btnAdmin: { padding: '9px 20px', background: '#1a1a2e', color: '#fff', border: 'none', borderRadius: '8px', fontWeight: '600', fontSize: '13px', cursor: 'pointer' },
  btnDanger: { padding: '9px 20px', background: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca', borderRadius: '8px', fontWeight: '600', fontSize: '13px', cursor: 'pointer' },
  btnSmallAdmin: { padding: '4px 10px', background: '#1a1a2e', color: '#fff', border: 'none', borderRadius: '6px', fontSize: '11px', cursor: 'pointer', fontWeight: '600' },
  btnSmallGray: { padding: '4px 10px', background: '#f3f4f6', color: '#374151', border: '1px solid #e5e7eb', borderRadius: '6px', fontSize: '11px', cursor: 'pointer', fontWeight: '600' },
  btnSmallDanger: { padding: '4px 10px', background: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca', borderRadius: '6px', fontSize: '11px', cursor: 'pointer', fontWeight: '600' },
  btnSmallGreen: { padding: '4px 10px', background: '#f0fdf4', color: '#16a34a', border: '1px solid #bbf7d0', borderRadius: '6px', fontSize: '11px', cursor: 'pointer', fontWeight: '600' },
};
