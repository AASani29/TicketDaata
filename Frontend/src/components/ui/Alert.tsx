import React from 'react';
import { AlertCircle, CheckCircle2, Info } from 'lucide-react';

type AlertVariant = 'error' | 'success' | 'info';

interface AlertProps {
  variant?: AlertVariant;
  children: React.ReactNode;
  className?: string;
}

const variantConfig: Record<AlertVariant, { classes: string; icon: React.ReactNode }> = {
  error: { classes: 'bg-red-50 text-red-700 border-red-200', icon: <AlertCircle className="h-5 w-5 text-red-500" /> },
  success: { classes: 'bg-green-50 text-green-700 border-green-200', icon: <CheckCircle2 className="h-5 w-5 text-green-500" /> },
  info: { classes: 'bg-primary-50 text-primary-700 border-primary-200', icon: <Info className="h-5 w-5 text-primary-500" /> },
};

export const Alert: React.FC<AlertProps> = ({ variant = 'info', children, className = '' }) => {
  const config = variantConfig[variant];

  return (
    <div className={`flex items-start gap-3 rounded-lg border px-4 py-3 text-sm ${config.classes} ${className}`} role="alert">
      <span className="shrink-0 mt-0.5">{config.icon}</span>
      <span>{children}</span>
    </div>
  );
};

export default Alert;
