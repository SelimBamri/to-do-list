package com.example.todolist.services;

import com.example.todolist.dtos.requests.LoginRequestDto;
import com.example.todolist.dtos.requests.RegisterUserRequestDto;
import com.example.todolist.entities.Role;
import com.example.todolist.entities.RoleEnum;
import com.example.todolist.entities.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    public User signup(RegisterUserRequestDto input) {
        Optional<Role> optionalRole = roleService.getRoleByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) {
            return null;
        }
        User user = modelMapper.map(input, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(optionalRole.get());
        return userService.saveUser(user);
    }

    public User authenticate(LoginRequestDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        return userService.getUserByUsername(input.getUsername())
                .orElseThrow();
    }
}
