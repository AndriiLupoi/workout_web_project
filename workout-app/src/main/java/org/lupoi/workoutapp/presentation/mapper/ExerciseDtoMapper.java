package org.lupoi.workoutapp.presentation.mapper;

import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.presentation.dto.response.ExerciseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseDtoMapper {

    @Mapping(target = "muscleGroup", expression = "java(exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().name() : null)")
    @Mapping(target = "difficulty", expression = "java(exercise.getDifficulty() != null ? exercise.getDifficulty().name() : null)")
    @Mapping(target = "equipmentType", expression = "java(exercise.getEquipmentType() != null ? exercise.getEquipmentType().name() : null)")
    ExerciseResponse toResponse(Exercise exercise);
}