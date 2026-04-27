package org.lupoi.workoutapp.application.usecase.user;/*
    @author Andrii
    @project workout
    @class UpdateUserUseCase
    @version 1.0.0
    @since 22.04.2026 - 21.08
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase{

    private final UserRepository userRepository;

    public User execute(String userId, String firstName, String lastName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User updated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(user.getCreatedAt())
                .build();

        return userRepository.save(updated);
    }
}
