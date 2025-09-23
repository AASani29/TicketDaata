import apiService from './api';
import type { 
  Order, 
  CreateOrderRequest, 
  OrderResponse 
} from '../types/api';

export const orderService = {
  // Get all orders for a user
  async getOrdersByUser(userId: string): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(`/api/orders/user/${userId}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch user orders');
    }
  },

  // Get orders where user is the seller
  async getOrdersBySeller(sellerId: string): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(`/api/orders/seller/${sellerId}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch seller orders');
    }
  },

  // Get single order by ID
  async getOrderById(id: string): Promise<Order> {
    try {
      return await apiService.get<Order>(`/api/orders/${id}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch order');
    }
  },

  // Create new order
  async createOrder(orderData: CreateOrderRequest): Promise<OrderResponse> {
    try {
      return await apiService.post<OrderResponse>('/api/orders', orderData);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to create order');
    }
  },

  // Approve order (seller action)
  async approveOrder(id: string): Promise<OrderResponse> {
    try {
      return await apiService.put<OrderResponse>(`/api/orders/${id}/approve`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to approve order');
    }
  },

  // Reject order (seller action)
  async rejectOrder(id: string): Promise<OrderResponse> {
    try {
      return await apiService.put<OrderResponse>(`/api/orders/${id}/reject`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to reject order');
    }
  },

  // Cancel order (buyer action)
  async cancelOrder(id: string): Promise<OrderResponse> {
    try {
      return await apiService.put<OrderResponse>(`/api/orders/${id}/cancel`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to cancel order');
    }
  },

  // Simulate payment
  async simulatePayment(orderId: string): Promise<OrderResponse> {
    try {
      return await apiService.post<OrderResponse>(`/api/orders/${orderId}/payment`);
    } catch (error: any) {
      throw new Error(error.message || 'Payment failed');
    }
  }
};