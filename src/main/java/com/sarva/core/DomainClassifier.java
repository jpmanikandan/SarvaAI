package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DomainClassifier {

    private final ChatClient chatClient;

    public DomainClassifier(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String classify(String question) {
        String classification = chatClient.prompt()
                .system("You are a query classifier. Categorize the user query into one of these categories: LAW, DEV, FINANCE, HEALTH, EDU, MATRIMONY, IOT, COMMERCE, YOGA, GYM, DIET, JAVA_INTERVIEW, SSLC, TWELFTH, LOG_ANALYZER. "
                        +
                        "LAW: for legal, court, or act-related queries. " +
                        "DEV: for general programming, software, or coding queries. " +
                        "FINANCE: for money, gold, market, or investment queries. " +
                        "HEALTH: for general wellness, medical guidance, or healthy habits. " +
                        "EDU: for general learning, spoken English, or grammar. " +
                        "MATRIMONY: for wedding, marriage, matchmaking, or relationship compatibility. " +
                        "IOT: for smart home, automation, sensors, hardware, or predictive maintenance. " +
                        "COMMERCE: for shopping, products, price comparison, or bazaar recommendations. " +
                        "YOGA: for yoga poses, asanas, meditation, and flexibility exercises. " +
                        "GYM: for gym workouts, weightlifting, strength training, and bodybuilding. " +
                        "DIET: for meal plans, nutrition, weight loss/gain diets, and food advice. " +
                        "JAVA_INTERVIEW: for Java interview questions, core Java concepts, multithreading, collections, and frameworks like Spring Boot. "
                        +
                        "SSLC: for 10th standard/SSLC subjects, specifically Mathematics and Science formulas/concepts. "
                        +
                        "TWELFTH: for 12th standard/HSC subjects, specifically Mathematics (Calculus), Physics, and Chemistry. "
                        +
                        "LOG_ANALYZER: for analyzing Java logs, exceptions, errors, stack traces, and suggesting code fixes. "
                        +
                        "Output ONLY the category name.")
                .user(question)
                .call()
                .content();

        classification = classification.trim().toUpperCase();
        if (classification.contains("LAW"))
            return "LAW";
        if (classification.contains("DEV") && !classification.contains("JAVA_INTERVIEW"))
            return "DEV";
        if (classification.contains("JAVA_INTERVIEW"))
            return "JAVA_INTERVIEW";
        if (classification.contains("FINANCE"))
            return "FINANCE";
        if (classification.contains("HEALTH"))
            return "HEALTH";
        if (classification.contains("EDU"))
            return "EDU";
        if (classification.contains("MATRIMONY"))
            return "MATRIMONY";
        if (classification.contains("IOT"))
            return "IOT";
        if (classification.contains("COMMERCE"))
            return "COMMERCE";
        if (classification.contains("YOGA"))
            return "YOGA";
        if (classification.contains("GYM"))
            return "GYM";
        if (classification.contains("DIET"))
            return "DIET";
        if (classification.contains("SSLC"))
            return "SSLC";
        if (classification.contains("TWELFTH"))
            return "TWELFTH";
        if (classification.contains("LOG_ANALYZER") || classification.contains("LOG")
                || classification.contains("EXCEPTION") || classification.contains("STACKTRACE"))
            return "LOG_ANALYZER";

        return "GENERAL";
    }
}
