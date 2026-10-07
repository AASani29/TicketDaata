import apiService from './api';
import type {
  Order,
  CreateOrderRequest,
  OrderResponse,
  ApiError
} from '../types/api';

export const orderService = {
  // Get all orders for a user (as buyer)
  async getOrdersByUser(userId: string): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(`/api/orders/user/${userId}`);
    } catch (error) {
      throw new Error((error as ApiError).message || 'Failed to fetch user orders');
    }
  },

  // Get orders where user is the seller
  async getOrdersBySeller(sellerId: string): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(`/api/orders/seller/${sellerId}`);
    } catch (error) {
      throw new Error((error as ApiError).message || 'Failed to fetch seller orders');
    }
  },

  // Get single order by ID
  async getOrderById(id: string): Promise<Order> {
    try {
      return await apiService.get<Order>(`/api/orders/${id}`);
    } catch (error) {
      throw new Error((error as ApiError).message || 'Failed to fetch order');
    }
  },

  // Create new order
  async createOrder(orderData: CreateOrderRequest): Promise<OrderResponse> {
    try {
      return await apiService.post<OrderResponse>('/api/orders', orderData);
    } catch (error) {
      throw new Error((error as ApiError).message || 'Failed to create order');
    }
  },

  // Pay for a pending order (buyer action) - completes the purchase
  async payOrder(id: string): Promise<OrderResponse> {
    try {
      const paymentId = crypto.randomUUID();
      return await apiService.post<OrderResponse>(`/api/orders/${id}/complete`, { paymentId });
    } catch (error) {
      throw new Error((error as ApiError).message || 'Payment failed');
    }
  },

  // Cancel a pending order (either party)
  async cancelOrder(id: string, reason = 'Cancelled by user'): Promise<OrderResponse> {
    try {
      return await apiService.post<OrderResponse>(`/api/orders/${id}/cancel`, { reason });
    } catch (error) {
      throw new Error((error as ApiError).message || 'Failed to cancel order');
    }
  }
};
