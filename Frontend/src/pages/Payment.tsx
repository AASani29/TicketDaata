import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { CreditCard, Lock } from 'lucide-react';
import { orderService } from '../services/orderService';
import { useToast } from '../components/ui/Toast';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import type { Order } from '../types/api';

interface PaymentLocationState {
  orders: Order[];
}

export const Payment: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [isPaying, setIsPaying] = useState(false);
  const [cardNumber, setCardNumber] = useState('4242 4242 4242 4242');
  const [expiry, setExpiry] = useState('12/30');
  const [cvc, setCvc] = useState('123');

  const state = location.state as PaymentLocationState | null;
  const orders = state?.orders ?? [];
  const total = orders.reduce((sum, order) => sum + order.totalAmount, 0);

  if (orders.length === 0) {
    navigate('/orders', { replace: true });
    return null;
  }

  const handlePay = async () => {
    setIsPaying(true);
    const paidOrders: Order[] = [];

    for (const order of orders) {
      try {
        const paid = await orderService.payOrder(order.id);
        paidOrders.push(paid);
      } catch (err) {
        showToast('error', `${order.eventName ?? order.id}: ${err instanceof Error ? err.message : 'Payment failed'}`);
      }
    }

    setIsPaying(false);

    if (paidOrders.length === 0) {
      return;
    }

    navigate('/confirmation', { state: { orders: paidOrders } });
  };

  return (
    <div className="max-w-md mx-auto">
      <h1 className="text-3xl font-bold text-secondary-900 mb-8">Payment</h1>

      <div className="card mb-6">
        <h2 className="text-sm font-semibold text-secondary-700 mb-3">Order Total</h2>
        <div className="space-y-2 mb-4">
          {orders.map((order) => (
            <div key={order.id} className="flex justify-between text-sm text-secondary-600">
              <span>{order.eventName ?? `Order #${order.id.slice(-8)}`}</span>
              <span>${order.totalAmount}</span>
            </div>
          ))}
        </div>
        <div className="flex justify-between items-center pt-3 border-t border-secondary-200">
          <span className="font-semibold text-secondary-900">Total due</span>
          <span className="text-2xl font-bold text-primary-600">${total.toFixed(2)}</span>
        </div>
      </div>

      <div className="card">
        <div className="flex items-center gap-2 mb-4 text-sm text-secondary-500">
          <Lock className="h-4 w-4" />
          Simulated payment &mdash; no real card is charged
        </div>

        <div className="space-y-4">
          <Input
            label="Card number"
            icon={<CreditCard className="h-4 w-4" />}
            value={cardNumber}
            onChange={(e) => setCardNumber(e.target.value)}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Expiry" value={expiry} onChange={(e) => setExpiry(e.target.value)} />
            <Input label="CVC" value={cvc} onChange={(e) => setCvc(e.target.value)} />
          </div>
        </div>

        <Button fullWidth size="lg" className="mt-6" onClick={handlePay} isLoading={isPaying}>
          Pay ${total.toFixed(2)}
        </Button>
      </div>
    </div>
  );
};
