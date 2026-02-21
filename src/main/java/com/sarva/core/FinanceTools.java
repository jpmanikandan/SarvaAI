package com.sarva.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;

@Configuration
public class FinanceTools {

    private final GoldSilverService goldSilverService;

    public FinanceTools(GoldSilverService goldSilverService) {
        this.goldSilverService = goldSilverService;
    }

    public static class PriceRequest {
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class PredictionRequest {
        private String city;
        private String metal;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getMetal() {
            return metal;
        }

        public void setMetal(String metal) {
            this.metal = metal;
        }
    }

    @Bean
    @Description("Get the current gold and silver prices for a specific city")
    public Function<PriceRequest, String> getCurrentPrices() {
        return request -> goldSilverService.getCurrentPrices(request.getCity() != null ? request.getCity() : "Trichy");
    }

    @Bean
    @Description("Predict the price of gold or silver for tomorrow in a specific city")
    public Function<PredictionRequest, String> predictPrice() {
        return request -> goldSilverService.predictTomorrow(
            request.getCity() != null ? request.getCity() : "Trichy", 
            request.getMetal() != null ? request.getMetal() : "24K"
        );
    }
}
