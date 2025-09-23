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
  status: 'AVAILABLE' | 'SOLD' | 'RESERVED';
  createdAt: string;
  updatedAt: string;
}

export interface CreateTicketRequest {
  title: string;
  description: string;
  price: number;
  eventId: string;
}

export interface UpdateTicketRequest {
  title?: string;
  description?: string;
  price?: number;
  status?: 'AVAILABLE' | 'SOLD' | 'RESERVED';
}

export interface Order {
  id: string;
  userId: string;
  ticketId: string;
  quantity: number;
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}