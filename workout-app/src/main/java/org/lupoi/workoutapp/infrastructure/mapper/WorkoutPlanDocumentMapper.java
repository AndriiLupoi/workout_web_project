package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.WorkoutExercise;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.lupoi.workoutapp.domain.enums.PlanStatus;
import org.lupoi.workoutapp.domain.enums.PlanType;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;
import org.lupoi.workoutapp.infrastructure.document.workout.WorkoutDayDocument;
import org.lupoi.workoutapp.infrastructure.document.workout.WorkoutExerciseDocument;
import org.lupoi.workoutapp.infrastructure.document.workout.WorkoutPlanDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WorkoutPlanDocumentMapper {

    @Mapping(target = "goal",         source = "goal",         qualifiedByName = "stringToGoal")
    @Mapping(target = "status",       source = "status",       qualifiedByName = "stringToStatus")
    @Mapping(target = "planType",     source = "planType",     qualifiedByName = "stringToPlanType")
    WorkoutPlan toDomain(WorkoutPlanDocument document);

    @Mapping(target = "goal",         source = "goal",         qualifiedByName = "goalToString")
    @Mapping(target = "status",       source = "status",       qualifiedByName = "statusToString")
    @Mapping(target = "planType",     source = "planType",     qualifiedByName = "planTypeToString")
    WorkoutPlanDocument toDocument(WorkoutPlan plan);

    @Mapping(target = "intensityType", source = "intensityType", qualifiedByName = "stringToIntensity")
    WorkoutDay toDomain(WorkoutDayDocument doc);

    @Mapping(target = "intensityType", source = "intensityType", qualifiedByName = "intensityToString")
    WorkoutDayDocument toDocument(WorkoutDay day);

    default WorkoutExercise toDomain(WorkoutExerciseDocument doc) {
        return WorkoutExercise.builder()
                .exerciseId(doc.getExerciseId())
                .exerciseName(doc.getExerciseName())
                .sets(doc.getSets())
                .reps(doc.getReps())
                .restSeconds(doc.getRestSeconds())
                .plannedWeight(doc.getPlannedWeight())
                .muscleGroup(doc.getMuscleGroup())
                .equipmentType(doc.getEquipmentType())
                .description(doc.getDescription())
                .build();
    }

    default WorkoutExerciseDocument toDocument(WorkoutExercise ex) {
        return WorkoutExerciseDocument.builder()
                .exerciseId(ex.getExerciseId())
                .exerciseName(ex.getExerciseName())
                .sets(ex.getSets())
                .reps(ex.getReps())
                .restSeconds(ex.getRestSeconds())
                .plannedWeight(ex.getPlannedWeight())
                .muscleGroup(ex.getMuscleGroup())
                .equipmentType(ex.getEquipmentType())
                .description(ex.getDescription())
                .build();
    }

    @Named("stringToGoal")
    default TrainingGoal stringToGoal(String value) {
        return value != null ? TrainingGoal.valueOf(value) : null;
    }

    @Named("stringToStatus")
    default PlanStatus stringToStatus(String value) {
        return value != null ? PlanStatus.valueOf(value) : null;
    }

    @Named("stringToPlanType")
    default PlanType stringToPlanType(String value) {
        return value != null ? PlanType.valueOf(value) : null;
    }

    @Named("stringToIntensity")
    default IntensityType stringToIntensity(String value) {
        return value != null ? IntensityType.valueOf(value) : null;
    }

    @Named("goalToString")
    default String goalToString(TrainingGoal goal) {
        return goal != null ? goal.name() : null;
    }

    @Named("statusToString")
    default String statusToString(PlanStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("planTypeToString")
    default String planTypeToString(PlanType planType) {
        return planType != null ? planType.name() : null;
    }

    @Named("intensityToString")
    default String intensityToString(IntensityType intensity) {
        return intensity != null ? intensity.name() : null;
    }
}