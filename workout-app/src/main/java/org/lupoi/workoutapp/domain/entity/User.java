package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class User
    @version 1.0.0
    @since 24.03.2026 - 14.39
*/

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lupoi.workoutapp.domain.enums.Role;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private Role role;
}
