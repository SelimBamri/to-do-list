package com.example.todolist.services;

import com.example.todolist.entities.RefreshToken;
import com.example.todolist.entities.User;
import com.example.todolist.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Service
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;
    UserService userService;
    RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Value("${jwt.refresh}")
    int refresh;

    public RefreshToken createRefreshToken(String username){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userService.getUserByUsername(username).get());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }
    public RefreshToken findByUser(User user){
        return refreshTokenRepository.findByUser(user).orElse(null);
    }
    public void deleteByToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteByUser(User user){
        refreshTokenRepository.deleteByUser(user);
    }

    public void flush(){
        refreshTokenRepository.flush();
    }

}
