import React, { createContext, useContext, useState, useEffect } from 'react';
import type { User, LoginCredentials, RegisterCredentials } from '../types/auth';
import { api } from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (credentials: RegisterCredentials) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [refreshToken, setRefreshToken] = useState<string | null>(() => localStorage.getItem('refreshToken'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = async () => {
      const savedToken = localStorage.getItem('token');
      if (savedToken) {
        try {
          const res = await api.get('/auth/me');
          const userData = res.data.data ? res.data.data : res.data;
          setUser(userData);
          localStorage.setItem('user', JSON.stringify(userData));
        } catch (err) {
          console.error('Failed to verify token', err);
          logout();
        }
      }
      setIsLoading(false);
    };
    initAuth();
  }, []);

  const login = async (credentials: LoginCredentials) => {
    const res = await api.post('/auth/login', credentials);
    const authData = res.data.data ? res.data.data : res.data;
    const { accessToken, refreshToken: refToken, user: userData } = authData;

    setToken(accessToken);
    setRefreshToken(refToken);
    setUser(userData);

    localStorage.setItem('token', accessToken);
    if (refToken) localStorage.setItem('refreshToken', refToken);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const register = async (credentials: RegisterCredentials) => {
    const res = await api.post('/auth/register', credentials);
    const authData = res.data.data ? res.data.data : res.data;
    const { accessToken, refreshToken: refToken, user: userData } = authData;

    setToken(accessToken);
    setRefreshToken(refToken);
    setUser(userData);

    localStorage.setItem('token', accessToken);
    if (refToken) localStorage.setItem('refreshToken', refToken);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const logout = () => {
    setToken(null);
    setRefreshToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        refreshToken,
        isAuthenticated: !!token && !!user,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
