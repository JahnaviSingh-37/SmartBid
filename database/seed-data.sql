-- Sample data for SmartBid Marketplace
-- Insert after running schema.sql

USE smartbid_db;

-- Insert sample users (passwords are hashed version of "password123")
INSERT INTO users (username, email, password, first_name, last_name, phone_number, address, credit_score, successful_transactions, is_verified) VALUES
('john_doe', 'john@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Doe', '+1234567890', '123 Main St, New York, NY', 750.00, 15, TRUE),
('jane_smith', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane', 'Smith', '+1234567891', '456 Oak Ave, Los Angeles, CA', 680.50, 8, TRUE),
('mike_wilson', 'mike@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mike', 'Wilson', '+1234567892', '789 Pine Rd, Chicago, IL', 820.25, 22, TRUE),
('sarah_johnson', 'sarah@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sarah', 'Johnson', '+1234567893', '321 Elm St, Houston, TX', 590.75, 5, TRUE),
('david_brown', 'david@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'David', 'Brown', '+1234567894', '654 Maple Dr, Phoenix, AZ', 720.00, 12, TRUE);

-- Insert sample auctions
INSERT INTO auctions (user_id, title, description, starting_price, current_price, start_time, end_time, status, category, condition_type, featured_image_url, view_count, watch_count, bid_count) VALUES
(1, 'MacBook Pro 16" 2023 M2 Max', 'Brand new MacBook Pro with M2 Max chip, 32GB RAM, 1TB SSD. Still sealed in box with 1-year Apple warranty.', 2500.00, 2750.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), 'ACTIVE', 'Electronics', 'NEW', 'https://example.com/images/macbook-pro.jpg', 156, 23, 8),

(2, 'Vintage Rolex Submariner 1970s', 'Authentic vintage Rolex Submariner from 1975. Recently serviced by authorized dealer. Comes with original papers.', 8000.00, 9200.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 6 DAY), 'ACTIVE', 'Watches', 'GOOD', 'https://example.com/images/rolex-submariner.jpg', 89, 31, 12),

(3, 'Tesla Model 3 Performance 2022', '2022 Tesla Model 3 Performance, white exterior, black interior. 15,000 miles, excellent condition. Full self-driving capability included.', 45000.00, 47500.00, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'ACTIVE', 'Automotive', 'LIKE_NEW', 'https://example.com/images/tesla-model3.jpg', 234, 18, 6),

(4, 'Hermès Birkin Bag 35cm', 'Authentic Hermès Birkin bag in Togo leather, Gold color with palladium hardware. Excellent condition, comes with dust bag and authenticity card.', 12000.00, 12000.00, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 8 DAY), 'UPCOMING', 'Fashion', 'LIKE_NEW', 'https://example.com/images/hermes-birkin.jpg', 67, 42, 0),

(5, 'Rare Pokemon Card Collection', 'Collection of 50+ rare Pokemon cards including first edition Charizard, Blastoise, and Venusaur. All cards professionally graded PSA 9-10.', 3000.00, 3500.00, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), 'ACTIVE', 'Collectibles', 'LIKE_NEW', 'https://example.com/images/pokemon-cards.jpg', 178, 56, 15),

(1, 'Professional Camera Kit', 'Canon EOS R5 with 24-70mm f/2.8L lens, 70-200mm f/2.8L lens, flash, tripod, and accessories. Perfect for professional photography.', 4500.00, 4500.00, DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 'UPCOMING', 'Electronics', 'GOOD', 'https://example.com/images/camera-kit.jpg', 45, 12, 0),

(3, 'Vintage Gibson Les Paul 1959', 'Extremely rare 1959 Gibson Les Paul Standard in Sunburst. One of the most sought-after guitars in the world. Excellent playing condition.', 150000.00, 150000.00, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 'UPCOMING', 'Musical Instruments', 'GOOD', 'https://example.com/images/gibson-les-paul.jpg', 312, 89, 0);

-- Insert sample bids
INSERT INTO bids (bidder_id, auction_id, bid_amount, status, bidder_ip, created_at) VALUES
(2, 1, 2600.00, 'OUTBID', '192.168.1.1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 1, 2700.00, 'OUTBID', '192.168.1.2', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(4, 1, 2750.00, 'WINNING', '192.168.1.3', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

(1, 2, 8500.00, 'OUTBID', '192.168.1.4', DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(3, 2, 8800.00, 'OUTBID', '192.168.1.5', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(5, 2, 9200.00, 'WINNING', '192.168.1.6', DATE_SUB(NOW(), INTERVAL 2 HOUR)),

(1, 3, 46000.00, 'OUTBID', '192.168.1.7', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(2, 3, 47500.00, 'WINNING', '192.168.1.8', DATE_SUB(NOW(), INTERVAL 1 HOUR)),

(1, 5, 3200.00, 'OUTBID', '192.168.1.9', DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(2, 5, 3350.00, 'OUTBID', '192.168.1.10', DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(4, 5, 3500.00, 'WINNING', '192.168.1.11', DATE_SUB(NOW(), INTERVAL 4 HOUR));

-- Insert sample auction watches
INSERT INTO auction_watches (user_id, auction_id, notification_enabled, email_alerts) VALUES
(1, 2, TRUE, TRUE),
(1, 4, TRUE, TRUE),
(2, 1, TRUE, FALSE),
(2, 3, TRUE, TRUE),
(3, 4, TRUE, TRUE),
(4, 2, TRUE, TRUE),
(4, 5, FALSE, FALSE),
(5, 1, TRUE, TRUE),
(5, 3, TRUE, FALSE);

-- Insert sample payments (for completed auctions)
INSERT INTO payments (user_id, auction_id, amount, status, method, stripe_payment_intent_id, processor_fee, platform_fee, net_amount, processed_at) VALUES
(3, 1, 2750.00, 'SUCCEEDED', 'CREDIT_CARD', 'pi_1234567890', 80.08, 137.50, 2532.42, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 2, 9200.00, 'SUCCEEDED', 'CREDIT_CARD', 'pi_0987654321', 266.88, 460.00, 8473.12, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Insert sample price alerts
INSERT INTO price_alerts (user_id, product_name, target_price, current_price, is_active) VALUES
(1, 'MacBook Pro M2', 2200.00, 2500.00, TRUE),
(2, 'Rolex Submariner', 7500.00, 8500.00, TRUE),
(3, 'Tesla Model 3', 40000.00, 45000.00, TRUE),
(4, 'iPhone 15 Pro', 900.00, 999.00, TRUE),
(5, 'Pokemon Charizard Card', 500.00, 800.00, TRUE);

-- Insert sample recommendations
INSERT INTO recommendations (user_id, auction_id, recommendation_score, recommendation_type) VALUES
(1, 2, 8.5, 'COLLABORATIVE'),
(1, 4, 7.2, 'CONTENT_BASED'),
(2, 1, 9.1, 'HYBRID'),
(2, 6, 6.8, 'CONTENT_BASED'),
(3, 5, 7.9, 'COLLABORATIVE'),
(4, 3, 8.3, 'HYBRID'),
(5, 7, 9.4, 'CONTENT_BASED');

-- Insert sample notifications
INSERT INTO notifications (user_id, type, title, message, is_read, related_auction_id) VALUES
(1, 'OUTBID', 'You have been outbid!', 'Someone placed a higher bid on MacBook Pro 16" 2023 M2 Max', FALSE, 1),
(2, 'BID_PLACED', 'Bid placed successfully', 'Your bid of $47,500 on Tesla Model 3 Performance 2022 has been placed', TRUE, 3),
(3, 'AUCTION_WON', 'Congratulations! You won an auction', 'You are the winning bidder for MacBook Pro 16" 2023 M2 Max with a bid of $2,750', FALSE, 1),
(4, 'PRICE_ALERT', 'Price drop alert', 'The item you are watching has dropped in price', FALSE, NULL),
(5, 'PAYMENT_RECEIVED', 'Payment confirmed', 'Your payment of $9,200 for Vintage Rolex Submariner has been processed', TRUE, 2);

-- Insert sample chat messages
INSERT INTO chat_messages (user_id, session_id, message, response, message_type, intent, confidence_score) VALUES
(1, 'session_123', 'What is the current highest bid on the MacBook?', 'The current highest bid on the MacBook Pro 16" 2023 M2 Max is $2,750.', 'USER', 'price_inquiry', 9.2),
(1, 'session_123', 'The current highest bid on the MacBook Pro 16" 2023 M2 Max is $2,750.', NULL, 'BOT', NULL, NULL),
(2, 'session_456', 'Can you recommend similar watches to the Rolex?', 'Based on your interest in luxury watches, I recommend checking out the Omega Speedmaster and TAG Heuer Monaco collections.', 'USER', 'product_recommendation', 8.7),
(2, 'session_456', 'Based on your interest in luxury watches, I recommend checking out the Omega Speedmaster and TAG Heuer Monaco collections.', NULL, 'BOT', NULL, NULL);

-- Update auction current prices based on highest bids
UPDATE auctions a 
SET current_price = (
    SELECT MAX(bid_amount) 
    FROM bids b 
    WHERE b.auction_id = a.id AND b.status IN ('ACTIVE', 'WINNING')
) 
WHERE id IN (1, 2, 3, 5);

-- Update bid counts
UPDATE auctions a 
SET bid_count = (
    SELECT COUNT(*) 
    FROM bids b 
    WHERE b.auction_id = a.id
) 
WHERE id IN (1, 2, 3, 5);

-- Update watch counts
UPDATE auctions a 
SET watch_count = (
    SELECT COUNT(*) 
    FROM auction_watches aw 
    WHERE aw.auction_id = a.id
) 
WHERE id IN (1, 2, 3, 4, 5);

-- Update user credit scores based on successful transactions
UPDATE users u 
SET credit_score = (
    CASE 
        WHEN u.successful_transactions > 20 THEN 900.00
        WHEN u.successful_transactions > 10 THEN 800.00
        WHEN u.successful_transactions > 5 THEN 700.00
        ELSE 600.00
    END
);

COMMIT;
