package com.example.todolist.controllers;

import com.example.todolist.dtos.requests.RegisterUserRequestDto;
import com.example.todolist.dtos.responses.ProfileResponseDto;
import com.example.todolist.entities.Role;
import com.example.todolist.entities.RoleEnum;
import com.example.todolist.entities.User;
import com.example.todolist.services.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RequestMapping("/admin")
@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully deleted account");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ProfileResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<ProfileResponseDto> res = new ArrayList<ProfileResponseDto>();
        for (User user : users) {
            ProfileResponseDto resUser = new ProfileResponseDto();
            resUser.setId(user.getId());
            resUser.setUsername(user.getUsername());
            resUser.setCreatedAt(user.getCreatedAt());
            resUser.setUpdatedAt(user.getUpdatedAt());
            resUser.setFirstName(user.getFirstName());
            resUser.setLastName(user.getLastName());
            if(user.getPhoto()!= null){
                resUser.setPhoto(new String(user.getPhoto(), StandardCharsets.UTF_8));
            }
            res.add(resUser);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ProfileResponseDto> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        ProfileResponseDto resUser = new ProfileResponseDto();
        resUser.setId(user.getId());
        resUser.setUsername(user.getUsername());
        resUser.setCreatedAt(user.getCreatedAt());
        resUser.setUpdatedAt(user.getUpdatedAt());
        resUser.setFirstName(user.getFirstName());
        resUser.setLastName(user.getLastName());
        if(user.getPhoto()!= null){
            resUser.setPhoto(new String(user.getPhoto(), StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok(resUser);
    }

    @PostMapping("/new")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequestDto registerUserDto) {
        Optional<Role> optionalRole = roleService.getRoleByName(RoleEnum.ADMIN);
        if (optionalRole.isEmpty()) {
            return ResponseEntity.badRequest().body("Try Later... Unable to find admin role");
        }
        User adminUser = new User();
        adminUser.setUsername(registerUserDto.getUsername());
        adminUser.setFirstName(registerUserDto.getFirstName());
        adminUser.setLastName(registerUserDto.getFirstName());
        adminUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        adminUser.setRole(optionalRole.get());
        if(registerUserDto.getPhoto() != null) {
            adminUser.setPhoto(registerUserDto.getPhoto().getBytes(StandardCharsets.UTF_8));
        }
        userService.saveUser(adminUser);
        return ResponseEntity.ok(adminUser);
    }
}

