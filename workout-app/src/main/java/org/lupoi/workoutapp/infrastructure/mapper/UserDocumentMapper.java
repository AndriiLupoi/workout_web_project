package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.infrastructure.document.UserDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDocumentMapper {
    User toDomain(UserDocument document);
    UserDocument toDocument(User user);
}