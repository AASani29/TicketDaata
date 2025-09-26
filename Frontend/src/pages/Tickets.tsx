import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuthContext } from '../components/AuthProvider';
import { ticketService } from '../services/ticketService';
import { orderService } from '../services/orderService';
import type { Ticket } from '../types/api';

export const Tickets: React.FC = () => {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('AVAILABLE');
  const { user, isAuthenticated } = useAuthContext();

  useEffect(() => {
    loadTickets();
  }, [statusFilter]);

  const loadTickets = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = statusFilter === 'ALL' 
        ? await ticketService.getAllTickets()
        : await ticketService.getTicketsByStatus(statusFilter);
      setTickets(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load tickets');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBuyTicket = async (ticketId: string) => {
    if (!user) return;
    
    try {
      await orderService.createOrder({
        ticketId,
        userId: user.id,
        quantity: 1
      });
      alert('Order created successfully! Check your orders page.');
      loadTickets(); // Refresh to show updated status
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create order');
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Browse Tickets</h1>
        {isAuthenticated && (
          <Link to="/create-ticket" className="btn-primary">
            Sell a Ticket
          </Link>
        )}
      </div>

      {/* Filter Options */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Filter by Status
        </label>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="input-field max-w-xs"
        >
          <option value="AVAILABLE">Available</option>
          <option value="RESERVED">Reserved</option>
          <option value="SOLD">Sold</option>
          <option value="ALL">All</option>
        </select>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
          {error}
        </div>
      )}

      {tickets.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No tickets found</p>
          {isAuthenticated && (
            <Link to="/create-ticket" className="btn-primary mt-4 inline-block">
              Create the First Ticket
            </Link>
          )}
        </div>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tickets.map((ticket) => (
            <div key={ticket.id} className="card">
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-xl font-semibold text-gray-900">
                  {ticket.eventName}
                </h3>
                <span
                  className={`px-2 py-1 text-xs font-medium rounded-full ${
                    ticket.status === 'AVAILABLE'
                      ? 'bg-green-100 text-green-800'
                      : ticket.status === 'RESERVED'
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-red-100 text-red-800'
                  }`}
                >
                  {ticket.status}
                </span>
              </div>
              
              <p className="text-gray-600 mb-4">{ticket.description}</p>
              
              <div className="flex justify-between items-center">
                <span className="text-2xl font-bold text-blue-600">
                  ${ticket.price}
                </span>
                
                {isAuthenticated && ticket.status === 'AVAILABLE' && user?.id !== ticket.userId && (
                  <button
                    onClick={() => handleBuyTicket(ticket.id)}
                    className="btn-primary"
                  >
                    Buy Now
                  </button>
                )}
                
                {user?.id === ticket.userId && (
                  <span className="text-sm text-gray-500">Your ticket</span>
                )}
                
                {!isAuthenticated && (
                  <Link to="/login" className="btn-secondary">
                    Login to Buy
                  </Link>
                )}
              </div>
              
              <div className="mt-4 text-sm text-gray-500">
                <p>Posted: {new Date(ticket.createdAt).toLocaleDateString()}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};