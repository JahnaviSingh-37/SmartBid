/**
 * SmartBid Marketplace Constants
 * Central configuration file for the application
 */

// API Configuration
export const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  WS_URL: process.env.REACT_APP_WS_URL || 'ws://localhost:8080/ws',
  TIMEOUT: 10000,
} as const;

// Authentication Constants
export const AUTH_CONFIG = {
  TOKEN_KEY: 'smartbid_auth_token',
  REFRESH_TOKEN_KEY: 'smartbid_refresh_token',
  USER_DATA_KEY: 'smartbid_user_data',
  TOKEN_EXPIRY_BUFFER: 5 * 60 * 1000, // 5 minutes
} as const;

// Auction Status Constants
export const AUCTION_STATUS = {
  UPCOMING: 'UPCOMING',
  ACTIVE: 'ACTIVE',
  ENDED: 'ENDED',
  CANCELLED: 'CANCELLED',
  SUSPENDED: 'SUSPENDED'
} as const;

// Auction Types
export const AUCTION_TYPE = {
  STANDARD: 'STANDARD',
  RESERVE: 'RESERVE',
  BUY_NOW: 'BUY_NOW',
  DUTCH: 'DUTCH',
  PENNY: 'PENNY'
} as const;

// Bid Status Constants
export const BID_STATUS = {
  ACTIVE: 'ACTIVE',
  WINNING: 'WINNING',
  OUTBID: 'OUTBID',
  WON: 'WON',
  LOST: 'LOST',
  RETRACTED: 'RETRACTED',
  REJECTED: 'REJECTED'
} as const;

// User Roles
export const USER_ROLES = {
  USER: 'USER',
  ADMIN: 'ADMIN',
  MODERATOR: 'MODERATOR'
} as const;

// User Status
export const USER_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  SUSPENDED: 'SUSPENDED',
  BANNED: 'BANNED'
} as const;

// SmartBid Credit Score Ranges
export const CREDIT_SCORE_RANGES = {
  EXCELLENT: { min: 800, max: 850, label: 'Excellent', color: 'text-green-600' },
  GOOD: { min: 700, max: 799, label: 'Good', color: 'text-blue-600' },
  FAIR: { min: 600, max: 699, label: 'Fair', color: 'text-yellow-600' },
  POOR: { min: 300, max: 599, label: 'Poor', color: 'text-red-600' }
} as const;

// Product Condition Types
export const CONDITION_TYPES = {
  NEW: 'NEW',
  LIKE_NEW: 'LIKE_NEW',
  GOOD: 'GOOD',
  FAIR: 'FAIR',
  POOR: 'POOR',
  REFURBISHED: 'REFURBISHED'
} as const;

// Payment Methods
export const PAYMENT_METHODS = {
  CREDIT_CARD: 'CREDIT_CARD',
  DEBIT_CARD: 'DEBIT_CARD',
  UPI: 'UPI',
  NET_BANKING: 'NET_BANKING',
  WALLET: 'WALLET',
  SMARTBID_CREDITS: 'SMARTBID_CREDITS'
} as const;

// Payment Status
export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED',
  CANCELLED: 'CANCELLED'
} as const;

// File Upload Constants
export const FILE_UPLOAD = {
  MAX_SIZE: 5 * 1024 * 1024, // 5MB
  MAX_FILES: 10,
  ALLOWED_IMAGE_TYPES: ['image/jpeg', 'image/png', 'image/webp', 'image/jpg'],
  ALLOWED_DOCUMENT_TYPES: ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
} as const;

// Pagination Constants
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
  MIN_PAGE_SIZE: 5
} as const;

// Notification Types
export const NOTIFICATION_TYPES = {
  BID_PLACED: 'BID_PLACED',
  BID_OUTBID: 'BID_OUTBID',
  AUCTION_WON: 'AUCTION_WON',
  AUCTION_LOST: 'AUCTION_LOST',
  AUCTION_ENDED: 'AUCTION_ENDED',
  AUCTION_STARTING: 'AUCTION_STARTING',
  PRICE_ALERT: 'PRICE_ALERT',
  PAYMENT_SUCCESS: 'PAYMENT_SUCCESS',
  PAYMENT_FAILED: 'PAYMENT_FAILED',
  ACCOUNT_VERIFIED: 'ACCOUNT_VERIFIED',
  CREDIT_SCORE_UPDATED: 'CREDIT_SCORE_UPDATED'
} as const;

// Application Routes
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  PROFILE: '/profile',
  AUCTIONS: '/auctions',
  AUCTION_DETAIL: '/auctions/:id',
  CREATE_AUCTION: '/create-auction',
  MY_BIDS: '/my-bids',
  MY_AUCTIONS: '/my-auctions',
  WATCHLIST: '/watchlist',
  SEARCH: '/search',
  CATEGORIES: '/categories',
  HELP: '/help',
  TERMS: '/terms',
  PRIVACY: '/privacy',
  ADMIN: '/admin',
  NOT_FOUND: '/404'
} as const;

// Search and Filter Constants
export const SEARCH_CONFIG = {
  MIN_QUERY_LENGTH: 2,
  DEBOUNCE_DELAY: 300,
  MAX_SUGGESTIONS: 10
} as const;

// Auction Categories
export const AUCTION_CATEGORIES = [
  'Electronics',
  'Fashion & Accessories',
  'Home & Garden',
  'Sports & Outdoors',
  'Automotive',
  'Books & Media',
  'Art & Collectibles',
  'Jewelry & Watches',
  'Health & Beauty',
  'Toys & Games',
  'Real Estate',
  'Business & Industrial',
  'Other'
] as const;

// Time Constants
export const TIME_CONFIG = {
  BID_EXTENSION_MINUTES: 5,
  AUCTION_MIN_DURATION_HOURS: 1,
  AUCTION_MAX_DURATION_DAYS: 30,
  PAYMENT_TIMEOUT_DAYS: 7,
  SHIPPING_TIMEOUT_DAYS: 14
} as const;

// AI Features Constants
export const AI_FEATURES = {
  IMAGE_SEARCH: 'IMAGE_SEARCH',
  PRICE_PREDICTION: 'PRICE_PREDICTION',
  QUALITY_ASSESSMENT: 'QUALITY_ASSESSMENT',
  RECOMMENDATION: 'RECOMMENDATION',
  FRAUD_DETECTION: 'FRAUD_DETECTION',
  AUTO_CATEGORIZATION: 'AUTO_CATEGORIZATION'
} as const;

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your connection.',
  UNAUTHORIZED: 'You are not authorized to perform this action.',
  FORBIDDEN: 'Access forbidden.',
  NOT_FOUND: 'Resource not found.',
  VALIDATION_ERROR: 'Please check your input and try again.',
  SERVER_ERROR: 'Something went wrong. Please try again later.',
  AUCTION_ENDED: 'This auction has ended.',
  INSUFFICIENT_CREDITS: 'Insufficient SmartBid credits.',
  BID_TOO_LOW: 'Your bid must be higher than the current price.',
  PAYMENT_FAILED: 'Payment processing failed. Please try again.'
} as const;

// Success Messages
export const SUCCESS_MESSAGES = {
  BID_PLACED: 'Your bid has been placed successfully!',
  AUCTION_CREATED: 'Auction created successfully!',
  PROFILE_UPDATED: 'Profile updated successfully!',
  PAYMENT_SUCCESS: 'Payment completed successfully!',
  EMAIL_VERIFIED: 'Email verified successfully!',
  PASSWORD_CHANGED: 'Password changed successfully!'
} as const;

// WebSocket Events
export const WS_EVENTS = {
  BID_UPDATE: 'bid_update',
  AUCTION_UPDATE: 'auction_update',
  NOTIFICATION: 'notification',
  USER_ONLINE: 'user_online',
  USER_OFFLINE: 'user_offline',
  CONNECT: 'connect',
  DISCONNECT: 'disconnect',
  ERROR: 'error'
} as const;

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'smartbid_auth_token',
  REFRESH_TOKEN: 'smartbid_refresh_token',
  USER_DATA: 'smartbid_user_data',
  THEME: 'smartbid_theme',
  SEARCH_HISTORY: 'smartbid_search_history',
  WATCHLIST: 'smartbid_watchlist',
  BID_SETTINGS: 'smartbid_bid_settings',
  NOTIFICATIONS_SETTINGS: 'smartbid_notifications',
  LANGUAGE: 'smartbid_language'
} as const;
