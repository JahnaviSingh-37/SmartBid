package com.smartbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SmartBid Marketplace Application
 * 
 * AI-powered fintech auction platform with:
 * - Real-time bidding system
 * - Image search capabilities
 * - Price alerts and recommendations
 * - Secure payment processing
 * - Fraud detection
 * 
 * @author SmartBid Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class SmartBidApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartBidApplication.class, args);
        System.out.println("🚀 SmartBid Marketplace is running!");
        System.out.println("📍 Server: http://localhost:8080");
        System.out.println("📖 API Docs: http://localhost:8080/swagger-ui.html");
        System.out.println("💡 Ready for intelligent bidding!");
    }
}
