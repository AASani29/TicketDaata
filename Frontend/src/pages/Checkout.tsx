import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar, MapPin, AlertTriangle } from 'lucide-react';
import { useCart } from '../components/CartProvider';
import { useAuthContext } from '../components/AuthProvider';
import { orderService } from '../services/orderService';
import { useToast } from '../components/ui/Toast';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';
import type { Order } from '../types/api';

export const Checkout: React.FC = () => {
  const { items, removeFromCart, clearCart, subtotal } = useCart();
  const { user } = useAuthContext();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [isPlacing, setIsPlacing] = useState(false);
  const [failedItems, setFailedItems] = useState<string[]>([]);

  const handlePlaceOrder = async () => {
    if (!user) return;

    setIsPlacing(true);
    setFailedItems([]);

    const createdOrders: Order[] = [];
    const failedNames: string[] = [];

    for (const item of items) {
      try {
        const order = await orderService.createOrder({
          ticketId: item.ticketId,
          userId: user.id,
          quantity: 1
        });
        createdOrders.push(order);
      } catch (err) {
        failedNames.push(item.eventName);
        showToast('error', `${item.eventName}: ${err instanceof Error ? err.message : 'Could not reserve this ticket'}`);
        // Remove the failed item from the cart; successfully-ordered items get
        // cleared below via clearCart(), so only failures need individual removal.
        removeFromCart(item.ticketId);
      }
    }

    if (createdOrders.length === 0) {
      setFailedItems(failedNames);
      setIsPlacing(false);
      return;
    }

    clearCart();
    showToast('success', `${createdOrders.length} ticket${createdOrders.length > 1 ? 's' : ''} reserved. Complete payment to confirm.`);
    navigate('/payment', { state: { orders: createdOrders } });
  };

  if (items.length === 0 && failedItems.length === 0) {
    navigate('/cart', { replace: true });
    return null;
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-secondary-900 mb-8">Checkout</h1>

      {failedItems.length > 0 && (
        <Alert variant="error" className="mb-6">
          Couldn't reserve: {failedItems.join(', ')}. They may have just been bought by someone else.
        </Alert>
      )}

      <div className="card mb-6">
        <h2 className="text-lg font-semibold text-secondary-900 mb-4">Order Summary</h2>
        <div className="space-y-4 divide-y divide-secondary-100">
          {items.map((item) => (
            <div key={item.ticketId} className="pt-4 first:pt-0 flex justify-between items-start">
              <div>
                <p className="font-medium text-secondary-900">{item.eventName}</p>
                <div className="flex items-center gap-4 mt-1 text-sm text-secondary-500">
                  <span className="flex items-center gap-1.5">
                    <MapPin className="h-3.5 w-3.5" />
                    {item.location}
                  </span>
                  <span className="flex items-center gap-1.5">
                    <Calendar className="h-3.5 w-3.5" />
                    {new Date(item.eventDate).toLocaleDateString()}
                  </span>
                </div>
              </div>
              <span className="font-semibold text-secondary-900">${item.price}</span>
            </div>
          ))}
        </div>
        <div className="flex justify-between items-center pt-4 mt-4 border-t border-secondary-200">
          <span className="font-semibold text-secondary-900">Total</span>
          <span className="text-xl font-bold text-primary-600">${subtotal.toFixed(2)}</span>
        </div>
      </div>

      <Alert variant="info" className="mb-6">
        <span className="flex items-start gap-2">
          <AlertTriangle className="h-4 w-4 shrink-0 mt-0.5" />
          Placing your order reserves each ticket for 15 minutes. Complete payment in that window or the reservation expires.
        </span>
      </Alert>

      <div className="flex justify-between items-center">
        <Button variant="secondary" onClick={() => navigate('/cart')} disabled={isPlacing}>
          Back to Cart
        </Button>
        <Button size="lg" onClick={handlePlaceOrder} isLoading={isPlacing} disabled={items.length === 0}>
          Place Order
        </Button>
      </div>
    </div>
  );
};
