import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { CheckCircle2, Calendar, MapPin } from 'lucide-react';
import { Button } from '../components/ui/Button';
import type { Order } from '../types/api';

interface ConfirmationLocationState {
  orders: Order[];
}

export const Confirmation: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const state = location.state as ConfirmationLocationState | null;
  const orders = state?.orders ?? [];
  const total = orders.reduce((sum, order) => sum + order.totalAmount, 0);

  if (orders.length === 0) {
    navigate('/orders', { replace: true });
    return null;
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="text-center mb-8">
        <div className="mx-auto h-16 w-16 rounded-full bg-green-50 flex items-center justify-center mb-4">
          <CheckCircle2 className="h-8 w-8 text-green-600" />
        </div>
        <h1 className="text-3xl font-bold text-secondary-900">Payment confirmed</h1>
        <p className="text-secondary-600 mt-2">
          Your {orders.length > 1 ? 'tickets are' : 'ticket is'} confirmed. A copy of this order is in your Orders page.
        </p>
      </div>

      <div className="card mb-6">
        <div className="space-y-4 divide-y divide-secondary-100">
          {orders.map((order) => (
            <div key={order.id} className="pt-4 first:pt-0">
              <div className="flex justify-between items-start">
                <div>
                  <p className="font-semibold text-secondary-900">{order.eventName ?? `Order #${order.id.slice(-8)}`}</p>
                  <div className="flex items-center gap-4 mt-1 text-sm text-secondary-500">
                    {order.eventDate && (
                      <span className="flex items-center gap-1.5">
                        <Calendar className="h-3.5 w-3.5" />
                        {new Date(order.eventDate).toLocaleDateString()}
                      </span>
                    )}
                    {order.seatInfo && (
                      <span className="flex items-center gap-1.5">
                        <MapPin className="h-3.5 w-3.5" />
                        {order.seatInfo}
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-secondary-400 mt-1">Order #{order.id.slice(-8)} &middot; Payment #{order.paymentId?.slice(-8)}</p>
                </div>
                <span className="font-semibold text-secondary-900">${order.totalAmount}</span>
              </div>
            </div>
          ))}
        </div>
        <div className="flex justify-between items-center pt-4 mt-4 border-t border-secondary-200">
          <span className="font-semibold text-secondary-900">Total paid</span>
          <span className="text-xl font-bold text-primary-600">${total.toFixed(2)}</span>
        </div>
      </div>

      <div className="flex justify-center gap-4">
        <Link to="/tickets">
          <Button variant="secondary">Browse More Tickets</Button>
        </Link>
        <Link to="/orders">
          <Button>View My Orders</Button>
        </Link>
      </div>
    </div>
  );
};
