package com.smartbid.controllers;

import com.smartbid.models.Auction;
import com.smartbid.models.User;
import com.smartbid.services.AuctionService;
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
 * REST Controller for Auction operations
 */
@RestController
@RequestMapping("/api/auctions")
@CrossOrigin(origins = "*")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    /**
     * Get all auctions with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Page<Auction>> getAllAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Auction> auctions;
        
        if (category != null || location != null || minPrice != null || maxPrice != null || condition != null) {
            auctions = auctionService.searchAuctions(category, location, minPrice, maxPrice, condition, pageable);
        } else if (status != null) {
            Auction.AuctionStatus auctionStatus = Auction.AuctionStatus.valueOf(status.toUpperCase());
            auctions = auctionService.getAuctionsByStatus(auctionStatus, pageable);
        } else {
            auctions = auctionService.getAllAuctions(pageable);
        }
        
        return ResponseEntity.ok(auctions);
    }

    /**
     * Get auction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable Long id) {
        Optional<Auction> auction = auctionService.findById(id);
        return auction.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new auction
     */
    @PostMapping
    public ResponseEntity<?> createAuction(@Valid @RequestBody Auction auction) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            auction.setSeller(currentUser);
            Auction createdAuction = auctionService.createAuction(auction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAuction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating auction: " + e.getMessage());
        }
    }

    /**
     * Update auction
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuction(@PathVariable Long id, @Valid @RequestBody Auction auction) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            Optional<Auction> existingAuction = auctionService.findById(id);
            if (existingAuction.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if user is the seller
            if (!existingAuction.get().getSeller().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own auctions");
            }

            auction.setId(id);
            auction.setSeller(currentUser);
            Auction updatedAuction = auctionService.updateAuction(auction);
            return ResponseEntity.ok(updatedAuction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating auction: " + e.getMessage());
        }
    }

    /**
     * End auction manually
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<?> endAuction(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            Optional<Auction> auction = auctionService.findById(id);
            if (auction.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if user is the seller
            if (!auction.get().getSeller().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only end your own auctions");
            }

            Auction endedAuction = auctionService.endAuction(id);
            return ResponseEntity.ok(endedAuction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error ending auction: " + e.getMessage());
        }
    }

    /**
     * Get auctions by seller
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<Auction>> getAuctionsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Auction> auctions = auctionService.getAuctionsBySeller(sellerId, pageable);
        return ResponseEntity.ok(auctions);
    }

    /**
     * Get my auctions (current user)
     */
    @GetMapping("/my-auctions")
    public ResponseEntity<?> getMyAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Auction> auctions = auctionService.getAuctionsBySeller(currentUser.getId(), pageable);
        return ResponseEntity.ok(auctions);
    }

    /**
     * Get ending soon auctions
     */
    @GetMapping("/ending-soon")
    public ResponseEntity<List<Auction>> getEndingSoonAuctions() {
        List<Auction> auctions = auctionService.getEndingSoonAuctions();
        return ResponseEntity.ok(auctions);
    }

    /**
     * Get popular auctions
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Auction>> getPopularAuctions() {
        List<Auction> auctions = auctionService.getPopularAuctions();
        return ResponseEntity.ok(auctions);
    }

    /**
     * Get similar auctions
     */
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<Auction>> getSimilarAuctions(@PathVariable Long id) {
        List<Auction> auctions = auctionService.getSimilarAuctions(id);
        return ResponseEntity.ok(auctions);
    }

    /**
     * Get auction statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AuctionService.AuctionStatistics> getAuctionStatistics() {
        AuctionService.AuctionStatistics stats = auctionService.getAuctionStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Search auctions by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Auction>> searchAuctions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Auction> auctions = auctionService.searchAuctionsByTitle(query, pageable);
        return ResponseEntity.ok(auctions);
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
}
