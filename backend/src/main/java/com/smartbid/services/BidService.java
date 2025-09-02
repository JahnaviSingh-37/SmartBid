package com.smartbid.services;

import com.smartbid.models.Auction;
import com.smartbid.models.Bid;
import com.smartbid.models.Bid.BidStatus;
import com.smartbid.models.User;
import com.smartbid.repositories.AuctionRepository;
import com.smartbid.repositories.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // Place a bid
    public Bid placeBid(Long auctionId, BigDecimal bidAmount, User bidder) {
        // Get auction
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction auction = auctionOpt.get();

        // Validate bid
        validateBid(auction, bidAmount, bidder);

        // Get current highest bid
        Optional<Bid> currentHighestBidOpt = bidRepository.findHighestBidByAuction(auctionId);

        // Create new bid
        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setUser(bidder);
        newBid.setBidAmount(bidAmount);
        newBid.setStatus(BidStatus.ACTIVE);
        newBid.setType(Bid.BidType.MANUAL);

        // Save the bid
        newBid = bidRepository.save(newBid);

        // Update auction current price and bid count
        auction.setCurrentPrice(bidAmount);
        auction.incrementBidCount();
        auctionRepository.save(auction);

        // Update previous highest bid to OUTBID if exists
        if (currentHighestBidOpt.isPresent()) {
            Bid previousHighestBid = currentHighestBidOpt.get();
            if (!previousHighestBid.getUser().getId().equals(bidder.getId())) {
                previousHighestBid.setStatus(BidStatus.OUTBID);
                bidRepository.save(previousHighestBid);

                // Send outbid notification
                notificationService.sendOutbidNotification(previousHighestBid.getUser(), auction, newBid);
            }
        }

        // Update bid status to WINNING (highest bid)
        newBid.setStatus(BidStatus.WINNING);
        bidRepository.save(newBid);

        // Send bid placed notification
        notificationService.sendBidPlacedNotification(bidder, auction, newBid);

        // Update user's bidding statistics (credit score factor)
        userService.updateBiddingStatistics(bidder.getId(), bidAmount, true);

        return newBid;
    }

    // Validate bid
    private void validateBid(Auction auction, BigDecimal bidAmount, User bidder) {
        // Check if auction is active
        if (!auction.isActive()) {
            throw new IllegalArgumentException("Auction is not active");
        }

        // Check if bidder is not the auction owner
        if (auction.getUser().getId().equals(bidder.getId())) {
            throw new IllegalArgumentException("You cannot bid on your own auction");
        }

        // Check minimum bid amount
        BigDecimal minimumBid = auction.getMinimumNextBid();
        if (bidAmount.compareTo(minimumBid) < 0) {
            throw new IllegalArgumentException(
                String.format("Bid must be at least $%.2f", minimumBid));
        }

        // Check user's credit limit (if any restrictions based on credit score)
        if (bidder.getCreditScore().compareTo(BigDecimal.valueOf(300)) < 0) {
            throw new IllegalArgumentException("Credit score too low to place bids");
        }

        // Check if user has sufficient SmartBid credits for high-value bids
        if (bidAmount.compareTo(new BigDecimal("1000")) > 0 && bidder.getCreditScore().compareTo(BigDecimal.valueOf(600)) < 0) {
            throw new IllegalArgumentException("Higher credit score required for bids over $1000");
        }
    }

    // Get bid by ID
    @Transactional(readOnly = true)
    public Optional<Bid> getBidById(Long id) {
        return bidRepository.findById(id);
    }

    // Get bids by auction
    @Transactional(readOnly = true)
    public Page<Bid> getBidsByAuction(Long auctionId, Pageable pageable) {
        return bidRepository.findByAuctionIdOrderByBidAmountDescCreatedAtDesc(auctionId, pageable);
    }

    // Get bids by user
    @Transactional(readOnly = true)
    public Page<Bid> getBidsByUser(Long userId, Pageable pageable) {
        return bidRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    // Get highest bid for auction
    @Transactional(readOnly = true)
    public Optional<Bid> getHighestBidForAuction(Long auctionId) {
        return bidRepository.findHighestBidByAuction(auctionId);
    }

    // Get user's highest bid for auction
    @Transactional(readOnly = true)
    public Optional<Bid> getUserHighestBidForAuction(Long auctionId, Long userId) {
        return bidRepository.findUserHighestBidForAuction(auctionId, userId);
    }

    // Get winning bids for user
    @Transactional(readOnly = true)
    public Page<Bid> getWinningBidsForUser(Long userId, Pageable pageable) {
        return bidRepository.findWinningBidsByUser(userId, pageable);
    }

    // Get outbid bids for user
    @Transactional(readOnly = true)
    public Page<Bid> getOutbidBidsForUser(Long userId, Pageable pageable) {
        return bidRepository.findOutbidBidsByUser(userId, pageable);
    }

    // Check if user has bid on auction
    @Transactional(readOnly = true)
    public boolean hasUserBidOnAuction(Long auctionId, Long userId) {
        return bidRepository.hasUserBidOnAuction(auctionId, userId);
    }

    // Retract bid (only if auction hasn't ended and user has valid reason)
    public Bid retractBid(Long bidId, User user, String reason) {
        Optional<Bid> bidOpt = bidRepository.findById(bidId);
        if (!bidOpt.isPresent()) {
            throw new IllegalArgumentException("Bid not found");
        }

        Bid bid = bidOpt.get();

        // Check ownership
        if (!bid.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only retract your own bids");
        }

        // Check if auction is still active
        if (!bid.getAuction().isActive()) {
            throw new IllegalArgumentException("Cannot retract bid from inactive auction");
        }

        // Check if this is the highest bid
        Optional<Bid> highestBidOpt = bidRepository.findHighestBidByAuction(bid.getAuction().getId());
        if (highestBidOpt.isPresent() && highestBidOpt.get().getId().equals(bidId)) {
            // Find second highest bid and make it the winner
            Optional<Bid> secondHighestOpt = bidRepository.findSecondHighestBid(
                bid.getAuction().getId(), bid.getBidAmount());
            
            if (secondHighestOpt.isPresent()) {
                Bid secondHighest = secondHighestOpt.get();
                secondHighest.setStatus(BidStatus.WINNING);
                bidRepository.save(secondHighest);
                
                // Update auction current price
                Auction auction = bid.getAuction();
                auction.setCurrentPrice(secondHighest.getBidAmount());
                auctionRepository.save(auction);
            }
        }

        // Update bid status
        bid.setStatus(BidStatus.RETRACTED);
        bid.setNotes(reason);

        // Penalize user's credit score for bid retraction
        userService.penalizeForBidRetraction(user.getId());

        return bidRepository.save(bid);
    }

    // Get bid statistics for user
    @Transactional(readOnly = true)
    public BidStatistics getBidStatisticsForUser(Long userId) {
        Long totalBids = bidRepository.countBidsByUser(userId);
        Long activeBids = bidRepository.countActiveBidsByUser(userId);
        Long winningBids = bidRepository.countWinningBidsByUser(userId);
        Optional<BigDecimal> totalWinningAmount = bidRepository.getTotalWinningBidAmountByUser(userId);
        Optional<BigDecimal> averageBidAmount = bidRepository.getAverageBidAmountByUser(userId);
        Optional<BigDecimal> maxBidAmount = bidRepository.getMaxBidAmountByUser(userId);
        
        return new BidStatistics(
            totalBids, 
            activeBids, 
            winningBids,
            totalWinningAmount.orElse(BigDecimal.ZERO),
            averageBidAmount.orElse(BigDecimal.ZERO),
            maxBidAmount.orElse(BigDecimal.ZERO)
        );
    }

    // Get active auction bids for user
    @Transactional(readOnly = true)
    public List<Bid> getActiveAuctionBidsForUser(Long userId) {
        return bidRepository.findActiveAuctionBidsByUser(userId);
    }

    // Auto-bid functionality (proxy bidding)
    public Bid placeProxyBid(Long auctionId, BigDecimal maxBidAmount, User bidder) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction auction = auctionOpt.get();
        
        // Validate
        validateBid(auction, maxBidAmount, bidder);

        // Create proxy bid
        Bid proxyBid = new Bid();
        proxyBid.setAuction(auction);
        proxyBid.setUser(bidder);
        proxyBid.setMaxBidAmount(maxBidAmount);
        proxyBid.setType(Bid.BidType.PROXY);
        proxyBid.setStatus(BidStatus.ACTIVE);

        // Calculate actual bid amount (just enough to win)
        Optional<Bid> currentHighestOpt = bidRepository.findHighestBidByAuction(auctionId);
        BigDecimal actualBidAmount;
        
        if (currentHighestOpt.isPresent()) {
            actualBidAmount = currentHighestOpt.get().getBidAmount().add(new BigDecimal("1.00"));
            if (actualBidAmount.compareTo(maxBidAmount) > 0) {
                actualBidAmount = maxBidAmount;
            }
        } else {
            actualBidAmount = auction.getCurrentPrice().add(new BigDecimal("1.00"));
        }

        proxyBid.setBidAmount(actualBidAmount);
        proxyBid = bidRepository.save(proxyBid);

        // Update auction
        auction.setCurrentPrice(actualBidAmount);
        auction.incrementBidCount();
        auctionRepository.save(auction);

        return proxyBid;
    }

    // Process proxy bids when new regular bid is placed
    public void processProxyBids(Long auctionId, BigDecimal newBidAmount) {
        List<Bid> proxyBids = bidRepository.findByAuctionIdAndStatusOrderByBidAmountDesc(
            auctionId, BidStatus.ACTIVE);
        
        for (Bid proxyBid : proxyBids) {
            if (proxyBid.getType() == Bid.BidType.PROXY && 
                proxyBid.getMaxBidAmount().compareTo(newBidAmount) > 0) {
                
                // Auto-increment proxy bid
                BigDecimal newProxyBid = newBidAmount.add(new BigDecimal("1.00"));
                if (newProxyBid.compareTo(proxyBid.getMaxBidAmount()) <= 0) {
                    proxyBid.setBidAmount(newProxyBid);
                    bidRepository.save(proxyBid);
                    
                    // Update auction current price
                    Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
                    if (auctionOpt.isPresent()) {
                        Auction auction = auctionOpt.get();
                        auction.setCurrentPrice(newProxyBid);
                        auctionRepository.save(auction);
                    }
                }
            }
        }
    }

    // Bid statistics inner class
    public static class BidStatistics {
        private final Long totalBids;
        private final Long activeBids;
        private final Long winningBids;
        private final BigDecimal totalWinningAmount;
        private final BigDecimal averageBidAmount;
        private final BigDecimal maxBidAmount;

        public BidStatistics(Long totalBids, Long activeBids, Long winningBids,
                           BigDecimal totalWinningAmount, BigDecimal averageBidAmount, 
                           BigDecimal maxBidAmount) {
            this.totalBids = totalBids;
            this.activeBids = activeBids;
            this.winningBids = winningBids;
            this.totalWinningAmount = totalWinningAmount;
            this.averageBidAmount = averageBidAmount;
            this.maxBidAmount = maxBidAmount;
        }

        // Getters
        public Long getTotalBids() { return totalBids; }
        public Long getActiveBids() { return activeBids; }
        public Long getWinningBids() { return winningBids; }
        public BigDecimal getTotalWinningAmount() { return totalWinningAmount; }
        public BigDecimal getAverageBidAmount() { return averageBidAmount; }
        public BigDecimal getMaxBidAmount() { return maxBidAmount; }
    }
}
