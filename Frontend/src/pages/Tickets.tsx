import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, MapPin, Tag, PlusCircle, TicketX, ShoppingCart, Check } from 'lucide-react';
import { useAuthContext } from '../components/AuthProvider';
import { useCart } from '../components/CartProvider';
import { ticketService } from '../services/ticketService';
import { useToast } from '../components/ui/Toast';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import type { Ticket } from '../types/api';

const STATUS_FILTERS = ['AVAILABLE', 'RESERVED', 'SOLD', 'ALL'] as const;

export const Tickets: React.FC = () => {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<string>('AVAILABLE');
  const { user, isAuthenticated } = useAuthContext();
  const { addToCart, isInCart } = useCart();
  const { showToast } = useToast();

  const loadTickets = useCallback(async () => {
    try {
      setIsLoading(true);
      const data = statusFilter === 'ALL'
        ? await ticketService.getAllTickets()
        : await ticketService.getTicketsByStatus(statusFilter);
      setTickets(data);
    } catch (err) {
      showToast('error', err instanceof Error ? err.message : 'Failed to load tickets');
    } finally {
      setIsLoading(false);
    }
  }, [statusFilter, showToast]);

  useEffect(() => {
    loadTickets();
  }, [loadTickets]);

  const handleAddToCart = (ticket: Ticket) => {
    addToCart({
      ticketId: ticket.id,
      eventName: ticket.eventName,
      category: ticket.category,
      location: ticket.location,
      eventDate: ticket.eventDate,
      seatInfo: ticket.seatInfo,
      price: ticket.price,
      sellerId: ticket.sellerId
    });
    showToast('success', `${ticket.eventName} added to cart.`);
  };

  if (isLoading) {
    return <Spinner fullPage size="lg" />;
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-secondary-900">Browse Tickets</h1>
        {isAuthenticated && (
          <Link to="/create-ticket">
            <Button icon={<PlusCircle className="h-4 w-4" />}>Sell a Ticket</Button>
          </Link>
        )}
      </div>

      <div className="mb-6">
        <label className="block text-sm font-medium text-secondary-700 mb-2">
          Filter by status
        </label>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="input-field max-w-xs"
        >
          {STATUS_FILTERS.map((status) => (
            <option key={status} value={status}>
              {status === 'ALL' ? 'All' : status.charAt(0) + status.slice(1).toLowerCase()}
            </option>
          ))}
        </select>
      </div>

      {tickets.length === 0 ? (
        <EmptyState
          icon={<TicketX className="h-6 w-6" />}
          title="No tickets found"
          description="There are no tickets matching this filter right now."
          action={
            isAuthenticated ? (
              <Link to="/create-ticket">
                <Button icon={<PlusCircle className="h-4 w-4" />}>Create the first ticket</Button>
              </Link>
            ) : undefined
          }
        />
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tickets.map((ticket) => (
            <div key={ticket.id} className="card flex flex-col">
              <div className="flex justify-between items-start mb-3">
                <h3 className="text-lg font-semibold text-secondary-900">{ticket.eventName}</h3>
                <Badge status={ticket.status} />
              </div>

              <div className="space-y-1.5 text-sm text-secondary-600 mb-4">
                <div className="flex items-center gap-2">
                  <Tag className="h-4 w-4 text-secondary-400" />
                  {ticket.category}
                </div>
                <div className="flex items-center gap-2">
                  <MapPin className="h-4 w-4 text-secondary-400" />
                  {ticket.location}
                </div>
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-secondary-400" />
                  {new Date(ticket.eventDate).toLocaleString()}
                </div>
                {ticket.seatInfo && <p className="pl-6 text-secondary-500">Seat: {ticket.seatInfo}</p>}
              </div>

              <div className="mt-auto flex justify-between items-center pt-4 border-t border-secondary-100">
                <span className="text-2xl font-bold text-primary-600">${ticket.price}</span>

                {isAuthenticated && ticket.status === 'AVAILABLE' && user?.id !== ticket.userId && (
                  isInCart(ticket.id) ? (
                    <Button variant="secondary" icon={<Check className="h-4 w-4" />} disabled>
                      In Cart
                    </Button>
                  ) : (
                    <Button icon={<ShoppingCart className="h-4 w-4" />} onClick={() => handleAddToCart(ticket)}>
                      Add to Cart
                    </Button>
                  )
                )}

                {user?.id === ticket.userId && (
                  <span className="text-sm text-secondary-500">Your ticket</span>
                )}

                {!isAuthenticated && (
                  <Link to="/login">
                    <Button variant="secondary">Login to Buy</Button>
                  </Link>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
