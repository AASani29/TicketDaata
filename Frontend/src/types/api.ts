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

export type TicketStatus = 'AVAILABLE' | 'RESERVED' | 'SOLD';

export interface Ticket {
  id: string;
  eventName: string;
  category: string;
  location: string;
  eventDate: string;
  seatInfo?: string;
  price: number;
  status: TicketStatus;
  userId: string;
  sellerId: string;
  version?: number;
}

export interface CreateTicketRequest {
  eventName: string;
  category: string;
  location: string;
  eventDate: string;
  seatInfo?: string;
  price: number;
  userId: string;
  sellerId: string;
}

export type TicketResponse = Ticket;

export interface TicketFormData {
  eventName: string;
  category: string;
  location: string;
  eventDate: string;
  seatInfo: string;
  price: number;
}

export type OrderStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED' | 'EXPIRED';

export interface Order {
  id: string;
  userId: string;
  ticketId: string;
  ticketTitle?: string;
  eventName?: string;
  eventDate?: string;
  seatInfo?: string;
  price?: number;
  quantity: number;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
  expiresAt?: string;
  paymentId?: string;
  sellerId?: string;
  sellerUsername?: string;
  cancellationReason?: string;
  timeRemainingMinutes?: number;
}

export type OrderResponse = Order;

export interface CreateOrderRequest {
  ticketId: string;
  userId: string;
  quantity: number;
}

export interface UserBalance {
  username: string;
  balance: number;
}

export interface CartItem {
  ticketId: string;
  eventName: string;
  category: string;
  location: string;
  eventDate: string;
  seatInfo?: string;
  price: number;
  sellerId: string;
}
