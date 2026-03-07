package com.idalgo.daniel.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Role entity.
 *
 * Roles: ROLE_USER, ROLE_ADMIN, ROLE_MANAGER
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name; // ROLE_USER, ROLE_ADMIN

    @Column(length = 200)
    private String description;

//    @ManyToMany(mappedBy = "roles")
//    @Builder.Default
//    private Set<User> users = new HashSet<>();
}