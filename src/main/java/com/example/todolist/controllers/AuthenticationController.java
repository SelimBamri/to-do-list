package com.example.todolist.controllers;

import com.example.todolist.services.AuthenticationService;
import com.example.todolist.services.BlacklistTokenService;
import com.example.todolist.services.JwtService;
import com.example.todolist.services.RefreshTokenService;
import com.example.todolist.dtos.requests.LoginRequestDto;
import com.example.todolist.dtos.responses.LoginResponseDto;
import com.example.todolist.dtos.requests.RefreshTokenRequestDto;
import com.example.todolist.dtos.requests.RegisterUserRequestDto;
import com.example.todolist.entities.RefreshToken;
import com.example.todolist.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistTokenService blacklistTokenService;


    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserRequestDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginRequestDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getUsername());
        LoginResponseDto loginResponse = new LoginResponseDto(jwtToken, refreshToken.getToken(), jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenDto) {
        RefreshToken refreshTokenOptional = refreshTokenService.findByToken(refreshTokenDto.getRefreshToken()).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
        RefreshToken verifiedRefreshToken = refreshTokenService.verifyExpiration(refreshTokenOptional);
        User user = verifiedRefreshToken.getUser();
        String accessToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshTokenDto.getRefreshToken(), jwtService.getExpirationTime()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDto refreshTokenDto) {
        refreshTokenService.deleteByToken(refreshTokenDto.getRefreshToken());
        String jwtToken = refreshTokenDto.getRefreshToken();
        long expirationTime = jwtService.getExpirationTime();
        blacklistTokenService.blacklistToken(jwtToken, expirationTime);
        return ResponseEntity.ok().body("Successfully logged out");
    }
}
