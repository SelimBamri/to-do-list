package com.example.todolist.bootstrap;

import com.example.todolist.entities.Role;
import com.example.todolist.entities.RoleEnum;
import com.example.todolist.entities.User;
import com.example.todolist.services.AuthenticationService;
import com.example.todolist.services.RoleService;
import com.example.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!userService.adminExists()) {
            Optional<Role> optionalRole = roleService.getRoleByName(RoleEnum.ADMIN);
            if (optionalRole.isEmpty()) {
                return;
            }
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setFullName("Admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRole(optionalRole.get());
            userService.saveUser(adminUser);
        }
    }
}
