import apiService from './api';
import type { 
  LoginRequest, 
  RegisterRequest, 
  AuthResponse, 
  User 
} from '../types/auth';

export const authService = {
  // Login user
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await apiService.post<AuthResponse>('/auth/login', credentials);
      
      if (response.token) {
        this.storeAuthData(response);
      }
      
      return response;
    } catch (error: any) {
      throw new Error(error.message || 'Login failed');
    }
  },

  // Register new user
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    try {
      console.log('Sending registration request:', userData);
      console.log('API Base URL:', apiService.defaults?.baseURL);
      
      const response = await apiService.post<AuthResponse>('/auth/register', userData);
      console.log('Registration response:', response);
      
      if (response.token) {
        this.storeAuthData(response);
      }
      
      return response;
    } catch (error: any) {
      console.error('Registration error details:', error);
      console.error('Error response:', error.response);
      console.error('Error message:', error.message);
      throw new Error(error.message || 'Registration failed');
    }
  },

  // Get current user profile
  async getCurrentUser(): Promise<User> {
    const token = apiService.getToken();
    if (!token) {
      throw new Error('No authentication token found');
    }

    try {
      // Decode JWT token to get user info
      const payload = JSON.parse(atob(token.split('.')[1]));
      return {
        id: payload.sub,
        username: payload.username,
        email: payload.email,
        role: payload.role,
      };
    } catch (error) {
      throw new Error('Invalid token format');
    }
  },

  // Logout (client-side only for JWT)
  logout(): void {
    apiService.removeToken();
    localStorage.removeItem('user');
  },

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return apiService.isAuthenticated();
  },

  // Get stored user data
  getStoredUser(): User | null {
    const userData = localStorage.getItem('user');
    return userData ? JSON.parse(userData) : null;
  },

  // Store auth data
  storeAuthData(authResponse: AuthResponse): void {
    if (authResponse.token) {
      apiService.setToken(authResponse.token);
    }
    
    // Create user object from backend response
    if (authResponse.username) {
      const user: User = {
        id: authResponse.username, // Using username as ID since backend doesn't provide separate ID
        username: authResponse.username,
        email: '', // Backend doesn't return email in auth response
        role: (authResponse as any).role || 'USER' // Backend returns role separately
      };
      localStorage.setItem('user', JSON.stringify(user));
    }
  }
};