package com.example.todolist.dtos.requests;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshTokenRequestDto{
    private String refreshToken;
}
