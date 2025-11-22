package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.LoginRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.domain.user.UserService;
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

    private final UserService userService;

    //Takes data if request was successful
    //e.g. right url request, google correct work etc.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleAuthId = oAuth2User.getAttribute("sub");


        log.info("OAuth2 login success for email: {}", email);

        UserResponseDto user = userService.loginViaGoogle(name, new LoginRequestDto(email, null, googleAuthId, googleAuthId));

        //For now, it response basic statistics report and login-ed user
        response.setContentType("application/json");
        response.getWriter().write("{\"status\": \"success\", \"email\": \"" + email + "\"}\n"+user);

        // super.onAuthenticationSuccess(request, response, authentication);
    }
}
