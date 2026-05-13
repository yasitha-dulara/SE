import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  getProfile, updateProfile, changePassword,
  addAddress, deleteAddress, deleteAccount,
  submitPayment, getMyPayments, cancelPayment
} from '../services/api';

export default function Dashboard() {
  const { user, setUser, logoutUser } = useAuth();
  const navigate = useNavigate();

  const [profile, setProfile] = useState(null);
  const [activeTab, setActiveTab] = useState('profile');
  const [msg, setMsg] = useState({ text: '', type: '' });

  const [editForm, setEditForm] = useState({ name: '', phoneNumber: '' });
  const [editLoading, setEditLoading] = useState(false);

  const [pwForm, setPwForm] = useState({ currentPassword: '', newPassword: '' });
  const [pwLoading, setPwLoading] = useState(false);

  const [addrForm, setAddrForm] = useState({ label: '', street: '', city: '', postalCode: '', isDefault: false });
  const [addrLoading, setAddrLoading] = useState(false);
  const [showAddrForm, setShowAddrForm] = useState(false);

  const [payments, setPayments] = useState([]);
  const [payForm, setPayForm] = useState({ amount: '', method: 'CASH', orderReference: '', cardLastFour: '', notes: '' });
  const [payLoading, setPayLoading] = useState(false);
  const [showPayForm, setShowPayForm] = useState(false);

  useEffect(() => { loadProfile(); }, []);
  useEffect(() => { if (activeTab === 'payments') loadPayments(); }, [activeTab]);

  const loadProfile = async () => {
    try {
      const res = await getProfile();
      setProfile(res.data);
      setEditForm({ name: res.data.name, phoneNumber: res.data.phoneNumber || '' });
    } catch { showMsg('Failed to load profile', 'error'); }
  };

  const loadPayments = async () => {
    try {
      const res = await getMyPayments();
      setPayments(res.data);
    } catch { showMsg('Failed to load payments', 'error'); }
  };

  const showMsg = (text, type = 'success') => {
    setMsg({ text, type });
    setTimeout(() => setMsg({ text: '', type: '' }), 3000);
  };

  const handleLogout = () => { logoutUser(); navigate('/login'); };

  const handleUpdateProfile = async (e) => {
    e.preventDefault(); setEditLoading(true);
    try {
      const res = await updateProfile(editForm);
      setProfile(res.data);
      setUser((prev) => ({ ...prev, name: res.data.name }));
      showMsg('Profile updated!');
    } catch (err) { showMsg(err.response?.data?.message || 'Update failed', 'error'); }
    finally { setEditLoading(false); }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault(); setPwLoading(true);
    try {
      await changePassword(pwForm);
      setPwForm({ currentPassword: '', newPassword: '' });
      showMsg('Password changed!');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
    finally { setPwLoading(false); }
  };

  const handleAddAddress = async (e) => {
    e.preventDefault(); setAddrLoading(true);
    try {
      await addAddress(addrForm);
      setAddrForm({ label: '', street: '', city: '', postalCode: '', isDefault: false });
      setShowAddrForm(false);
      await loadProfile();
      showMsg('Address added!');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
    finally { setAddrLoading(false); }
  };

  const handleDeleteAddress = async (id) => {
    if (!window.confirm('Delete this address?')) return;
    try {
      await deleteAddress(id);
      await loadProfile();
      showMsg('Address deleted.');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('Delete your account? This cannot be undone.')) return;
    try {
      await deleteAccount();
      logoutUser();
      navigate('/login');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const handleSubmitPayment = async (e) => {
    e.preventDefault(); setPayLoading(true);
    try {
      const body = {
        amount: parseFloat(payForm.amount),
        method: payForm.method,
        orderReference: payForm.orderReference || undefined,
        cardLastFour: payForm.method === 'CARD' ? payForm.cardLastFour : undefined,
        notes: payForm.notes || undefined,
      };
      await submitPayment(body);
      setPayForm({ amount: '', method: 'CASH', orderReference: '', cardLastFour: '', notes: '' });
      setShowPayForm(false);
      await loadPayments();
      showMsg('Payment submitted!');
    } catch (err) { showMsg(err.response?.data?.message || 'Payment failed', 'error'); }
    finally { setPayLoading(false); }
  };

  const handleCancelPayment = async (id) => {
    if (!window.confirm('Cancel this payment?')) return;
    try {
      await cancelPayment(id);
      await loadPayments();
      showMsg('Payment cancelled.');
    } catch (err) { showMsg(err.response?.data?.message || 'Failed', 'error'); }
  };

  const statusColor = (s) => {
    const map = { PENDING:'#f59e0b', COMPLETED:'#10b981', FAILED:'#ef4444', CANCELLED:'#6b7280', REFUNDED:'#8b5cf6' };
    return map[s] || '#6b7280';
  };

  if (!profile) return <div style={S.loadingPage}><span style={S.spinner}>Loading...</span></div>;

  const tabs = ['profile', 'password', 'addresses', 'payments'];

  return (
    <div style={S.page}>
      {/* Sidebar */}
      <nav style={S.sidebar}>
        <div style={S.sidebarTop}>
          <div style={S.avatar}>{profile.name?.charAt(0).toUpperCase()}</div>
          <p style={S.sidebarName}>{profile.name}</p>
          <p style={S.sidebarEmail}>{profile.email}</p>
          <span style={S.badge}>{profile.role}</span>
        </div>
        <div style={S.nav}>
          {tabs.map((t) => (
            <button key={t} onClick={() => setActiveTab(t)}
              style={{ ...S.navItem, ...(activeTab === t ? S.navItemActive : {}) }}>
              {t === 'profile' && '👤 '}{t === 'password' && '🔒 '}{t === 'addresses' && '📍 '}{t === 'payments' && '💳 '}
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>
        <button onClick={handleLogout} style={S.logoutBtn}>🚪 Logout</button>
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

        {/* Profile */}
        {activeTab === 'profile' && (
          <section style={S.section}>
            <h2 style={S.sectionTitle}>👤 My Profile</h2>
            <form onSubmit={handleUpdateProfile} style={S.form}>
              <div style={S.field}>
                <label style={S.label}>Full Name</label>
                <input value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} required style={S.input} />
              </div>
              <div style={S.field}>
                <label style={S.label}>Email Address</label>
                <input value={profile.email} disabled style={{ ...S.input, background: '#f3f4f6', color: '#9ca3af' }} />
                <small style={S.hint}>Email cannot be changed</small>
              </div>
              <div style={S.field}>
                <label style={S.label}>Phone Number</label>
                <input value={editForm.phoneNumber} onChange={(e) => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                  placeholder="+94 77 123 4567" style={S.input} />
              </div>
              <div style={S.infoRow}>
                <span style={S.infoLabel}>Member Since</span>
                <span>{new Date(profile.createdAt).toLocaleDateString()}</span>
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '8px' }}>
                <button type="submit" disabled={editLoading} style={S.btnPrimary}>{editLoading ? 'Saving...' : 'Save Changes'}</button>
                <button type="button" onClick={handleDeleteAccount} style={S.btnDanger}>Delete Account</button>
              </div>
            </form>
          </section>
        )}

        {/* Password */}
        {activeTab === 'password' && (
          <section style={S.section}>
            <h2 style={S.sectionTitle}>🔒 Change Password</h2>
            <form onSubmit={handleChangePassword} style={S.form}>
              <div style={S.field}>
                <label style={S.label}>Current Password</label>
                <input type="password" value={pwForm.currentPassword}
                  onChange={(e) => setPwForm({ ...pwForm, currentPassword: e.target.value })} required style={S.input} />
              </div>
              <div style={S.field}>
                <label style={S.label}>New Password</label>
                <input type="password" value={pwForm.newPassword}
                  onChange={(e) => setPwForm({ ...pwForm, newPassword: e.target.value })}
                  required minLength={6} placeholder="At least 6 characters" style={S.input} />
              </div>
              <button type="submit" disabled={pwLoading} style={S.btnPrimary}>{pwLoading ? 'Changing...' : 'Change Password'}</button>
            </form>
          </section>
        )}

        {/* Addresses */}
        {activeTab === 'addresses' && (
          <section style={S.section}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ ...S.sectionTitle, marginBottom: 0 }}>📍 Delivery Addresses</h2>
              <button onClick={() => setShowAddrForm(!showAddrForm)} style={S.btnPrimary}>{showAddrForm ? 'Cancel' : '+ Add Address'}</button>
            </div>
            {showAddrForm && (
              <form onSubmit={handleAddAddress} style={{ ...S.form, background: '#f9fafb', borderRadius: '12px', padding: '20px', marginBottom: '20px' }}>
                <h3 style={{ margin: '0 0 16px', fontSize: '16px' }}>New Address</h3>
                <div style={S.grid2}>
                  <div style={S.field}>
                    <label style={S.label}>Label</label>
                    <input value={addrForm.label} onChange={(e) => setAddrForm({ ...addrForm, label: e.target.value })} required style={S.input} placeholder="Home" />
                  </div>
                  <div style={S.field}>
                    <label style={S.label}>City</label>
                    <input value={addrForm.city} onChange={(e) => setAddrForm({ ...addrForm, city: e.target.value })} style={S.input} placeholder="Colombo" />
                  </div>
                </div>
                <div style={S.field}>
                  <label style={S.label}>Street Address</label>
                  <input value={addrForm.street} onChange={(e) => setAddrForm({ ...addrForm, street: e.target.value })} required style={S.input} placeholder="123 Main St" />
                </div>
                <div style={S.field}>
                  <label style={S.label}>Postal Code</label>
                  <input value={addrForm.postalCode} onChange={(e) => setAddrForm({ ...addrForm, postalCode: e.target.value })} style={S.input} placeholder="10100" />
                </div>
                <button type="submit" disabled={addrLoading} style={S.btnPrimary}>{addrLoading ? 'Adding...' : 'Add Address'}</button>
              </form>
            )}
            {profile.deliveryAddresses?.length === 0 ? (
              <div style={S.emptyState}><p>📭 No addresses saved yet.</p></div>
            ) : (
              <div style={S.addressList}>
                {profile.deliveryAddresses.map((addr) => (
                  <div key={addr.id} style={S.addressCard}>
                    <div>
                      <strong>{addr.label}</strong>
                      {addr.isDefault && <span style={S.defaultBadge}>Default</span>}
                      <p style={{ margin: '4px 0 0', color: '#4b5563', fontSize: '14px' }}>
                        {addr.street}{addr.city ? `, ${addr.city}` : ''}{addr.postalCode ? ` ${addr.postalCode}` : ''}
                      </p>
                    </div>
                    <button onClick={() => handleDeleteAddress(addr.id)} style={S.deleteAddrBtn}>🗑</button>
                  </div>
                ))}
              </div>
            )}
          </section>
        )}

        {/* Payments */}
        {activeTab === 'payments' && (
          <section style={S.section}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ ...S.sectionTitle, marginBottom: 0 }}>💳 Payments</h2>
              <button onClick={() => setShowPayForm(!showPayForm)} style={S.btnPrimary}>{showPayForm ? 'Cancel' : '+ New Payment'}</button>
            </div>

            {showPayForm && (
              <form onSubmit={handleSubmitPayment} style={{ background: '#f9fafb', borderRadius: '12px', padding: '20px', marginBottom: '24px' }}>
                <h3 style={{ margin: '0 0 16px', fontSize: '16px' }}>Submit Payment</h3>
                <div style={S.grid2}>
                  <div style={S.field}>
                    <label style={S.label}>Amount (LKR)</label>
                    <input type="number" step="0.01" min="0.01" value={payForm.amount}
                      onChange={(e) => setPayForm({ ...payForm, amount: e.target.value })} required style={S.input} placeholder="0.00" />
                  </div>
                  <div style={S.field}>
                    <label style={S.label}>Payment Method</label>
                    <select value={payForm.method} onChange={(e) => setPayForm({ ...payForm, method: e.target.value })} style={S.input}>
                      <option value="CASH">Cash</option>
                      <option value="CARD">Card</option>
                      <option value="ONLINE">Online</option>
                    </select>
                  </div>
                </div>
                {payForm.method === 'CARD' && (
                  <div style={S.field}>
                    <label style={S.label}>Last 4 Digits of Card</label>
                    <input value={payForm.cardLastFour} onChange={(e) => setPayForm({ ...payForm, cardLastFour: e.target.value })}
                      maxLength={4} placeholder="1234" style={S.input} />
                  </div>
                )}
                <div style={S.field}>
                  <label style={S.label}>Order Reference (optional)</label>
                  <input value={payForm.orderReference} onChange={(e) => setPayForm({ ...payForm, orderReference: e.target.value })}
                    placeholder="ORD-001" style={S.input} />
                </div>
                <div style={S.field}>
                  <label style={S.label}>Notes (optional)</label>
                  <input value={payForm.notes} onChange={(e) => setPayForm({ ...payForm, notes: e.target.value })}
                    placeholder="Any notes..." style={S.input} />
                </div>
                <button type="submit" disabled={payLoading} style={S.btnPrimary}>{payLoading ? 'Processing...' : '💳 Submit Payment'}</button>
              </form>
            )}

            {payments.length === 0 ? (
              <div style={S.emptyState}><p>💸 No payment history yet.</p></div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {payments.map((p) => (
                  <div key={p.id} style={{ border: '1.5px solid #e5e7eb', borderRadius: '12px', padding: '16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <div>
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '6px' }}>
                          <strong style={{ fontSize: '16px' }}>LKR {parseFloat(p.amount).toFixed(2)}</strong>
                          <span style={{ background: statusColor(p.status) + '20', color: statusColor(p.status), padding: '2px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: '700' }}>{p.status}</span>
                          <span style={{ background: '#f3f4f6', color: '#6b7280', padding: '2px 8px', borderRadius: '12px', fontSize: '11px' }}>{p.method}</span>
                        </div>
                        <p style={{ margin: 0, fontSize: '13px', color: '#6b7280' }}>
                          Receipt: <strong>{p.receiptNumber}</strong>
                          {p.orderReference && <> · Ref: {p.orderReference}</>}
                          {p.cardLastFour && <> · ****{p.cardLastFour}</>}
                        </p>
                        <p style={{ margin: '4px 0 0', fontSize: '12px', color: '#9ca3af' }}>{new Date(p.createdAt).toLocaleString()}</p>
                        {p.notes && <p style={{ margin: '4px 0 0', fontSize: '13px', color: '#4b5563' }}>{p.notes}</p>}
                      </div>
                      {p.status === 'PENDING' && (
                        <button onClick={() => handleCancelPayment(p.id)} style={{ ...S.btnDanger, padding: '6px 14px', fontSize: '12px' }}>Cancel</button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        )}
      </main>
    </div>
  );
}

const S = {
  loadingPage: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' },
  spinner: { fontSize: '18px', color: '#5f9ea0' },
  page: { display: 'flex', minHeight: '100vh', background: '#f3f4f6', fontFamily: 'Inter, sans-serif' },
  sidebar: { width: '260px', background: '#1a3c4e', color: '#fff', display: 'flex', flexDirection: 'column', padding: '32px 20px', flexShrink: 0 },
  sidebarTop: { textAlign: 'center', marginBottom: '32px' },
  avatar: { width: '72px', height: '72px', borderRadius: '50%', background: '#5f9ea0', fontSize: '28px', fontWeight: '700', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px' },
  sidebarName: { margin: '0 0 4px', fontWeight: '600', fontSize: '16px' },
  sidebarEmail: { margin: '0 0 8px', fontSize: '12px', color: '#94a3b8', wordBreak: 'break-all' },
  badge: { background: '#5f9ea0', color: '#fff', padding: '2px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: '600' },
  nav: { flex: 1 },
  navItem: { width: '100%', padding: '12px 16px', background: 'transparent', color: '#cbd5e1', border: 'none', borderRadius: '8px', textAlign: 'left', fontSize: '14px', cursor: 'pointer', marginBottom: '4px' },
  navItemActive: { background: '#5f9ea0', color: '#fff', fontWeight: '600' },
  logoutBtn: { width: '100%', padding: '12px', background: 'rgba(255,255,255,0.1)', color: '#fff', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px', cursor: 'pointer', fontSize: '14px' },
  main: { flex: 1, padding: '40px', maxWidth: '780px' },
  toast: { padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', fontSize: '14px', fontWeight: '500' },
  section: { background: '#fff', borderRadius: '16px', padding: '32px', boxShadow: '0 1px 4px rgba(0,0,0,0.07)' },
  sectionTitle: { margin: '0 0 24px', fontSize: '20px', fontWeight: '700', color: '#1a1a2e' },
  form: {},
  field: { marginBottom: '16px' },
  label: { display: 'block', fontSize: '13px', fontWeight: '600', color: '#374151', marginBottom: '6px' },
  hint: { color: '#9ca3af', fontSize: '12px', marginTop: '4px', display: 'block' },
  input: { width: '100%', padding: '10px 14px', border: '1.5px solid #e5e7eb', borderRadius: '8px', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
  infoRow: { display: 'flex', justifyContent: 'space-between', padding: '12px 0', borderTop: '1px solid #f3f4f6', fontSize: '14px', color: '#4b5563', marginBottom: '16px' },
  infoLabel: { fontWeight: '600', color: '#374151' },
  btnPrimary: { padding: '10px 24px', background: '#5f9ea0', color: '#fff', border: 'none', borderRadius: '8px', fontWeight: '600', fontSize: '14px', cursor: 'pointer' },
  btnDanger: { padding: '10px 24px', background: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca', borderRadius: '8px', fontWeight: '600', fontSize: '14px', cursor: 'pointer' },
  grid2: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
  emptyState: { textAlign: 'center', padding: '40px', color: '#6b7280' },
  addressList: { display: 'flex', flexDirection: 'column', gap: '12px' },
  addressCard: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px', border: '1.5px solid #e5e7eb', borderRadius: '10px' },
  defaultBadge: { marginLeft: '8px', background: '#dbeafe', color: '#2563eb', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: '600' },
  deleteAddrBtn: { background: 'none', border: 'none', fontSize: '18px', cursor: 'pointer', padding: '4px' },
};
