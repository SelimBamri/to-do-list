package com.example.todolist.dtos.requests;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class LoginRequestDto {
    private String username;
    private String password;
}
