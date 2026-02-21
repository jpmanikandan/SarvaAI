package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final ChatClient chatClient;

    public TranslationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String detectLanguage(String text) {
        String language = chatClient.prompt()
                .system("Detect the language of the following text. Output ONLY the name of the language (e.g., 'Tamil', 'Hindi', 'English', 'French').")
                .user(text)
                .call()
                .content();
        return language.trim();
    }

    public String translate(String text, String sourceLang, String targetLang) {
        if (sourceLang.equalsIgnoreCase(targetLang)) {
            return text;
        }
        return chatClient.prompt()
                .system("You are a professional translator. Translate the following " + sourceLang + " text to "
                        + targetLang + ". Return only the translated text.")
                .user(text)
                .call()
                .content();
    }
}

