package org.lupoi.workoutapp.domain.enums;/*
    @author Andrii
    @project workout
    @class PlanType
    @version 1.0.0
    @since 25.04.2026 - 19.39
*/

public enum PlanType {
    // Маса (MASS) — 8 тижнів
    HYPERTROPHY,

    // Сила (STRENGTH) — 10 тижнів
    STRENGTH,

    // Сила + Маса (STRENGTH_AND_MASS) — 9 тижнів, твій поточний підхід
    STRENGTH_HYPERTROPHY,

    // Схуднення (LOSS) — 8 тижнів
    FAT_LOSS,

    // Витривалість (ENDURANCE) — 8 тижнів
    ENDURANCE
}