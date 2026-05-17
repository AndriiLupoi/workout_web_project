package org.lupoi.workoutapp.infrastructure.repoImplement;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.lupoi.workoutapp.infrastructure.mapper.UserProfileDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoUserProfileRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final MongoUserProfileRepository mongoRepository;
    private final UserProfileDocumentMapper mapper;

    @Override
    public Optional<UserProfile> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public UserProfile save(UserProfile profile) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(profile)));
    }
}