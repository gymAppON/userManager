package com.example.userManager.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        log.info("Income request URI: {}", request.getRequestURI());
        log.info("Expected Key (Server): '{}'", internalApiKey);
        log.info("Received Key (Header): '{}'", requestKey);
        // Key check
        if (requestKey == null || !requestKey.equals(internalApiKey)) {
            //False key
            log.warn("Unauthorized access attempt without valid API Key from IP: {}", request.getRemoteAddr());

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access Denied: Invalid Internal Key");
            return;
        }

        // True key
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