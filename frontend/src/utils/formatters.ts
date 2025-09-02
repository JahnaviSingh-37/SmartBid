/**
 * Format currency values
 */
export const formatCurrency = (amount: number, currency: string = 'USD'): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
};

/**
 * Format date and time
 */
export const formatDate = (date: string | Date, options?: Intl.DateTimeFormatOptions): string => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  
  const defaultOptions: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  };
  
  return new Intl.DateTimeFormat('en-US', { ...defaultOptions, ...options }).format(dateObj);
};

/**
 * Format time remaining until auction end
 */
export const formatTimeRemaining = (endTime: string): string => {
  const now = new Date();
  const end = new Date(endTime);
  const diff = end.getTime() - now.getTime();
  
  if (diff <= 0) {
    return 'Ended';
  }
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((diff % (1000 * 60)) / 1000);
  
  if (days > 0) {
    return `${days}d ${hours}h`;
  } else if (hours > 0) {
    return `${hours}h ${minutes}m`;
  } else if (minutes > 0) {
    return `${minutes}m ${seconds}s`;
  } else {
    return `${seconds}s`;
  }
};

/**
 * Format credit score with color coding
 */
export const formatCreditScore = (score: number): { 
  score: string; 
  color: string; 
  level: string; 
} => {
  const scoreString = score.toFixed(0);
  
  if (score >= 800) {
    return { score: scoreString, color: 'text-success-600', level: 'Excellent' };
  } else if (score >= 700) {
    return { score: scoreString, color: 'text-primary-600', level: 'Good' };
  } else if (score >= 600) {
    return { score: scoreString, color: 'text-warning-600', level: 'Fair' };
  } else {
    return { score: scoreString, color: 'text-danger-600', level: 'Poor' };
  }
};

/**
 * Format large numbers with K, M suffixes
 */
export const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
};

/**
 * Truncate text to specified length
 */
export const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) {
    return text;
  }
  return text.slice(0, maxLength) + '...';
};

/**
 * Format file size
 */
export const formatFileSize = (bytes: number): string => {
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  if (bytes === 0) return '0 Bytes';
  
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
};

/**
 * Get auction status color
 */
export const getAuctionStatusColor = (status: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'text-success-600 bg-success-100';
    case 'ENDED':
      return 'text-gray-600 bg-gray-100';
    case 'UPCOMING':
      return 'text-primary-600 bg-primary-100';
    case 'CANCELLED':
      return 'text-danger-600 bg-danger-100';
    case 'SUSPENDED':
      return 'text-warning-600 bg-warning-100';
    default:
      return 'text-gray-600 bg-gray-100';
  }
};

/**
 * Get bid status color
 */
export const getBidStatusColor = (status: string): string => {
  switch (status) {
    case 'WINNING':
      return 'text-success-600 bg-success-100';
    case 'OUTBID':
      return 'text-danger-600 bg-danger-100';
    case 'WON':
      return 'text-success-600 bg-success-100';
    case 'ACTIVE':
      return 'text-primary-600 bg-primary-100';
    case 'RETRACTED':
      return 'text-warning-600 bg-warning-100';
    case 'REJECTED':
      return 'text-danger-600 bg-danger-100';
    default:
      return 'text-gray-600 bg-gray-100';
  }
};
