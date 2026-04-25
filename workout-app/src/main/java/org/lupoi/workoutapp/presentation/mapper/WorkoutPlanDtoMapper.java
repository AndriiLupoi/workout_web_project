package org.lupoi.workoutapp.presentation.mapper;

import org.lupoi.workoutapp.domain.entity.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.WorkoutExercise;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutDayResponse;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutExerciseResponse;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutPlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutPlanDtoMapper {

    @Mapping(target = "goal",      expression = "java(plan.getGoal()     != null ? plan.getGoal().name()     : null)")
    @Mapping(target = "status",    expression = "java(plan.getStatus()   != null ? plan.getStatus().name()   : null)")
    @Mapping(target = "planType",  expression = "java(plan.getPlanType() != null ? plan.getPlanType().name() : null)")
    @Mapping(target = "createdAt", expression = "java(plan.getCreatedAt() != null ? plan.getCreatedAt().toString() : null)")
    WorkoutPlanResponse toResponse(WorkoutPlan plan);

    @Mapping(target = "intensityType", expression = "java(day.getIntensityType() != null ? day.getIntensityType().name() : null)")
    WorkoutDayResponse toResponse(WorkoutDay day);

    WorkoutExerciseResponse toResponse(WorkoutExercise exercise);
}