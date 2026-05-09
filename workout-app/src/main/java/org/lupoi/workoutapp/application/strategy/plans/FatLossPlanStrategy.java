package org.lupoi.workoutapp.application.strategy.plans;

import org.lupoi.workoutapp.application.strategy.BasePlanStrategy;
import org.lupoi.workoutapp.application.strategy.config.PlanConfig;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fat Loss plan — 8 weeks.
 *
 * Key insight: fat loss happens through diet, NOT exercise.
 * Training goal: preserve muscle mass + increase caloric expenditure.
 *
 * Trainer logic:
 *   - Higher reps (12–20), shorter rest (45s) = more calories burned
 *   - Supersets and circuits (SETS intensity) dominate
 *   - Still includes MEDIUM weeks to maintain strength and prevent muscle loss
 *   - NOT "cardio circuits" — still resistance training with compound movements
 *   - Full Body approach works well here for caloric expenditure
 *
 * What NOT to do:
 *   - Don't go too light — you'll lose muscle
 *   - Don't go full HIIT every session — leads to injury
 */
@Component("FAT_LOSS")
public class FatLossPlanStrategy extends BasePlanStrategy {

    @Override
    public int totalWeeks() { return PlanConfig.WEEKS_FAT_LOSS; }

    @Override
    public String planTitle() { return "Fat Loss Plan — 8 weeks"; }

    @Override
    public List<IntensityType> intensityCycle() {
        return PlanConfig.CYCLE_FAT_LOSS;
    }

    /**
     * Fat loss benefits from more frequent sessions — allow up to 4 Full Body weeks
     * if the user is a beginner/returning, since full body burns more total calories.
     */
    @Override
    protected int fullBodySessionsPerWeek(int requested) {
        return Math.min(requested, 4);
    }
}