package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class PasswordResetToken
    @version 1.0.0
    @since 09.05.2026 - 12.06
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    private String id;
    private String userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
}
