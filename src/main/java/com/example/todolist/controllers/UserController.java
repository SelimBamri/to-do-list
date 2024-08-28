package com.example.todolist.controllers;

import com.example.todolist.dtos.requests.RegisterUserRequestDto;
import com.example.todolist.dtos.responses.LoginResponseDto;
import com.example.todolist.dtos.responses.ProfileResponseDto;
import com.example.todolist.entities.User;
import com.example.todolist.services.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/user")
@RestController
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public ResponseEntity<ProfileResponseDto> getMyAccount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        ProfileResponseDto response = new ProfileResponseDto();
        response.setId(currentUser.getId());
        response.setUsername(currentUser.getUsername());
        response.setCreatedAt(currentUser.getCreatedAt());
        response.setUpdatedAt(currentUser.getUpdatedAt());
        response.setFirstName(currentUser.getFirstName());
        response.setLastName(currentUser.getLastName());
        if(currentUser.getPhoto() != null){
            response.setPhoto(new String(currentUser.getPhoto(), StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok(response);
    }

    @Transactional
    @DeleteMapping("/")
    public ResponseEntity<?> deleteMyAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        userService.deleteUserById(currentUser.getId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully deleted account");
        return ResponseEntity.ok(response);
    }

    @Transactional
    @PutMapping("/")
    public ResponseEntity<?> updateMyAccount(@RequestBody RegisterUserRequestDto input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        currentUser = userService.getUserById(currentUser.getId());
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
        if(input.getFirstName()!= null) {
            currentUser.setFirstName(input.getFirstName());
            change = true;
        }
        if(input.getLastName()!= null) {
            currentUser.setLastName(input.getLastName());
            change = true;
        }
        if(input.getPhoto() != null) {
            currentUser.setPhoto(input.getPhoto().getBytes(StandardCharsets.UTF_8));
            change = true;
        }
        else{
            currentUser.setPhoto(null);
        }
        if(newToken || change) {
            userService.saveUser(currentUser);
        }
        if(newToken){
            String jwtToken = jwtService.generateToken(currentUser);
            LoginResponseDto loginResponse = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        }
        else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully updated account");
            return ResponseEntity.ok(response);
        }
    }
}

