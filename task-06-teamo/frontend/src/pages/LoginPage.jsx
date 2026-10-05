import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { CheckSquare, Shield, User, Lock, Mail, UserPlus, LogIn } from 'lucide-react';

const LoginPage = () => {
  const { login, register } = useAuth();
  const [isRegister, setIsRegister] = useState(false);

  // Form Fields
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('EMPLOYEE');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleQuickLogin = async (demoEmail, demoPassword) => {
    setLoading(true);
    setError('');
    try {
      await login(demoEmail, demoPassword);
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isRegister) {
        await register(name, email, password, role);
      } else {
        await login(email, password);
      }
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', color: '#4f46e5', marginBottom: '0.5rem' }}>
            <CheckSquare size={36} />
            <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Teamo</h1>
          </div>
          <p style={{ color: '#64748b', fontSize: '0.925rem' }}>
            Team Task Management & Code Review Platform
          </p>
        </div>

        {/* Quick Demo Accounts */}
        <div className="quick-demo-box">
          <p style={{ fontSize: '0.8rem', fontWeight: 700, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: '0.5rem' }}>
            ⚡ Instant Demo Login:
          </p>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
            <button
              type="button"
              onClick={() => handleQuickLogin('krishna@rabtech.com', 'password123')}
              className="btn btn-outline"
              style={{ fontSize: '0.8rem', padding: '0.4rem', justifyContent: 'flex-start', background: '#ffffff' }}
              disabled={loading}
            >
              <Shield size={14} color="#4f46e5" />
              <span><strong>Krishna</strong> (Manager)</span>
            </button>

            <button
              type="button"
              onClick={() => handleQuickLogin('suhas@rabtech.com', 'password123')}
              className="btn btn-outline"
              style={{ fontSize: '0.8rem', padding: '0.4rem', justifyContent: 'flex-start', background: '#ffffff' }}
              disabled={loading}
            >
              <User size={14} color="#10b981" />
              <span><strong>Suhas</strong> (Employee)</span>
            </button>
          </div>
        </div>

        {error && (
          <div style={{ background: '#fee2e2', color: '#b91c1c', padding: '0.75rem', borderRadius: '8px', marginBottom: '1.25rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}

        {/* Form Tabs */}
        <div style={{ display: 'flex', borderBottom: '1px solid #e2e8f0', marginBottom: '1.5rem' }}>
          <button
            type="button"
            onClick={() => { setIsRegister(false); setError(''); }}
            style={{
              flex: 1,
              padding: '0.75rem',
              background: 'none',
              borderBottom: !isRegister ? '2px solid #4f46e5' : 'none',
              color: !isRegister ? '#4f46e5' : '#64748b',
              fontWeight: 600,
              borderRadius: 0
            }}
          >
            <LogIn size={16} style={{ display: 'inline', marginRight: '0.3rem' }} />
            Login
          </button>

          <button
            type="button"
            onClick={() => { setIsRegister(true); setError(''); }}
            style={{
              flex: 1,
              padding: '0.75rem',
              background: 'none',
              borderBottom: isRegister ? '2px solid #4f46e5' : 'none',
              color: isRegister ? '#4f46e5' : '#64748b',
              fontWeight: 600,
              borderRadius: 0
            }}
          >
            <UserPlus size={16} style={{ display: 'inline', marginRight: '0.3rem' }} />
            Register
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {isRegister && (
            <div className="form-group">
              <label>Full Name *</label>
              <input
                type="text"
                placeholder="e.g. Krishna"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
          )}

          <div className="form-group">
            <label>Email Address *</label>
            <input
              type="email"
              placeholder="name@rabtech.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password *</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {isRegister && (
            <div className="form-group">
              <label>Account Role *</label>
              <select value={role} onChange={(e) => setRole(e.target.value)} required>
                <option value="EMPLOYEE">Employee (Task Assignee & Developer)</option>
                <option value="MANAGER">Manager (Task Creator & Reviewer)</option>
              </select>
            </div>
          )}

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem', padding: '0.75rem' }} disabled={loading}>
            {loading ? 'Processing...' : (isRegister ? 'Create Account' : 'Sign In')}
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;

