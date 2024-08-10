package com.example.todolist.controllers;

import com.example.todolist.dtos.requests.RefreshTokenRequestDto;
import com.example.todolist.dtos.requests.RegisterUserRequestDto;
import com.example.todolist.dtos.responses.LoginResponseDto;
import com.example.todolist.entities.RefreshToken;
import com.example.todolist.entities.User;
import com.example.todolist.services.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@RequestMapping("/admin")
@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final BlacklistTokenService blacklistTokenService;
    private final PasswordEncoder passwordEncoder;

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().body("Successfully deleted user");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/")
    public ResponseEntity<User> getMyAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteMyAccount(@RequestBody RefreshTokenRequestDto refreshTokenDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        userService.deleteUserById(currentUser.getId());
        refreshTokenService.deleteByToken(refreshTokenDto.getRefreshToken());
        String jwtToken = refreshTokenDto.getRefreshToken();
        long expirationTime = jwtService.getExpirationTime();
        blacklistTokenService.blacklistToken(jwtToken, expirationTime);
        return ResponseEntity.ok().body("Successfully logged out");
    }

    @PutMapping("/")
    public ResponseEntity<?> updateMyAccount(@RequestBody RegisterUserRequestDto input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        boolean newToken = false;
        boolean change = false;
        if(input.getPassword()!= null && currentUser.getPassword()!= null && !passwordEncoder.matches(input.getPassword(), currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(input.getPassword()));
            newToken = true;
        }
        if(input.getUsername()!= null && !currentUser.getUsername().equals(input.getUsername())) {
            currentUser.setUsername(input.getUsername());
            newToken = true;
        }
        if(input.getFullName()!= null) {
            currentUser.setFullName(input.getFullName());
            change = true;
        }
        if(newToken || change) {
            userService.saveUser(currentUser);
        }
        if(newToken){
            String jwtToken = jwtService.generateToken(currentUser);
            RefreshToken refreshToken = refreshTokenService.findByUser(currentUser);
            if(refreshToken!= null){
                refreshTokenService.deleteByToken(refreshToken.getToken());
            }
            refreshToken = refreshTokenService.createRefreshToken(currentUser.getUsername());
            LoginResponseDto loginResponse = new LoginResponseDto(jwtToken, refreshToken.getToken(), jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        }
        else{
            return ResponseEntity.ok().body("done");
        }
    }
    
}

