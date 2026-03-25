package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;
import org.lupoi.workoutapp.infrastructure.document.UserProfileDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserProfileDocumentMapper {

    @org.mapstruct.Mapping(target = "goal", source = "goal", qualifiedByName = "stringToGoal")
    @org.mapstruct.Mapping(target = "level", source = "level", qualifiedByName = "stringToLevel")
    UserProfile toDomain(UserProfileDocument document);

    @org.mapstruct.Mapping(target = "goal", source = "goal", qualifiedByName = "goalToString")
    @org.mapstruct.Mapping(target = "level", source = "level", qualifiedByName = "levelToString")
    UserProfileDocument toDocument(UserProfile profile);

    @Named("stringToGoal")
    default TrainingGoal stringToGoal(String value) {
        return value != null ? TrainingGoal.valueOf(value) : null;
    }

    @Named("stringToLevel")
    default FitnessLevel stringToLevel(String value) {
        return value != null ? FitnessLevel.valueOf(value) : null;
    }

    @Named("goalToString")
    default String goalToString(TrainingGoal goal) {
        return goal != null ? goal.name() : null;
    }

    @Named("levelToString")
    default String levelToString(FitnessLevel level) {
        return level != null ? level.name() : null;
    }
}