export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface ApiError {
  message: string;
  status?: number;
  code?: string;
}

export interface LoadingState {
  isLoading: boolean;
  error: string | null;
}

export interface Ticket {
  id: string;
  title: string;
  description: string;
  price: number;
  eventId: string;
  eventName?: string;
  userId: string;
  status: 'AVAILABLE' | 'SOLD' | 'RESERVED';
  createdAt: string;
  updatedAt: string;
}

export interface CreateTicketRequest {
  eventName: string;
  description: string;
  price: number;
  userId: string;
}

export interface UpdateTicketRequest {
  title?: string;
  description?: string;
  price?: number;
  status?: 'AVAILABLE' | 'SOLD' | 'RESERVED';
}

export interface TicketResponse extends Ticket {}

export interface TicketFormData {
  eventName: string;
  description: string;
  price: number;
}

export interface Order {
  id: string;
  userId: string;
  ticketId: string;
  quantity: number;
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'APPROVED';
  expiresAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface OrderResponse extends Order {}

export interface CreateOrderRequest {
  ticketId: string;
  userId: string;
  quantity: number;
}