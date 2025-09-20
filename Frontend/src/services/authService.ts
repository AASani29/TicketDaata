import type { 
  LoginRequest, 
  RegisterRequest, 
  AuthResponse, 
  User 
} from '../types';

export const authService = {
  // Login user
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    // TODO: Implement API call to backend
    throw new Error('Not implemented');
  },

  // Register new user
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    // TODO: Implement API call to backend
    throw new Error('Not implemented');
  },

  // Get current user profile
  async getCurrentUser(): Promise<User> {
    // TODO: Implement API call to backend
    throw new Error('Not implemented');
  },

  // Logout (client-side only for JWT)
  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  },

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  },

  // Get stored user data
  getStoredUser(): User | null {
    const userData = localStorage.getItem('user');
    return userData ? JSON.parse(userData) : null;
  },

  // Store auth data
  storeAuthData(authResponse: AuthResponse): void {
    localStorage.setItem('authToken', authResponse.token);
    localStorage.setItem('user', JSON.stringify(authResponse.user));
  }
};