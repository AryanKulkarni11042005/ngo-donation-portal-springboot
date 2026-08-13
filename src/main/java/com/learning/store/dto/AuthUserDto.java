package com.learning.store.dto;

import com.learning.store.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserDto {
    private Integer id;
    private String name;
    private String email;
    private Role role;
}
