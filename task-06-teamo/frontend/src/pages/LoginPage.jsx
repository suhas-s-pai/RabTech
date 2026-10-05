import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { CheckSquare, Shield, User, Mail, UserPlus, LogIn, KeyRound } from 'lucide-react';

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
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', color: '#4f46e5', marginBottom: '0.5rem' }}>
            <CheckSquare size={36} />
            <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Teamo</h1>
          </div>
          <p style={{ color: '#64748b', fontSize: '0.925rem' }}>
            Team Task Management & Code Review Platform
          </p>
        </div>

        {/* Professional Demo Credentials Note */}
        <div style={{ background: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: '8px', padding: '0.85rem 1rem', marginBottom: '1.25rem', fontSize: '0.825rem', color: '#475569' }}>
          <div style={{ fontWeight: 700, color: '#334155', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <KeyRound size={14} color="#4f46e5" /> Demo Credentials
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', lineHeight: '1.4' }}>
            <div style={{ background: '#ffffff', padding: '0.5rem 0.65rem', borderRadius: '6px', border: '1px solid #f1f5f9' }}>
              <div style={{ fontWeight: 600, color: '#1e293b', marginBottom: '0.15rem', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                <Shield size={12} color="#4f46e5" /> Manager:
              </div>
              <div style={{ fontFamily: 'monospace', fontSize: '0.775rem', color: '#334155' }}>krishna@teamo.com</div>
              <div style={{ fontFamily: 'monospace', fontSize: '0.75rem', color: '#64748b' }}>password123</div>
            </div>
            <div style={{ background: '#ffffff', padding: '0.5rem 0.65rem', borderRadius: '6px', border: '1px solid #f1f5f9' }}>
              <div style={{ fontWeight: 600, color: '#1e293b', marginBottom: '0.15rem', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                <User size={12} color="#10b981" /> Employee:
              </div>
              <div style={{ fontFamily: 'monospace', fontSize: '0.775rem', color: '#334155' }}>suhas@teamo.com</div>
              <div style={{ fontFamily: 'monospace', fontSize: '0.75rem', color: '#64748b' }}>password123</div>
            </div>
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
              placeholder="name@teamo.com"
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

