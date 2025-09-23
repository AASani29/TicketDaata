import apiService from './api';
import type { 
  Ticket, 
  CreateTicketRequest, 
  TicketResponse
} from '../types/api';

export const ticketService = {
  // Get all available tickets
  async getAllTickets(): Promise<Ticket[]> {
    try {
      return await apiService.get<Ticket[]>('/api/tickets');
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch tickets');
    }
  },

  // Get tickets by status
  async getTicketsByStatus(status: string): Promise<Ticket[]> {
    try {
      return await apiService.get<Ticket[]>(`/api/tickets/status/${status}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch tickets by status');
    }
  },

  // Get tickets by user
  async getTicketsByUser(userId: string): Promise<Ticket[]> {
    try {
      return await apiService.get<Ticket[]>(`/api/tickets/user/${userId}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch user tickets');
    }
  },

  // Get single ticket by ID
  async getTicketById(id: string): Promise<Ticket> {
    try {
      return await apiService.get<Ticket>(`/api/tickets/${id}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to fetch ticket');
    }
  },

  // Create new ticket
  async createTicket(ticketData: CreateTicketRequest): Promise<TicketResponse> {
    try {
      return await apiService.post<TicketResponse>('/api/tickets', ticketData);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to create ticket');
    }
  },

  // Update ticket status
  async updateTicketStatus(id: string, status: string): Promise<TicketResponse> {
    try {
      return await apiService.put<TicketResponse>(`/api/tickets/${id}/status`, { status });
    } catch (error: any) {
      throw new Error(error.message || 'Failed to update ticket status');
    }
  },

  // Delete ticket
  async deleteTicket(id: string): Promise<void> {
    try {
      await apiService.delete(`/api/tickets/${id}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to delete ticket');
    }
  },

  // Search tickets
  async searchTickets(query: string): Promise<Ticket[]> {
    try {
      return await apiService.get<Ticket[]>(`/api/tickets/search?q=${encodeURIComponent(query)}`);
    } catch (error: any) {
      throw new Error(error.message || 'Failed to search tickets');
    }
  }
};