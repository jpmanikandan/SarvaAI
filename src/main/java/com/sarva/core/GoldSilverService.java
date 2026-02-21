package com.sarva.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class GoldSilverService {

    private static final String GOLD_URL_TEMPLATE = "https://www.goodreturns.in/gold-rates/%s.html";
    private static final String SILVER_URL_TEMPLATE = "https://www.goodreturns.in/silver-rates/%s.html";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    private org.jsoup.Connection connect(String url) {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .header("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Referer", "https://www.google.com/")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "cross-site")
                .header("Sec-Fetch-User", "?1")
                .header("Cache-Control", "max-age=0")
                .timeout(10000);
    }

    /**
     * Fetches current gold and silver prices for a given city using Jsoup.
     * Results are cached for 10 minutes to provide fast responses.
     */
    @Cacheable(value = "goldSilverPrices", key = "#city")
    public String getCurrentPrices(String city) {
        String cityUrlParam = city.toLowerCase().replace(" ", "-");
        StringBuilder result = new StringBuilder();

        result.append("## Current Gold & Silver Prices in ").append(city).append("\n\n");
        result.append("*Last updated: ").append(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))).append("*\n\n");

        try {
            // Fetch Gold Prices
            String goldUrl = String.format(GOLD_URL_TEMPLATE, cityUrlParam);
            System.out.println("[INFO] Fetching gold prices (Jsoup) from: " + goldUrl);
            Document goldDoc = connect(goldUrl).get();

            result.append(extractGoldTable(goldDoc, "24 Carat", "24 Carat Gold"));
            result.append(extractGoldTable(goldDoc, "22 Carat", "22 Carat Gold"));
            result.append(extractGoldTable(goldDoc, "18 Carat", "18 Carat Gold"));

            // Fetch Silver Prices
            String silverUrl = String.format(SILVER_URL_TEMPLATE, cityUrlParam);
            System.out.println("[INFO] Fetching silver prices (Jsoup) from: " + silverUrl);
            Document silverDoc = connect(silverUrl).get();
            result.append(extractSilverTable(silverDoc));

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch prices for " + city + ": " + e.getMessage());
            return "Sorry, I couldn't fetch the current gold and silver prices for " + city + " at the moment. " +
                    "Please try again in a moment or check https://www.goodreturns.in directly.";
        }

        return result.toString();
    }

    /**
     * Predicts tomorrow's price based on simple linear regression of last 10 days.
     */
    @Cacheable(value = "predictions", key = "#city + '_' + #metal")
    public String predictTomorrow(String city, String metal) {
        String cityUrlParam = city.toLowerCase().replace(" ", "-");
        String metalKey = metal.toUpperCase();

        List<Double> prices = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        try {
            if (metalKey.equals("SILVER")) {
                String url = String.format(SILVER_URL_TEMPLATE, cityUrlParam);
                Document doc = connect(url).get();
                extractHistoricalData(doc, "Silver", prices, dates, 1);
            } else {
                String url = String.format(GOLD_URL_TEMPLATE, cityUrlParam);
                Document doc = connect(url).get();

                int colIndex = 1; // Default to 24K
                if (metalKey.contains("22"))
                    colIndex = 2;
                if (metalKey.contains("18"))
                    colIndex = 3;

                extractHistoricalData(doc, "Gold", prices, dates, colIndex);
            }

            if (prices.size() < 3) {
                return "Not enough historical data to predict.";
            }

            // Linear Regression
            int n = prices.size();
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

            for (int i = 0; i < n; i++) {
                double x = i;
                double y = prices.get(n - 1 - i); // Reverse: Oldest first
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
            }

            double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
            double intercept = (sumY - slope * sumX) / n;

            double predictedPrice = slope * n + intercept;
            String metalDisplay = metalKey.equals("SILVER") ? "Silver" : metal + " Gold";
            String unit = metalKey.equals("SILVER") ? "per 10 grams" : "per gram";
            String trend = slope > 0 ? "📈 Increasing" : slope < 0 ? "📉 Decreasing" : "➡️ Stable";

            return String.format("## Tomorrow's %s Price Prediction for %s\n\n" +
                    "**Predicted Price**: ₹%.2f %s\n\n" +
                    "**Analysis**:\n" +
                    "- Based on %d days of historical data\n" +
                    "- Trend: %s\n" +
                    "- %s\n\n" +
                    "> Note: This is a statistical prediction based on recent trends.",
                    metalDisplay, city, predictedPrice, unit, n, trend,
                    slope < 0 ? "Prices are trending downward"
                            : slope > 0 ? "Prices are trending upward" : "Prices are relatively stable");

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to predict price: " + e.getMessage());
            return "Sorry, I couldn't predict tomorrow's " + metal + " price for " + city + " at the moment.";
        }
    }

    private String extractGoldTable(Document doc, String searchKey, String title) {
        StringBuilder sb = new StringBuilder();
        try {
            Element header = doc.select("h2:contains(" + searchKey + ")").first();
            if (header != null) {
                Element table = header.nextElementSibling();
                while (table != null && !table.tagName().equals("table")) {
                    table = table.nextElementSibling();
                }

                if (table != null) {
                    sb.append("**").append(title).append("**:\n");
                    Elements rows = table.select("tr");
                    for (int i = 1; i < rows.size(); i++) {
                        Elements cols = rows.get(i).select("td");
                        if (cols.size() >= 2) {
                            sb.append("- ").append(cols.get(0).text()).append(": ").append(cols.get(1).text())
                                    .append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to extract gold table for " + searchKey);
        }
        return sb.toString();
    }

    private String extractSilverTable(Document doc) {
        StringBuilder sb = new StringBuilder();
        try {
            Element header = doc.select("h2:contains(Silver Price Per Gram)").first();
            if (header != null) {
                Element table = header.nextElementSibling();
                while (table != null && !table.tagName().equals("table")) {
                    table = table.nextElementSibling();
                }

                if (table != null) {
                    sb.append("**Silver**:\n");
                    Elements rows = table.select("tr");
                    for (int i = 1; i < rows.size(); i++) {
                        Elements cols = rows.get(i).select("td");
                        if (cols.size() >= 2) {
                            sb.append("- ").append(cols.get(0).text()).append(": ").append(cols.get(1).text())
                                    .append("\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to extract silver table");
        }
        return sb.toString();
    }

    private void extractHistoricalData(Document doc, String type, List<Double> prices, List<String> dates,
            int colIndex) {
        try {
            Element header = doc.select("h2:contains(Last 10 Days)").first();
            if (header != null) {
                Element table = header.nextElementSibling();
                while (table != null && !table.tagName().equals("table")) {
                    table = table.nextElementSibling();
                }

                if (table != null) {
                    Elements rows = table.select("tr");
                    for (int i = 1; i < rows.size(); i++) {
                        Elements cols = rows.get(i).select("td");
                        if (cols.size() > colIndex) {
                            String date = cols.get(0).text();
                            String priceStr = cols.get(colIndex).text();
                            priceStr = priceStr.replaceAll("\\([^)]*\\)", "").replaceAll("[^0-9.]", "").trim();
                            if (!priceStr.isEmpty()) {
                                prices.add(Double.parseDouble(priceStr));
                                dates.add(date);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to extract historical data");
        }
    }

    @Scheduled(fixedRate = 1800000) // Every 30 minutes
    public void refreshPopularCities() {
        System.out.println("[SCHEDULER] Starting background price refresh (Jsoup)...");
        String[] cities = { "Trichy", "Chennai", "Salem", "Coimbatore", "Madurai" };
        java.util.Random random = new java.util.Random();
        for (String city : cities) {
            try {
                // Add a small random delay between cities to avoid detection
                Thread.sleep(1000 + random.nextInt(2000));

                getCurrentPrices(city);
                predictTomorrow(city, "24K");
                predictTomorrow(city, "SILVER");
            } catch (Exception e) {
                System.err.println("[SCHEDULER] Failed to refresh " + city);
            }
        }
    }
}
