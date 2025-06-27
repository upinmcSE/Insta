import React from 'react'
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const RouteGurad = ({children, requireAuth = true }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="container">
        <div className="spinner"></div>
      </div>
    );
  }

  if (requireAuth && !isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!requireAuth && isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
  
}

const styles = `
  .container {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .spinner {
    animation: spin 1s linear infinite;
    border-radius: 50%;
    height: 48px;
    width: 48px;
    border-top: 2px solid #0095f6; /* Màu Instagram blue */
    border-bottom: 2px solid #0095f6; /* Màu Instagram blue */
    border-left: 2px solid transparent;
    border-right: 2px solid transparent;
  }

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;

const styleSheet = document.createElement("style");
styleSheet.textContent = styles;
document.head.appendChild(styleSheet);

export default RouteGurad
