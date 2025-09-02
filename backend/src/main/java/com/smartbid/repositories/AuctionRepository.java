package com.smartbid.repositories;

import com.smartbid.models.Auction;
import com.smartbid.models.Auction.AuctionStatus;
import com.smartbid.models.Auction.AuctionType;
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
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    
    // Find auctions by status
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);
    
    // Find active auctions
    @Query("SELECT a FROM Auction a WHERE a.status = 'ACTIVE' AND a.startTime <= :now AND a.endTime > :now")
    Page<Auction> findActiveAuctions(@Param("now") LocalDateTime now, Pageable pageable);
    
    // Find auctions ending soon
    @Query("SELECT a FROM Auction a WHERE a.status = 'ACTIVE' AND a.endTime BETWEEN :now AND :endTime ORDER BY a.endTime ASC")
    List<Auction> findAuctionsEndingSoon(@Param("now") LocalDateTime now, @Param("endTime") LocalDateTime endTime);
    
    // Find auctions by category
    Page<Auction> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    // Find auctions by user
    Page<Auction> findByUserId(Long userId, Pageable pageable);
    
    // Search auctions by title or description
    @Query("SELECT a FROM Auction a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Auction> searchAuctions(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find auctions with filters
    @Query("SELECT a FROM Auction a WHERE " +
           "(:category IS NULL OR LOWER(a.category) = LOWER(:category)) AND " +
           "(:minPrice IS NULL OR a.currentPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.currentPrice <= :maxPrice) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:type IS NULL OR a.type = :type)")
    Page<Auction> findWithFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") AuctionStatus status,
            @Param("type") AuctionType type,
            Pageable pageable);
    
    // Find featured auctions (high bid count or watch count)
    @Query("SELECT a FROM Auction a WHERE a.status = 'ACTIVE' AND " +
           "(a.bidCount >= 5 OR a.watchCount >= 10) ORDER BY a.bidCount DESC, a.watchCount DESC")
    List<Auction> findFeaturedAuctions(Pageable pageable);
    
    // Find popular auctions by category
    @Query("SELECT a FROM Auction a WHERE a.status = 'ACTIVE' AND " +
           "LOWER(a.category) = LOWER(:category) ORDER BY a.viewCount DESC, a.bidCount DESC")
    List<Auction> findPopularByCategory(@Param("category") String category, Pageable pageable);
    
    // Find auctions by price range
    Page<Auction> findByCurrentPriceBetweenAndStatus(
            BigDecimal minPrice, BigDecimal maxPrice, AuctionStatus status, Pageable pageable);
    
    // Count active auctions by user
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.user.id = :userId AND a.status = 'ACTIVE'")
    Long countActiveAuctionsByUser(@Param("userId") Long userId);
    
    // Find auctions created in date range
    Page<Auction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find won auctions by user
    @Query("SELECT a FROM Auction a WHERE a.winnerId = :userId ORDER BY a.updatedAt DESC")
    Page<Auction> findWonAuctionsByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Update auction status for ended auctions
    @Query("UPDATE Auction a SET a.status = 'ENDED' WHERE a.status = 'ACTIVE' AND a.endTime <= :now")
    int updateEndedAuctions(@Param("now") LocalDateTime now);
    
    // Find similar auctions by category and price range
    @Query("SELECT a FROM Auction a WHERE a.id != :auctionId AND " +
           "LOWER(a.category) = LOWER(:category) AND a.status = 'ACTIVE' AND " +
           "a.currentPrice BETWEEN :minPrice AND :maxPrice ORDER BY ABS(a.currentPrice - :targetPrice)")
    List<Auction> findSimilarAuctions(
            @Param("auctionId") Long auctionId,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("targetPrice") BigDecimal targetPrice,
            Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.user.id = :userId")
    Long countTotalAuctionsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.user.id = :userId AND a.status = 'ENDED' AND a.winnerId IS NOT NULL")
    Long countSoldAuctionsByUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(a.finalPrice) FROM Auction a WHERE a.user.id = :userId AND a.status = 'ENDED' AND a.finalPrice IS NOT NULL")
    Optional<BigDecimal> getAverageSalePriceByUser(@Param("userId") Long userId);
}
