package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.BodyWeightLog;
import org.lupoi.workoutapp.infrastructure.document.BodyWeightLogDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BodyWeightLogDocumentMapper {
    BodyWeightLog toDomain(BodyWeightLogDocument doc);
    BodyWeightLogDocument toDocument(BodyWeightLog log);
}