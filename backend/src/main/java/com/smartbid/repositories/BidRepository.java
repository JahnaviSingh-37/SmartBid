package com.smartbid.repositories;

import com.smartbid.models.Bid;
import com.smartbid.models.Bid.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    
    // Find bids by auction ID
    Page<Bid> findByAuctionIdOrderByBidAmountDescCreatedAtDesc(Long auctionId, Pageable pageable);
    
    // Find bids by user ID
    Page<Bid> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find highest bid for an auction
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.status = 'ACTIVE' ORDER BY b.bidAmount DESC, b.createdAt ASC")
    Optional<Bid> findHighestBidByAuction(@Param("auctionId") Long auctionId);
    
    // Find user's highest bid for an auction
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.user.id = :userId AND b.status = 'ACTIVE' ORDER BY b.bidAmount DESC")
    Optional<Bid> findUserHighestBidForAuction(@Param("auctionId") Long auctionId, @Param("userId") Long userId);
    
    // Find all active bids for an auction
    List<Bid> findByAuctionIdAndStatusOrderByBidAmountDesc(Long auctionId, BidStatus status);
    
    // Find bids by status
    Page<Bid> findByStatus(BidStatus status, Pageable pageable);
    
    // Find winning bids for a user
    @Query("SELECT b FROM Bid b WHERE b.user.id = :userId AND b.status = 'WINNING' ORDER BY b.createdAt DESC")
    Page<Bid> findWinningBidsByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find outbid bids for a user
    @Query("SELECT b FROM Bid b WHERE b.user.id = :userId AND b.status = 'OUTBID' ORDER BY b.createdAt DESC")
    Page<Bid> findOutbidBidsByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Count total bids by user
    @Query("SELECT COUNT(b) FROM Bid b WHERE b.user.id = :userId")
    Long countBidsByUser(@Param("userId") Long userId);
    
    // Count active bids by user
    @Query("SELECT COUNT(b) FROM Bid b WHERE b.user.id = :userId AND b.status = 'ACTIVE'")
    Long countActiveBidsByUser(@Param("userId") Long userId);
    
    // Count winning bids by user
    @Query("SELECT COUNT(b) FROM Bid b WHERE b.user.id = :userId AND b.status = 'WINNING'")
    Long countWinningBidsByUser(@Param("userId") Long userId);
    
    // Find bids above certain amount
    Page<Bid> findByBidAmountGreaterThanEqualOrderByBidAmountDesc(BigDecimal amount, Pageable pageable);
    
    // Find bids in time range
    Page<Bid> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // Find second highest bid for an auction (for proxy bidding)
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.status = 'ACTIVE' AND b.bidAmount < :highestBid ORDER BY b.bidAmount DESC")
    Optional<Bid> findSecondHighestBid(@Param("auctionId") Long auctionId, @Param("highestBid") BigDecimal highestBid);
    
    // Check if user has already bid on auction
    @Query("SELECT COUNT(b) > 0 FROM Bid b WHERE b.auction.id = :auctionId AND b.user.id = :userId AND b.status != 'RETRACTED'")
    boolean hasUserBidOnAuction(@Param("auctionId") Long auctionId, @Param("userId") Long userId);
    
    // Find all bids by a user that are still active
    @Query("SELECT b FROM Bid b WHERE b.user.id = :userId AND b.status = 'ACTIVE' AND b.auction.status = 'ACTIVE' ORDER BY b.createdAt DESC")
    List<Bid> findActiveAuctionBidsByUser(@Param("userId") Long userId);
    
    // Update bid status for ended auctions
    @Query("UPDATE Bid b SET b.status = 'LOST' WHERE b.auction.endTime <= :now AND b.status = 'ACTIVE' AND b.id NOT IN " +
           "(SELECT b2.id FROM Bid b2 WHERE b2.auction = b.auction AND b2.status = 'ACTIVE' ORDER BY b2.bidAmount DESC, b2.createdAt ASC LIMIT 1)")
    int updateLostBidsForEndedAuctions(@Param("now") LocalDateTime now);
    
    // Find potential winner bids for ended auctions
    @Query("SELECT b FROM Bid b WHERE b.auction.endTime <= :now AND b.auction.status = 'ACTIVE' AND b.status = 'ACTIVE' " +
           "AND b.bidAmount = (SELECT MAX(b2.bidAmount) FROM Bid b2 WHERE b2.auction = b.auction AND b2.status = 'ACTIVE')")
    List<Bid> findPotentialWinnerBids(@Param("now") LocalDateTime now);
    
    // Statistics
    @Query("SELECT SUM(b.bidAmount) FROM Bid b WHERE b.user.id = :userId AND b.status = 'WON'")
    Optional<BigDecimal> getTotalWinningBidAmountByUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(b.bidAmount) FROM Bid b WHERE b.user.id = :userId")
    Optional<BigDecimal> getAverageBidAmountByUser(@Param("userId") Long userId);
    
    @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.user.id = :userId")
    Optional<BigDecimal> getMaxBidAmountByUser(@Param("userId") Long userId);
}
