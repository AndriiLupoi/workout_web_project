package org.lupoi.workoutapp.application.strategy.plans;

import org.lupoi.workoutapp.application.strategy.BasePlanStrategy;
import org.lupoi.workoutapp.application.strategy.config.PlanConfig;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Strength plan — 10 weeks.
 *
 * Goal: increase neuromuscular strength via low-rep, high-weight compounds.
 * Trainer logic:
 *   - 3–5 reps, long rest (3 min)
 *   - Mostly HEAVY with deload weeks (MEDIUM) for recovery
 *   - Fewer accessory exercises, more compound focus
 *   - 10 weeks to allow for proper linear/wave progression
 */
@Component("STRENGTH")
public class StrengthPlanStrategy extends BasePlanStrategy {

    @Override
    public int totalWeeks() { return PlanConfig.WEEKS_STRENGTH; }

    @Override
    public String planTitle() { return "Strength Plan — 10 weeks"; }

    @Override
    public List<IntensityType> intensityCycle() {
        return PlanConfig.CYCLE_STRENGTH;
    }
}