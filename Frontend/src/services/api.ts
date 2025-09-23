import axios, { AxiosError } from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';

interface ApiError {
  message: string;
  status?: number;
  code?: string;
}

class ApiService {
  private api: AxiosInstance;
  private tokenKey = import.meta.env.VITE_JWT_STORAGE_KEY || 'ticketdaata_token';

  constructor() {
    this.api = axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor to add auth token
    this.api.interceptors.request.use(
      (config) => {
        console.log('Making API request:', {
          method: config.method,
          url: config.url,
          baseURL: config.baseURL,
          data: config.data,
          headers: config.headers
        });
        
        const token = this.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor for error handling
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log('API response received:', {
          status: response.status,
          statusText: response.statusText,
          data: response.data
        });
        return response;
      },
      (error: AxiosError) => {
        console.error('API error occurred:', {
          status: error.response?.status,
          statusText: error.response?.statusText,
          data: error.response?.data,
          message: error.message
        });
        
        const apiError: ApiError = {
          message: 'An error occurred',
          status: error.response?.status,
        };

        if (error.response?.data) {
          const errorData = error.response.data as any;
          apiError.message = errorData.message || errorData.error || 'An error occurred';
        } else if (error.message) {
          apiError.message = error.message;
        }

        // Handle 401 unauthorized
        if (error.response?.status === 401) {
          this.removeToken();
          window.location.href = '/login';
        }

        return Promise.reject(apiError);
      }
    );
  }

  // Token management
  public setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  public getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  public removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  public isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // HTTP methods
  public async get<T>(url: string): Promise<T> {
    const response = await this.api.get<T>(url);
    return response.data;
  }

  public async post<T>(url: string, data?: any): Promise<T> {
    const response = await this.api.post<T>(url, data);
    return response.data;
  }

  public async put<T>(url: string, data?: any): Promise<T> {
    const response = await this.api.put<T>(url, data);
    return response.data;
  }

  public async delete<T>(url: string): Promise<T> {
    const response = await this.api.delete<T>(url);
    return response.data;
  }

  // Direct service URLs for bypassing gateway if needed
  public getAuthServiceUrl(): string {
    return import.meta.env.VITE_AUTH_SERVICE_URL || 'http://localhost:9000';
  }

  public getTicketServiceUrl(): string {
    return import.meta.env.VITE_TICKET_SERVICE_URL || 'http://localhost:8082';
  }

  public getOrdersServiceUrl(): string {
    return import.meta.env.VITE_ORDERS_SERVICE_URL || 'http://localhost:8081';
  }
}

export const apiService = new ApiService();
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
export default apiService;