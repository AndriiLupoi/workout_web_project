package org.lupoi.workoutapp.application.strategy.plans;

import org.lupoi.workoutapp.application.strategy.BasePlanStrategy;
import org.lupoi.workoutapp.application.strategy.config.PlanConfig;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Strength + Hypertrophy plan — 9 weeks.
 *
 * The most popular approach for intermediate athletes.
 * Goal: build both strength AND muscle simultaneously.
 *
 * Trainer logic:
 *   - Heavy compounds (5x5 style) + volume accessories (3x10)
 *   - Alternates HEAVY and MEDIUM weeks for progressive overload
 *   - SETS weeks serve as "intensification" (pump, metabolic stress)
 *   - Classic structure: "Do a heavy set, follow with volume work"
 *
 * Example day:
 *   Bench Press     5 × 3-5
 *   Incline DB Press 4 × 8-12
 *   Cable Flye      3 × 12-20
 */
@Component("STRENGTH_HYPERTROPHY")
public class StrengthHypertrophyPlanStrategy extends BasePlanStrategy {

    @Override
    public int totalWeeks() { return PlanConfig.WEEKS_STRENGTH_HYPERTROPHY; }

    @Override
    public String planTitle() { return "Strength & Mass Plan — 9 weeks"; }

    @Override
    public List<IntensityType> intensityCycle() {
        return PlanConfig.CYCLE_STRENGTH_HYPERTROPHY;
    }
}