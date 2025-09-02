/**
 * Validation utilities for SmartBid
 */

/**
 * Email validation
 */
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Password validation
 * Requirements: At least 8 characters, 1 uppercase, 1 lowercase, 1 number
 */
export const isValidPassword = (password: string): boolean => {
  const minLength = password.length >= 8;
  const hasUpperCase = /[A-Z]/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasNumbers = /\d/.test(password);
  
  return minLength && hasUpperCase && hasLowerCase && hasNumbers;
};

/**
 * Get password strength level
 */
export const getPasswordStrength = (password: string): {
  strength: 'weak' | 'medium' | 'strong' | 'very-strong';
  score: number;
  feedback: string[];
} => {
  const feedback: string[] = [];
  let score = 0;
  
  if (password.length >= 8) {
    score += 1;
  } else {
    feedback.push('Password should be at least 8 characters long');
  }
  
  if (/[A-Z]/.test(password)) {
    score += 1;
  } else {
    feedback.push('Include at least one uppercase letter');
  }
  
  if (/[a-z]/.test(password)) {
    score += 1;
  } else {
    feedback.push('Include at least one lowercase letter');
  }
  
  if (/\d/.test(password)) {
    score += 1;
  } else {
    feedback.push('Include at least one number');
  }
  
  if (/[^A-Za-z0-9]/.test(password)) {
    score += 1;
    if (password.length >= 12) {
      score += 1;
    }
  } else {
    feedback.push('Consider adding special characters for better security');
  }
  
  let strength: 'weak' | 'medium' | 'strong' | 'very-strong' = 'weak';
  
  if (score >= 5) {
    strength = 'very-strong';
  } else if (score >= 4) {
    strength = 'strong';
  } else if (score >= 3) {
    strength = 'medium';
  }
  
  return { strength, score, feedback };
};

/**
 * Username validation
 * Requirements: 3-20 characters, alphanumeric and underscore only
 */
export const isValidUsername = (username: string): boolean => {
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
  return usernameRegex.test(username);
};

/**
 * Bid amount validation
 */
export const isValidBidAmount = (
  bidAmount: number,
  currentPrice: number,
  minimumIncrement: number = 1
): { isValid: boolean; message?: string } => {
  if (bidAmount <= currentPrice) {
    return {
      isValid: false,
      message: `Bid must be higher than current price of $${currentPrice.toFixed(2)}`,
    };
  }
  
  if (bidAmount < currentPrice + minimumIncrement) {
    return {
      isValid: false,
      message: `Bid must be at least $${minimumIncrement.toFixed(2)} higher than current price`,
    };
  }
  
  if (bidAmount > 1000000) {
    return {
      isValid: false,
      message: 'Bid amount cannot exceed $1,000,000',
    };
  }
  
  return { isValid: true };
};

/**
 * Phone number validation (US format)
 */
export const isValidPhoneNumber = (phone: string): boolean => {
  const phoneRegex = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;
  return phoneRegex.test(phone);
};

/**
 * Credit card number validation using Luhn algorithm
 */
export const isValidCreditCard = (cardNumber: string): boolean => {
  const cleanNumber = cardNumber.replace(/\D/g, '');
  
  if (cleanNumber.length < 13 || cleanNumber.length > 19) {
    return false;
  }
  
  let sum = 0;
  let isEven = false;
  
  for (let i = cleanNumber.length - 1; i >= 0; i--) {
    let digit = parseInt(cleanNumber.charAt(i));
    
    if (isEven) {
      digit *= 2;
      if (digit > 9) {
        digit -= 9;
      }
    }
    
    sum += digit;
    isEven = !isEven;
  }
  
  return sum % 10 === 0;
};

/**
 * CVV validation
 */
export const isValidCVV = (cvv: string, cardType?: string): boolean => {
  const cleanCVV = cvv.replace(/\D/g, '');
  
  if (cardType === 'amex') {
    return cleanCVV.length === 4;
  } else {
    return cleanCVV.length === 3;
  }
};

/**
 * Expiry date validation
 */
export const isValidExpiryDate = (month: string, year: string): boolean => {
  const currentDate = new Date();
  const currentMonth = currentDate.getMonth() + 1;
  const currentYear = currentDate.getFullYear();
  
  const expMonth = parseInt(month);
  const expYear = parseInt(year);
  
  if (expMonth < 1 || expMonth > 12) {
    return false;
  }
  
  if (expYear < currentYear) {
    return false;
  }
  
  if (expYear === currentYear && expMonth < currentMonth) {
    return false;
  }
  
  return true;
};

/**
 * Auction title validation
 */
export const isValidAuctionTitle = (title: string): boolean => {
  return title.trim().length >= 5 && title.trim().length <= 100;
};

/**
 * Auction description validation
 */
export const isValidAuctionDescription = (description: string): boolean => {
  return description.trim().length >= 20 && description.trim().length <= 2000;
};

/**
 * Starting price validation
 */
export const isValidStartingPrice = (price: number): boolean => {
  return price > 0 && price <= 1000000;
};
