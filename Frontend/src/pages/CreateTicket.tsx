import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Info } from 'lucide-react';
import { useAuthContext } from '../components/AuthProvider';
import { ticketService } from '../services/ticketService';
import { useToast } from '../components/ui/Toast';
import { Button } from '../components/ui/Button';
import { Input, Select, Textarea } from '../components/ui/Input';
import { Alert } from '../components/ui/Alert';
import type { TicketFormData } from '../types/api';

const CATEGORY_OPTIONS = [
  { value: 'Concert', label: 'Concert' },
  { value: 'Sports', label: 'Sports' },
  { value: 'Theater', label: 'Theater' },
  { value: 'Comedy', label: 'Comedy' },
  { value: 'Conference', label: 'Conference' },
  { value: 'Other', label: 'Other' },
];

const minEventDate = new Date(Date.now() + 60 * 60 * 1000).toISOString().slice(0, 16);

export const CreateTicket: React.FC = () => {
  const [formData, setFormData] = useState<TicketFormData>({
    eventName: '',
    category: 'Concert',
    location: '',
    eventDate: '',
    seatInfo: '',
    price: 0
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuthContext();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'price' ? parseFloat(value) || 0 : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user) {
      setError('You must be logged in to create a ticket');
      return;
    }

    if (formData.price <= 0) {
      setError('Price must be greater than 0');
      return;
    }

    try {
      setIsLoading(true);
      setError(null);

      await ticketService.createTicket({
        eventName: formData.eventName,
        category: formData.category,
        location: formData.location,
        eventDate: formData.eventDate,
        seatInfo: formData.seatInfo || undefined,
        price: formData.price,
        userId: user.id,
        sellerId: user.id,
      });

      showToast('success', 'Ticket created successfully.');
      navigate('/tickets');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create ticket');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-secondary-900 mb-8">Sell a Ticket</h1>

      <div className="card">
        {error && (
          <Alert variant="error" className="mb-6">
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <Input
            label="Event name"
            name="eventName"
            value={formData.eventName}
            onChange={handleChange}
            placeholder="e.g., Taylor Swift Concert 2026"
            required
          />

          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Category"
              name="category"
              options={CATEGORY_OPTIONS}
              value={formData.category}
              onChange={handleChange}
            />

            <Input
              label="Location"
              name="location"
              value={formData.location}
              onChange={handleChange}
              placeholder="e.g., Wembley Stadium"
              required
            />
          </div>

          <Input
            label="Event date & time"
            type="datetime-local"
            name="eventDate"
            value={formData.eventDate}
            onChange={handleChange}
            min={minEventDate}
            required
          />

          <Textarea
            label="Seat information (optional)"
            name="seatInfo"
            value={formData.seatInfo}
            onChange={handleChange}
            rows={2}
            placeholder="e.g., Section B, Row 12, Seat 5"
          />

          <Input
            label="Price ($)"
            type="number"
            name="price"
            value={formData.price || ''}
            onChange={handleChange}
            placeholder="0.00"
            min="0.01"
            step="0.01"
            required
          />

          <div className="flex justify-between items-center pt-4">
            <Button type="button" variant="secondary" onClick={() => navigate('/tickets')}>
              Cancel
            </Button>
            <Button type="submit" isLoading={isLoading}>
              Create Ticket
            </Button>
          </div>
        </form>
      </div>

      <div className="mt-8 flex gap-3 bg-primary-50 border border-primary-200 rounded-lg p-6">
        <Info className="h-5 w-5 text-primary-600 shrink-0 mt-0.5" />
        <div>
          <h3 className="text-sm font-semibold text-primary-900 mb-2">How it works</h3>
          <ul className="text-sm text-primary-800 space-y-1 list-disc list-inside">
            <li>Your ticket is listed as "Available" for buyers to see.</li>
            <li>When someone places an order, the ticket becomes "Reserved" for 15 minutes.</li>
            <li>If the buyer completes payment in that window, the ticket becomes "Sold".</li>
            <li>If they don't, the order expires automatically and the ticket becomes available again.</li>
          </ul>
        </div>
      </div>
    </div>
  );
};
