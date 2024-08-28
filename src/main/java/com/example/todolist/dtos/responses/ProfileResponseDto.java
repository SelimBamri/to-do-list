package com.example.todolist.dtos.responses;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String photo;
    private Date createdAt;
    private Date updatedAt;
}
