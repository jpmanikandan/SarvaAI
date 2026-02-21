package com.sarva.security;

import com.sarva.entity.LoginHistory;
import com.sarva.entity.User;
import com.sarva.repository.LoginHistoryRepository;
import com.sarva.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository, LoginHistoryRepository loginHistoryRepository) {
        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauthUser = oauthToken.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            String providerId = oauthUser.getName();

            // Save or Update User
            User user = userRepository.findByEmail(email).orElse(new User());
            user.setEmail(email);
            user.setName(name);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setLastLogin(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            // Log Login History
            LoginHistory history = new LoginHistory();
            history.setUser(savedUser);
            history.setLoginTime(LocalDateTime.now());
            history.setIpAddress(getClientIp(request));
            history.setUserAgent(request.getHeader("User-Agent"));
            history.setStatus("SUCCESS");
            loginHistoryRepository.save(history);

            log.info("User {} logged in successfully.", email);
        }

        response.sendRedirect("/index.html");
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
