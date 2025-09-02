import React from 'react';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  // This would typically check authentication status
  // For now, just return children - will be implemented with AuthContext
  return <>{children}</>;
};

export default ProtectedRoute;
