package com.example.todolist.services;

import com.example.todolist.entities.Role;
import com.example.todolist.entities.RoleEnum;
import com.example.todolist.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Optional<Role> getRoleByName(RoleEnum name) {
        return roleRepository.findByName(name);
    }
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

}
