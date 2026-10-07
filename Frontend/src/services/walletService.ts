import apiService from './api';
import type { ApiError, UserBalance } from '../types/api';

export const walletService = {
  async getBalance(username: string): Promise<UserBalance> {
    try {
      return await apiService.get<UserBalance>(`/auth/balance/${username}`);
    } catch (error) {
      throw new Error((error as ApiError).message || 'Failed to fetch balance');
    }
  }
};
