package org.lupoi.workoutapp.application.usecase;/*
    @author Andrii
    @project workout
    @class GetUserUseCase
    @version 1.0.0
    @since 22.04.2026 - 21.13
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;

    public User execute(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
