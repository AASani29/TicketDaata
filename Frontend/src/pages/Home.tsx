import React from 'react';
import { Link } from 'react-router-dom';
import { useAuthContext } from '../components/AuthProvider';

export const Home: React.FC = () => {
  const { isAuthenticated } = useAuthContext();

  return (
    <div className="max-w-6xl mx-auto">
      {/* Hero Section */}
      <div className="text-center py-20">
        <h1 className="text-5xl font-bold text-gray-900 mb-6">
          Welcome to <span className="text-blue-600">TicketDaata</span>
        </h1>
        <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
          The trusted marketplace for buying and selling event tickets. 
          Connect with other fans and never miss your favorite events again.
        </p>
        <div className="flex justify-center space-x-4">
          <Link to="/tickets" className="btn-primary text-lg px-8 py-3">
            Browse Tickets
          </Link>
          {!isAuthenticated && (
            <Link to="/register" className="btn-secondary text-lg px-8 py-3">
              Sign Up Today
            </Link>
          )}
        </div>
      </div>

      {/* Features Section */}
      <div className="grid md:grid-cols-3 gap-8 py-16">
        <div className="text-center">
          <div className="bg-blue-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className="text-xl font-semibold mb-2">Secure Transactions</h3>
          <p className="text-gray-600">
            All transactions are processed securely with our built-in payment system.
          </p>
        </div>

        <div className="text-center">
          <div className="bg-blue-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className="text-xl font-semibold mb-2">Time-Limited Orders</h3>
          <p className="text-gray-600">
            Orders expire automatically after 15 minutes to ensure fair access for all buyers.
          </p>
        </div>

        <div className="text-center">
          <div className="bg-blue-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <h3 className="text-xl font-semibold mb-2">Easy Management</h3>
          <p className="text-gray-600">
            Manage all your tickets and orders in one place with our intuitive dashboard.
          </p>
        </div>
      </div>

      {/* Quick Actions */}
      {isAuthenticated && (
        <div className="bg-white rounded-lg shadow-md p-8 text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Quick Actions</h2>
          <div className="flex justify-center space-x-4">
            <Link to="/create-ticket" className="btn-primary">
              Sell a Ticket
            </Link>
            <Link to="/orders" className="btn-secondary">
              View My Orders
            </Link>
          </div>
        </div>
      )}
    </div>
  );
};