package com.sarva.core;

public class TestGoldSilver {
    public static void main(String[] args) {
        GoldSilverService service = new GoldSilverService();

        System.out.println("--- Testing Current Prices (Trichy) ---");
        System.out.println(service.getCurrentPrices("Trichy"));

        System.out.println("\n--- Testing Prediction (Gold 24K - Trichy) ---");
        System.out.println(service.predictTomorrow("Trichy", "24K"));

        System.out.println("\n--- Testing Prediction (Silver - Trichy) ---");
        System.out.println(service.predictTomorrow("Trichy", "Silver"));
    }
}

