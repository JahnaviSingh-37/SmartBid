package com.smartbid.repositories;

import com.smartbid.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides database operations for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by verification token
     */
    Optional<User> findByVerificationToken(String verificationToken);

    /**
     * Find user by password reset token
     */
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    /**
     * Find users by status
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * Find users by role
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Find verified users with credit score greater than or equal to specified amount
     */
    List<User> findByVerifiedTrueAndCreditScoreGreaterThanEqual(BigDecimal creditScore);

    /**
     * Find users who haven't logged in since specified date
     */
    List<User> findByLastLoginBefore(LocalDateTime dateTime);

    /**
     * Find users created between dates
     */
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Check if username exists (case insensitive)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);

    /**
     * Check if email exists (case insensitive)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Find top users by credit score
     */
    @Query("SELECT u FROM User u WHERE u.isVerified = true ORDER BY u.creditScore DESC")
    List<User> findTopUsersByCreditScore();

    /**
     * Find users with suspicious activity (many failed transactions)
     */
    @Query("SELECT u FROM User u WHERE u.failedTransactions > :threshold AND u.failedTransactions > u.successfulTransactions")
    List<User> findUsersWithSuspiciousActivity(@Param("threshold") Integer threshold);

    /**
     * Get user statistics for credit score calculation
     */
    @Query("SELECT u.successfulTransactions, u.failedTransactions, u.totalBidAmount FROM User u WHERE u.id = :userId")
    Object[] getUserTransactionStats(@Param("userId") Long userId);

    /**
     * Find users who need credit score recalculation (modified recently)
     */
    @Query("SELECT u FROM User u WHERE u.updatedAt > :since AND (u.successfulTransactions > 0 OR u.failedTransactions > 0)")
    List<User> findUsersNeedingCreditScoreUpdate(@Param("since") LocalDateTime since);

    /**
     * Search users by name or username (for admin purposes)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsersByName(@Param("searchTerm") String searchTerm);

    /**
     * Count active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    Long countActiveUsers();

    /**
     * Count verified users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = true")
    Long countVerifiedUsers();

    /**
     * Get users with recent activity (logged in within specified days)
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin > :since")
    List<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since);
}
