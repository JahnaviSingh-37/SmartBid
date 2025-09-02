package com.smartbid.repositories;

import com.smartbid.models.AuctionWatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionWatchRepository extends JpaRepository<AuctionWatch, Long> {
    
    // Find user's watchlist
    Page<AuctionWatch> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find watchers for an auction
    Page<AuctionWatch> findByAuctionIdOrderByCreatedAtDesc(Long auctionId, Pageable pageable);
    
    // Check if user is watching an auction
    @Query("SELECT COUNT(aw) > 0 FROM AuctionWatch aw WHERE aw.user.id = :userId AND aw.auction.id = :auctionId")
    boolean isUserWatchingAuction(@Param("userId") Long userId, @Param("auctionId") Long auctionId);
    
    // Find specific watch relationship
    Optional<AuctionWatch> findByUserIdAndAuctionId(Long userId, Long auctionId);
    
    // Count watchers for an auction
    @Query("SELECT COUNT(aw) FROM AuctionWatch aw WHERE aw.auction.id = :auctionId")
    Long countWatchersByAuction(@Param("auctionId") Long auctionId);
    
    // Count watchlist items for a user
    @Query("SELECT COUNT(aw) FROM AuctionWatch aw WHERE aw.user.id = :userId")
    Long countWatchlistByUser(@Param("userId") Long userId);
    
    // Find active auction watches for a user
    @Query("SELECT aw FROM AuctionWatch aw WHERE aw.user.id = :userId AND aw.auction.status = 'ACTIVE' ORDER BY aw.createdAt DESC")
    List<AuctionWatch> findActiveAuctionWatchesByUser(@Param("userId") Long userId);
    
    // Find watches created in time range
    Page<AuctionWatch> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // Delete watch relationship
    void deleteByUserIdAndAuctionId(Long userId, Long auctionId);
    
    // Find ending soon notifications (auctions ending in next hour that user is watching)
    @Query("SELECT aw FROM AuctionWatch aw WHERE aw.user.id = :userId AND aw.auction.status = 'ACTIVE' " +
           "AND aw.auction.endTime BETWEEN :now AND :oneHourLater AND aw.notificationsEnabled = true")
    List<AuctionWatch> findEndingSoonNotifications(@Param("userId") Long userId, 
                                                  @Param("now") LocalDateTime now, 
                                                  @Param("oneHourLater") LocalDateTime oneHourLater);
    
    // Find price alert notifications
    @Query("SELECT aw FROM AuctionWatch aw WHERE aw.auction.status = 'ACTIVE' AND aw.priceAlert IS NOT NULL " +
           "AND aw.auction.currentPrice <= aw.priceAlert AND aw.notificationsEnabled = true")
    List<AuctionWatch> findPriceAlertNotifications();
    
    // Find most watched auctions
    @Query("SELECT aw.auction.id, COUNT(aw) as watchCount FROM AuctionWatch aw " +
           "WHERE aw.auction.status = 'ACTIVE' GROUP BY aw.auction.id ORDER BY watchCount DESC")
    List<Object[]> findMostWatchedAuctions(Pageable pageable);
}
