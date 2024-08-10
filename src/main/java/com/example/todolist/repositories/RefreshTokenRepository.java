package com.example.todolist.repositories;


import com.example.todolist.entities.RefreshToken;
import com.example.todolist.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}