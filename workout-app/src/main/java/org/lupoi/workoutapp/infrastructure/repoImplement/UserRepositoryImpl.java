package org.lupoi.workoutapp.infrastructure.repoImplement;
import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.model.PageResult;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.infrastructure.mapper.UserDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    public List<User> findAll() {
        return mongoRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<User> findAll(int page, int size) {
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<User> result = mongoRepository.findAll(pageable)
                .map(mapper::toDomain);

        return new PageResult<>(
                result.getContent(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.getSize()
        );
    }


    @Override
    public User updateRole(String userId, Role role) {
        var doc = mongoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        doc.setRole(role);
        return mapper.toDomain(mongoRepository.save(doc));
    }

    @Override
    public void deleteById(String userId) {
        mongoRepository.deleteById(userId);
    }

}