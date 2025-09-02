package com.smartbid.services;

import com.smartbid.models.Auction;
import com.smartbid.models.Auction.AuctionStatus;
import com.smartbid.models.Auction.AuctionType;
import com.smartbid.models.User;
import com.smartbid.repositories.AuctionRepository;
import com.smartbid.repositories.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private NotificationService notificationService;

    // Create a new auction
    public Auction createAuction(Auction auction, User user) {
        auction.setUser(user);
        auction.setStatus(AuctionStatus.UPCOMING);
        auction.setCurrentPrice(auction.getStartingPrice());
        auction.setViewCount(0);
        auction.setWatchCount(0);
        auction.setBidCount(0);
        
        // Validate auction dates
        if (auction.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be in the past");
        }
        if (auction.getEndTime().isBefore(auction.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        return auctionRepository.save(auction);
    }

    // Get auction by ID
    @Transactional(readOnly = true)
    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }

    // Get auction by ID with view count increment
    public Optional<Auction> getAuctionByIdAndIncrementView(Long id) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            auction.incrementViewCount();
            auctionRepository.save(auction);
        }
        return auctionOpt;
    }

    // Get all auctions with pagination
    @Transactional(readOnly = true)
    public Page<Auction> getAllAuctions(Pageable pageable) {
        return auctionRepository.findAll(pageable);
    }

    // Get active auctions
    @Transactional(readOnly = true)
    public Page<Auction> getActiveAuctions(Pageable pageable) {
        return auctionRepository.findActiveAuctions(LocalDateTime.now(), pageable);
    }

    // Get auctions by status
    @Transactional(readOnly = true)
    public Page<Auction> getAuctionsByStatus(AuctionStatus status, Pageable pageable) {
        return auctionRepository.findByStatus(status, pageable);
    }

    // Get auctions by category
    @Transactional(readOnly = true)
    public Page<Auction> getAuctionsByCategory(String category, Pageable pageable) {
        return auctionRepository.findByCategoryIgnoreCase(category, pageable);
    }

    // Get auctions by user
    @Transactional(readOnly = true)
    public Page<Auction> getAuctionsByUser(Long userId, Pageable pageable) {
        return auctionRepository.findByUserId(userId, pageable);
    }

    // Search auctions
    @Transactional(readOnly = true)
    public Page<Auction> searchAuctions(String searchTerm, Pageable pageable) {
        return auctionRepository.searchAuctions(searchTerm, pageable);
    }

    // Get auctions with filters
    @Transactional(readOnly = true)
    public Page<Auction> getAuctionsWithFilters(String category, BigDecimal minPrice, 
                                               BigDecimal maxPrice, AuctionStatus status, 
                                               AuctionType type, Pageable pageable) {
        return auctionRepository.findWithFilters(category, minPrice, maxPrice, status, type, pageable);
    }

    // Get featured auctions
    @Transactional(readOnly = true)
    public List<Auction> getFeaturedAuctions(Pageable pageable) {
        return auctionRepository.findFeaturedAuctions(pageable);
    }

    // Get popular auctions by category
    @Transactional(readOnly = true)
    public List<Auction> getPopularByCategory(String category, Pageable pageable) {
        return auctionRepository.findPopularByCategory(category, pageable);
    }

    // Get auctions ending soon
    @Transactional(readOnly = true)
    public List<Auction> getAuctionsEndingSoon(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(hours);
        return auctionRepository.findAuctionsEndingSoon(now, endTime);
    }

    // Update auction
    public Auction updateAuction(Long auctionId, Auction updatedAuction, User user) {
        Optional<Auction> existingAuctionOpt = auctionRepository.findById(auctionId);
        if (!existingAuctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction existingAuction = existingAuctionOpt.get();
        
        // Check if user owns the auction
        if (!existingAuction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only update your own auctions");
        }

        // Check if auction can be updated (only upcoming auctions)
        if (existingAuction.getStatus() != AuctionStatus.UPCOMING) {
            throw new IllegalArgumentException("Can only update upcoming auctions");
        }

        // Update allowed fields
        existingAuction.setTitle(updatedAuction.getTitle());
        existingAuction.setDescription(updatedAuction.getDescription());
        existingAuction.setCategory(updatedAuction.getCategory());
        existingAuction.setConditionType(updatedAuction.getConditionType());
        existingAuction.setShippingCost(updatedAuction.getShippingCost());
        existingAuction.setShippingLocation(updatedAuction.getShippingLocation());
        existingAuction.setImageUrls(updatedAuction.getImageUrls());
        existingAuction.setFeaturedImageUrl(updatedAuction.getFeaturedImageUrl());

        // Update prices only if no bids exist
        if (existingAuction.getBidCount() == 0) {
            existingAuction.setStartingPrice(updatedAuction.getStartingPrice());
            existingAuction.setCurrentPrice(updatedAuction.getStartingPrice());
            existingAuction.setReservePrice(updatedAuction.getReservePrice());
            existingAuction.setBuyNowPrice(updatedAuction.getBuyNowPrice());
        }

        return auctionRepository.save(existingAuction);
    }

    // Start auction (change status from UPCOMING to ACTIVE)
    public Auction startAuction(Long auctionId, User user) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction auction = auctionOpt.get();
        
        // Check ownership
        if (!auction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only start your own auctions");
        }

        if (auction.getStatus() != AuctionStatus.UPCOMING) {
            throw new IllegalArgumentException("Only upcoming auctions can be started");
        }

        auction.setStatus(AuctionStatus.ACTIVE);
        return auctionRepository.save(auction);
    }

    // End auction manually
    public Auction endAuction(Long auctionId, User user) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction auction = auctionOpt.get();
        
        // Check ownership or admin privileges
        if (!auction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only end your own auctions");
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active auctions can be ended");
        }

        return endAuctionProcess(auction);
    }

    // Cancel auction
    public Auction cancelAuction(Long auctionId, User user) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction auction = auctionOpt.get();
        
        // Check ownership
        if (!auction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only cancel your own auctions");
        }

        // Can only cancel upcoming or active auctions with no bids
        if (auction.getStatus() == AuctionStatus.ENDED) {
            throw new IllegalArgumentException("Cannot cancel ended auctions");
        }

        if (auction.getBidCount() > 0) {
            throw new IllegalArgumentException("Cannot cancel auctions with bids");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        return auctionRepository.save(auction);
    }

    // Process ended auctions (scheduled task)
    @Transactional
    public void processEndedAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> endingAuctions = auctionRepository.findAuctionsEndingSoon(now.minusMinutes(1), now);
        
        for (Auction auction : endingAuctions) {
            if (auction.getStatus() == AuctionStatus.ACTIVE) {
                endAuctionProcess(auction);
            }
        }
    }

    // Internal method to end auction and determine winner
    private Auction endAuctionProcess(Auction auction) {
        auction.setStatus(AuctionStatus.ENDED);
        
        // Find highest bid
        Optional<com.smartbid.models.Bid> highestBidOpt = bidRepository.findHighestBidByAuction(auction.getId());
        
        if (highestBidOpt.isPresent()) {
            com.smartbid.models.Bid winningBid = highestBidOpt.get();
            
            // Check if reserve price is met
            if (auction.isReserveMet()) {
                auction.setWinnerId(winningBid.getUser().getId());
                auction.setFinalPrice(winningBid.getBidAmount());
                
                // Update bid status
                winningBid.setStatus(com.smartbid.models.Bid.BidStatus.WON);
                bidRepository.save(winningBid);
                
                // Send winner notification
                notificationService.sendAuctionWonNotification(winningBid.getUser(), auction);
                notificationService.sendAuctionSoldNotification(auction.getUser(), auction);
            } else {
                // Reserve not met, no winner
                notificationService.sendReserveNotMetNotification(auction.getUser(), auction);
            }
        }
        
        // Update other losing bids
        bidRepository.updateLostBidsForEndedAuctions(LocalDateTime.now());
        
        return auctionRepository.save(auction);
    }

    // Get auction statistics for user
    @Transactional(readOnly = true)
    public AuctionStatistics getAuctionStatisticsForUser(Long userId) {
        Long totalAuctions = auctionRepository.countTotalAuctionsByUser(userId);
        Long activeAuctions = auctionRepository.countActiveAuctionsByUser(userId);
        Long soldAuctions = auctionRepository.countSoldAuctionsByUser(userId);
        Optional<BigDecimal> averageSalePrice = auctionRepository.getAverageSalePriceByUser(userId);
        
        return new AuctionStatistics(totalAuctions, activeAuctions, soldAuctions, 
                                   averageSalePrice.orElse(BigDecimal.ZERO));
    }

    // Get similar auctions
    @Transactional(readOnly = true)
    public List<Auction> getSimilarAuctions(Long auctionId, Pageable pageable) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            return List.of();
        }

        Auction auction = auctionOpt.get();
        BigDecimal currentPrice = auction.getCurrentPrice();
        BigDecimal minPrice = currentPrice.multiply(new BigDecimal("0.5")); // 50% of current price
        BigDecimal maxPrice = currentPrice.multiply(new BigDecimal("2.0")); // 200% of current price

        return auctionRepository.findSimilarAuctions(
            auctionId, auction.getCategory(), minPrice, maxPrice, currentPrice, pageable);
    }

    // Delete auction (only if no bids and upcoming)
    public void deleteAuction(Long auctionId, User user) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new IllegalArgumentException("Auction not found");
        }

        Auction auction = auctionOpt.get();
        
        // Check ownership
        if (!auction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own auctions");
        }

        // Can only delete upcoming auctions with no bids
        if (auction.getStatus() != AuctionStatus.UPCOMING) {
            throw new IllegalArgumentException("Can only delete upcoming auctions");
        }

        if (auction.getBidCount() > 0) {
            throw new IllegalArgumentException("Cannot delete auctions with bids");
        }

        auctionRepository.delete(auction);
    }

    // Additional methods for controller compatibility
    
    /**
     * Create auction - overloaded method for controller compatibility
     */
    public Auction createAuction(Auction auction) {
        if (auction.getUser() == null) {
            throw new IllegalArgumentException("Auction must have a seller");
        }
        return createAuction(auction, auction.getUser());
    }

    /**
     * Find auction by ID
     */
    public Optional<Auction> findById(Long id) {
        return getAuctionById(id);
    }

    /**
     * Update auction
     */
    public Auction updateAuction(Auction auction) {
        if (auction.getId() == null) {
            throw new IllegalArgumentException("Auction ID is required for update");
        }
        
        Optional<Auction> existing = getAuctionById(auction.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Auction not found");
        }

        // Only allow updates for upcoming or active auctions
        Auction existingAuction = existing.get();
        if (existingAuction.getStatus() == Auction.AuctionStatus.ENDED || 
            existingAuction.getStatus() == Auction.AuctionStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update ended or cancelled auctions");
        }

        return auctionRepository.save(auction);
    }

    /**
     * End auction - overloaded for controller compatibility
     */
    public Auction endAuction(Long auctionId) {
        Optional<Auction> auction = getAuctionById(auctionId);
        if (auction.isEmpty()) {
            throw new IllegalArgumentException("Auction not found");
        }
        return endAuction(auctionId, auction.get().getUser());
    }

    /**
     * Get auctions by seller with pagination
     */
    public Page<Auction> getAuctionsBySeller(Long sellerId, Pageable pageable) {
        return auctionRepository.findByUserId(sellerId, pageable);
    }

    /**
     * Get ending soon auctions
     */
    public List<Auction> getEndingSoonAuctions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(24); // Next 24 hours
        return auctionRepository.findAuctionsEndingSoon(now, endTime);
    }

    /**
     * Get popular auctions
     */
    public List<Auction> getPopularAuctions() {
        return auctionRepository.findFeaturedAuctions(PageRequest.of(0, 10));
    }

    /**
     * Get similar auctions - wrapper for single ID
     */
    public List<Auction> getSimilarAuctions(Long auctionId) {
        List<Auction> similarAuctions = getSimilarAuctions(auctionId, PageRequest.of(0, 10));
        return similarAuctions;
    }

    /**
     * Get auction statistics
     */
    public AuctionStatistics getAuctionStatistics() {
        Long totalAuctions = auctionRepository.count();
        // Use simple counts for now
        Long activeAuctions = 0L;
        Long soldAuctions = 0L;
        BigDecimal avgPrice = BigDecimal.ZERO;
        
        return new AuctionStatistics(totalAuctions, activeAuctions, soldAuctions, avgPrice);
    }

    /**
     * Search auctions by title with pagination
     */
    public Page<Auction> searchAuctionsByTitle(String query, Pageable pageable) {
        return searchAuctions(query, pageable);
    }

    /**
     * Enhanced search with multiple filters
     */
    public Page<Auction> searchAuctions(String category, String location, BigDecimal minPrice, 
                                      BigDecimal maxPrice, String condition, Pageable pageable) {
        // For now, fallback to simple search - can be enhanced later
        return getAllAuctions(pageable);
    }

    /**
     * Statistics inner class
     */

    // Statistics inner class
    public static class AuctionStatistics {
        private final Long totalAuctions;
        private final Long activeAuctions;
        private final Long soldAuctions;
        private final BigDecimal averageSalePrice;

        public AuctionStatistics(Long totalAuctions, Long activeAuctions, 
                               Long soldAuctions, BigDecimal averageSalePrice) {
            this.totalAuctions = totalAuctions;
            this.activeAuctions = activeAuctions;
            this.soldAuctions = soldAuctions;
            this.averageSalePrice = averageSalePrice;
        }

        // Getters
        public Long getTotalAuctions() { return totalAuctions; }
        public Long getActiveAuctions() { return activeAuctions; }
        public Long getSoldAuctions() { return soldAuctions; }
        public BigDecimal getAverageSalePrice() { return averageSalePrice; }
    }
}
