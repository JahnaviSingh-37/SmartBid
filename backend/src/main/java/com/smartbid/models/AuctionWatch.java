package com.smartbid.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * AuctionWatch entity for users watching auctions
 * Enables price alerts and notifications
 */
@Entity
@Table(name = "auction_watches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "auction_id"})
})
public class AuctionWatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled = true;

    @Column(name = "email_alerts")
    private Boolean emailAlerts = true;

    @Column(name = "sms_alerts")
    private Boolean smsAlerts = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    // Constructors
    public AuctionWatch() {}

    public AuctionWatch(User user, Auction auction) {
        this.user = user;
        this.auction = auction;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }

    public Boolean getEmailAlerts() { return emailAlerts; }
    public void setEmailAlerts(Boolean emailAlerts) { this.emailAlerts = emailAlerts; }

    public Boolean getSmsAlerts() { return smsAlerts; }
    public void setSmsAlerts(Boolean smsAlerts) { this.smsAlerts = smsAlerts; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Auction getAuction() { return auction; }
    public void setAuction(Auction auction) { this.auction = auction; }
}
