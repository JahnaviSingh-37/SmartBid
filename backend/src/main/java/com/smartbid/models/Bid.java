package com.smartbid.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bid entity representing bids placed on auctions
 * Includes fraud detection and AI-powered bid validation
 */
@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.01", message = "Bid amount must be greater than 0")
    @Column(name = "bid_amount", precision = 15, scale = 2)
    private BigDecimal bidAmount;

    @Column(name = "max_bid_amount", precision = 15, scale = 2)
    private BigDecimal maxBidAmount; // For automatic bidding

    @Enumerated(EnumType.STRING)
    private BidStatus status = BidStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private BidType type = BidType.MANUAL;

    @Column(name = "bidder_ip")
    private String bidderIp;

    @Column(name = "user_agent")
    private String userAgent;

    // Fraud detection fields
    @Column(name = "fraud_score", precision = 3, scale = 2)
    private BigDecimal fraudScore; // 0-10 scale

    @Column(name = "is_suspicious")
    private Boolean isSuspicious = false;

    @Column(name = "fraud_reasons", columnDefinition = "TEXT")
    private String fraudReasons; // JSON array of fraud indicators

    // AI analysis
    @Column(name = "bid_timing_score", precision = 3, scale = 2)
    private BigDecimal bidTimingScore; // How suspicious is the timing

    @Column(name = "bid_pattern_score", precision = 3, scale = 2)
    private BigDecimal bidPatternScore; // Pattern analysis score

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    // Constructors
    public Bid() {}

    public Bid(BigDecimal bidAmount, User bidder, Auction auction) {
        this.bidAmount = bidAmount;
        this.bidder = bidder;
        this.auction = auction;
    }

    // Enums
    public enum BidStatus {
        ACTIVE, OUTBID, WINNING, WON, RETRACTED, REJECTED
    }

    public enum BidType {
        MANUAL, AUTOMATIC, BUY_NOW
    }

    // Business Methods
    public boolean isWinning() {
        return status == BidStatus.WINNING || status == BidStatus.WON;
    }

    public boolean isActive() {
        return status == BidStatus.ACTIVE || status == BidStatus.WINNING;
    }

    public boolean isSuspiciousBid() {
        return isSuspicious || (fraudScore != null && fraudScore.compareTo(BigDecimal.valueOf(7.0)) > 0);
    }

    public void markAsSuspicious(String reason) {
        this.isSuspicious = true;
        if (this.fraudReasons == null) {
            this.fraudReasons = "[\"" + reason + "\"]";
        } else {
            // Add to existing reasons (simplified JSON handling)
            this.fraudReasons = this.fraudReasons.replace("]", ",\"" + reason + "\"]");
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }

    public BigDecimal getMaxBidAmount() { return maxBidAmount; }
    public void setMaxBidAmount(BigDecimal maxBidAmount) { this.maxBidAmount = maxBidAmount; }

    public BidStatus getStatus() { return status; }
    public void setStatus(BidStatus status) { this.status = status; }

    public BidType getType() { return type; }
    public void setType(BidType type) { this.type = type; }

    public String getBidderIp() { return bidderIp; }
    public void setBidderIp(String bidderIp) { this.bidderIp = bidderIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public BigDecimal getFraudScore() { return fraudScore; }
    public void setFraudScore(BigDecimal fraudScore) { this.fraudScore = fraudScore; }

    public Boolean getIsSuspicious() { return isSuspicious; }
    public void setIsSuspicious(Boolean isSuspicious) { this.isSuspicious = isSuspicious; }

    public String getFraudReasons() { return fraudReasons; }
    public void setFraudReasons(String fraudReasons) { this.fraudReasons = fraudReasons; }

    public BigDecimal getBidTimingScore() { return bidTimingScore; }
    public void setBidTimingScore(BigDecimal bidTimingScore) { this.bidTimingScore = bidTimingScore; }

    public BigDecimal getBidPatternScore() { return bidPatternScore; }
    public void setBidPatternScore(BigDecimal bidPatternScore) { this.bidPatternScore = bidPatternScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getBidder() { return bidder; }
    public void setBidder(User bidder) { this.bidder = bidder; }

    public Auction getAuction() { return auction; }
    public void setAuction(Auction auction) { this.auction = auction; }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                ", bidAmount=" + bidAmount +
                ", status=" + status +
                ", type=" + type +
                ", isSuspicious=" + isSuspicious +
                ", createdAt=" + createdAt +
                '}';
    }
}
