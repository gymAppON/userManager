package com.example.userManager.security;

import com.example.userManager.dto.response.UserResponseDto;
import com.example.userManager.user.UserEntity;
import com.example.userManager.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService; // Твой сервис для работы с БД

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name"); // Или "given_name" + "family_name"

        log.info("OAuth2 login success for email: {}", email);

        UserResponseDto user = userService.processOAuthPostLogin(email, name);

        response.setContentType("application/json");
        response.getWriter().write("{\"status\": \"success\", \"email\": \"" + email + "\"}");

        // super.onAuthenticationSuccess(request, response, authentication);
    }
}
