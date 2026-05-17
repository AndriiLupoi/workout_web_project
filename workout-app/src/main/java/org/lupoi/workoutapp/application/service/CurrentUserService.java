package org.lupoi.workoutapp.application.service;/*
    @author Andrii
    @project workout
    @class CurrentUserService
    @version 1.0.0
    @since 09.05.2026 - 15.18
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthorized");
        }

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
