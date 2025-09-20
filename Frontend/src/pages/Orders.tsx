import React, { useState, useEffect } from 'react';
import { useAuthContext } from '../components/AuthProvider';
import { orderService } from '../services/orderService';
import type { Order } from '../types';

export const Orders: React.FC = () => {
  const [buyerOrders, setBuyerOrders] = useState<Order[]>([]);
  const [sellerOrders, setSellerOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'buyer' | 'seller'>('buyer');
  const { user } = useAuthContext();

  useEffect(() => {
    if (user) {
      loadOrders();
    }
  }, [user]);

  const loadOrders = async () => {
    if (!user) return;
    
    try {
      setIsLoading(true);
      setError(null);
      
      const [buyerData, sellerData] = await Promise.all([
        orderService.getOrdersByUser(user.id),
        orderService.getOrdersBySeller(user.id)
      ]);
      
      setBuyerOrders(buyerData);
      setSellerOrders(sellerData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load orders');
    } finally {
      setIsLoading(false);
    }
  };

  const handleApproveOrder = async (orderId: string) => {
    try {
      await orderService.approveOrder(orderId);
      alert('Order approved successfully!');
      loadOrders();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to approve order');
    }
  };

  const handleRejectOrder = async (orderId: string) => {
    try {
      await orderService.rejectOrder(orderId);
      alert('Order rejected');
      loadOrders();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to reject order');
    }
  };

  const handleSimulatePayment = async (orderId: string) => {
    try {
      await orderService.simulatePayment(orderId);
      alert('Payment processed successfully!');
      loadOrders();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Payment failed');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED':
        return 'bg-red-100 text-red-800';
      case 'EXPIRED':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatTimeRemaining = (expiresAt: string) => {
    const now = new Date();
    const expiry = new Date(expiresAt);
    const diff = expiry.getTime() - now.getTime();
    
    if (diff <= 0) return 'Expired';
    
    const minutes = Math.floor(diff / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);
    
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
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
      <h1 className="text-3xl font-bold text-gray-900 mb-8">My Orders</h1>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
          {error}
        </div>
      )}

      {/* Tabs */}
      <div className="flex border-b border-gray-200 mb-6">
        <button
          onClick={() => setActiveTab('buyer')}
          className={`py-2 px-4 font-medium ${
            activeTab === 'buyer'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          My Purchases ({buyerOrders.length})
        </button>
        <button
          onClick={() => setActiveTab('seller')}
          className={`py-2 px-4 font-medium ${
            activeTab === 'seller'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          My Sales ({sellerOrders.length})
        </button>
      </div>

      {/* Orders List */}
      <div className="space-y-4">
        {(activeTab === 'buyer' ? buyerOrders : sellerOrders).length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">
              No {activeTab === 'buyer' ? 'purchases' : 'sales'} found
            </p>
          </div>
        ) : (
          (activeTab === 'buyer' ? buyerOrders : sellerOrders).map((order) => (
            <div key={order.id} className="card">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    Order #{order.id.slice(-8)}
                  </h3>
                  <p className="text-gray-600">
                    Ticket ID: {order.ticketId}
                  </p>
                </div>
                <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(order.status)}`}>
                  {order.status}
                </span>
              </div>

              <div className="grid md:grid-cols-2 gap-4 mb-4">
                <div>
                  <p className="text-sm text-gray-500">Amount</p>
                  <p className="font-semibold">${order.totalAmount}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Created</p>
                  <p className="font-semibold">
                    {new Date(order.createdAt).toLocaleString()}
                  </p>
                </div>
                {order.status === 'PENDING' && (
                  <div>
                    <p className="text-sm text-gray-500">Time Remaining</p>
                    <p className="font-semibold text-red-600">
                      {formatTimeRemaining(order.expiresAt)}
                    </p>
                  </div>
                )}
              </div>

              {/* Actions */}
              <div className="flex space-x-2">
                {activeTab === 'seller' && order.status === 'PENDING' && (
                  <>
                    <button
                      onClick={() => handleApproveOrder(order.id)}
                      className="btn-primary text-sm"
                    >
                      Approve
                    </button>
                    <button
                      onClick={() => handleRejectOrder(order.id)}
                      className="btn-secondary text-sm"
                    >
                      Reject
                    </button>
                  </>
                )}

                {activeTab === 'buyer' && order.status === 'APPROVED' && (
                  <button
                    onClick={() => handleSimulatePayment(order.id)}
                    className="btn-primary text-sm"
                  >
                    Pay Now (Simulate)
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};