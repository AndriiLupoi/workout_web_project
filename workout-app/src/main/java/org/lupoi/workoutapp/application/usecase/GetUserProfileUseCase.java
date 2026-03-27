package org.lupoi.workoutapp.application.usecase;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.exception.ProfileNotFoundException;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCase {

    private final UserProfileRepository userProfileRepository;

    public UserProfile execute(String userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
    }
}
