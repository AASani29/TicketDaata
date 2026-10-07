import React, { createContext, useContext, useEffect, useState } from 'react';
import type { CartItem } from '../types/api';

const CART_STORAGE_KEY = 'ticketdaata_cart';

interface CartContextType {
  items: CartItem[];
  addToCart: (item: CartItem) => void;
  removeFromCart: (ticketId: string) => void;
  clearCart: () => void;
  isInCart: (ticketId: string) => boolean;
  subtotal: number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

// eslint-disable-next-line react-refresh/only-export-components -- hook is colocated with its Provider/Context by design
export const useCart = () => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

const loadCart = (): CartItem[] => {
  try {
    const stored = localStorage.getItem(CART_STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  } catch {
    return [];
  }
};

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>(loadCart);

  useEffect(() => {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
  }, [items]);

  const addToCart = (item: CartItem) => {
    setItems((prev) => (prev.some((i) => i.ticketId === item.ticketId) ? prev : [...prev, item]));
  };

  const removeFromCart = (ticketId: string) => {
    setItems((prev) => prev.filter((i) => i.ticketId !== ticketId));
  };

  const clearCart = () => setItems([]);

  const isInCart = (ticketId: string) => items.some((i) => i.ticketId === ticketId);

  const subtotal = items.reduce((sum, item) => sum + item.price, 0);

  return (
    <CartContext.Provider value={{ items, addToCart, removeFromCart, clearCart, isInCart, subtotal }}>
      {children}
    </CartContext.Provider>
  );
};
