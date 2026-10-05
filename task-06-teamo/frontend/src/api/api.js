import axios from 'axios';

const API = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token if available
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('teamo_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Response Interceptor: Handle 401 Unauthorized globally
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Optional: Auto-logout on expired/invalid token if not on login page
      if (window.location.pathname !== '/login') {
        localStorage.removeItem('teamo_token');
        localStorage.removeItem('teamo_user');
      }
    }
    return Promise.reject(error);
  }
);

export default API;

