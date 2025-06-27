import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  // Kiểm tra trạng thái đăng nhập từ localStorage khi khởi động
  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    const storedToken = localStorage.getItem('token');

    if (storedUser && storedToken) {
        setUser(JSON.parse(storedUser));
        setIsAuthenticated(true);
    }else{
        setUser(null);
        setIsAuthenticated(false);
    }
    setLoading(false);
  }, []);

  // Hàm đăng nhập
  const login = (email, password) => {
    // Logic giả lập đăng nhập (thay bằng API call thực tế)
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const mockUser = { id: 1, email, name: 'Test User' };
        if (email === 'test@example.com' && password === 'password123') {
          setUser(mockUser);
          localStorage.setItem('user', JSON.stringify(mockUser));
          localStorage.setItem('token', 'mockToken'); // Giả lập token
          setIsAuthenticated(true);
          resolve(mockUser);
        } else {
          reject(new Error('Invalid credentials'));
        }
      }, 1000);
    });
  };

  // Hàm đăng ký
  const register = (fullName, email, password) => {
    // Logic giả lập đăng ký (thay bằng API call thực tế)
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const newUser = { id: Date.now(), fullName, email };
        setUser(newUser);
        localStorage.setItem('user', JSON.stringify(newUser));
        resolve(newUser);
      }, 1000);
    });
  };

  // Hàm đăng xuất
  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    setIsAuthenticated(false);
  };

  const value = {
    isAuthenticated,
    user,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{!loading && children}</AuthContext.Provider>;
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};