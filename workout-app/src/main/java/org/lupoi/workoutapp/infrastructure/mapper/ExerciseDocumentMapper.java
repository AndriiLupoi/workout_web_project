package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.infrastructure.document.ExerciseDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExerciseDocumentMapper {

    @org.mapstruct.Mapping(target = "muscleGroup", source = "muscleGroup", qualifiedByName = "stringToMuscle")
    @org.mapstruct.Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "stringToDifficulty")
    @org.mapstruct.Mapping(target = "equipmentType", source = "equipmentType", qualifiedByName = "stringToEquipment")
    Exercise toDomain(ExerciseDocument document);

    @org.mapstruct.Mapping(target = "muscleGroup", source = "muscleGroup", qualifiedByName = "muscleToString")
    @org.mapstruct.Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "difficultyToString")
    @org.mapstruct.Mapping(target = "equipmentType", source = "equipmentType", qualifiedByName = "equipmentToString")
    ExerciseDocument toDocument(Exercise exercise);

    @Named("stringToMuscle")
    default MuscleGroup stringToMuscle(String value) {
        return value != null ? MuscleGroup.valueOf(value) : null;
    }

    @Named("stringToDifficulty")
    default Difficulty stringToDifficulty(String value) {
        return value != null ? Difficulty.valueOf(value) : null;
    }

    @Named("stringToEquipment")
    default EquipmentType stringToEquipment(String value) {
        return value != null ? EquipmentType.valueOf(value) : null;
    }

    @Named("muscleToString")
    default String muscleToString(MuscleGroup value) {
        return value != null ? value.name() : null;
    }

    @Named("difficultyToString")
    default String difficultyToString(Difficulty value) {
        return value != null ? value.name() : null;
    }

    @Named("equipmentToString")
    default String equipmentToString(EquipmentType value) {
        return value != null ? value.name() : null;
    }
}