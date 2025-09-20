import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthContext } from './AuthProvider';

export const Navbar: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuthContext();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="bg-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center py-4">
          <Link to="/" className="text-2xl font-bold text-blue-600">
            TicketDaata
          </Link>
          
          <div className="flex items-center space-x-6">
            <Link 
              to="/tickets" 
              className="text-gray-600 hover:text-blue-600 transition-colors"
            >
              Browse Tickets
            </Link>
            
            {isAuthenticated ? (
              <>
                <Link 
                  to="/create-ticket" 
                  className="text-gray-600 hover:text-blue-600 transition-colors"
                >
                  Sell Ticket
                </Link>
                <Link 
                  to="/orders" 
                  className="text-gray-600 hover:text-blue-600 transition-colors"
                >
                  My Orders
                </Link>
                <div className="flex items-center space-x-4">
                  <span className="text-sm text-gray-600">
                    Welcome, {user?.username}
                  </span>
                  <button
                    onClick={handleLogout}
                    className="btn-secondary text-sm"
                  >
                    Logout
                  </button>
                </div>
              </>
            ) : (
              <div className="flex items-center space-x-4">
                <Link 
                  to="/login" 
                  className="text-gray-600 hover:text-blue-600 transition-colors"
                >
                  Login
                </Link>
                <Link 
                  to="/register" 
                  className="btn-primary text-sm"
                >
                  Sign Up
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};