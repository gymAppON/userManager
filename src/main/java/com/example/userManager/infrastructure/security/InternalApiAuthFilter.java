package com.example.userManager.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class InternalApiAuthFilter extends OncePerRequestFilter {

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestKey = request.getHeader("x-internal-api-key");

        // Checking GateWay key
        if (requestKey == null || !requestKey.equals(internalApiKey)) {
            log.warn("Unauthorized access attempt without valid API Key from IP: {}", request.getRemoteAddr());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access Denied: Invalid Internal Key\"}");
            return;
        }

        String userId = request.getHeader("X-User-Id");

        if (userId != null && !userId.isBlank()) {
            //Setting security context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user {} via Gateway headers", userId);
        } else {
            log.debug("Request from Gateway without user identity (anonymous/public request)");
        }

        filterChain.doFilter(request, response);
    }

    // While app is being built, to access app via swagger
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs");
    }
}