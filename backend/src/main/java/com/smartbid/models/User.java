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
 * User entity representing platform users
 * Includes credit scoring system for SmartBid
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true)
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    // SmartBid Credit Score System
    @DecimalMin(value = "0.0", message = "Credit score cannot be negative")
    @DecimalMax(value = "1000.0", message = "Credit score cannot exceed 1000")
    @Column(name = "credit_score", precision = 5, scale = 2)
    private BigDecimal creditScore = BigDecimal.valueOf(500.0); // Default score

    @Column(name = "successful_transactions")
    private Integer successfulTransactions = 0;

    @Column(name = "failed_transactions")
    private Integer failedTransactions = 0;

    @Column(name = "total_bid_amount", precision = 15, scale = 2)
    private BigDecimal totalBidAmount = BigDecimal.ZERO;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Auction> auctions = new HashSet<>();

    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bid> bids = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments = new HashSet<>();

    // Constructors
    public User() {}

    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Enums
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, BANNED
    }

    public enum UserRole {
        USER, ADMIN, MODERATOR
    }

    // Business Methods
    public void updateCreditScore() {
        if (successfulTransactions == 0 && failedTransactions == 0) {
            this.creditScore = BigDecimal.valueOf(500.0);
            return;
        }

        double successRate = (double) successfulTransactions / (successfulTransactions + failedTransactions);
        double baseScore = 500.0;
        double bonus = successRate * 300.0; // Max bonus: 300 points
        double penalty = (failedTransactions * 10.0); // 10 points per failed transaction

        double newScore = baseScore + bonus - penalty;
        newScore = Math.max(0.0, Math.min(1000.0, newScore)); // Clamp between 0-1000

        this.creditScore = BigDecimal.valueOf(newScore);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasHighCreditScore() {
        return creditScore.compareTo(BigDecimal.valueOf(700.0)) >= 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public BigDecimal getCreditScore() { return creditScore; }
    public void setCreditScore(BigDecimal creditScore) { this.creditScore = creditScore; }

    public Integer getSuccessfulTransactions() { return successfulTransactions; }
    public void setSuccessfulTransactions(Integer successfulTransactions) { this.successfulTransactions = successfulTransactions; }

    public Integer getFailedTransactions() { return failedTransactions; }
    public void setFailedTransactions(Integer failedTransactions) { this.failedTransactions = failedTransactions; }

    public BigDecimal getTotalBidAmount() { return totalBidAmount; }
    public void setTotalBidAmount(BigDecimal totalBidAmount) { this.totalBidAmount = totalBidAmount; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public LocalDateTime getPasswordResetExpires() { return passwordResetExpires; }
    public void setPasswordResetExpires(LocalDateTime passwordResetExpires) { this.passwordResetExpires = passwordResetExpires; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<Auction> getAuctions() { return auctions; }
    public void setAuctions(Set<Auction> auctions) { this.auctions = auctions; }

    public Set<Bid> getBids() { return bids; }
    public void setBids(Set<Bid> bids) { this.bids = bids; }

    public Set<Payment> getPayments() { return payments; }
    public void setPayments(Set<Payment> payments) { this.payments = payments; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", creditScore=" + creditScore +
                ", status=" + status +
                '}';
    }
}
