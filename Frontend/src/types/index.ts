// User types
export interface User {
  id: string;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

// Ticket types
export interface Ticket {
  id: string;
  eventName: string;
  venue: string;
  date: string;
  seatSection: string;
  seatRow: string;
  seatNumber: string;
  description: string;
  price: number;
  originalPrice: number;
  userId: string;
  sellerId: string;
  sellerUsername: string;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD';
  category: 'CONCERT' | 'SPORTS' | 'THEATER' | 'OTHER';
  createdAt: string;
  updatedAt?: string;
}

export interface CreateTicketRequest {
  eventName: string;
  venue: string;
  date: string;
  seatSection: string;
  seatRow: string;
  seatNumber: string;
  description: string;
  price: number;
  originalPrice: number;
  category: 'CONCERT' | 'SPORTS' | 'THEATER' | 'OTHER';
}

export interface TicketResponse {
  success: boolean;
  message: string;
  ticket: Ticket;
}

// Order types
export interface Order {
  id: string;
  ticketId: string;
  buyerId: string;
  sellerId: string;
  totalAmount: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED';
  expiresAt: string;
  createdAt: string;
  updatedAt: string;
  ticket?: Ticket;
}

export interface CreateOrderRequest {
  ticketId: string;
  userId: string;
  quantity: number;
}

export interface OrderResponse {
  id: string;
  ticketId: string;
  buyerId: string;
  sellerId: string;
  totalAmount: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED';
  expiresAt: string;
  createdAt: string;
  updatedAt: string;
}

// API Response types
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// Form types
export interface TicketFormData {
  eventName: string;
  description: string;
  price: number;
}

// UI State types
export interface LoadingState {
  isLoading: boolean;
  error: string | null;
}