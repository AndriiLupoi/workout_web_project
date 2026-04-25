package org.lupoi.workoutapp.presentation.mapper;

import org.lupoi.workoutapp.application.command.SaveUserProfileCommand;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.presentation.dto.request.SaveProfileRequest;
import org.lupoi.workoutapp.presentation.dto.response.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileDtoMapper {

    default SaveUserProfileCommand toCommand(SaveProfileRequest request) {
        return new SaveUserProfileCommand(
                request.goal(),
                request.level(),
                request.planType(),
                request.workoutsPerWeek(),
                request.currentWeight(),
                request.targetWeight(),
                request.height(),
                request.age(),
                request.availableEquipment()
        );
    }

    @Mapping(target = "goal", expression = "java(profile.getGoal() != null ? profile.getGoal().name() : null)")
    @Mapping(target = "level", expression = "java(profile.getLevel() != null ? profile.getLevel().name() : null)")
    @Mapping(target = "planType", expression = "java(profile.getPlanType() != null ? profile.getPlanType().name() : null)")
    ProfileResponse toResponse(UserProfile profile);
}