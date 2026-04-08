package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class UserProfile
    @version 1.0.0
    @since 24.03.2026 - 14.42
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String id;
    private String userId;
    private TrainingGoal goal;
    private FitnessLevel level;
    private Integer workoutsPerWeek;
    private Double currentWeight;
    private Double targetWeight;
    private Double height;
    private Integer age;
    private List<String> availableEquipment;
}