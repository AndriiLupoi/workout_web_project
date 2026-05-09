package org.lupoi.workoutapp.application.strategy.plans;

import org.lupoi.workoutapp.application.strategy.BasePlanStrategy;
import org.lupoi.workoutapp.application.strategy.config.PlanConfig;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Endurance plan — 8 weeks.
 *
 * Goal: increase muscular endurance and cardiovascular capacity.
 * Trainer logic:
 *   - High reps (15–20+), very short rest (30–45s)
 *   - Circuit-style training (SETS intensity)
 *   - Includes MEDIUM weeks to maintain baseline strength
 *   - Progressive overload via MORE REPS, not more weight
 *   - Works well for people preparing for sports, events, or general fitness
 *
 * Note: "Endurance training" in resistance context means:
 *   - Metabolic endurance (lactic acid threshold)
 *   - Muscular endurance (high-rep compound movements)
 *   NOT pure cardio — that's done separately.
 */
@Component("ENDURANCE")
public class EndurancePlanStrategy extends BasePlanStrategy {

    @Override
    public int totalWeeks() { return PlanConfig.WEEKS_ENDURANCE; }

    @Override
    public String planTitle() { return "Endurance Plan — 8 weeks"; }

    @Override
    public List<IntensityType> intensityCycle() {
        return PlanConfig.CYCLE_ENDURANCE;
    }

    /**
     * Endurance athletes can handle higher frequency Full Body sessions.
     */
    @Override
    protected int fullBodySessionsPerWeek(int requested) {
        return Math.min(requested, 4);
    }
}