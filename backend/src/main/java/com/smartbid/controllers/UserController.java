package com.smartbid.controllers;

import com.smartbid.models.User;
import com.smartbid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAddress(request.getAddress());

            User createdUser = userService.createUser(user);
            
            // Don't return password in response
            createdUser.setPassword(null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating user: " + e.getMessage());
        }
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            Optional<User> user = userService.authenticate(request.getUsernameOrEmail(), request.getPassword());
            
            if (user.isPresent()) {
                User authenticatedUser = user.get();
                // Don't return password in response
                authenticatedUser.setPassword(null);
                
                return ResponseEntity.ok(authenticatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login error: " + e.getMessage());
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Don't return password in response
        currentUser.setPassword(null);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserUpdateRequest request) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // Update allowed fields
            currentUser.setFirstName(request.getFirstName());
            currentUser.setLastName(request.getLastName());
            currentUser.setPhoneNumber(request.getPhoneNumber());
            currentUser.setAddress(request.getAddress());
            currentUser.setProfileImageUrl(request.getProfileImageUrl());

            User updatedUser = userService.updateProfile(currentUser);
            
            // Don't return password in response
            updatedUser.setPassword(null);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    /**
     * Get user by ID (public profile)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        
        if (user.isPresent()) {
            User userData = user.get();
            
            // Only return public information
            User publicUser = new User();
            publicUser.setId(userData.getId());
            publicUser.setUsername(userData.getUsername());
            publicUser.setFirstName(userData.getFirstName());
            publicUser.setLastName(userData.getLastName());
            publicUser.setCreditScore(userData.getCreditScore());
            publicUser.setCreatedAt(userData.getCreatedAt());
            publicUser.setProfileImageUrl(userData.getProfileImageUrl());
            
            return ResponseEntity.ok(publicUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verify email address
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerificationRequest request) {
        try {
            boolean verified = userService.verifyEmail(request.getToken());
            
            if (verified) {
                return ResponseEntity.ok().body("Email verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired verification token");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Verification error: " + e.getMessage());
        }
    }

    /**
     * Request password reset
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            boolean resetInitiated = userService.initiatePasswordReset(request.getEmail());
            
            if (resetInitiated) {
                return ResponseEntity.ok().body("Password reset email sent");
            } else {
                return ResponseEntity.badRequest().body("Email not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error initiating password reset: " + e.getMessage());
        }
    }

    /**
     * Reset password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            boolean resetSuccessful = userService.resetPassword(request.getToken(), request.getNewPassword());
            
            if (resetSuccessful) {
                return ResponseEntity.ok().body("Password reset successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired reset token");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resetting password: " + e.getMessage());
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UserService.UserStatistics stats = userService.getUserStatistics(currentUser.getId());
        return ResponseEntity.ok(stats);
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

    // Request/Response Classes
    
    public static class UserRegistrationRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;

        // Constructors
        public UserRegistrationRequest() {}

        // Getters and Setters
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
    }

    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;

        // Constructors
        public LoginRequest() {}

        // Getters and Setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String profileImageUrl;

        // Constructors
        public UserUpdateRequest() {}

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    }

    public static class VerificationRequest {
        private String token;

        // Constructors
        public VerificationRequest() {}

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class ForgotPasswordRequest {
        private String email;

        // Constructors
        public ForgotPasswordRequest() {}

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class PasswordResetRequest {
        private String token;
        private String newPassword;

        // Constructors
        public PasswordResetRequest() {}

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
