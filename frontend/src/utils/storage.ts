/**
 * Storage utilities for browser storage
 */

const STORAGE_KEYS = {
  AUTH_TOKEN: 'smartbid_auth_token',
  REFRESH_TOKEN: 'smartbid_refresh_token',
  USER_DATA: 'smartbid_user_data',
  THEME: 'smartbid_theme',
  SEARCH_HISTORY: 'smartbid_search_history',
  WATCHLIST: 'smartbid_watchlist',
  BID_SETTINGS: 'smartbid_bid_settings',
  NOTIFICATIONS: 'smartbid_notifications',
} as const;

/**
 * Local storage utilities
 */
export const localStorage = {
  set: <T>(key: string, value: T): void => {
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error('Error saving to localStorage:', error);
    }
  },
  
  get: <T>(key: string): T | null => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    } catch (error) {
      console.error('Error reading from localStorage:', error);
      return null;
    }
  },
  
  remove: (key: string): void => {
    try {
      window.localStorage.removeItem(key);
    } catch (error) {
      console.error('Error removing from localStorage:', error);
    }
  },
  
  clear: (): void => {
    try {
      window.localStorage.clear();
    } catch (error) {
      console.error('Error clearing localStorage:', error);
    }
  },
};

/**
 * Session storage utilities
 */
export const sessionStorage = {
  set: <T>(key: string, value: T): void => {
    try {
      window.sessionStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error('Error saving to sessionStorage:', error);
    }
  },
  
  get: <T>(key: string): T | null => {
    try {
      const item = window.sessionStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    } catch (error) {
      console.error('Error reading from sessionStorage:', error);
      return null;
    }
  },
  
  remove: (key: string): void => {
    try {
      window.sessionStorage.removeItem(key);
    } catch (error) {
      console.error('Error removing from sessionStorage:', error);
    }
  },
  
  clear: (): void => {
    try {
      window.sessionStorage.clear();
    } catch (error) {
      console.error('Error clearing sessionStorage:', error);
    }
  },
};

/**
 * Authentication token management
 */
export const authStorage = {
  setTokens: (authToken: string, refreshToken: string): void => {
    localStorage.set(STORAGE_KEYS.AUTH_TOKEN, authToken);
    localStorage.set(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
  },
  
  getAuthToken: (): string | null => {
    return localStorage.get<string>(STORAGE_KEYS.AUTH_TOKEN);
  },
  
  getRefreshToken: (): string | null => {
    return localStorage.get<string>(STORAGE_KEYS.REFRESH_TOKEN);
  },
  
  removeTokens: (): void => {
    localStorage.remove(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.remove(STORAGE_KEYS.REFRESH_TOKEN);
  },
  
  hasValidToken: (): boolean => {
    const token = localStorage.get<string>(STORAGE_KEYS.AUTH_TOKEN);
    if (!token) return false;
    
    try {
      // Check if token is expired (JWT structure)
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch {
      return false;
    }
  },
};

/**
 * User data management
 */
export const userStorage = {
  setUserData: (userData: any): void => {
    localStorage.set(STORAGE_KEYS.USER_DATA, userData);
  },
  
  getUserData: (): any | null => {
    return localStorage.get(STORAGE_KEYS.USER_DATA);
  },
  
  removeUserData: (): void => {
    localStorage.remove(STORAGE_KEYS.USER_DATA);
  },
};

/**
 * Theme management
 */
export const themeStorage = {
  setTheme: (theme: 'light' | 'dark' | 'system'): void => {
    localStorage.set(STORAGE_KEYS.THEME, theme);
  },
  
  getTheme: (): 'light' | 'dark' | 'system' => {
    return localStorage.get<'light' | 'dark' | 'system'>(STORAGE_KEYS.THEME) || 'system';
  },
};

/**
 * Search history management
 */
export const searchStorage = {
  addSearch: (searchTerm: string): void => {
    const history = localStorage.get<string[]>(STORAGE_KEYS.SEARCH_HISTORY) || [];
    const updatedHistory = [searchTerm, ...history.filter(term => term !== searchTerm)].slice(0, 10);
    localStorage.set(STORAGE_KEYS.SEARCH_HISTORY, updatedHistory);
  },
  
  getSearchHistory: (): string[] => {
    return localStorage.get<string[]>(STORAGE_KEYS.SEARCH_HISTORY) || [];
  },
  
  clearSearchHistory: (): void => {
    localStorage.remove(STORAGE_KEYS.SEARCH_HISTORY);
  },
};

/**
 * Watchlist management
 */
export const watchlistStorage = {
  addToWatchlist: (auctionId: string): void => {
    const watchlist = localStorage.get<string[]>(STORAGE_KEYS.WATCHLIST) || [];
    if (!watchlist.includes(auctionId)) {
      watchlist.push(auctionId);
      localStorage.set(STORAGE_KEYS.WATCHLIST, watchlist);
    }
  },
  
  removeFromWatchlist: (auctionId: string): void => {
    const watchlist = localStorage.get<string[]>(STORAGE_KEYS.WATCHLIST) || [];
    const updatedWatchlist = watchlist.filter(id => id !== auctionId);
    localStorage.set(STORAGE_KEYS.WATCHLIST, updatedWatchlist);
  },
  
  getWatchlist: (): string[] => {
    return localStorage.get<string[]>(STORAGE_KEYS.WATCHLIST) || [];
  },
  
  isInWatchlist: (auctionId: string): boolean => {
    const watchlist = localStorage.get<string[]>(STORAGE_KEYS.WATCHLIST) || [];
    return watchlist.includes(auctionId);
  },
};

/**
 * Bidding settings management
 */
export const bidSettingsStorage = {
  setBidSettings: (settings: any): void => {
    localStorage.set(STORAGE_KEYS.BID_SETTINGS, settings);
  },
  
  getBidSettings: (): any | null => {
    return localStorage.get(STORAGE_KEYS.BID_SETTINGS);
  },
};

/**
 * Notification settings management
 */
export const notificationStorage = {
  setNotificationSettings: (settings: any): void => {
    localStorage.set(STORAGE_KEYS.NOTIFICATIONS, settings);
  },
  
  getNotificationSettings: (): any | null => {
    return localStorage.get(STORAGE_KEYS.NOTIFICATIONS);
  },
};
