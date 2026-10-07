import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Timer, LayoutDashboard } from 'lucide-react';
import { useAuthContext } from '../components/AuthProvider';
import { Button } from '../components/ui/Button';

const features = [
  {
    icon: ShieldCheck,
    title: 'Secure transactions',
    description: 'Every purchase goes through a verified order and payment flow before a ticket changes hands.',
  },
  {
    icon: Timer,
    title: 'Time-limited reservations',
    description: 'Orders reserve a ticket for 15 minutes, keeping access fair for every buyer.',
  },
  {
    icon: LayoutDashboard,
    title: 'Simple management',
    description: 'Track everything you’re buying and selling from a single orders dashboard.',
  },
];

export const Home: React.FC = () => {
  const { isAuthenticated } = useAuthContext();

  return (
    <div className="max-w-6xl mx-auto">
      <div className="text-center py-20">
        <h1 className="text-5xl font-bold text-secondary-900 mb-6">
          Welcome to <span className="text-primary-600">TicketDaata</span>
        </h1>
        <p className="text-xl text-secondary-600 mb-8 max-w-3xl mx-auto">
          The trusted marketplace for buying and selling event tickets. Connect with other fans and
          never miss your favorite events again.
        </p>
        <div className="flex justify-center gap-4">
          <Link to="/tickets">
            <Button size="lg">Browse Tickets</Button>
          </Link>
          {!isAuthenticated && (
            <Link to="/register">
              <Button variant="secondary" size="lg">Sign Up Today</Button>
            </Link>
          )}
        </div>
      </div>

      <div className="grid md:grid-cols-3 gap-8 py-16">
        {features.map(({ icon: Icon, title, description }) => (
          <div key={title} className="text-center">
            <div className="bg-primary-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <Icon className="h-7 w-7 text-primary-600" />
            </div>
            <h3 className="text-xl font-semibold text-secondary-900 mb-2">{title}</h3>
            <p className="text-secondary-600">{description}</p>
          </div>
        ))}
      </div>

      {isAuthenticated && (
        <div className="card text-center">
          <h2 className="text-2xl font-bold text-secondary-900 mb-4">Quick actions</h2>
          <div className="flex justify-center gap-4">
            <Link to="/create-ticket">
              <Button>Sell a Ticket</Button>
            </Link>
            <Link to="/orders">
              <Button variant="secondary">View My Orders</Button>
            </Link>
          </div>
        </div>
      )}
    </div>
  );
};
