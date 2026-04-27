package org.lupoi.workoutapp.presentation.mapper;/*
    @author Andrii
    @project workout
    @class WorkoutLogDtoMapper
    @version 1.0.0
    @since 27.04.2026 - 20.57
*/

import org.lupoi.workoutapp.application.command.LogWorkoutCommand;
import org.lupoi.workoutapp.domain.entity.LoggedExercise;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.presentation.dto.request.LogWorkoutRequest;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutLogDtoMapper {


    LogWorkoutCommand toCommand(LogWorkoutRequest req);

    LogWorkoutCommand.LoggedExerciseCommand toCommand(LogWorkoutRequest.LoggedExerciseRequest e);



    @Mapping(target = "completedAt",
            expression = "java(log.getCompletedAt() != null ? log.getCompletedAt().toString() : null)")
    WorkoutLogResponse toResponse(WorkoutLog log);

    @Mapping(target = "feltEasy", source = "felt_easy")
    WorkoutLogResponse.LoggedExerciseResponse toResponse(LoggedExercise e);
}
