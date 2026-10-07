import React from 'react';

interface EmptyStateProps {
  icon: React.ReactNode;
  title: string;
  description?: string;
  action?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = ({ icon, title, description, action }) => {
  return (
    <div className="text-center py-16 px-4">
      <div className="mx-auto h-14 w-14 rounded-full bg-secondary-100 flex items-center justify-center text-secondary-400 mb-4">
        {icon}
      </div>
      <h3 className="text-base font-semibold text-secondary-900">{title}</h3>
      {description && <p className="mt-1 text-sm text-secondary-500">{description}</p>}
      {action && <div className="mt-6">{action}</div>}
    </div>
  );
};

export default EmptyState;
