package org.lupoi.workoutapp.infrastructure.repoImplement;
import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.infrastructure.mapper.UserDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final MongoUserRepository mongoRepository;
    private final UserDocumentMapper mapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(user)));
    }
}