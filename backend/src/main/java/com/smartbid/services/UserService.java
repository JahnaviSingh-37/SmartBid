package com.smartbid.services;

import com.smartbid.models.User;
import com.smartbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserService handles all user-related business logic
 * Includes SmartBid credit score management
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Create a new user account
     */
    public User createUser(User user) {
        // Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRole(User.UserRole.USER);
        user.setCreditScore(BigDecimal.valueOf(500.0)); // Default credit score
        user.setIsVerified(false);
        
        // Generate verification token
        user.setVerificationToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(savedUser);

        return savedUser;
    }

    /**
     * Authenticate user login
     */
    public Optional<User> authenticate(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Update last login
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user profile
     */
    public User updateProfile(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User userToUpdate = existingUser.get();
        
        // Update allowed fields (don't update password, credit score, etc. here)
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setPhoneNumber(user.getPhoneNumber());
        userToUpdate.setAddress(user.getAddress());
        userToUpdate.setProfileImageUrl(user.getProfileImageUrl());

        return userRepository.save(userToUpdate);
    }

    /**
     * Update user credit score based on transaction history
     */
    public void updateCreditScore(Long userId, boolean isSuccessfulTransaction) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        
        if (isSuccessfulTransaction) {
            user.setSuccessfulTransactions(user.getSuccessfulTransactions() + 1);
        } else {
            user.setFailedTransactions(user.getFailedTransactions() + 1);
        }

        // Recalculate credit score
        user.updateCreditScore();
        userRepository.save(user);
    }

    /**
     * Verify user email
     */
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * Initiate password reset
     */
    public boolean initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetExpires(LocalDateTime.now().plusHours(1)); // 1 hour expiry
            userRepository.save(user);

            emailService.sendPasswordResetEmail(user, resetToken);
            return true;
        }
        return false;
    }

    /**
     * Reset password using token
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByPasswordResetToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if token is still valid
            if (user.getPasswordResetExpires().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setPasswordResetToken(null);
                user.setPasswordResetExpires(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Get users with high credit scores (for premium features)
     */
    public List<User> getHighCreditScoreUsers() {
        return userRepository.findByVerifiedTrueAndCreditScoreGreaterThanEqual(BigDecimal.valueOf(700.0));
    }

    /**
     * Get user statistics
     */
    public UserStatistics getUserStatistics(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        
        return new UserStatistics(
            user.getId(),
            user.getCreditScore(),
            user.getSuccessfulTransactions(),
            user.getFailedTransactions(),
            user.getTotalBidAmount(),
            user.getAuctions().size(),
            user.getBids().size()
        );
    }

    /**
     * Suspend user account
     */
    public void suspendUser(Long userId, String reason) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(User.UserStatus.SUSPENDED);
            userRepository.save(user);
            
            // Log suspension and send notification
            emailService.sendAccountSuspensionEmail(user, reason);
        }
    }

    /**
     * Spring Security UserDetailsService implementation
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = userOpt.get();
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .accountExpired(false)
                .accountLocked(user.getStatus() == User.UserStatus.SUSPENDED || 
                              user.getStatus() == User.UserStatus.BANNED)
                .credentialsExpired(false)
                .disabled(user.getStatus() == User.UserStatus.INACTIVE)
                .build();
    }

    /**
     * Inner class for user statistics
     */
    public static class UserStatistics {
        private Long userId;
        private BigDecimal creditScore;
        private Integer successfulTransactions;
        private Integer failedTransactions;
        private BigDecimal totalBidAmount;
        private Integer totalAuctions;
        private Integer totalBids;

        public UserStatistics(Long userId, BigDecimal creditScore, Integer successfulTransactions,
                            Integer failedTransactions, BigDecimal totalBidAmount, 
                            Integer totalAuctions, Integer totalBids) {
            this.userId = userId;
            this.creditScore = creditScore;
            this.successfulTransactions = successfulTransactions;
            this.failedTransactions = failedTransactions;
            this.totalBidAmount = totalBidAmount;
            this.totalAuctions = totalAuctions;
            this.totalBids = totalBids;
        }

        // Getters
        public Long getUserId() { return userId; }
        public BigDecimal getCreditScore() { return creditScore; }
        public Integer getSuccessfulTransactions() { return successfulTransactions; }
        public Integer getFailedTransactions() { return failedTransactions; }
        public BigDecimal getTotalBidAmount() { return totalBidAmount; }
        public Integer getTotalAuctions() { return totalAuctions; }
        public Integer getTotalBids() { return totalBids; }
    }
}
