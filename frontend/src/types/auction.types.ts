export interface Auction {
  id: number;
  title: string;
  description: string;
  startingPrice: number;
  currentPrice: number;
  reservePrice?: number;
  buyNowPrice?: number;
  startTime: string;
  endTime: string;
  status: 'UPCOMING' | 'ACTIVE' | 'ENDED' | 'CANCELLED' | 'SUSPENDED';
  type: 'STANDARD' | 'RESERVE' | 'BUY_NOW' | 'DUTCH' | 'PENNY';
  category: string;
  conditionType: 'NEW' | 'LIKE_NEW' | 'GOOD' | 'FAIR' | 'POOR' | 'REFURBISHED';
  shippingCost: number;
  shippingLocation?: string;
  imageUrls: string[];
  featuredImageUrl?: string;
  aiGeneratedTags?: string[];
  pricePrediction?: number;
  qualityScore?: number;
  viewCount: number;
  watchCount: number;
  bidCount: number;
  winnerId?: number;
  finalPrice?: number;
  isPaid: boolean;
  isShipped: boolean;
  createdAt: string;
  updatedAt: string;
  seller: {
    id: number;
    username: string;
    creditScore: number;
    profileImageUrl?: string;
  };
}

export interface Bid {
  id: number;
  bidAmount: number;
  maxBidAmount?: number;
  status: 'ACTIVE' | 'OUTBID' | 'WINNING' | 'WON' | 'RETRACTED' | 'REJECTED';
  type: 'MANUAL' | 'AUTOMATIC' | 'BUY_NOW';
  isSuspicious: boolean;
  fraudScore?: number;
  createdAt: string;
  bidder: {
    id: number;
    username: string;
    profileImageUrl?: string;
  };
  auction: {
    id: number;
    title: string;
    featuredImageUrl?: string;
  };
}

export interface CreateAuctionRequest {
  title: string;
  description: string;
  startingPrice: number;
  reservePrice?: number;
  buyNowPrice?: number;
  startTime: string;
  endTime: string;
  type: Auction['type'];
  category: string;
  conditionType: Auction['conditionType'];
  shippingCost: number;
  shippingLocation?: string;
  images?: File[];
}

export interface PlaceBidRequest {
  auctionId: number;
  bidAmount: number;
  maxBidAmount?: number;
  type?: 'MANUAL' | 'AUTOMATIC';
}

export interface AuctionFilters {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  condition?: Auction['conditionType'];
  status?: Auction['status'];
  sortBy?: 'endTime' | 'currentPrice' | 'createdAt' | 'bidCount';
  sortOrder?: 'asc' | 'desc';
  search?: string;
}
