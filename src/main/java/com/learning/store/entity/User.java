package com.learning.store.entity;

import com.learning.store.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    private Role role;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
