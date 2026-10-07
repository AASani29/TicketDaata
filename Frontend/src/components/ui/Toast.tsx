import React, { createContext, useCallback, useContext, useState } from 'react';
import { AlertCircle, CheckCircle2, Info, X } from 'lucide-react';

type ToastVariant = 'error' | 'success' | 'info';

interface ToastItem {
  id: number;
  variant: ToastVariant;
  message: string;
}

interface ToastContextValue {
  showToast: (variant: ToastVariant, message: string) => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

// eslint-disable-next-line react-refresh/only-export-components -- hook is colocated with its Provider/Context by design
export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

const variantConfig: Record<ToastVariant, { classes: string; icon: React.ReactNode }> = {
  error: { classes: 'bg-white border-red-200', icon: <AlertCircle className="h-5 w-5 text-red-500" /> },
  success: { classes: 'bg-white border-green-200', icon: <CheckCircle2 className="h-5 w-5 text-green-500" /> },
  info: { classes: 'bg-white border-primary-200', icon: <Info className="h-5 w-5 text-primary-500" /> },
};

let nextId = 1;

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const dismissToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback((variant: ToastVariant, message: string) => {
    const id = nextId++;
    setToasts((prev) => [...prev, { id, variant, message }]);
    setTimeout(() => dismissToast(id), 5000);
  }, [dismissToast]);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 w-full max-w-sm">
        {toasts.map((toast) => (
          <div
            key={toast.id}
            role="status"
            className={`flex items-start gap-3 rounded-lg border shadow-lg px-4 py-3 text-sm text-secondary-800 ${variantConfig[toast.variant].classes}`}
          >
            <span className="shrink-0 mt-0.5">{variantConfig[toast.variant].icon}</span>
            <span className="flex-1">{toast.message}</span>
            <button
              onClick={() => dismissToast(toast.id)}
              className="shrink-0 text-secondary-400 hover:text-secondary-600"
              aria-label="Dismiss notification"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};
