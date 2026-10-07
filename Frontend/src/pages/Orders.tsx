import React, { useState, useEffect, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Calendar, MapPin, PackageX, Clock, Wallet as WalletIcon, ArrowDownLeft, ArrowUpRight } from 'lucide-react';
import { useAuthContext } from '../components/AuthProvider';
import { orderService } from '../services/orderService';
import { walletService } from '../services/walletService';
import { useToast } from '../components/ui/Toast';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import type { Order } from '../types/api';

type Tab = 'buyer' | 'seller' | 'wallet';

const formatTimeRemaining = (expiresAt?: string) => {
  if (!expiresAt) return null;
  const diff = new Date(expiresAt).getTime() - Date.now();
  if (diff <= 0) return null;
  const minutes = Math.floor(diff / (1000 * 60));
  const seconds = Math.floor((diff % (1000 * 60)) / 1000);
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

export const Orders: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [buyerOrders, setBuyerOrders] = useState<Order[]>([]);
  const [sellerOrders, setSellerOrders] = useState<Order[]>([]);
  const [balance, setBalance] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<Tab>(
    (location.state as { tab?: Tab } | null)?.tab ?? 'buyer'
  );
  const [actioningId, setActioningId] = useState<string | null>(null);
  const { user } = useAuthContext();
  const { showToast } = useToast();

  const loadOrders = useCallback(async () => {
    if (!user) return;

    try {
      setIsLoading(true);
      const [buyerData, sellerData, balanceData] = await Promise.all([
        orderService.getOrdersByUser(user.id),
        orderService.getOrdersBySeller(user.id),
        walletService.getBalance(user.id).catch(() => null)
      ]);
      setBuyerOrders(buyerData);
      setSellerOrders(sellerData);
      setBalance(balanceData?.balance ?? 0);
    } catch (err) {
      showToast('error', err instanceof Error ? err.message : 'Failed to load orders');
    } finally {
      setIsLoading(false);
    }
  }, [user, showToast]);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  const handlePay = (order: Order) => {
    navigate('/payment', { state: { orders: [order] } });
  };

  const handleCancel = async (orderId: string) => {
    try {
      setActioningId(orderId);
      await orderService.cancelOrder(orderId);
      showToast('success', 'Order cancelled.');
      loadOrders();
    } catch (err) {
      showToast('error', err instanceof Error ? err.message : 'Failed to cancel order');
    } finally {
      setActioningId(null);
    }
  };

  if (isLoading) {
    return <Spinner fullPage size="lg" />;
  }

  const orders = activeTab === 'seller' ? sellerOrders : buyerOrders;

  const transactions = [
    ...buyerOrders
      .filter((o) => o.status === 'COMPLETED')
      .map((o) => ({ order: o, direction: 'debit' as const })),
    ...sellerOrders
      .filter((o) => o.status === 'COMPLETED')
      .map((o) => ({ order: o, direction: 'credit' as const }))
  ].sort((a, b) => new Date(b.order.updatedAt).getTime() - new Date(a.order.updatedAt).getTime());

  return (
    <div className="max-w-5xl mx-auto">
      <h1 className="text-3xl font-bold text-secondary-900 mb-8">My Orders</h1>

      <div className="flex border-b border-secondary-200 mb-6">
        <button
          onClick={() => setActiveTab('buyer')}
          className={`py-2.5 px-4 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'buyer'
              ? 'border-primary-600 text-primary-600'
              : 'border-transparent text-secondary-500 hover:text-secondary-700'
          }`}
        >
          My Purchases ({buyerOrders.length})
        </button>
        <button
          onClick={() => setActiveTab('seller')}
          className={`py-2.5 px-4 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'seller'
              ? 'border-primary-600 text-primary-600'
              : 'border-transparent text-secondary-500 hover:text-secondary-700'
          }`}
        >
          My Sales ({sellerOrders.length})
        </button>
        <button
          onClick={() => setActiveTab('wallet')}
          className={`py-2.5 px-4 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'wallet'
              ? 'border-primary-600 text-primary-600'
              : 'border-transparent text-secondary-500 hover:text-secondary-700'
          }`}
        >
          Wallet
        </button>
      </div>

      {activeTab === 'wallet' ? (
        <div>
          <div className="card mb-6 flex items-center gap-4 bg-primary-50 border-primary-100">
            <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
              <WalletIcon className="h-6 w-6 text-primary-600" />
            </div>
            <div>
              <p className="text-sm text-secondary-600">Available balance</p>
              <p className="text-3xl font-bold text-secondary-900">${(balance ?? 0).toFixed(2)}</p>
            </div>
          </div>

          <h2 className="text-sm font-semibold text-secondary-700 mb-3">Transaction history</h2>
          {transactions.length === 0 ? (
            <EmptyState
              icon={<WalletIcon className="h-6 w-6" />}
              title="No transactions yet"
              description="Completed purchases and sales will show up here."
            />
          ) : (
            <div className="space-y-2">
              {transactions.map(({ order, direction }) => (
                <div key={`${direction}-${order.id}`} className="card flex items-center justify-between py-4">
                  <div className="flex items-center gap-3">
                    <div
                      className={`h-9 w-9 rounded-full flex items-center justify-center ${
                        direction === 'credit' ? 'bg-green-50 text-green-600' : 'bg-secondary-100 text-secondary-500'
                      }`}
                    >
                      {direction === 'credit' ? (
                        <ArrowDownLeft className="h-4 w-4" />
                      ) : (
                        <ArrowUpRight className="h-4 w-4" />
                      )}
                    </div>
                    <div>
                      <p className="font-medium text-secondary-900">
                        {order.eventName || `Order #${order.id.slice(-8)}`}
                      </p>
                      <p className="text-xs text-secondary-500">
                        {direction === 'credit' ? 'Sale' : 'Purchase'} &middot; {new Date(order.updatedAt).toLocaleString()}
                      </p>
                    </div>
                  </div>
                  <span className={`font-semibold ${direction === 'credit' ? 'text-green-600' : 'text-secondary-900'}`}>
                    {direction === 'credit' ? '+' : '-'}${order.totalAmount}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      ) : orders.length === 0 ? (
        <EmptyState
          icon={<PackageX className="h-6 w-6" />}
          title={activeTab === 'buyer' ? 'No purchases yet' : 'No sales yet'}
          description={
            activeTab === 'buyer'
              ? "Orders you place will show up here."
              : "Orders placed on your tickets will show up here."
          }
        />
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const timeRemaining = order.status === 'PENDING' ? formatTimeRemaining(order.expiresAt) : null;

            return (
              <div key={order.id} className="card">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-secondary-900">
                      {order.eventName || `Order #${order.id.slice(-8)}`}
                    </h3>
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
                  </div>
                  <Badge status={order.status} />
                </div>

                <div className="grid sm:grid-cols-3 gap-4 mb-4 text-sm">
                  <div>
                    <p className="text-secondary-500">Amount</p>
                    <p className="font-semibold text-secondary-900">${order.totalAmount}</p>
                  </div>
                  <div>
                    <p className="text-secondary-500">Placed</p>
                    <p className="font-semibold text-secondary-900">
                      {new Date(order.createdAt).toLocaleString()}
                    </p>
                  </div>
                  {timeRemaining && (
                    <div>
                      <p className="text-secondary-500">Time remaining</p>
                      <p className="font-semibold text-red-600 flex items-center gap-1">
                        <Clock className="h-3.5 w-3.5" />
                        {timeRemaining}
                      </p>
                    </div>
                  )}
                </div>

                {activeTab === 'buyer' && order.status === 'PENDING' && (
                  <div className="flex gap-2 pt-3 border-t border-secondary-100">
                    <Button size="sm" onClick={() => handlePay(order)}>
                      Pay Now
                    </Button>
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => handleCancel(order.id)}
                      disabled={actioningId === order.id}
                    >
                      Cancel
                    </Button>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
