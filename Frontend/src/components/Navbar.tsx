import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Ticket, PlusCircle, Package, LogOut, UserCircle2, ShoppingCart, Wallet } from 'lucide-react';
import { useAuthContext } from './AuthProvider';
import { useCart } from './CartProvider';
import { Button } from './ui/Button';

const navLinkClasses = (active: boolean) =>
  `flex items-center gap-1.5 text-sm font-medium transition-colors ${
    active ? 'text-primary-600' : 'text-secondary-600 hover:text-primary-600'
  }`;

export const Navbar: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuthContext();
  const { items } = useCart();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="bg-white border-b border-secondary-200 sticky top-0 z-40">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="text-xl font-bold text-primary-600">
            TicketDaata
          </Link>

          <div className="flex items-center gap-6">
            <Link to="/tickets" className={navLinkClasses(location.pathname === '/tickets')}>
              <Ticket className="h-4 w-4" />
              Browse Tickets
            </Link>

            {isAuthenticated ? (
              <>
                <Link to="/create-ticket" className={navLinkClasses(location.pathname === '/create-ticket')}>
                  <PlusCircle className="h-4 w-4" />
                  Sell Ticket
                </Link>
                <Link to="/orders" className={navLinkClasses(location.pathname === '/orders')}>
                  <Package className="h-4 w-4" />
                  My Orders
                </Link>
                <Link to="/orders" state={{ tab: 'wallet' }} className={navLinkClasses(false)}>
                  <Wallet className="h-4 w-4" />
                  Wallet
                </Link>
                <Link to="/cart" className={`relative ${navLinkClasses(location.pathname === '/cart')}`}>
                  <ShoppingCart className="h-4 w-4" />
                  Cart
                  {items.length > 0 && (
                    <span className="absolute -top-2 -right-3 flex items-center justify-center h-4 w-4 rounded-full bg-primary-600 text-white text-[10px] font-semibold">
                      {items.length}
                    </span>
                  )}
                </Link>
                <div className="flex items-center gap-3 pl-4 border-l border-secondary-200">
                  <span className="flex items-center gap-1.5 text-sm text-secondary-600">
                    <UserCircle2 className="h-5 w-5 text-secondary-400" />
                    {user?.username}
                  </span>
                  <Button variant="secondary" size="sm" icon={<LogOut className="h-4 w-4" />} onClick={handleLogout}>
                    Logout
                  </Button>
                </div>
              </>
            ) : (
              <div className="flex items-center gap-3">
                <Link to="/login" className={navLinkClasses(location.pathname === '/login')}>
                  Login
                </Link>
                <Link to="/register">
                  <Button size="sm">Sign Up</Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};
