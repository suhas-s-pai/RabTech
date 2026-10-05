import React from 'react';
import { useAuth } from '../context/AuthContext';
import { CheckSquare, LogOut, User as UserIcon, Shield } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <CheckSquare size={28} />
        <span>Teamo</span>
      </div>

      {user && (
        <div className="nav-user">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            {user.role === 'MANAGER' ? <Shield size={18} color="#4f46e5" /> : <UserIcon size={18} color="#0f172a" />}
            <span style={{ fontWeight: 600 }}>{user.name}</span>
            <span className="badge-role">{user.role}</span>
          </div>

          <button onClick={logout} className="btn btn-outline" style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>
            <LogOut size={16} />
            Logout
          </button>
        </div>
      )}
    </nav>
  );
};

export default Navbar;

