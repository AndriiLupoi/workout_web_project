package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.WorkoutExercise;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.PlanStatus;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;
import org.lupoi.workoutapp.infrastructure.document.WorkoutDayDocument;
import org.lupoi.workoutapp.infrastructure.document.WorkoutExerciseDocument;
import org.lupoi.workoutapp.infrastructure.document.WorkoutPlanDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WorkoutPlanDocumentMapper {

    @org.mapstruct.Mapping(target = "goal", source = "goal", qualifiedByName = "stringToGoal")
    @org.mapstruct.Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    WorkoutPlan toDomain(WorkoutPlanDocument document);

    @org.mapstruct.Mapping(target = "goal", source = "goal", qualifiedByName = "goalToString")
    @org.mapstruct.Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    WorkoutPlanDocument toDocument(WorkoutPlan plan);

    WorkoutDay toDomain(WorkoutDayDocument doc);
    WorkoutDayDocument toDocument(WorkoutDay day);

    WorkoutExercise toDomain(WorkoutExerciseDocument doc);
    WorkoutExerciseDocument toDocument(WorkoutExercise ex);

    @Named("stringToGoal")
    default TrainingGoal stringToGoal(String value) {
        return value != null ? TrainingGoal.valueOf(value) : null;
    }

    @Named("stringToStatus")
    default PlanStatus stringToStatus(String value) {
        return value != null ? PlanStatus.valueOf(value) : null;
    }

    @Named("goalToString")
    default String goalToString(TrainingGoal goal) {
        return goal != null ? goal.name() : null;
    }

    @Named("statusToString")
    default String statusToString(PlanStatus status) {
        return status != null ? status.name() : null;
    }
}