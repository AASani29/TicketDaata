import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Lock } from 'lucide-react';
import { useAuthContext } from '../components/AuthProvider';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Alert } from '../components/ui/Alert';

export const Login: React.FC = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const { login, error } = useAuthContext();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsLoading(true);
      await login(formData);
      navigate('/');
    } catch {
      // Error handled by useAuth hook
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto">
      <div className="card">
        <h1 className="text-2xl font-bold text-center text-secondary-900 mb-8">
          Login to TicketDaata
        </h1>

        {error && (
          <Alert variant="error" className="mb-6">
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <Input
            label="Username"
            name="username"
            icon={<User className="h-4 w-4" />}
            value={formData.username}
            onChange={handleChange}
            required
          />

          <Input
            label="Password"
            type="password"
            name="password"
            icon={<Lock className="h-4 w-4" />}
            value={formData.password}
            onChange={handleChange}
            required
          />

          <Button type="submit" fullWidth isLoading={isLoading}>
            Login
          </Button>
        </form>

        <p className="text-center text-secondary-600 mt-6 text-sm">
          Don't have an account?{' '}
          <Link to="/register" className="text-primary-600 hover:text-primary-700 font-medium">
            Sign up here
          </Link>
        </p>
      </div>
    </div>
  );
};
