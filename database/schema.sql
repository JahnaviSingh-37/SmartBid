-- SmartBid Marketplace Database Schema
-- MySQL 8.0+ compatible

-- Create database
CREATE DATABASE IF NOT EXISTS smartbid_db;
USE smartbid_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS auction_watches;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bids;
DROP TABLE IF EXISTS auctions;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'BANNED') DEFAULT 'ACTIVE',
    role ENUM('USER', 'ADMIN', 'MODERATOR') DEFAULT 'USER',
    
    -- SmartBid Credit Score System
    credit_score DECIMAL(5,2) DEFAULT 500.00,
    successful_transactions INT DEFAULT 0,
    failed_transactions INT DEFAULT 0,
    total_bid_amount DECIMAL(15,2) DEFAULT 0.00,
    
    -- Profile & verification
    profile_image_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP NULL,
    last_login TIMESTAMP NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_credit_score (credit_score),
    INDEX idx_status (status)
);

-- Auctions table
CREATE TABLE auctions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    starting_price DECIMAL(15,2) NOT NULL,
    current_price DECIMAL(15,2),
    reserve_price DECIMAL(15,2),
    buy_now_price DECIMAL(15,2),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status ENUM('UPCOMING', 'ACTIVE', 'ENDED', 'CANCELLED', 'SUSPENDED') DEFAULT 'UPCOMING',
    type ENUM('STANDARD', 'RESERVE', 'BUY_NOW', 'DUTCH', 'PENNY') DEFAULT 'STANDARD',
    category VARCHAR(100) NOT NULL,
    condition_type ENUM('NEW', 'LIKE_NEW', 'GOOD', 'FAIR', 'POOR', 'REFURBISHED') DEFAULT 'NEW',
    shipping_cost DECIMAL(10,2) DEFAULT 0.00,
    shipping_location VARCHAR(255),
    
    -- Images and media
    image_urls TEXT, -- JSON array of image URLs
    featured_image_url VARCHAR(500),
    
    -- AI-powered features
    ai_generated_tags TEXT, -- AI-generated product tags
    image_embeddings TEXT, -- CLIP embeddings for image search
    price_prediction DECIMAL(10,2), -- AI predicted final price
    quality_score DECIMAL(3,2), -- AI-assessed quality score (0-10)
    
    -- Auction metrics
    view_count INT DEFAULT 0,
    watch_count INT DEFAULT 0,
    bid_count INT DEFAULT 0,
    
    -- Winner information
    winner_id BIGINT,
    final_price DECIMAL(15,2),
    is_paid BOOLEAN DEFAULT FALSE,
    is_shipped BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_end_time (end_time),
    INDEX idx_current_price (current_price),
    INDEX idx_start_end_time (start_time, end_time)
);

-- Bids table
CREATE TABLE bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bidder_id BIGINT NOT NULL,
    auction_id BIGINT NOT NULL,
    bid_amount DECIMAL(15,2) NOT NULL,
    max_bid_amount DECIMAL(15,2), -- For automatic bidding
    status ENUM('ACTIVE', 'OUTBID', 'WINNING', 'WON', 'RETRACTED', 'REJECTED') DEFAULT 'ACTIVE',
    type ENUM('MANUAL', 'AUTOMATIC', 'BUY_NOW') DEFAULT 'MANUAL',
    
    -- Security and fraud detection
    bidder_ip VARCHAR(45),
    user_agent TEXT,
    fraud_score DECIMAL(3,2), -- 0-10 scale
    is_suspicious BOOLEAN DEFAULT FALSE,
    fraud_reasons TEXT, -- JSON array of fraud indicators
    bid_timing_score DECIMAL(3,2), -- How suspicious is the timing
    bid_pattern_score DECIMAL(3,2), -- Pattern analysis score
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (bidder_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE,
    INDEX idx_bidder_id (bidder_id),
    INDEX idx_auction_id (auction_id),
    INDEX idx_bid_amount (bid_amount),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
);

-- Payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    auction_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status ENUM('PENDING', 'PROCESSING', 'SUCCEEDED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIAL_REFUND') DEFAULT 'PENDING',
    method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'DIGITAL_WALLET'),
    
    -- Payment processor integration
    stripe_payment_intent_id VARCHAR(255),
    stripe_charge_id VARCHAR(255),
    transaction_id VARCHAR(255),
    failure_reason TEXT,
    
    -- Fraud detection
    fraud_score DECIMAL(3,2),
    risk_assessment TEXT, -- JSON with risk factors
    is_flagged BOOLEAN DEFAULT FALSE,
    
    -- Fee breakdown
    processor_fee DECIMAL(10,2),
    platform_fee DECIMAL(10,2),
    net_amount DECIMAL(15,2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_auction_id (auction_id),
    INDEX idx_status (status),
    INDEX idx_stripe_payment_intent_id (stripe_payment_intent_id)
);

-- Auction watches table (for notifications and alerts)
CREATE TABLE auction_watches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    auction_id BIGINT NOT NULL,
    notification_enabled BOOLEAN DEFAULT TRUE,
    email_alerts BOOLEAN DEFAULT TRUE,
    sms_alerts BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_auction (user_id, auction_id),
    INDEX idx_user_id (user_id),
    INDEX idx_auction_id (auction_id)
);

-- Additional tables for AI features

-- Price alerts table
CREATE TABLE price_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    target_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE,
    alert_frequency ENUM('IMMEDIATE', 'DAILY', 'WEEKLY') DEFAULT 'IMMEDIATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    triggered_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active)
);

-- Product recommendations table
CREATE TABLE recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    auction_id BIGINT NOT NULL,
    recommendation_score DECIMAL(3,2), -- 0-10 scale
    recommendation_type ENUM('COLLABORATIVE', 'CONTENT_BASED', 'HYBRID') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_recommendation_score (recommendation_score)
);

-- Chat messages table (for AI chatbot)
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    response TEXT,
    message_type ENUM('USER', 'BOT') NOT NULL,
    intent VARCHAR(100), -- Detected intent (price_inquiry, product_search, etc.)
    confidence_score DECIMAL(3,2), -- AI confidence in response
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at)
);

-- System notifications table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('BID_PLACED', 'OUTBID', 'AUCTION_WON', 'AUCTION_ENDED', 'PAYMENT_RECEIVED', 'PRICE_ALERT') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    related_auction_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (related_auction_id) REFERENCES auctions(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_type (type)
);

-- Audit log table for security
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_entity_type (entity_type),
    INDEX idx_created_at (created_at)
);

-- Create views for commonly used queries

-- Active auctions view
CREATE VIEW active_auctions AS
SELECT 
    a.*,
    u.username as seller_username,
    u.credit_score as seller_credit_score,
    COUNT(b.id) as total_bids,
    MAX(b.bid_amount) as highest_bid
FROM auctions a
JOIN users u ON a.user_id = u.id
LEFT JOIN bids b ON a.id = b.auction_id AND b.status IN ('ACTIVE', 'WINNING')
WHERE a.status = 'ACTIVE' 
    AND a.start_time <= NOW() 
    AND a.end_time > NOW()
GROUP BY a.id;

-- User statistics view
CREATE VIEW user_statistics AS
SELECT 
    u.id,
    u.username,
    u.credit_score,
    COUNT(DISTINCT a.id) as total_auctions_created,
    COUNT(DISTINCT b.id) as total_bids_placed,
    COUNT(DISTINCT p.id) as total_payments,
    COALESCE(SUM(p.amount), 0) as total_spent
FROM users u
LEFT JOIN auctions a ON u.id = a.user_id
LEFT JOIN bids b ON u.id = b.bidder_id
LEFT JOIN payments p ON u.id = p.user_id AND p.status = 'SUCCEEDED'
GROUP BY u.id;

-- Insert initial admin user
INSERT INTO users (username, email, password, first_name, last_name, role, is_verified, credit_score) 
VALUES (
    'admin', 
    'admin@smartbid.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- "password"
    'Admin', 
    'User', 
    'ADMIN', 
    TRUE, 
    1000.00
);

-- Insert sample categories (could be moved to a separate categories table)
-- For now, we'll use these as ENUM values in the application

COMMIT;
