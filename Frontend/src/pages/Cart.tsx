import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Calendar, MapPin, ShoppingCart, Trash2, Tag } from 'lucide-react';
import { useCart } from '../components/CartProvider';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';

export const Cart: React.FC = () => {
  const { items, removeFromCart, subtotal } = useCart();
  const navigate = useNavigate();

  if (items.length === 0) {
    return (
      <div className="max-w-3xl mx-auto">
        <h1 className="text-3xl font-bold text-secondary-900 mb-8">Your Cart</h1>
        <EmptyState
          icon={<ShoppingCart className="h-6 w-6" />}
          title="Your cart is empty"
          description="Browse tickets and add some to get started."
          action={
            <Link to="/tickets">
              <Button>Browse Tickets</Button>
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-3xl font-bold text-secondary-900 mb-8">Your Cart</h1>

      <div className="space-y-4 mb-6">
        {items.map((item) => (
          <div key={item.ticketId} className="card flex justify-between items-start">
            <div>
              <h3 className="text-lg font-semibold text-secondary-900">{item.eventName}</h3>
              <div className="flex items-center gap-4 mt-1.5 text-sm text-secondary-500">
                <span className="flex items-center gap-1.5">
                  <Tag className="h-3.5 w-3.5" />
                  {item.category}
                </span>
                <span className="flex items-center gap-1.5">
                  <MapPin className="h-3.5 w-3.5" />
                  {item.location}
                </span>
                <span className="flex items-center gap-1.5">
                  <Calendar className="h-3.5 w-3.5" />
                  {new Date(item.eventDate).toLocaleDateString()}
                </span>
              </div>
              {item.seatInfo && <p className="mt-1 text-sm text-secondary-500">Seat: {item.seatInfo}</p>}
            </div>

            <div className="flex flex-col items-end gap-3">
              <span className="text-xl font-bold text-primary-600">${item.price}</span>
              <button
                onClick={() => removeFromCart(item.ticketId)}
                className="flex items-center gap-1.5 text-sm text-red-600 hover:text-red-700"
              >
                <Trash2 className="h-4 w-4" />
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="card flex justify-between items-center">
        <div>
          <p className="text-sm text-secondary-500">Subtotal ({items.length} {items.length === 1 ? 'ticket' : 'tickets'})</p>
          <p className="text-2xl font-bold text-secondary-900">${subtotal.toFixed(2)}</p>
        </div>
        <Button size="lg" onClick={() => navigate('/checkout')}>
          Proceed to Checkout
        </Button>
      </div>
    </div>
  );
};
