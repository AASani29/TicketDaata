import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthContext } from './AuthProvider';
import { Spinner } from './ui/Spinner';

interface PrivateRouteProps {
  children: React.ReactNode;
}

export const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuthContext();

  if (isLoading) {
    return <Spinner fullPage size="lg" />;
  }

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};
