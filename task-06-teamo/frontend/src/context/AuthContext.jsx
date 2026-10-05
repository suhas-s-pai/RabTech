import React, { createContext, useContext, useState, useEffect } from 'react';
import API from '../api/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('teamo_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [token, setToken] = useState(() => localStorage.getItem('teamo_token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      // Validate current token / get current user
      API.get('/auth/me')
        .then((res) => {
          setUser(res.data);
          localStorage.setItem('teamo_user', JSON.stringify(res.data));
        })
        .catch(() => {
          logout();
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const res = await API.post('/auth/login', { email, password });
    const { token: jwtToken, user: userData } = res.data;

    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('teamo_token', jwtToken);
    localStorage.setItem('teamo_user', JSON.stringify(userData));
    return userData;
  };

  const register = async (name, email, password, role) => {
    const res = await API.post('/auth/register', { name, email, password, role });
    const { token: jwtToken, user: userData } = res.data;

    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('teamo_token', jwtToken);
    localStorage.setItem('teamo_user', JSON.stringify(userData));
    return userData;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('teamo_token');
    localStorage.removeItem('teamo_user');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

