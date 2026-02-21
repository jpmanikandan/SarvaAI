package com.sarva.scheduler;

import com.sarva.core.GoldSilverService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PriceUpdateScheduler {

    private final GoldSilverService goldSilverService;
    
    // List of cities to pre-fetch prices for
    private static final List<String> CITIES = Arrays.asList(
        "Trichy", "Chennai", "Madurai", "Coimbatore", "Salem"
    );
    
    // List of metals to pre-fetch predictions for
    private static final List<String> METALS = Arrays.asList(
        "24K", "22K", "Silver"
    );

    public PriceUpdateScheduler(GoldSilverService goldSilverService) {
        this.goldSilverService = goldSilverService;
    }

    /**
     * Scheduled task to refresh gold/silver prices and predictions every 10 minutes
     * This ensures the cache is always warm and responses are instant
     */
    @Scheduled(fixedRate = 600000, initialDelay = 60000) // 10 minutes, start after 1 minute
    public void refreshPrices() {
        System.out.println("[SCHEDULER] Starting background price refresh...");
        long startTime = System.currentTimeMillis();
        
        for (String city : CITIES) {
            try {
                // Fetch current prices
                System.out.println("[SCHEDULER] Fetching prices for " + city);
                goldSilverService.getCurrentPrices(city);
                System.out.println("[SCHEDULER] Successfully cached prices for " + city);
                
                // Fetch predictions for common metals
                for (String metal : METALS) {
                    try {
                        System.out.println("[SCHEDULER] Fetching " + metal + " prediction for " + city);
                        goldSilverService.predictTomorrow(city, metal);
                        System.out.println("[SCHEDULER] Successfully cached " + metal + " prediction for " + city);
                    } catch (Exception e) {
                        System.err.println("[SCHEDULER] Failed to fetch " + metal + " prediction for " + city + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("[SCHEDULER] Failed to fetch prices for " + city + ": " + e.getMessage());
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[SCHEDULER] Price refresh completed in " + duration + "ms");
    }
    
    /**
     * Warm up cache on application startup
     */
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE) // Run once after 5 seconds
    public void warmUpCache() {
        System.out.println("[CACHE-WARMUP] Warming up price cache...");
        refreshPrices();
        System.out.println("[CACHE-WARMUP] Cache warmup complete. All future requests will be instant!");
    }
}
