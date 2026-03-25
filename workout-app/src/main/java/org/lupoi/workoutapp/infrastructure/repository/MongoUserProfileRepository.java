package org.lupoi.workoutapp.infrastructure.repository;
import org.lupoi.workoutapp.infrastructure.document.UserProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserProfileRepository extends MongoRepository<UserProfileDocument, String> {
    Optional<UserProfileDocument> findByUserId(String userId);
}