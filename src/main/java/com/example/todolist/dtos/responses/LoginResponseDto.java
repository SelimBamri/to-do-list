package com.example.todolist.dtos.responses;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private long expiresIn;
}
