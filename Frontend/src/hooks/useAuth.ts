import { useState, useEffect } from 'react';
import { authService } from '../services/authService';
import type { User, LoginRequest, RegisterRequest } from '../types/auth';

export const useAuth = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Check if user is already authenticated on mount
    const storedUser = authService.getStoredUser();
    if (storedUser && authService.isAuthenticated()) {
      setUser(storedUser);
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginRequest) => {
    try {
      setIsLoading(true);
      setError(null);
      const authResponse = await authService.login(credentials);
      authService.storeAuthData(authResponse);
      
      // Create user object from backend response
      if (authResponse.username) {
        const user: User = {
          id: authResponse.username,
          username: authResponse.username,
          email: '',
          role: (authResponse as any).role || 'USER'
        };
        setUser(user);
      }
      
      return authResponse;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Login failed';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (userData: RegisterRequest) => {
    try {
      setIsLoading(true);
      setError(null);
      const authResponse = await authService.register(userData);
      authService.storeAuthData(authResponse);
      
      // Create user object from backend response
      if (authResponse.username) {
        const user: User = {
          id: authResponse.username,
          username: authResponse.username,
          email: userData.email, // Use email from registration data
          role: (authResponse as any).role || 'USER'
        };
        setUser(user);
      }
      
      return authResponse;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Registration failed';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setError(null);
  };

  return {
    user,
    isLoading,
    error,
    login,
    register,
    logout,
    isAuthenticated: !!user
  };
};