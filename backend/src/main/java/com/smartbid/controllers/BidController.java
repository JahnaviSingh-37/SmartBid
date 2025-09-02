package com.smartbid.controllers;

import com.smartbid.models.Bid;
import com.smartbid.models.User;
import com.smartbid.services.BidService;
import com.smartbid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Bid operations
 */
@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    @Autowired
    private BidService bidService;

    @Autowired
    private UserService userService;

    /**
     * Place a new bid
     */
    @PostMapping
    public ResponseEntity<?> placeBid(@Valid @RequestBody BidRequest bidRequest) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            Bid bid = bidService.placeBid(
                bidRequest.getAuctionId(),
                bidRequest.getBidAmount(),
                currentUser
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(bid);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error placing bid: " + e.getMessage());
        }
    }

    /**
     * Get bid by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBidById(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            Optional<Bid> bid = bidService.getBidById(id);
            if (bid.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Only return bid if it belongs to current user or they're the auction owner
            Bid bidData = bid.get();
            if (!bidData.getBidder().getId().equals(currentUser.getId()) &&
                !bidData.getAuction().getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            return ResponseEntity.ok(bidData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving bid: " + e.getMessage());
        }
    }

    /**
     * Get bids for an auction
     */
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<Page<Bid>> getBidsByAuction(
            @PathVariable Long auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Bid> bids = bidService.getBidsByAuction(auctionId, pageable);
        return ResponseEntity.ok(bids);
    }

    /**
     * Get my bids (current user)
     */
    @GetMapping("/my-bids")
    public ResponseEntity<?> getMyBids(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Bid> bids;
        
        if (status != null) {
            // For now, return all bids - can be filtered later
            bids = bidService.getBidsByUser(currentUser.getId(), pageable);
        } else {
            bids = bidService.getBidsByUser(currentUser.getId(), pageable);
        }

        return ResponseEntity.ok(bids);
    }

    /**
     * Get winning bids for user
     */
    @GetMapping("/winning")
    public ResponseEntity<?> getWinningBids() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Page<Bid> winningBids = bidService.getWinningBidsForUser(currentUser.getId(), PageRequest.of(0, 20));
        return ResponseEntity.ok(winningBids.getContent());
    }

    /**
     * Retract a bid (if allowed)
     */
    @DeleteMapping("/{bidId}")
    public ResponseEntity<?> retractBid(@PathVariable Long bidId) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // Check if bid belongs to current user
            Optional<Bid> bid = bidService.getBidById(bidId);
            if (bid.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!bid.get().getBidder().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only retract your own bids");
            }

            bidService.retractBid(bidId, currentUser, "User requested retraction");
            return ResponseEntity.ok().body("Bid retracted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retracting bid: " + e.getMessage());
        }
    }

    /**
     * Get bid history for auction
     */
    @GetMapping("/auction/{auctionId}/history")
    public ResponseEntity<List<Bid>> getBidHistory(@PathVariable Long auctionId) {
        Page<Bid> bidHistoryPage = bidService.getBidsByAuction(auctionId, PageRequest.of(0, 100));
        return ResponseEntity.ok(bidHistoryPage.getContent());
    }

    /**
     * Get bid statistics for user
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getBidStatistics() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        BidService.BidStatistics stats = bidService.getBidStatisticsForUser(currentUser.getId());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get highest bid for auction
     */
    @GetMapping("/auction/{auctionId}/highest")
    public ResponseEntity<Bid> getHighestBid(@PathVariable Long auctionId) {
        Optional<Bid> highestBid = bidService.getHighestBidForAuction(auctionId);
        return highestBid.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Helper method to get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);
        return user.orElse(null);
    }

    /**
     * Request class for placing bids
     */
    public static class BidRequest {
        private Long auctionId;
        private BigDecimal bidAmount;
        private BigDecimal maxBidAmount; // For proxy bidding

        // Constructors
        public BidRequest() {}

        public BidRequest(Long auctionId, BigDecimal bidAmount, BigDecimal maxBidAmount) {
            this.auctionId = auctionId;
            this.bidAmount = bidAmount;
            this.maxBidAmount = maxBidAmount;
        }

        // Getters and Setters
        public Long getAuctionId() { return auctionId; }
        public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

        public BigDecimal getBidAmount() { return bidAmount; }
        public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }

        public BigDecimal getMaxBidAmount() { return maxBidAmount; }
        public void setMaxBidAmount(BigDecimal maxBidAmount) { this.maxBidAmount = maxBidAmount; }
    }
}
