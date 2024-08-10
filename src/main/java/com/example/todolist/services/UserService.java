package com.example.todolist.services;

import com.example.todolist.entities.User;
import com.example.todolist.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public boolean adminExists() {
        return getAllUsers().stream()
               .anyMatch(user -> user.getRole().getName().equals("ADMIN"));
    }

    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

}
