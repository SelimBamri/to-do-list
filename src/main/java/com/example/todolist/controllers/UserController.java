package com.example.todolist.controllers;

import com.example.todolist.dtos.requests.RegisterUserRequestDto;
import com.example.todolist.dtos.responses.LoginResponseDto;
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

@RequestMapping("/user")
@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public ResponseEntity<User> getMyAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(currentUser.getId()));
    }

    @Transactional
    @DeleteMapping("/")
    public ResponseEntity<?> deleteMyAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        userService.deleteUserById(currentUser.getId());
        return ResponseEntity.ok().body("Successfully deleted account");
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
        if(input.getFullName()!= null) {
            currentUser.setFullName(input.getFullName());
            change = true;
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
            return ResponseEntity.ok().body("done");
        }
    }
}

