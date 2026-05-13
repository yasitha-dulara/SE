import axios from 'axios';

const API = axios.create({ baseURL: '/api' });

API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Auth
export const register = (data) => API.post('/auth/register', data);
export const login    = (data) => API.post('/auth/login', data);

// Profile
export const getProfile     = ()     => API.get('/auth/profile');
export const updateProfile  = (data) => API.put('/auth/profile', data);
export const changePassword = (data) => API.put('/auth/change-password', data);

// Addresses
export const addAddress    = (data) => API.post('/auth/addresses', data);
export const deleteAddress = (id)   => API.delete(`/auth/addresses/${id}`);

// Account
export const deleteAccount = () => API.delete('/auth/account');

// Payments (customer)
export const submitPayment  = (data) => API.post('/payments', data);
export const getMyPayments  = ()     => API.get('/payments');
export const getPayment     = (id)   => API.get(`/payments/${id}`);
export const cancelPayment  = (id)   => API.put(`/payments/${id}/cancel`);

// Admin - users
export const adminGetUsers      = ()              => API.get('/admin/users');
export const adminGetUser       = (id)            => API.get(`/admin/users/${id}`);
export const adminChangeRole    = (id, role)      => API.put(`/admin/users/${id}/role`, { role });
export const adminDeleteUser    = (id)            => API.delete(`/admin/users/${id}`);
export const adminCreateAdmin   = (data)          => API.post('/admin/users/create-admin', data);

// Admin - payments
export const adminGetPayments       = ()           => API.get('/admin/payments');
export const adminUpdatePayStatus   = (id, status) => API.put(`/admin/payments/${id}/status`, { status });
export const adminDeletePayment     = (id)         => API.delete(`/admin/payments/${id}`);
