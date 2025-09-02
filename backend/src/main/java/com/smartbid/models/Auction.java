package com.smartbid.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Auction entity representing auction items on the platform
 * Supports various auction types and AI-powered features
 */
@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.01", message = "Starting price must be greater than 0")
    @Column(name = "starting_price", precision = 15, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "reserve_price", precision = 15, scale = 2)
    private BigDecimal reservePrice;

    @Column(name = "buy_now_price", precision = 15, scale = 2)
    private BigDecimal buyNowPrice;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    private AuctionType type = AuctionType.STANDARD;

    @NotBlank(message = "Category is required")
    private String category;

    @Column(name = "condition_type")
    @Enumerated(EnumType.STRING)
    private ConditionType conditionType = ConditionType.NEW;

    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "shipping_location")
    private String shippingLocation;

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls; // JSON array of image URLs

    @Column(name = "featured_image_url")
    private String featuredImageUrl;

    // AI-powered features
    @Column(name = "ai_generated_tags", columnDefinition = "TEXT")
    private String aiGeneratedTags; // AI-generated product tags

    @Column(name = "image_embeddings", columnDefinition = "TEXT")
    private String imageEmbeddings; // CLIP embeddings for image search

    @Column(name = "price_prediction", precision = 10, scale = 2)
    private BigDecimal pricePrediction; // AI predicted final price

    @Column(name = "quality_score", precision = 3, scale = 2)
    private BigDecimal qualityScore; // AI-assessed quality score (0-10)

    // Auction metrics
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "watch_count")
    private Integer watchCount = 0;

    @Column(name = "bid_count")
    private Integer bidCount = 0;

    // Winner information
    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "final_price", precision = 15, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "is_shipped")
    private Boolean isShipped = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Auction creator

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bid> bids = new HashSet<>();

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AuctionWatch> watchers = new HashSet<>();

    // Constructors
    public Auction() {}

    public Auction(String title, String description, BigDecimal startingPrice, 
                   LocalDateTime startTime, LocalDateTime endTime, User user) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
    }

    // Enums
    public enum AuctionStatus {
        UPCOMING, ACTIVE, ENDED, CANCELLED, SUSPENDED
    }

    public enum AuctionType {
        STANDARD, RESERVE, BUY_NOW, DUTCH, PENNY
    }

    public enum ConditionType {
        NEW, LIKE_NEW, GOOD, FAIR, POOR, REFURBISHED
    }

    // Business Methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == AuctionStatus.ACTIVE && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
    }

    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endTime) || status == AuctionStatus.ENDED;
    }

    public boolean canBid() {
        return isActive() && !hasEnded();
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1 : this.viewCount + 1;
    }

    public void incrementBidCount() {
        this.bidCount = (this.bidCount == null) ? 1 : this.bidCount + 1;
    }

    public boolean isReserveMetOrNoReserve() {
        return reservePrice == null || 
               (currentPrice != null && currentPrice.compareTo(reservePrice) >= 0);
    }

    public boolean isReserveMet() {
        if (reservePrice == null) return true;
        return currentPrice != null && currentPrice.compareTo(reservePrice) >= 0;
    }

    public BigDecimal getMinimumNextBid() {
        if (currentPrice == null) {
            return startingPrice;
        }
        // Add minimum increment based on current price
        BigDecimal increment = currentPrice.multiply(BigDecimal.valueOf(0.05)); // 5% increment
        increment = increment.max(BigDecimal.ONE); // Minimum $1 increment
        return currentPrice.add(increment);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getReservePrice() { return reservePrice; }
    public void setReservePrice(BigDecimal reservePrice) { this.reservePrice = reservePrice; }

    public BigDecimal getBuyNowPrice() { return buyNowPrice; }
    public void setBuyNowPrice(BigDecimal buyNowPrice) { this.buyNowPrice = buyNowPrice; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public AuctionStatus getStatus() { return status; }
    public void setStatus(AuctionStatus status) { this.status = status; }

    public AuctionType getType() { return type; }
    public void setType(AuctionType type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }

    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }

    public String getShippingLocation() { return shippingLocation; }
    public void setShippingLocation(String shippingLocation) { this.shippingLocation = shippingLocation; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public String getFeaturedImageUrl() { return featuredImageUrl; }
    public void setFeaturedImageUrl(String featuredImageUrl) { this.featuredImageUrl = featuredImageUrl; }

    public String getAiGeneratedTags() { return aiGeneratedTags; }
    public void setAiGeneratedTags(String aiGeneratedTags) { this.aiGeneratedTags = aiGeneratedTags; }

    public String getImageEmbeddings() { return imageEmbeddings; }
    public void setImageEmbeddings(String imageEmbeddings) { this.imageEmbeddings = imageEmbeddings; }

    public BigDecimal getPricePrediction() { return pricePrediction; }
    public void setPricePrediction(BigDecimal pricePrediction) { this.pricePrediction = pricePrediction; }

    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getWatchCount() { return watchCount; }
    public void setWatchCount(Integer watchCount) { this.watchCount = watchCount; }

    public Integer getBidCount() { return bidCount; }
    public void setBidCount(Integer bidCount) { this.bidCount = bidCount; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }

    public Boolean getIsShipped() { return isShipped; }
    public void setIsShipped(Boolean isShipped) { this.isShipped = isShipped; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Convenience methods for controller compatibility
    public User getSeller() { return getUser(); }
    public void setSeller(User seller) { setUser(seller); }

    public Set<Bid> getBids() { return bids; }
    public void setBids(Set<Bid> bids) { this.bids = bids; }

    public Set<AuctionWatch> getWatchers() { return watchers; }
    public void setWatchers(Set<AuctionWatch> watchers) { this.watchers = watchers; }

    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", currentPrice=" + currentPrice +
                ", status=" + status +
                ", endTime=" + endTime +
                '}';
    }
}
