package org.lupoi.workoutapp.infrastructure.mapper;/*
    @author Andrii
    @project workout
    @class WorkoutLogDocumentMapper
    @version 1.0.0
    @since 27.04.2026 - 20.54
*/

import org.lupoi.workoutapp.domain.entity.LoggedExercise;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.infrastructure.document.WorkoutLogDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutLogDocumentMapper {


    WorkoutLogDocument toDocument(WorkoutLog log);

    WorkoutLog toDomain(WorkoutLogDocument doc);



    @Mapping(target = "feltEasy", source = "felt_easy")
    WorkoutLogDocument.LoggedExerciseDoc toDocument(LoggedExercise exercise);

    @Mapping(target = "felt_easy", source = "feltEasy")
    LoggedExercise toDomain(WorkoutLogDocument.LoggedExerciseDoc doc);
}
