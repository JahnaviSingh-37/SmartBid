import { LoginRequest, RegisterRequest, AuthResponse, User } from '../types/auth.types';
import api from './api';

export const authService = {
  // Login user
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    try {
      const response = await api.post<AuthResponse>('/auth/login', credentials);
      
      // Store tokens
      localStorage.setItem('smartbid_auth_token', response.data.token);
      localStorage.setItem('smartbid_refresh_token', response.data.refreshToken || '');
      
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  },

  // Register user
  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    try {
      const response = await api.post<AuthResponse>('/auth/register', userData);
      
      // Store tokens
      localStorage.setItem('smartbid_auth_token', response.data.token);
      localStorage.setItem('smartbid_refresh_token', response.data.refreshToken || '');
      
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  },

  // Logout user
  logout: async (): Promise<void> => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      // Even if logout fails on server, clear local storage
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('smartbid_auth_token');
      localStorage.removeItem('smartbid_refresh_token');
      localStorage.removeItem('smartbid_user_data');
    }
  },

  // Refresh token
  refreshToken: async (): Promise<AuthResponse> => {
    try {
      const refreshToken = localStorage.getItem('smartbid_refresh_token');
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await api.post<AuthResponse>('/auth/refresh', {
        refreshToken,
      });
      
      // Store new tokens
      localStorage.setItem('smartbid_auth_token', response.data.token);
      localStorage.setItem('smartbid_refresh_token', response.data.refreshToken || '');
      
      return response.data;
    } catch (error: any) {
      // Clear tokens if refresh fails
      localStorage.removeItem('smartbid_auth_token');
      localStorage.removeItem('smartbid_refresh_token');
      throw new Error(error.response?.data?.message || 'Token refresh failed');
    }
  },

  // Get current user
  getCurrentUser: async (): Promise<User> => {
    try {
      const response = await api.get<User>('/auth/me');
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to get user data');
    }
  },

  // Update user profile
  updateProfile: async (userData: Partial<User>): Promise<User> => {
    try {
      const response = await api.put<User>('/auth/profile', userData);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Profile update failed');
    }
  },

  // Change password
  changePassword: async (currentPassword: string, newPassword: string): Promise<void> => {
    try {
      await api.put('/auth/change-password', {
        currentPassword,
        newPassword,
      });
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Password change failed');
    }
  },

  // Forgot password
  forgotPassword: async (email: string): Promise<void> => {
    try {
      await api.post('/auth/forgot-password', { email });
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to send reset email');
    }
  },

  // Reset password
  resetPassword: async (token: string, newPassword: string): Promise<void> => {
    try {
      await api.post('/auth/reset-password', {
        token,
        newPassword,
      });
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Password reset failed');
    }
  },

  // Verify email
  verifyEmail: async (token: string): Promise<void> => {
    try {
      await api.post('/auth/verify-email', { token });
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Email verification failed');
    }
  },

  // Resend verification email
  resendVerificationEmail: async (): Promise<void> => {
    try {
      await api.post('/auth/resend-verification');
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to resend verification email');
    }
  },

  // Get user statistics
  getUserStats: async (): Promise<{
    totalBids: number;
    wonAuctions: number;
    totalSpent: number;
    savedAmount: number;
    creditScore: number;
    accountLevel: string;
  }> => {
    try {
      const response = await api.get('/auth/stats');
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to get user statistics');
    }
  },

  // Check if token is valid
  isTokenValid: (): boolean => {
    const token = localStorage.getItem('smartbid_auth_token');
    if (!token) return false;

    try {
      // Decode JWT token to check expiration
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch {
      return false;
    }
  },
};
