package org.lupoi.workoutapp.infrastructure.mapper;

import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.PlanType;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;
import org.lupoi.workoutapp.infrastructure.document.UserProfileDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserProfileDocumentMapper {

    @Mapping(target = "goal",     source = "goal",     qualifiedByName = "stringToGoal")
    @Mapping(target = "level",    source = "level",    qualifiedByName = "stringToLevel")
    @Mapping(target = "planType", source = "planType", qualifiedByName = "stringToPlanType")
    UserProfile toDomain(UserProfileDocument document);

    @Mapping(target = "goal",     source = "goal",     qualifiedByName = "goalToString")
    @Mapping(target = "level",    source = "level",    qualifiedByName = "levelToString")
    @Mapping(target = "planType", source = "planType", qualifiedByName = "planTypeToString")
    UserProfileDocument toDocument(UserProfile profile);

    @Named("stringToGoal")
    default TrainingGoal stringToGoal(String value) {
        return value != null ? TrainingGoal.valueOf(value) : null;
    }

    @Named("stringToLevel")
    default FitnessLevel stringToLevel(String value) {
        return value != null ? FitnessLevel.valueOf(value) : null;
    }

    @Named("stringToPlanType")
    default PlanType stringToPlanType(String value) {
        return value != null ? PlanType.valueOf(value) : null;
    }

    @Named("goalToString")
    default String goalToString(TrainingGoal goal) {
        return goal != null ? goal.name() : null;
    }

    @Named("levelToString")
    default String levelToString(FitnessLevel level) {
        return level != null ? level.name() : null;
    }

    @Named("planTypeToString")
    default String planTypeToString(PlanType planType) {
        return planType != null ? planType.name() : null;
    }
}