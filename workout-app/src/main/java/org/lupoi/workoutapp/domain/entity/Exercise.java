package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class Exercise
    @version 1.0.0
    @since 24.03.2026 - 14.42
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    private String id;
    private String name;
    private MuscleGroup muscleGroup;
    private Difficulty difficulty;
    private EquipmentType equipmentType;
    private String description;
}