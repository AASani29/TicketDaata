import React from 'react';
import { Loader2 } from 'lucide-react';

interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
  fullPage?: boolean;
}

const sizeClasses = {
  sm: 'h-5 w-5',
  md: 'h-8 w-8',
  lg: 'h-12 w-12',
};

export const Spinner: React.FC<SpinnerProps> = ({ size = 'md', className = '', fullPage = false }) => {
  const spinner = <Loader2 className={`animate-spin text-primary-600 ${sizeClasses[size]} ${className}`} />;

  if (fullPage) {
    return (
      <div className="flex justify-center items-center min-h-[400px]" role="status" aria-label="Loading">
        {spinner}
      </div>
    );
  }

  return spinner;
};

export default Spinner;
