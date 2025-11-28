package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    //Takes data if request was successful
    //e.g. right url request, google correct work etc.
    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleAuthId = oAuth2User.getAttribute("sub");


        log.info("OAuth2 login success for email: {}", email);

        String jwt = authService.loginViaGoogle(name, new LoginRequestDto(email, null, googleAuthId, googleAuthId));

        response.setContentType("application/json");
        response.getWriter().write(jwt);

        // super.onAuthenticationSuccess(request, response, authentication);
    }
}
