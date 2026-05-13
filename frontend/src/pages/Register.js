import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const navigate = useNavigate();
  const { loginUser } = useAuth();
  const [form, setForm] = useState({ name: '', email: '', password: '', phoneNumber: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await register(form);
      loginUser(res.data.token, res.data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.logo}>🍔</div>
        <h2 style={styles.title}>Create Account</h2>
        <p style={styles.subtitle}>Join Food Ordering System</p>

        {error && <div style={styles.error}>{error}</div>}

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.field}>
            <label style={styles.label}>Full Name</label>
            <input
              name="name"
              type="text"
              placeholder="Yasitha Dulara"
              value={form.name}
              onChange={handleChange}
              required
              style={styles.input}
            />
          </div>

          <div style={styles.field}>
            <label style={styles.label}>Email Address</label>
            <input
              name="email"
              type="email"
              placeholder="yasitha@email.com"
              value={form.email}
              onChange={handleChange}
              required
              style={styles.input}
            />
          </div>

          <div style={styles.field}>
            <label style={styles.label}>Password</label>
            <input
              name="password"
              type="password"
              placeholder="At least 6 characters"
              value={form.password}
              onChange={handleChange}
              required
              style={styles.input}
            />
          </div>

          <div style={styles.field}>
            <label style={styles.label}>Phone Number <span style={styles.optional}>(optional)</span></label>
            <input
              name="phoneNumber"
              type="tel"
              placeholder="+94 70 559 6481"
              value={form.phoneNumber}
              onChange={handleChange}
              style={styles.input}
            />
          </div>

          <button type="submit" disabled={loading} style={styles.button}>
            {loading ? 'Creating Account...' : 'Register'}
          </button>
        </form>

        <p style={styles.link}>
          Already have an account? <Link to="/login" style={styles.anchor}>Login here</Link>
        </p>
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #5f9ea0 0%, #3d7a8a 100%)',
    padding: '20px',
  },
  card: {
    background: '#fff',
    borderRadius: '16px',
    padding: '40px',
    width: '100%',
    maxWidth: '420px',
    boxShadow: '0 20px 60px rgba(0,0,0,0.15)',
    textAlign: 'center',
  },
  logo: { fontSize: '48px', marginBottom: '12px' },
  title: { margin: '0 0 4px', fontSize: '24px', fontWeight: '700', color: '#1a1a2e' },
  subtitle: { margin: '0 0 24px', color: '#888', fontSize: '14px' },
  error: {
    background: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca',
    padding: '10px 14px', borderRadius: '8px', marginBottom: '16px', fontSize: '14px',
  },
  form: { textAlign: 'left' },
  field: { marginBottom: '16px' },
  label: { display: 'block', fontSize: '13px', fontWeight: '600', color: '#374151', marginBottom: '6px' },
  optional: { fontWeight: '400', color: '#9ca3af' },
  input: {
    width: '100%', padding: '10px 14px', border: '1.5px solid #e5e7eb',
    borderRadius: '8px', fontSize: '15px', outline: 'none', boxSizing: 'border-box',
    transition: 'border-color 0.2s',
  },
  button: {
    width: '100%', padding: '12px', background: '#5f9ea0', color: '#fff',
    border: 'none', borderRadius: '8px', fontSize: '16px', fontWeight: '600',
    cursor: 'pointer', marginTop: '8px', transition: 'background 0.2s',
  },
  link: { marginTop: '20px', fontSize: '14px', color: '#666' },
  anchor: { color: '#5f9ea0', fontWeight: '600', textDecoration: 'none' },
};
