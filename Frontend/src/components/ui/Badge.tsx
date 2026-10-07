import React from 'react';
import { Circle, CheckCircle2, Clock, XCircle, Timer } from 'lucide-react';
import type { TicketStatus, OrderStatus } from '../../types/api';

type Status = TicketStatus | OrderStatus;

const statusConfig: Record<Status, { label: string; classes: string; icon: React.ReactNode }> = {
  AVAILABLE: { label: 'Available', classes: 'bg-green-50 text-green-700 ring-green-600/20', icon: <Circle className="h-3 w-3 fill-current" /> },
  RESERVED: { label: 'Reserved', classes: 'bg-amber-50 text-amber-700 ring-amber-600/20', icon: <Clock className="h-3 w-3" /> },
  SOLD: { label: 'Sold', classes: 'bg-secondary-100 text-secondary-600 ring-secondary-500/20', icon: <CheckCircle2 className="h-3 w-3" /> },
  PENDING: { label: 'Pending', classes: 'bg-amber-50 text-amber-700 ring-amber-600/20', icon: <Timer className="h-3 w-3" /> },
  COMPLETED: { label: 'Completed', classes: 'bg-green-50 text-green-700 ring-green-600/20', icon: <CheckCircle2 className="h-3 w-3" /> },
  CANCELLED: { label: 'Cancelled', classes: 'bg-secondary-100 text-secondary-600 ring-secondary-500/20', icon: <XCircle className="h-3 w-3" /> },
  EXPIRED: { label: 'Expired', classes: 'bg-red-50 text-red-700 ring-red-600/20', icon: <XCircle className="h-3 w-3" /> },
};

interface BadgeProps {
  status: Status;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({ status, className = '' }) => {
  const config = statusConfig[status];

  return (
    <span
      className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium ring-1 ring-inset ${config.classes} ${className}`}
    >
      {config.icon}
      {config.label}
    </span>
  );
};

export default Badge;
