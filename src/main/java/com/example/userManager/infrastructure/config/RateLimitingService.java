package com.example.userManager.infrastructure.config;

import com.example.userManager.shared.exception.LogEnum;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.RateLimiter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitingService {
    private final Map<String, Bucket> cacheByEmail = new ConcurrentHashMap<>();

    public boolean allowRequest(String email) {
        Bucket bucket = cacheByEmail.computeIfAbsent(email, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            log.info("{}:Login attempt allowed for email: {}", LogEnum.CONFIG, email);;
            return true;
        } else {
            log.warn("{}:Rate limit exceeded for email: {}", LogEnum.CONFIG, email);
            return false;
        }
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
