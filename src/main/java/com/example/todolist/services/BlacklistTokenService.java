package com.example.todolist.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class BlacklistTokenService {
    private RedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void blacklistToken(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, true, expirationTime, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        Boolean isBlacklisted = (Boolean) redisTemplate.opsForValue().get(key);
        return isBlacklisted != null && isBlacklisted;
    }
}

